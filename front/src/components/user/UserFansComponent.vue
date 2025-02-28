<script setup>
import {ref, computed, onMounted} from "vue";
import {cancelSubscribe, getFansList, subscribe} from "@/api/user.js";
import {ElMessage} from "element-plus";

const userInfo = defineModel()
const uid = userInfo.value.userId;
const users = ref([]); //用户列表
const page = ref(0);
const loading = ref(false);
const noMore = ref(false);
const disabled = computed(() => loading.value || noMore.value);

const load = async () => {
  loading.value = true;
  let response = await getFansList(uid, page.value);
  if (response && response.data.length) {
    users.value = users.value.concat(response.data);
    page.value += 6;
  }
  else
    noMore.value = true;
  loading.value = false;
};

onMounted( async () => {
  await load();
});

async function doSubscribe(user) {
  if (user.subscribe) {
    let response = await cancelSubscribe(user.userInfo.userId);
    if (response.code === 1) {
      ElMessage.success("已取消关注");
      user.subscribe = !user.subscribe;
    }
    else {
      ElMessage.error(response.msg);
    }
  }
  else {
    let response = await subscribe(user.userInfo.userId);
    if (response.code === 1) {
      ElMessage.success("关注成功~");
      user.subscribe = !user.subscribe;
    }
    else {
      ElMessage.error(response.msg);
    }
  }
}

</script>

<template>
  <div class="user-list-wrapper" v-infinite-scroll="load" :infinite-scroll-disabled="disabled">
    <ul class="user-list">
      <el-empty v-if="users.length === 0" description="TA还没有粉丝哦..." style="background-color: white"/>
      <li v-for="user in users" :key="user" class="user">
        <div style="display: flex; justify-content: space-between;">
          <div>
            <el-link type="info" :underline="false" :href="`/user/${user.userInfo.userId}`">
              <el-avatar :size="50" :src="user.userInfo.icon ? user.userInfo.icon : '/api/sys_images/default.webp'" style="margin-right: 10px"/>
              {{ user.userInfo.nickName }}
            </el-link>
          </div>
          <div>
            <el-button :type="user.subscribe ? 'info' : 'primary'" style="width: 73px" @click="doSubscribe(user)">{{ user.subscribe ? '已关注' : '关注' }}</el-button>
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

.user-list {
  padding: 0;
  margin: 0;
  border-radius: 5px;
  box-shadow: 0 0 0 1px #eee;
}

.user {
  border-radius: 5px;
  display: block;
  background: #ffffff;
  margin: 0 auto;
  padding: 20px;
  border-bottom: 1px solid #f8f8fa;
}
</style>