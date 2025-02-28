<script setup>
import {ref, onMounted, defineAsyncComponent, watch, markRaw} from "vue";
import {useRoute} from "vue-router";
import {cancelSubscribe, getUserInfoById, subscribe, getIsSubscribe} from '@/api/user.js'
import {ElMessage} from "element-plus";
import {House, StarFilled} from "@element-plus/icons-vue";
import router from "@/router/index.js";
import QuestionIconComponent from "@/components/icon/QuestionIcon.vue";
import {useUserInfoStore} from "@/stores/userInfo.js";

const route = useRoute();
const userInfo = ref({
  userId: 0,
  nickName: '',
  icon: '',
  about: '',
  background: ''
});
const isMe = ref();

const isSubscribe = ref(false);
const followedAndFans = ref({
  followed: 0,
  fans: 0
})
const buttonType = ref("primary")
const buttonText = ref('关注')
const followedLinkClass = ref('followed-link')
const fansLinkClass = ref('fans-link')
const userInfoStore = useUserInfoStore();
const subButtonLoading = ref(false);


async function loadUserInfo() {
  let uid = route.params.uid;
  let data = (await getUserInfoById(uid)).data;

  if (!data) {
    await router.replace('/404');
  }
  else {
    isMe.value = data.me;
    userInfo.value = data.userInfo;
    followedAndFans.value.fans = data.fans;
    followedAndFans.value.followed = data.followers;
  }
}

async function handleSubscribe() {
  subButtonLoading.value = true;
  if (isSubscribe.value) {
    let response = await cancelSubscribe(userInfo.value.userId);
    if (response.code === 1) {
      followedAndFans.value.fans--;
      buttonType.value = "primary";
      buttonText.value = "关注";
      ElMessage.success("已取消关注");
    }
    else if (response.code === 0) {
      ElMessage.error(response.msg);
    }
  }
  else {
    let response = await subscribe(userInfo.value.userId)
    if (response.code === 1) {
      followedAndFans.value.fans++;
      buttonType.value = "info";
      buttonText.value = "已关注";
      ElMessage.success("关注成功~");
    }
    else if (response.code === 0) {
      ElMessage.error(response.msg);
    }
  }
  subButtonLoading.value = false;
  isSubscribe.value = !isSubscribe.value;
}

onMounted(async () => {
  await loadUserInfo();
  let newTab = route.params.tab;
  switch (newTab) {
    case 'questions':
    case 'favors':
    case 'followed':
    case 'fans': activeTab.value = newTab;break;
    case '': activeTab.value = 'home';break;
    default: await router.push('/user/' + route.params.uid);
  }
  if (userInfoStore.userInfo.userId) {
    let response = await getIsSubscribe(userInfo.value.userId);
    if (response.code === 1 && response.data) {
      isSubscribe.value = true;
    }
    if (isSubscribe.value === true) {
      buttonType.value = 'info';
      buttonText.value = '已关注';
    }
  }
})

// 当前激活的标签页
const activeTab = ref('');
if (route.params.tab) {
  activeTab.value = route.params.tab;
}
else  {
  activeTab.value = 'home';
}
const currentComponent = ref(null);


// 懒加载组件的函数
const loadComponent = (tab) => {
  followedLinkClass.value = 'followed-link'
  fansLinkClass.value = 'fans-link'
  if (tab === 'home') {
    currentComponent.value = markRaw(defineAsyncComponent(() =>
        import('@/components/user/UserAnswersComponent.vue')
    ))
  } else if (tab === 'questions') {
    currentComponent.value = markRaw(defineAsyncComponent(() =>
        import('@/components/user/UserQuestionsComponent.vue')
    ));
  } else if (tab === 'favors') {
    currentComponent.value = markRaw(defineAsyncComponent(() =>
        import('@/components/user/UserFavoriteFolderComponent.vue')
    ));
  } else if (tab === 'fans') {
    fansLinkClass.value = 'link-selected';
    currentComponent.value = markRaw(defineAsyncComponent(() =>
        import('@/components/user/UserFansComponent.vue')
      ));
    } else if (tab === 'followed') {
    followedLinkClass.value = 'link-selected';
    currentComponent.value = markRaw(defineAsyncComponent(() =>
        import('@/components/user/UserFollowedComponent.vue')
    ));
  } else {
    activeTab.value = 'home';
  }
}

async function handleTabClick(tab) {
  await router.push(`/user/${userInfo.value.userId}/${tab}`);
}

async function handleTabClick2() {
  let path = activeTab.value === 'home' ? '' : '/' + activeTab.value;
  await router.push(`/user/${userInfo.value.userId}` + path);
}


