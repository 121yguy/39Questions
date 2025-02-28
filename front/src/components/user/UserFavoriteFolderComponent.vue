<script setup>
import {ref, computed, onMounted} from "vue";
import {deleteFavoritesFolder, getFavoritesFolders} from "@/api/favorite.js";
import {Delete} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";

const userInfo = defineModel();
const isMe = defineModel('isMe');
const uid = userInfo.value.userId;
const favoriteFolders = ref([]);
const page = ref(1);
const loading = ref(false);
const noMore = ref(false);
const disabled = computed(() => loading.value || noMore.value);

const load = async () => {
  loading.value = true;
  let response = await getFavoritesFolders(uid, page.value);
  if (response.data && response.data.length) {
    favoriteFolders.value = favoriteFolders.value.concat(response.data);
    page.value++;
  }
  else
    noMore.value = true;
  loading.value = false;
};

onMounted( async () => {
  await load();
});

async function doDelete(fid) {
  let response = await deleteFavoritesFolder(fid);
  if (response.code === 1) {
    ElMessage.success("删除成功");
  }
  else {
    ElMessage.error(response.msg);
  }
}
</script>

<template>
  <div class="favorite-folders-wrapper" v-infinite-scroll="load" :infinite-scroll-disabled="disabled">
    <ul class="favorite-folders">
      <el-empty v-if="favoriteFolders.length === 0" description="TA还没有收藏过回答哦..." style="background-color: white"/>
      <li v-for="favoritesFolder in favoriteFolders" :key="favoritesFolder" class="favorite-folder">
        <div style="display: flex;flex-direction: row;justify-content: space-between;">
          <div>
            <el-link type="info" :underline="false" :href="`/favoritesFolder/${favoritesFolder.id}`">
              <div class="favorite-folder-name">
                {{favoritesFolder.name}}
              </div>
            </el-link>
          </div>
          <div v-if="isMe">
            <el-button type="danger" text :icon="Delete" @click="doDelete(favoritesFolder.id)"></el-button>
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

.favorite-folders {
  border-radius: 5px;
  box-shadow: 0 0 0 1px #eee;
  padding: 0;
  margin: 0;
}

.favorite-folder {
  display: block;
  background: #ffffff;
  margin: 0 auto;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f2f7;
  min-height: 80px;
  border-radius: 5px;
}

.favorite-folder-name {
  color: black;
  font-size: 18px;
  font-weight: bold;
}

.favorite-folder-name:hover {
  color: #09408e;
  font-size: 18px;
  font-weight: bold;
}
</style>