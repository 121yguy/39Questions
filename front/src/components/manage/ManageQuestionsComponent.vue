<script setup>

import {onMounted, onUnmounted, ref} from "vue";
import {reject, finishReview, getList, checkStatus, approve} from "@/api/manage.js";
import {CloseBold, Select, SwitchButton} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";

const start = ref(false)
const questions = ref([]);
const question = ref({
  id: 0,
  title: '',
  text: '',
  authorId: 0
});
const manageType = 'questions';

onMounted(async () => {
  if ((await checkStatus(manageType)).data) {
    start.value = true;
    questions.value = (await getList(manageType)).data;
    question.value = questions.value.shift();
  }
})

onUnmounted( async () => {
  await finishReview(manageType);
})

async function getQuestions() {
  questions.value = (await getList(manageType)).data;
  if (questions.value.length === 0) {
    ElMessage({
      type: "success",
      message: "暂时没有待审核内容了，待会儿再来看看吧~"
    });
  } else {
    ElMessage({
      type: "success",
      message: "hello worker!审核开始了~"
    });
    start.value = true;
    question.value = questions.value.shift();
  }
}

async function fail(id) {
  await reject(id, manageType);
  await refreshQuestions();
  question.value = questions.value.shift();
}

async function pass(id) {
  await approve(id, manageType);
  await refreshQuestions();
  question.value = questions.value.shift();
}

async function stopReview() {
  await finishReview(manageType);
  questions.value = [];
  question.value = null;
  start.value = false;
  ElMessage({
    type: "success",
    message: "审核结束，辛苦了~"
  });
}

async function refreshQuestions() {
  if (questions.value.length === 0) {
    questions.value = (await getList(manageType)).data;
    if (questions.value.length === 0) {
      ElMessage({
        type: "success",
        message: "暂时没有待审核内容了，待会儿再来看看吧~"
      })
      start.value = false;
    }
  }
}

</script>

<template>
  <div class="review-type">
    问题审核
  </div>
  <div class="start-or-stop-button" v-if="start">
    <el-popconfirm
        confirm-button-text="确定"
        cancel-button-text="取消"
        icon-color="#626AEF"
        title="确定退出审核吗?"
        @confirm="stopReview"
    >
      <template #reference>
        <el-button :icon="SwitchButton" type="danger"></el-button>
      </template>
    </el-popconfirm>
  </div>
  <div class="start-or-stop-button" v-if="!start">
    <el-popconfirm
        confirm-button-text="确定"
        cancel-button-text="取消"
        icon-color="#626AEF"
        title="确定开始审核吗?"
        @confirm="getQuestions"
    >
      <template #reference>
        <el-button color="#FAAFBE" style="color: white">开始审核</el-button>
      </template>
    </el-popconfirm>
  </div>
  <div class="toolbar" v-if="start">
    <el-button @click="pass(question.id)" type="success" :icon="Select"></el-button>
    <el-button @click="fail(question.id)" type="danger" :icon="CloseBold"></el-button>
  </div>
  <div class="content">
    <div v-if="!start">
      <div style="display: flex;align-items: center;flex-direction: column">
        <el-image src="/api/sys_images/hello_worker.webp" style="width: 1000px;height: 560px" fit="contain"></el-image>
        <div style="font-size: 50px;color: #FAAFBE;font-family:Papyrus,fantasy">
          hello worker
        </div>
      </div>
    </div>
    <div class="title" v-if="start">
      <h1>{{question.title}}</h1>
    </div>
    <div class="text" v-if="start">
      <div v-html="question.text"></div>
    </div>
  </div>
</template>

<style scoped>
.content {
  width: 1400px;
  height: 100%;
  overflow: auto;
  position: relative;
}

.review-type {
  position: absolute;
  top: 5px;
  left: 10px;
  border-radius: 10px;
  color: #FAAFBE;
}

.start-or-stop-button {
  position: absolute;
  top: 0;
  right: 0;
  border-radius: 10px;
}

.start-or-stop-button :deep(.el-button) {
  border-radius: 10px;
}


.text {
  padding-bottom: 10px;
}

.toolbar {
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  bottom: 0;
  height: 40px;
  z-index: 2;
  background-color: white;
  width: 1400px;
}
</style>