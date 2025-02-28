<script setup>
import {ref, onMounted, onBeforeUnmount, markRaw, defineAsyncComponent} from "vue";
import {useRoute, useRouter} from "vue-router";
import {ElMessage} from "element-plus";
import {cancelLike, getQuestionAndAnswer, like} from "@/api/answer.js";
import {CaretTop, Share, StarFilled} from "@element-plus/icons-vue";
import {useUserInfoStore} from "@/stores/userInfo.js";

const route = useRoute();
const router = useRouter();

const question = ref({
  id: '',
  authorId: '',
  nickName: '',
  icon: '',
  title: '',
  text: ''
})

const answer = ref({
  id: '',
  authorId: '',
  nickName: '',
  icon: '',
  text: '',
  likes: 0,
  buttons: {
    like: '',
    favor: ''
  }
})

const textClass = ref('question-text');
const userInfoStore = useUserInfoStore();

async function handleLike(answer) {
  if (answer.buttons.like === 'like-button') {
    let response = await like(answer.id);
    if (response.code === 1) {
      answer.buttons.like = 'liked-button';
      answer.likes++;
    }
    else if (response.code === 0) {
      ElMessage.error(response.msg);
    }
  }
  else {
    let response = await cancelLike(answer.id);
    if (response.code === 1) {
      answer.buttons.like = 'like-button';
      answer.likes--;
    }
    else if (response.code === 0) {
      ElMessage.error(response.msg);
    }
  }
}

onMounted(async () => {
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

function share() {
  navigator.clipboard.writeText(`http://localhost:5173${route.path}`);
  ElMessage({
    type: "success",
    message: "已复制链接"
  })
}

onMounted( async () => {
  let response = await getQuestionAndAnswer(route.params.aid, route.params.qid);
  if (response.data === null)
    await router.push('/404');
  question.value = response.data.question;
  answer.value.id = response.data.answer.id;
  answer.value.icon = response.data.answer.icon === null ? '/api/sys_images/default.webp' : response.data.answer.icon;
  answer.value.nickName = response.data.answer.nickName;
  answer.value.buttons.like = response.data.answer.like ? "liked-button" : "like-button";
  answer.value.buttons.favor = response.data.answer.favorite ? "favored" : "favor";
  answer.value.likes = response.data.answer.likes;
  answer.value.text = response.data.answer.text;
});
</script>

<template>

  <el-dialog v-model="centerDialogVisible" title="选择收藏夹" width="500" center style="max-height: 600px">
    <component ref="editor" :is="favorComponent" v-model:answerId="favorAnswerId" v-model:dialogVisible="centerDialogVisible" v-model:question="favoringAnswer"/>
  </el-dialog>

  <div class="question-and-answers">
    <div class="mini-question">
      <div style="display: flex;width: 80%; justify-content: space-between;margin: auto;">
        <div class="question-title">
          {{ question.title }}
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
    </div>
    <div class="answer">
      <el-link type="info" :underline="false" :href="`/user/${question.authorId}`">
        <el-avatar :size="40" :src="answer.icon" />
        <br>
        {{answer.nickName}}
      </el-link>
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
      </div>
    </div>
    <div style="width: 100%;display: flex;justify-content: center;">
      <div class="list-bottom-info"><el-button style="width: 100%;height: 40px" text type="primary" @click="router.push(`/question/${question.id}`)">查看全部回答</el-button></div>
    </div>
  </div>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

.question {
  padding: 10px;
  border-radius: 5px;
  box-shadow: 0 1px 3px rgba(26, 26, 26, .1);
  background-color: #f7f8fa;
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
}

.question-and-answers .question {
  margin: 0;
  background: #ffffff;
  color: black;
  align-items: center;
  justify-content: center;
  padding: 10px;
}

.question-title {
  color: black;
  font-size: 18px;
  font-weight: bold;
}

.answer {
  border-radius: 5px;
  border-bottom: 1px solid #f8f8fa;
  display: block;
  width: 650px;
  background: #ffffff;
  margin: 10px auto;
  box-shadow: 0 1px 3px rgba(26, 26, 26, .1);
  padding: 20px;
}

.answer .answer-text {
  text-align: start;
  word-break: break-word;
}

.toolbar {
  display: flex;
  text-align: start;
}

.list-bottom-info {
  background-color: white;
  width: 650px;
  box-shadow: 0 1px 3px rgba(26, 26, 26, .1);
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