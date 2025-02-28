<script setup>
import {ref, computed, onMounted, onBeforeUnmount, markRaw, defineAsyncComponent} from "vue";
import {getQuestionById} from "@/api/quetion.js";
import {
  addPendingAnswer, addUnsubmittedPendingAnswer,
  cancelLike,
  deleteAnswer,
  getAnswersByQuestionId,
  like
} from "@/api/answer.js";
import {useRoute, useRouter} from "vue-router";
import {ElMessage} from "element-plus";
import {Edit} from "@element-plus/icons-vue/global";
import {CaretTop, Comment, EditPen, More, Share, StarFilled} from "@element-plus/icons-vue";
import EditorComponent from "@/components/editor/EditorComponent.vue";
import {useUserInfoStore} from "@/stores/userInfo.js";
import ReviewedIcon from "@/components/icon/ReviewingIcon.vue";
import {getImageSources} from "@/utils/htmlUtils.js";
import {updatePendingAnswers} from "@/api/draft.js";


const postButton = ref('')

const route = useRoute();
const router = useRouter();

const question = ref({
  title: '',
  text: '',
  nickName: '',
  icon: '',
  authorId: 0,
});
const questionId = ref();

const answers = ref([]);
const startId = ref(0);
const loading = ref(false);
const noMore = ref(false);
const disabled = computed(() => loading.value || noMore.value);

const useEditor = ref(false);
const editButtonType = ref('primary');
const valueHtml = ref('');
const userInfoStore = useUserInfoStore();
const userInfo = userInfoStore.userInfo;

const pendingImages = ref([]);

const hoveredAnswerId = ref(0);
const isDoEdit = ref(false)
const editingAnswerId = ref(0);


onMounted(async () => {
  questionId.value = route.params.id;
  let data = (await getQuestionById(questionId.value)).data;
  if (!data) {
    await router.push('/404');
  }
  question.value = data;
  if (data.icon)
    question.value.icon = data.icon;
  else
    question.value.icon = '/api/sys_images/default.webp';

  startId.value = 0;
});

onMounted(async () => {
  await load()
  if (route.query.write === null) {
    useSwitcher(false);
  }
  window.addEventListener('scroll', handleScroll);
});

onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll);
});

const handleScroll = () => {
  const questionElement = document.querySelector('.question');
  const miniQuestionElement = document.querySelector('.mini-question');

  if (questionElement) {
    const rect = questionElement.getBoundingClientRect();
    // 当 question 的底部（rect.bottom）超过视口的顶部时，显示 mini-question
    if (rect.bottom < 0) {
      miniQuestionElement.style.display = 'block';
    } else {
      miniQuestionElement.style.display = 'none';
    }
  }
};

const load = async () => {
  loading.value = true;
  let response = await getAnswersByQuestionId(questionId.value, startId.value);
  if (response && response.data.length) {
    answers.value = answers.value.concat(
        await Promise.all(response.data.map(answer => ({
          ...answer,
          icon: answer.icon === null ? '/api/sys_images/default.webp' : answer.icon,
          buttons: {
            like: answer.like ? 'liked-button' : 'like-button',
            favor: answer.favorite ? 'favored' : 'favor',
          },
          isMyAnswer: answer.authorId === userInfo.userId,
          visible: false
        })))
    );
    startId.value = response.data[response.data.length - 1].id;
  }
  else
    noMore.value = true;
  loading.value = false;
};

async function handleLike(answer) {
  if (answer.buttons.like === 'like-button') {
    let response = await like(answer.id);
    if (response.code === 1) {
      answer.buttons.like = 'liked-button';
      answer.likes++;
    }
    else if (response.data.code === 0) {
      ElMessage.error(response.msg);
    }
  }
  else {
    let response = await cancelLike(answer.id);
    if (response.code === 1) {
      answer.buttons.like = 'like-button';
      answer.likes--;
    }
    else if (response.data.code === 0) {
      ElMessage.error(response.msg);
    }
  }
}
async function doEdit(answer) {
  editingAnswerId.value = answer.id;
  answer.visible = false;
  valueHtml.value = answer.text;
  pendingImages.value = getImageSources(answer.text);
  useSwitcher(true);
}

