<script setup>
import {computed, onMounted, ref} from "vue";
import {getFavorites, getFavoritesFolderInfo} from "@/api/favorite.js";
import {useRoute} from "vue-router";
import router from "@/router/index.js";

const route = useRoute();
const fid = ref(0);
const favorites = ref([]);
const page = ref(1);
const loading = ref(false);
const noMore = ref(false);
const disabled = computed(() => loading.value || noMore.value);
const folderInfo = ref({
  name: '',
  userId: 0,
  nickName: '',
  icon: '',
});

const load = async () => {
  loading.value = true;
  let response = await getFavorites(fid.value, page.value++);
  if (response && response.data.length) {
    for (let i = 0; i < response.data.length;i++) {
      let txt = response.data[i].popularAnswer.text;
      response.data[i].popularAnswer.text = strippedText(txt);
      response.data[i].image = {};
      response.data[i].image.src = getFirstImage(txt);
    }
    favorites.value = favorites.value.concat(response.data);
  }
  else
    noMore.value = true;
  loading.value = false;
};

onMounted( async () => {
  fid.value = route.params.fid;
  let data = (await getFavoritesFolderInfo(fid.value)).data;
  if (!data) await router.push('/404')
  else folderInfo.value = (await getFavoritesFolderInfo(fid.value)).data;
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
  <div class="container">
    <div class="folder-info">
      <div class="folder-title">{{ folderInfo.name }}</div>
      <div style="display: flex;font-size: small;align-items: center;padding-left: 10px;">
        <div style="margin-right: 5px">创建者:</div>
        <div><el-link type="info" :underline="false" :href="`/user/${folderInfo.userId}`"><el-avatar :src="folderInfo.icon" size="small"/></el-link></div>
        <div><el-link type="info" :underline="false" :href="`/user/${folderInfo.userId}`">{{ folderInfo.nickName }}</el-link></div>
      </div>
    </div>

    <div class="questions-list-wrapper">
      <div class="shows">
        <ul
            class="question-list"
            v-infinite-scroll="load"
            :infinite-scroll-disabled="disabled"
        >
          <el-empty v-if="favorites.length === 0" description="这个收藏夹还没有任何内容哦..." style="background-color: white"/>
          <li class="question" v-for="question in favorites" :key="question.id">
            <div>
              <el-link class="question-title" type="info" :underline="false" :href="`/question/${question.question.id}`"
                       target="_blank" rel="noopener noreferrer">
                {{ question.question.title }}
              </el-link>
            </div>

            <div style="display: flex;margin: 5px 0 5px 0;">
              <div class="answer">
                <el-link
                    type="info"
                    :underline="false"
                    :href="`/question/${question.question.id}/${question.popularAnswer.id}`"
                    target="_blank"
                    rel="noopener noreferrer"
                >
                  <div>
                    <el-image class="answer-img" :src="question.image.src" style="width: 100px;height: 100px;margin: 2px;border-radius: 5px;"
                              v-if="question.image.src" fit="cover"/>
                  </div>
                </el-link>
                <el-link
                    type="info"
                    :underline="false"
                    :href="`/question/${question.question.id}/${question.popularAnswer.id}`"
                    target="_blank"
                    rel="noopener noreferrer"
                >
                  <div class="answer-text" v-text="question.popularAnswer.text" style="text-align: inherit"></div>
                </el-link>
              </div>
            </div>
          </li>
          <p v-if="loading" v-loading="loading" style="text-align: center; margin: 0"></p>
        </ul>
      </div>
    </div>
  </div>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

.container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.folder-info {
  padding: 20px;
  border-radius: 5px;
  box-shadow: 0 1px 3px rgba(26, 26, 26, .1);
  margin: 0;
  background: #ffffff;
  color: black;
  align-items: center;
  justify-content: center;
  width: 650px;
  min-height: 100px;
}

.folder-title {
  color: black;
  font-size: 18px;
  font-weight: bold;
  margin: 10px;
}

.questions-list-wrapper {
  display: flex;
  align-items: center;
  margin: auto;
  height: 100%;
  background-color: #f7f8fa;
}

.questions-list-wrapper .shows {
  display: flex;
  margin: auto;
  background-color: #f7f8fa;
}

.questions-list-wrapper .question-list {
  padding: 0;
  display: block;
  width: 650px;
  margin: 5px 0;
  background: white;
  border-radius: 5px;
  box-shadow: 0 1px 3px rgba(26, 26, 26, .1);
}

.questions-list-wrapper .question {
  display: block;
  background: #ffffff;
  margin: 0 auto;
  padding: 20px;
  border-bottom: 1px solid #f0f2f7;
  border-radius: 5px;
}

.questions-list-wrapper .question + .question {
  margin-top: 4px;
}

.questions-list-wrapper .question .question-title {
  color: black;
  font-size: 18px;
  font-weight: bold;
}

.questions-list-wrapper .question .question-title :hover {
  color: #09408e;
  font-size: 18px;
  font-weight: bold;
}

.answer {
  display: flex;
  align-items: flex-start;
}

.answer p {
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