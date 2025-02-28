<script setup>
import {computed, onMounted, ref} from "vue";
import {getAnswersByUserId} from "@/api/answer.js";

const answers = ref([]);
const startId = ref(0);
const loading = ref(false);
const noMore = ref(false);
const disabled = computed(() => loading.value || noMore.value);
const userInfo = defineModel();
const load = async () => {
  if (userInfo.value.userId !== 0) {
    loading.value = true;
    let response = await getAnswersByUserId(userInfo.value.userId, startId.value);
    let data = [];
    if (response && response.data.length) {
      for (let i = 0; i < response.data.length;i++) {
        let item = {
          question: {
            id: 0,
            title: ''
          },
          popularAnswer: {
            id: 0,
            text: '',
          },
          image: {
            src: ''
          },
        }
        item.question = response.data[i].question;
        let txt = response.data[i].popularAnswer.text;
        response.data[i].popularAnswer.text = strippedText(txt);
        item.popularAnswer = response.data[i].popularAnswer;
        item.image.src = getFirstImage(txt);
        data.push(item);
      }
      answers.value = answers.value.concat(data);
      startId.value = response.data[response.data.length - 1].popularAnswer.id;
    }
    else
      noMore.value = true;
    loading.value = false;
  }
};

onMounted(async () => {
  await load();
})

function strippedText(text) {
  return text.replace(/<[^>]+>/g, '');
}

function getFirstImage(text) {
  const imgTagPattern = /<img\s+[^>]*src\s*=\s*"([^"]+)"/;
  const match = imgTagPattern.exec(text);
  return match ? match[1] : null;
}
</script>

<template>
  <div class="answer-list-wrapper" v-infinite-scroll="load" :infinite-scroll-disabled="disabled" style="background-color: #f7f8fa;">
    <ul class="answer-list">
      <el-empty v-if="answers.length === 0" description="TA还没有回答问题哦..." style="background-color: white"/>
      <li v-for="answer in answers" :key="answer" class="answer">
        <div>
          <el-link class="question-title" type="info" :underline="false" :href="`/question/${answer.question.id}/${answer.popularAnswer.id}`">
            {{ answer.question.title }}
          </el-link>
        </div>

        <div style="display: flex;margin: 5px 0 5px 0;">
          <div class="answer1">
            <el-link
                type="info"
                :underline="false"
                :href="`/question/${answer.question.id}/${answer.popularAnswer.id}`"
            >
              <div>
                <el-image class="answer-img" :src="answer.image.src" style="width: 100px;height: 100px;margin: 2px;border-radius: 5px;"
                          v-if="answer.image.src" fit="cover"/>
              </div>
            </el-link>
            <el-link
                type="info"
                :underline="false"
                :href="`/question/${answer.question.id}/${answer.popularAnswer.id}`"
            >
              <div class="answer-text" v-text="answer.popularAnswer.text" style="text-align: inherit"></div>
            </el-link>
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

.answer-list {
  border-radius: 5px;
  box-shadow: 0 0 0 1px #eee;
  padding: 0;
  margin: 0;
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