function useSwitcher(isEdit) {
  if (!useEditor.value) {
    isDoEdit.value = isEdit;
    if (isEdit)
      postButton.value = '提交编辑';
    else {
      postButton.value = '发布评论';
      valueHtml.value = '';
    }
    useEditor.value = true;
    editButtonType.value = 'info';
  }
  else {
    useEditor.value = false;
    editButtonType.value = 'primary';
  }
}

const addAnswerButtonLoading = ref(false);

async function handleSubmit(isEdit, isSubmit) {
  addAnswerButtonLoading.value = true;
  let editorContent = valueHtml.value;

  let parser = new DOMParser();
  let doc = parser.parseFromString(editorContent, 'text/html');

  let images = doc.querySelectorAll('img');

  images.forEach(img => {
    img.style.maxWidth = '610px';
    img.style.height = 'auto';
  });

  valueHtml.value = doc.body.innerHTML;
  if (isEdit) {
    if (isSubmit) {
      let response = await addPendingAnswer(questionId.value, valueHtml.value, editingAnswerId.value);
      if (response.code === 1) {
        answers.value = answers.value.filter(answer => answer.id !== editingAnswerId.value);
        let userInfo = useUserInfoStore().userInfo;
        let newAnswer = {
          authorId: userInfo.userId,
          icon: userInfo.icon,
          nickName: userInfo.nickName,
          text: doc.body.innerHTML,
          isPending: true
        }
        answers.value.unshift(newAnswer);
        ElMessage.success('提交成功，请耐心等待审核~');
      }
      else if (response.code === 0) {
        ElMessage.error(response.msg);
      }
    }
    else {
      let response = await addUnsubmittedPendingAnswer(questionId.value, valueHtml.value, editingAnswerId.value);
      if (response.code === 1) {
        ElMessage.success('已保存到稿件中心');
      }
      else if (response.code === 0) {
        ElMessage.error(response.msg);
      }
    }

  }
  else {
    if (!isSubmit) {
      let response = await addUnsubmittedPendingAnswer(questionId.value, valueHtml.value, 0);
      if (response.code === 1) {
        ElMessage.success('已保存到稿件中心');
      }
      else if (response.code === 0) {
        ElMessage.error(response.msg);
      }
    }
    else {
      let response = await addPendingAnswer(questionId.value, valueHtml.value, 0);
      if (response.code === 1) {
        let userInfo = useUserInfoStore().userInfo;
        let newAnswer = {
          authorId: userInfo.userId,
          icon: userInfo.icon,
          nickName: userInfo.nickName,
          text: doc.body.innerHTML,
          isPending: true
        }
        answers.value.unshift(newAnswer);
        ElMessage.success('提交成功，请耐心等待审核~');
      }
      else if (response.code === 0) {
        ElMessage.error(response.msg);
      }
    }
  }
  addAnswerButtonLoading.value = false;
  useSwitcher();
}

const textClass = ref('question-text');


const editor = ref(null)

function share(answer) {
  navigator.clipboard.writeText('http://localhost:5173' +`/question/${route.params.id}/${answer.id}`);
  ElMessage({
    type: "success",
    message: "已复制链接"
  })
}

const centerDialogVisible = ref(false)
const favorAnswerId = ref(0);
const favorComponent = ref(null);
const favoringAnswer = ref();

function handleFavor(answer) {
  if (!userInfoStore.userInfo.userId) {
    ElMessage.error("未登录，请登录后再试");
    return;
  }
  favoringAnswer.value = answer;
  let answerId = answer.id;
  favorComponent.value = markRaw(defineAsyncComponent(() =>
      import('@/components/favor/FavorComponent.vue')
  ));
  centerDialogVisible.value = true;
  favorAnswerId.value = answerId;
}

function strippedText(text) {
  return text.replace(/<[^>]+>/g, '');
}

