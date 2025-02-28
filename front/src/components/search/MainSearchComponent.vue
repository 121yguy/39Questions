<script setup>
import {computed, onMounted, ref} from "vue";
import {searchQuestions} from "@/api/quetion.js";

const questions = ref([]);
const page = ref(0);
const loading = ref(false);
const noMore = ref(false);
const disabled = computed(() => loading.value || noMore.value);
const keyword = defineModel();

const load = async () => {
  loading.value = true;
  let response = await searchQuestions(keyword.value, page.value);
  if (response.data) {
    questions.value = questions.value.concat(response.data);
    page.value++;
  }
  else
    noMore.value = true;
  loading.value = false;
};

onMounted( async () => {
  await load();
})

function getFirstImage(text) {
  const imgTagPattern = /<img\s+[^>]*src\s*=\s*"([^"]+)"/;
  const match = imgTagPattern.exec(text);
  return match ? match[1] : null;
}

</script>

<template>
  <div class="questions-list-wrapper" v-infinite-scroll="load" :infinite-scroll-disabled="disabled">
    <el-empty v-if="questions.length === 0" description="暂时没有搜索结果哦..." style="background-color: white"/>
    <ul class="questions-list">
      <li v-for="answer in questions" :key="answer" class="answer">
        <div>
          <el-link class="question-title" type="info" :underline="false" :href="`/question/${answer.question.id}/${answer.popularAnswer.id}`"
                   target="_blank" rel="noopener noreferrer">
            {{ answer.question.title }}
          </el-link>
        </div>

        <div style="display: flex;margin: 5px 0 5px 0;">
          <div class="answer1">
            <el-link
                type="info"
                :underline="false"
                :href="`/question/${answer.question.id}/${answer.popularAnswer.id}`"
                target="_blank"
                rel="noopener noreferrer"
            >
            </el-link>
            <el-link
                type="info"
                :underline="false"
                :href="`/question/${answer.question.id}/${answer.popularAnswer.id}`"
                target="_blank"
                rel="noopener noreferrer"
            >
              <div class="answer-text" v-text="answer.popularAnswer.text" style="text-align: inherit"></div>
            </el-link>
          </div>
        </div>
      </li>
    </ul>
    <p v-if="loading" v-loading="loading" style="text-align: center"></p>
  </div>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

.questions-list {
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

.answer {
  display: block;
  background: #ffffff;
  margin: 0 auto;
  padding: 20px;
  border-bottom: 1px solid #f0f2f7;
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

.answer1 {
  display: flex;
  align-items: flex-start;
}

.answer1 p {
  margin: 0;
}

.answer-text {
  color: black;
  max-height: 100px;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 5;
  overflow: hidden;
}

.answer-text:hover {
  color: #535861;
}
</style>