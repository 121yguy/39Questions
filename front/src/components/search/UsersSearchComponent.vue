<script setup>
import {computed, ref} from "vue";
import {searchUsers} from "@/api/user.js";

const users = ref([]);

const page = ref(0);


const loading = ref(false);
const noMore = ref(false);
const disabled = computed(() => loading.value || noMore.value);
const keyword = defineModel();

const load = async () => {
  loading.value = true;
  let response = await searchUsers(keyword.value, page.value);
  if (response.data) {
    for (let i = 0; i < response.data.length; i++) {
      if (!response.data[i].icon)
        response.data[i].icon = '/api/sys_images/default.webp';
    }
    users.value = users.value.concat(response.data);
    page.value++;
  }
  else
    noMore.value = true;
  loading.value = false;
};
</script>

<template>
  <div class="users-list-wrapper" v-infinite-scroll="load" :infinite-scroll-disabled="disabled">
    <el-empty v-if="users.length === 0" description="暂时没有搜索结果哦..." style="background-color: white"/>
    <ul class="users-list">
      <li v-for="user in users" :key="user" class="user">
        <el-link type="info" :underline="false" :href="`/user/${user.userId}`">
          <el-avatar :size="50" :src="user.icon"/>
          <br>
          {{ user.nickName }}
        </el-link>
      </li>
    </ul>
    <p v-if="loading" v-loading="loading" style="text-align: center"></p>
  </div>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

.users-list {
  padding: 0;
  margin: 0;
  background: #ffffff;
  border-radius: 0 0 5px 5px;
  box-shadow: 0 1px 3px rgba(26, 26, 26, .1);
}

.user {
  border-radius: 0 0 5px 5px;
  display: block;
  background: #ffffff;
  margin: 0 auto;
  padding: 20px;
  border-bottom: 1px solid #f8f8fa;
  max-height: 216px;
}
</style>