const deleteDialogVisible = ref(false);
const pendingDeleteAnswer = ref(null);

async function handleDelete(pendingDeleteAnswer) {
  let response = await deleteAnswer(pendingDeleteAnswer.id);
  if (response.code === 1) {
    answers.value = answers.value.filter(answer => answer.id !== pendingDeleteAnswer.id);
    ElMessage.success("删除成功~");
  }
  else if (response.code === 0) {
    ElMessage.error(response.msg);
  }
  deleteDialogVisible.value = false;
}

</script>

<template>

  <el-dialog v-model="centerDialogVisible" title="选择收藏夹" width="500" center style="max-height: 600px">
    <component ref="editor" :is="favorComponent" v-model:answerId="favorAnswerId" v-model:dialogVisible="centerDialogVisible" v-model:question="favoringAnswer"/>
  </el-dialog>

  <el-dialog
      v-model="deleteDialogVisible"
      title="提示"
      width="500"
      center
  >
    <span>确认删除此回答吗？删除后将无法恢复!!!</span>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="deleteDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleDelete(pendingDeleteAnswer)">
          确认
        </el-button>
      </div>
    </template>
  </el-dialog>

  <div class="question-and-answers">
    <div class="mini-question">
      <div style="display: flex;width: 80%; justify-content: space-between;margin: auto;">
        <div class="question-title">
          {{ question.title }}
        </div>
        <div>
          <el-button :type=editButtonType :icon="Edit" @click="useSwitcher(false)"/>
        </div>
      </div>
    </div>
    <div class="question">
      <div class="question-title">
        {{ question.title }}
      </div>

      <div style="display: inline-flex;align-items: center;max-width: 100%;">
        <div :class="textClass" v-html="question.text"></div>
        <el-button text size="small" type="primary" style="margin-left: 8px;" v-if="question.text && textClass === 'question-text'" @click="textClass = 'question-text-expanded';">点击展开</el-button>
      </div>
      <div>
        <el-button :type=editButtonType :icon="Edit" @click="useSwitcher(false)"/>
      </div>
    </div>

    <EditorComponent v-if="useEditor" v-model="valueHtml" v-model:pendingImages="pendingImages"/>
    <div class="editor-toolbar" v-if="useEditor">
      <el-button type="primary" @click="handleSubmit(isDoEdit, true)" :disabled="strippedText(valueHtml).length < 50" :loading="addAnswerButtonLoading">{{postButton}}</el-button>
      <el-button type="success" @click="handleSubmit(isDoEdit, false)" :disabled="strippedText(valueHtml).length === 0" :loading="addAnswerButtonLoading">保存到草稿箱</el-button>
    </div>

    <div class="answers-list-wrapper" v-infinite-scroll="load" :infinite-scroll-disabled="disabled" v-if="!useEditor">
      <ul class="answer-list">
        <el-empty v-if="answers.length === 0" description="该问题还没有解答哦..." style="background-color: white;"/>
        <li
            class="answer"
            v-for="answer in answers"
            :key="answer.id"
            @mouseenter="hoveredAnswerId = answer.id"
            @mouseleave="hoveredAnswerId = 0"
        >
          <div class="answer-userinfo">
            <el-link type="info" :underline="false" :href="`/user/${answer.authorId}`">
              <el-avatar :size="40" :src="answer.icon"/>
              {{ answer.nickName }}
            </el-link>
            <div v-if="answer.isPending">
              <div class="icon-and-text" v-if="!question.reviewed">
                <el-icon color="#e6a23c"><ReviewedIcon/></el-icon>
                <div class="icon-text" style="color: #e6a23c">审核中</div>
              </div>
            </div>
          </div>
          <div class="answer-text" v-html="answer.text"></div>
          <div class="toolbar" v-if="!answer.isPending">
            <div>
              <el-button :class="answer.buttons.like" :icon="CaretTop" type="info" size="large" @click="handleLike(answer)">{{ answer.likes }}</el-button>
            </div>
            <div>
              <el-button :class="answer.buttons.favor === 'favor' ? 'favor-button' : 'favored-button'" :icon="StarFilled" text type="info" size="large" @click="handleFavor(answer)"></el-button>
            </div>
            <div class="share">
              <el-button :icon="Share" text type="info" size="large" @click="share(answer)"></el-button>
            </div>
            <div class="more">
              <el-popover placement="bottom" :width="10" :teleported="false" trigger="click">
                <div style="display: flex;align-items: center;max-width: 100%;">
                  <el-button style="width: 100%" text @click="doEdit(answer)">编辑</el-button>
                  <el-button type="danger" style="width: 100%" text @click="deleteDialogVisible = true;pendingDeleteAnswer = answer;">删除</el-button>
                </div>
                <template #reference>
                  <el-button :icon="More" text size="large" v-if="hoveredAnswerId === answer.id && answer.authorId === userInfoStore.userInfo.userId"></el-button>
                </template>
              </el-popover>
            </div>
          </div>
        </li>
      </ul>
      <div class="list-bottom-info" v-if="loading" v-loading="loading"></div>
      <div class="list-bottom-info" v-if="noMore"><el-button style="width: 100%;height: 40px" text :icon="EditPen" type="primary" @click="useSwitcher(false)">写回答</el-button></div>
    </div>
  </div>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

