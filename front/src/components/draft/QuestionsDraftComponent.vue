<script setup>
import {ref, computed, onMounted} from "vue";
import {deletePendingQuestionsById, getPendingQuestionsByPage, updatePendingQuestion} from "@/api/draft.js";
import NotReviewedIcon from "@/components/icon/NotReviewedIcon.vue";
import ReviewedIcon from "@/components/icon/ReviewingIcon.vue";
import {Delete, EditPen} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";
import {addPendingQuestion, addUnsubmittedPendingQuestion} from "@/api/quetion.js";
import router from "@/router/index.js";
import {useUserInfoStore} from "@/stores/userInfo.js";

const questions = ref([]); //用户列表
const page = ref(0);
const loading = ref(false);
const noMore = ref(false);
const disabled = computed(() => loading.value || noMore.value);
const userInfoStore = useUserInfoStore();

const load = async () => {
  loading.value = true;
  let response = await getPendingQuestionsByPage(page.value)
  if (response && response.data.length) {
    questions.value = questions.value.concat(response.data);
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

const visible = ref(false);
const selectedQuestion = ref({
  id: 0,
  title: '',
  text: '',
  reviewed: false
})
const tmp = ref(null)
const buttonsLoading = ref(false);

function doEdit(question) {
  tmp.value = question;
  visible.value = true;
  selectedQuestion.value.id = question.id;
  selectedQuestion.value.title = question.title;
  selectedQuestion.value.text = question.text;
  selectedQuestion.value.reviewed = question.reviewed;
}

async function save() {
  let response = await updatePendingQuestion(selectedQuestion.value.id, selectedQuestion.value.title, selectedQuestion.value.text, 'save');
  if (response.code === 1) {
    ElMessage.success("保存成功");
    tmp.value.title = selectedQuestion.value.title;
    tmp.value.text = selectedQuestion.value.text;
    visible.value = false;
  }
  else {
    ElMessage.error(response.msg)
  }
}

async function submit() {
  let response = await updatePendingQuestion(selectedQuestion.value.id, selectedQuestion.value.title, selectedQuestion.value.text, 'submit');
  if (response.code === 1) {
    ElMessage.success("提交成功");
    tmp.value.title = selectedQuestion.value.title;
    tmp.value.text = selectedQuestion.value.text;
    tmp.value.reviewed = false;
    visible.value = false;
  }
  else {
    ElMessage.error(response.msg)
  }
}

async function handleDelete(questionId) {
  let response = await deletePendingQuestionsById(questionId);
  if (response === null) {
    ElMessage.error("服务器无响应，请稍后再试");
    return;
  }
  if (response.code === 1) {
    ElMessage.success("删除成功");
    questions.value = questions.value.filter(item => item.id !== questionId);
  }
  else {
    ElMessage.error(response.msg);
  }
}

async function handleSubmitQuestion(title, isSubmit) {
  buttonsLoading.value = true;
  if (title.trim().length < 5) {
    buttonsLoading.value = false;
    ElMessage.error('标题字数过少');
    return;
  }
  isSubmit ? await submit(): await save();
  buttonsLoading.value = false;
  visible.value = false;
}

function handleQuestionTitle() {
  let c = selectedQuestion.value.title.trim().charAt(selectedQuestion.value.title.length - 1);
  if (c !== '?' && c !== '？') selectedQuestion.value.title = selectedQuestion.value.title.trim() + '？';
}

</script>

<template>
  <el-dialog v-model="visible" title="修改问题" width="700" center style="max-height: 600px;">
   <div class="dialog-contain">
     <div class="dialog-input">
       <div class="dialog-input-type">问题标题</div>
       <el-input v-model="selectedQuestion.title" maxlength="50" show-word-limit @blur="handleQuestionTitle"></el-input>
     </div>
     <div class="dialog-input">
       <div class="dialog-input-type">问题描述</div>
       <el-input v-model="selectedQuestion.text" type="textarea" maxlength="100" show-word-limit></el-input>
     </div>
   </div>
    <template #footer>
      <el-button type="primary" :loading="buttonsLoading" @click="handleSubmitQuestion(selectedQuestion.title, true)" :disabled="selectedQuestion.title.trim().length < 5 || selectedQuestion.title.length > 50" v-if="selectedQuestion.reviewed === true">提交</el-button>
      <el-button type="success" :loading="buttonsLoading" @click="handleSubmitQuestion(selectedQuestion.title, false)" :disabled="selectedQuestion.title.trim().length < 5 || selectedQuestion.title.length > 50">保存</el-button>
    </template>
  </el-dialog>

  <div class="question-list-wrapper" v-infinite-scroll="load" :infinite-scroll-disabled="disabled">
    <ul class="question-list">
      <el-empty v-if="questions.length === 0" description="你还没有提问哦..." style="background-color: white"/>
      <li v-for="question in questions" :key="question" class="question">
        <div class="question-title">
          {{question.title}}
        </div>
        <div class="answer-text">
          {{question.text}}
        </div>
        <div class="toolbar">
          <div class="toolbar-buttons">
            <el-button type="primary" :icon="EditPen" text style="margin: 0" @click="doEdit(question)"></el-button>
            <el-popconfirm
                confirm-button-text="确定"
                cancel-button-text="取消"
                icon-color="#626AEF"
                title="您确定删除吗?"
                @confirm="handleDelete(question.id)"
            >
              <template #reference>
                <el-button type="danger" :icon="Delete" text style="margin: 0"></el-button>
              </template>
            </el-popconfirm>
          </div>
          <div class="toolbar-status">
            <div class="icon-and-text" v-if="question.reviewed">
              <el-icon size="20px"><NotReviewedIcon/></el-icon>
              <div class="icon-text" style="color: #d81e06">未提交</div>
            </div>
            <div class="icon-and-text" v-if="!question.reviewed">
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

.dialog-contain {
  display: flex;
  align-items: center;
  flex-direction: column;
}

.dialog-input {
  margin-top: 10px;
  width: 480px;
  display: flex;
  align-items: center;
}

.dialog-input-type {
  width: 80px;
}

.dialog-input :deep(.el-textarea__inner) {
  max-height: 300px;
}
</style>