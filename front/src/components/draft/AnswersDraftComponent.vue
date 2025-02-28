<script setup>
import {ref, computed, onMounted} from "vue";
import {deletePendingAnswersById, getPendingAnswersByPage, updatePendingAnswers} from "@/api/draft.js";
import NotReviewedIcon from "@/components/icon/NotReviewedIcon.vue";
import ReviewedIcon from "@/components/icon/ReviewingIcon.vue";
import {Delete, EditPen} from "@element-plus/icons-vue";
import EditorComponent from "@/components/editor/EditorComponent.vue";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";
import {useUserInfoStore} from "@/stores/userInfo.js";

const answers = ref([]); //用户列表
const page = ref(0);
const loading = ref(false);
const noMore = ref(false);
const disabled = computed(() => loading.value || noMore.value);
const userInfoStore = useUserInfoStore();

const load = async () => {
  loading.value = true;
  let response = await getPendingAnswersByPage(page.value)
  if (response && response.data.length) {
    response.data.forEach(data => {
      data.showText = strippedText(data.text)
    })
    answers.value = answers.value.concat(response.data);
    page.value++;
  }
  else
    noMore.value = true;
  loading.value = false;
};

onMounted( async () => {
  if (!userInfoStore.userInfo.userId) {
    await router.push("/");
  }
  else {
    await load();
  }
});

function strippedText(text) {
  return text.replace(/<[^>]+>/g, '');
}

const visible = ref(false);
const dialogTitle = ref('');
const draftContent = ref('');
const selectedAnswer = ref({
  id: 0,
  status: false
})
const tmp = ref(null);
const buttonsLoading = ref(false);


function doEdit(answer) {
  tmp.value = answer;
  visible.value = true;
  dialogTitle.value = answer.title;
  draftContent.value = answer.text;
  selectedAnswer.value.id = answer.id;
  selectedAnswer.value.status = answer.reviewed;
}

async function save() {
  buttonsLoading.value = true;
  let response = await updatePendingAnswers(selectedAnswer.value.id, selectedAnswer.value.status, draftContent.value, 'save');
  if (response.code === 1) {
    tmp.value.text = draftContent.value;
    tmp.value.showText = strippedText(draftContent.value);
    ElMessage.success("保存成功~");
    visible.value = false;
  }
  else {
    ElMessage.error(response.msg);
  }
  buttonsLoading.value = false;
}

async function submit() {
  buttonsLoading.value = true;
  let response = await updatePendingAnswers(selectedAnswer.value.id, selectedAnswer.value.status, draftContent.value, 'submit');
  if (response.code === 1) {
    tmp.value.text = draftContent.value;
    tmp.value.showText = strippedText(draftContent.value);
    tmp.value.reviewed = false;
    ElMessage.success("提交成功~");
    visible.value = false;
  }  else {
    ElMessage.error(response.msg);
  }
  buttonsLoading.value = false;
}

async function handleDelete(answerId) {
  let response = await deletePendingAnswersById(answerId);
  if (response.code === 1) {
    answers.value = answers.value.filter(item => item.id !== answerId);
    ElMessage.success("删除成功~");
  }
  else {
    ElMessage.error(response.msg);
  }
}

</script>

<template>

  <el-dialog v-model="visible" :title="dialogTitle" width="700" center style="max-height: 1000px;margin-top: 20px;margin-bottom: 0">
    <component :is="EditorComponent" v-model="draftContent"/>
    <template #footer>
      <el-button type="primary" :loading="buttonsLoading" :disabled="strippedText(draftContent).length < 50" @click="submit" v-if="selectedAnswer.status">提交</el-button>
      <el-button type="success" :loading="buttonsLoading" :disabled="strippedText(draftContent).length < 0 || (!selectedAnswer.status && strippedText(draftContent).length < 50)" @click="save">保存</el-button>
    </template>
  </el-dialog>

  <div class="question-list-wrapper" v-infinite-scroll="load" :infinite-scroll-disabled="disabled">
    <ul class="question-list">
      <el-empty v-if="answers.length === 0" description="你还没有回答过问题哦..." style="background-color: white"/>
      <li v-for="answer in answers" :key="answer" class="question">
        <div class="question-title">
          {{answer.title}}
        </div>
        <div class="answer-text">
          {{answer.showText}}
        </div>
        <div class="toolbar">
          <div class="toolbar-buttons">
            <el-button type="primary" :icon="EditPen" text style="margin: 0" @click="doEdit(answer)"></el-button>
            <el-popconfirm
                confirm-button-text="确定"
                cancel-button-text="取消"
                icon-color="#626AEF"
                title="您确定删除吗?"
                @confirm="handleDelete(answer.id)"
            >
              <template #reference>
                <el-button type="danger" :icon="Delete" text style="margin: 0"></el-button>
              </template>
            </el-popconfirm>
          </div>
          <div class="toolbar-status">
            <div class="icon-and-text" v-if="answer.reviewed">
              <el-icon size="20px"><NotReviewedIcon/></el-icon>
              <div class="icon-text" style="color: #d81e06">未提交</div>
            </div>
            <div class="icon-and-text" v-if="!answer.reviewed">
              <el-icon color="#e6a23c"><ReviewedIcon/></el-icon>
              <div class="icon-text" style="color: #e6a23c">审核中</div>
            </div>
          </div>
        </div>
      </li>
    </ul>
    <p v-if="loading" v-loading="loading"></p>
  </div>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

.question-list {
  padding: 0;
  margin: 0;
  background: #ffffff;
  border-radius: 0 0 5px 5px;
  box-shadow: 0 1px 3px rgba(26, 26, 26, .1);
}

.question {
  border-radius: 0 0 5px 5px;
  display: block;
  background: #ffffff;
  margin: 0 auto;
  padding: 20px;
  border-bottom: 1px solid #f8f8fa;
  max-height: 216px;
}

.answer-text {
  margin-top: 6px;
  color: black;
  max-height: 110px;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 5;
  overflow: hidden;
}

.question-title {
  color: black;
  font-size: 18px;
  font-weight: bold;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
}

.icon-and-text {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.icon-text {
  font-size: 12px;
}
</style>