body {
  background-color: #f7f8fa;
}

.question {
  padding: 10px;
  border-radius: 5px;
  box-shadow: 0 1px 3px rgba(26, 26, 26, .1);
  margin: 0;
  background: #ffffff;
  color: black;
  align-items: center;
  justify-content: center;
  min-width: 800px;
}

.question-and-answers {
  background-color: #f7f8fa;
}

.mini-question {
  margin: 0;
  position: fixed;
  top: 0;
  width: 100%;
  z-index: 1000;
  background-color: #fff;
  display: none;
  justify-content: space-between;
  box-shadow: 0 1px 3px rgba(26, 26, 26, .1);
  padding: 10px;
  border-radius: 5px;
  min-width: 800px;
}

.question-title {
  color: black;
  font-size: 18px;
  font-weight: bold;
}

.answers-list-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 90%;
  margin: 2px auto;
  padding: 0;
  flex: 1;
  text-align: center;
  background-color: #f7f8fa;
}

.answers-list-wrapper .answer-list {
  display: block;
  padding: 0;
  margin: 10px auto;
  width: 650px;
  list-style: none;
  background-color: #f7f8fa;
}

.answers-list-wrapper .answer-list .answer {
  border-bottom: 1px solid #f8f8fa;
  display: block;
  width: 650px;
  background: #ffffff;
  margin: 0 auto;
  box-shadow: 0 1px 3px rgba(26, 26, 26, .1);
  padding: 20px;
}

.answer .answer-userinfo {
  display: flex;
  justify-content: space-between;
  align-items: center;
  text-align: start;
}

.answer .answer-text {
  text-align: start;
  word-break: break-word;
}

.answer .answer-text p img {
  max-width: 100px;
}

.answer .toolbar {
  display: flex;
  text-align: start;
}

.editor-toolbar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 40px;
  background-color: white;
  position: fixed;
  bottom: 0;
  z-index: 1;
  border-top: 1px solid #ebeced;
}

.list-bottom-info {
  background-color: white;
  width: 650px;
  box-shadow: 0 1px 3px rgba(26, 26, 26, .1);
}

.icon-and-text {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.liked-button {
  border: none;
  background-color: #1772f6;
  color: white;
}

.like-button {
  border: none;
  background-color: #e7f0fe;
  color: #1772f6;
}

.like-button:hover {
  border: none;
  background-color: #dceafe;
  color: #1772f6;
}

.favor-button:hover {
  color: gold;
}

.favored-button {
  color: gold;
}

.share :hover {
  color: chartreuse;
}

.question-text {
  padding: 10px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.question-text-expanded {
  padding: 10px 0;
  word-break: break-all;
}

</style>