watch(() => route.params.tab, (newTab) => {
  // 根据 route.params.tab 来确定当前 activeTab
  if (newTab) {
    switch (newTab) {
      case 'fans':
      case 'followed':
      case 'questions':
      case 'favors':
        activeTab.value = newTab;
        break;
      default:
        activeTab.value = 'home';
    }
    // 加载对应的组件
    loadComponent(activeTab.value);
  } else {
    activeTab.value = 'home';
    loadComponent(activeTab.value);
  }
});

watch(() => route.params.uid, async () => {
  await loadUserInfo();
  activeTab.value = 'home';
  loadComponent(activeTab.value);
});



// 初始化加载第一个标签对应的组件
loadComponent(activeTab.value);

</script>

<template>
  <div class="container">
    <div class="userinfo">
      <div class="background-image">
        <el-image :src="userInfo.background ? userInfo.background : '/api/sys_images/defaultBackground.webp'" fit="cover" style="width: 1000px;height: 300px;"/>
      </div>
      <div class="info">
        <div style="display: flex">
          <div class="avatar" style="margin: 5px">
            <el-avatar :size="60" :src="userInfo.icon ? userInfo.icon : '/api/sys_images/default.webp'"/>
          </div>
          <div class="nickname-and-about" style="display: block;margin: 5px;">
            <div class="nickname">
              {{userInfo.nickName}}
            </div>
            <div class="about">
              {{userInfo.about}}
            </div>
          </div>
        </div>
        <div id="me">
          <el-link href="/user/edit" v-if="isMe" type="primary" :underline="false">编辑个人资料</el-link>
        </div>
        <div id="others" v-if="!isMe">
          <el-button :type="buttonType" @click="handleSubscribe" :loading="subButtonLoading">{{buttonText}}</el-button>
        </div>
      </div>
    </div>

    <div class="toolbar">
      <div class="left">
        <div class="left-tools">
          <el-tabs v-model="activeTab" @tab-change="handleTabClick2">
            <el-tab-pane label="Home" name="home">
              <template #label>
                <span class="custom-tabs-label">
                  <el-icon size="24px"><House /></el-icon>
                </span>
              </template>
            </el-tab-pane>
            <el-tab-pane label="Questions" name="questions">
              <template #label>
                <span class="custom-tabs-label">
                  <QuestionIconComponent height="24px" width="24px"/>
                </span>
              </template>
            </el-tab-pane>
            <el-tab-pane label="Favors" name="favors">
              <template #label>
                <span class="custom-tabs-label">
                  <el-icon size="24px"><StarFilled /></el-icon>
                </span>
              </template>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
      <div class="right">
        <el-link :class="fansLinkClass" @click="activeTab = 'fans';handleTabClick('fans')" :underline="false">
          <div class="right-tools">
            <p class="title">粉丝</p>
            <p class="number">{{followedAndFans.fans}}</p>
          </div>
        </el-link>
        <el-link :class="followedLinkClass" @click="activeTab = 'followed';handleTabClick('followed')" :underline="false">
          <div class="right-tools">
            <p class="title">关注</p>
            <p class="number">{{followedAndFans.followed}}</p>
          </div>
        </el-link>
      </div>
    </div>

    <div class="component">
      <component :is="currentComponent" v-model="userInfo" v-model:isMe="isMe" style="min-height: 300px"/>
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
  background-color: #f7f8fa;
  align-items: center;
}

.userinfo {
  position: relative;
  width: 1000px;
  height: 300px;
  display: flex;
  align-items: flex-end; /* 确保 info 区块出现在背景图片的下方 */
}

.background-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1; /* 背景图层级设为较低 */
}

.info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  position: relative;
  z-index: 2; /* 信息层级设为较高，确保其位于背景图片上方 */
  background-color: initial;
  padding: 10px;
  margin: 10px;
  border-radius: 5px; /* 可选的圆角效果 */
}

.toolbar {
  margin: 0 10px 10px 10px;
  display: flex;
  justify-content: space-between;
  background-color: white;
  width: 1000px;
  height: 66px;
  border-radius: 0 0 5px 5px;
  box-shadow: 0 0 0 1px #eee;
}

.toolbar .left {
  flex: 1;
}

.toolbar .right {
  padding: 10px;
  flex: 1;
  display: flex;
  flex-direction: row-reverse;
  align-items: center;
}

.toolbar .right-tools {
  margin: 4px;
}

.toolbar .left-tools {
  margin: 4px;
  padding: 10px;
  width: 210px;
}

.toolbar .left-tools :deep(.el-tabs__nav-wrap)::after {
  display: none;
}

.toolbar .title {
  font-size: small;
  margin: 0;
}

.toolbar .number {
  font-size: small;
  text-align: center;
  margin: 0;
}

.nickname-and-about {
  text-align: start;
}

.nickname {
  font-weight: bold;
  font-size: large;
}

.about {
  font-size: small;
}

.component {
  width: 1000px;
  height: 100%;
  background-color: #f7f8fa;
}

.link-selected {
  color: #409eff;
}


</style>