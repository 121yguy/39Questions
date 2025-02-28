<script setup>
import {onMounted} from "vue";
import {useUserInfoStore} from "@/stores/userInfo.js";
import {getUserInfo, updateToken} from "@/api/user.js";
import {checkCookie} from "@/utils/CookieUtils.js";

onMounted( async () => {
  // const userInfoStore = useUserInfoStore();
  // if (!userInfoStore.userInfo.userId) {
  //   let response = await getUserInfo();
  //   if (response.code === 1) {
  //     userInfoStore.userInfo = response.data;
  //   }
  //   else {
  //     userInfoStore.removeUserInfo();
  //   }
  // }

  const userInfoStore = useUserInfoStore();
  if (checkCookie("token")) {
    let response1 = await updateToken();
    if (response1.code !== 1) {
      let date = new Date();
      date.setDate(date.getDate() - 1);
      document.cookie = 'token' + '=null;expires=' + date.toUTCString() + ';path=/';
      userInfoStore.removeUserInfo();
      location.reload();
      return;
    }
  }
  else {
    userInfoStore.removeUserInfo();
    return;
  }
  let response = await getUserInfo();
  if (response.code === 1) {
    userInfoStore.userInfo = response.data;
  }
})

</script>

<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<style>
html, body, #app {
  min-height: 100%;
  background-color: #f7f8fa;
}

.el-popover.el-popper {
  line-height: normal !important;
}

</style>
