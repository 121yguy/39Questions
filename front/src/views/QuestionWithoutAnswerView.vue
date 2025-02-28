<script setup>
import {ref, computed, onMounted} from "vue";
import {getPendingQuestionsByPage, updatePendingQuestion} from "@/api/draft.js";
import NotReviewedIcon from "@/components/icon/NotReviewedIcon.vue";
import ReviewedIcon from "@/components/icon/ReviewingIcon.vue";
import {Delete, EditPen} from "@element-plus/icons-vue";
import {getQuestionsWithoutAnswer} from "@/api/quetion.js";
import router from "@/router/index.js";
import {useUserInfoStore} from "@/stores/userInfo.js";

const questions = ref([]); //用户列表
const headId = ref(0);
const loading = ref(false);
const noMore = ref(false);
const disabled = computed(() => loading.value || noMore.value);
const userInfoStore = useUserInfoStore();

const load = async () => {
  loading.value = true;
  let response = await getQuestionsWithoutAnswer(headId.value)
  if (response && response.data.length) {
    questions.value = questions.value.concat(response.data);
    headId.value = response.data[response.data.length - 1].id;
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
function doEdit(question) {
  visible.value = true;
  selectedQuestion.value.id = question.id;
  selectedQuestion.value.title = question.title;
  selectedQuestion.value.text = question.text;
  selectedQuestion.value.reviewed = question.reviewed;
}

async function save() {
  await updatePendingQuestion(selectedQuestion.value.id, selectedQuestion.value.title, selectedQuestion.value.text, 'save');
  visible.value = false;
}

async function submit() {
  await updatePendingQuestion(selectedQuestion.value.id, selectedQuestion.value.title, selectedQuestion.value.text, 'submit');
  visible.value = false;
}

</script>

<template>
  <div class="container">
    <div class="question-list-wrapper" v-infinite-scroll="load" :infinite-scroll-disabled="disabled">
      <ul class="question-list">
        <el-empty v-if="questions.length === 0" description="暂时没有待回答的问题哦..." style="background-color: white"/>
        <li v-for="question in questions" :key="question" class="question">
          <div class="question-title">
            {{question.title}}
          </div>
          <div>
            <el-button type="primary" @click="router.push(`/question/${question.id}?write`)" :icon="EditPen">写回答</el-button>
          </div>
        </li>
      </ul>
      <p v-if="loading" v-loading="loading"></p>
    </div>
  </div>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

.container {
  display: flex;
  width: 100%;
  justify-content: center;
}

.question-list-wrapper {
  width: 700px;
}

.question-list {
  padding: 0;
  margin: 0;
  background: #ffffff;
  border-radius: 5px;
  box-shadow: 0 1px 3px rgba(26, 26, 26, .1);
}

.question {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-radius: 5px;
  background: #ffffff;
  margin: 0 auto;
  padding: 20px;
  border-bottom: 1px solid #f8f8fa;
  min-height: 100px;
  max-height: 216px;
}

.question-title {
  color: black;
  font-size: 18px;
  font-weight: bold;
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

</style>