<script lang="js" setup>
import {ref, computed, onMounted} from "vue";
import {getUserQuestionsByPage2} from "@/api/quetion.js";


const userInfo = defineModel();
const questions = ref([]);
const startId = ref(0);
const loading = ref(false);
const noMore = ref(false);
const disabled = computed(() => loading.value || noMore.value);
const load = async () => {
  loading.value = true;
  let response = await getUserQuestionsByPage2(startId.value, userInfo.value.userId);
  if (response && response.data.length !== 0) {
    questions.value = questions.value.concat(response.data);
    startId.value = response.data[response.data.length - 1].id;
  }
  else
    noMore.value = true;
  loading.value = false;
};

onMounted(async () => {
  await load();
})
</script>

<template>
  <div class="question-list-wrapper" v-infinite-scroll="load" :infinite-scroll-disabled="disabled">
    <ul class="question-list">
      <el-empty v-if="questions.length === 0" description="TA还没有发布问题哦..." style="background-color: white"/>
      <li v-for="question in questions" :key="question" class="question">
        <el-link type="info" :underline="false" :href="`/question/${question.id}`">
          <div class="question-title">
            {{question.title}}
          </div>
        </el-link>
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
  border-radius: 5px;
  box-shadow: 0 0 0 1px #eee;
  padding: 0;
  margin: 0;
}

.question {
  display: block;
  background: #ffffff;
  margin: 0 auto;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f2f7;
  min-height: 80px;
  border-radius: 5px;
}

.question-title {
  color: black;
  font-size: 18px;
  font-weight: bold;
}

.question-title:hover {
  color: #09408e;
  font-size: 18px;
  font-weight: bold;
}
</style>