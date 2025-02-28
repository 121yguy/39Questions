<script lang="js" setup>
import {ref, onMounted, onUpdated, watch, onBeforeMount, markRaw, defineAsyncComponent} from "vue";
import {checkToken, logout} from '@/api/loginAndRegister.js';
import {getUserInfo, updateToken} from '@/api/user.js';
import router from "@/router/index.js";
import {ElMessage} from "element-plus";
import {Message, Search} from '@element-plus/icons-vue';
import {useRoute} from "vue-router";
import {getNumbersOfUnreadNotices} from "@/api/notice.js";
import {useUserInfoStore} from "@/stores/userInfo.js";
import {checkCookie} from "@/utils/CookieUtils.js";

const state = ref({
  circleUrl: ''
});

const route = useRoute();
const userInfoStore = useUserInfoStore();
const userInfo = ref(
    userInfoStore.userInfo
);

const unreadMessage = ref(0);
const activeIndex = ref('/home');
const input = ref('');
const messageClass = ref('message');

const currentComponent = ref(null);

const isLogin = ref(false);


onMounted(async () => {
  if (checkCookie("token")) isLogin.value = true;

  input.value = route.query.q;

  if (userInfo.value.icon)
    state.value.circleUrl = userInfo.value.icon;
  else
    state.value.circleUrl = '/api/sys_images/default.webp';

  activeIndex.value = route.path;

  if (isLogin.value) {
    // await updateToken();

    let response = await getNumbersOfUnreadNotices();
    if (response.code === 1) unreadMessage.value = response.data;
  }

});

onUpdated(async () => {
  input.value = route.query.q;
});

function handleKeydown(event) {
  if (event.key === ' ') {
    event.stopPropagation();  // 阻止空格键事件传播，防止触发 click
  }
}

function search(keyword) {
  if (!keyword.trim()) {
    ElMessage({
      type: 'info',
      message: '搜索内容不能为空'
    });
  } else {
    window.location.href = '/search?type=questions&q=' + encodeURIComponent(keyword);
  }
}

async function handleLogout() {
  let response = await logout();
  if (response.code !== 1) {
    ElMessage.error(response.msg);
    return;
  }
  userInfoStore.removeUserInfo();
  localStorage.removeItem("deviceId");
  let date = new Date();
  date.setDate(date.getDate() - 1);
  document.cookie = 'token' + '=null;expires=' + date.toUTCString() + ';path=/';

  userInfo.value = {
    userId: 0,
    icon: '',
    about: '',
    unreadMessage: 0
  };
  ElMessage.success("注销成功");
  location.reload();
}

function handleClickMsgButton() {
  currentComponent.value = markRaw(defineAsyncComponent(() =>
      import('@/components/notice/NoticeComponent.vue'))
  );
}


watch(unreadMessage, (newVal) => {
  if (newVal > 0) {
    messageClass.value = 'unread_message';
  } else {
    messageClass.value = 'read_message';
  }
});

watch(route, () => {
  activeIndex.value = route.path;
});

</script>


<template>
  <el-container class="header" style="margin: 0; padding: 0; width: 100%;">
    <el-header>
      <el-menu class="menu" :default-active="activeIndex" mode="horizontal" router>
        <el-menu-item index='/'>首页</el-menu-item>
        <el-sub-menu index="2">
          <template #title>分区</template>
          <el-menu-item index="/draft">稿件中心</el-menu-item>
          <el-menu-item index="/question/waiting">回答问题</el-menu-item>
          <el-menu-item index="/comments">留言板</el-menu-item>
        </el-sub-menu>
        <div style="display: flex;align-items: center;">
          <el-input
              v-model="input"
              style="max-width: 300px;pointer-events: auto;height: 34px;"
              placeholder="请输入搜索内容"
              class="input"
              @keydown="handleKeydown"
              @keydown.enter="search(input)"
              clearable
          >
            <template #append>
              <el-button @click="search(input)" :icon="Search"/>
            </template>
          </el-input>
        </div>
        <div style="position: absolute;top: 5px;right: 120px;width: 50px;height: 50px;display: flex;flex-direction: column;justify-content: center;align-items: center" v-if="userInfo.userId !== 0">
          <el-popover
              class="message-bar"
              placement="bottom"
              :width="520"
              trigger="click"
          >
            <template #reference>
              <el-button text style="width: 60px;height: 60px;" @click="handleClickMsgButton" v-if="isLogin"><el-icon :class="messageClass" size="24px"><Message /></el-icon></el-button>
            </template>
            <template #default>
              <component :is="currentComponent" v-model:userInfo="userInfo" v-model:unreadMessage="unreadMessage" style="max-width: 520px;"/>
            </template>
          </el-popover>
        </div>
        <el-sub-menu index="user" style="position: absolute; right: 10px;" v-if="userInfo.userId">
          <template #title>
            <div class="demo-basic--circle">
              <div class="block">
                <el-avatar :size="50" :src="state.circleUrl"/>
              </div>
            </div>
          </template>
          <el-menu-item :index="`/user/${userInfo.userId}`">个人中心</el-menu-item>
          <el-menu-item @click="handleLogout">退出登录</el-menu-item>
        </el-sub-menu>
        <el-menu-item index='/login' v-if="userInfo.userId === null" style="position: absolute; right: 40px;">
          <el-avatar :size="50" src="/api/sys_images/loginAvatar.webp"/>
        </el-menu-item>
      </el-menu>
    </el-header>
  </el-container>

  <router-view></router-view>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

.menu {
  margin: 0;
  position: fixed;
  top: 0;
  width: 100%;
  min-width: 700px;
  z-index: 1000;
  background-color: white;
  box-shadow: 0 1px 3px rgba(26, 26, 26, .1);
}

.unread_message {
  position: relative; /* 让伪元素相对父元素定位 */
  display: inline-block;
}

.unread_message::after {
  content: '';
  position: absolute;
  bottom: 0;
  right: 0;
  width: 8px;
  height: 8px;
  background-color: red;
  border-radius: 50%;
  border: 1px solid white;
}

</style>
