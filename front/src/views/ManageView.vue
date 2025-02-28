<script setup>
import {defineAsyncComponent, markRaw, onMounted, ref, watch} from "vue";
import {manage} from "@/api/user.js";
import router from "@/router/index.js";
import {useRoute} from "vue-router";

const display = ref(false);
const activeTab = ref('home');
const route = useRoute();
const currentComponent = ref(null);

onMounted( async () => {
  let newTab = route.params.tab;
  switch (newTab) {
    case 'questions':
    case 'answers':
    case 'userInfos': activeTab.value = newTab;break;
    case '': activeTab.value = 'home';break;
    default: await router.push('/manage');
  }

  if ((await manage()).code !== 1) {
    await router.replace('/manage ');
  }
  display.value = true;
})
const loadComponent = (tab) => {
  if (tab === 'home') {
    currentComponent.value = markRaw(defineAsyncComponent(() =>
        import('@/components/manage/ManageHomeComponent.vue')
    ))
  } else if (tab === 'questions') {
    currentComponent.value = markRaw(defineAsyncComponent(() =>
        import('@/components/manage/ManageQuestionsComponent.vue')
    ));
  } else if (tab === 'answers') {
    currentComponent.value = markRaw(defineAsyncComponent(() =>
        import('@/components/manage/ManageAnswersComponent.vue')
    ));
  } else if (tab === 'userInfos') {
    currentComponent.value = markRaw(defineAsyncComponent(() =>
        import('@/components/manage/ManageUserInfoComponent.vue')
    ));
  }
}

watch(activeTab, () => {
  if (activeTab.value === 'home')
    router.push('/manage')
  else {
    switch (activeTab.value) {
      case 'questions':
      case 'answers':
      case 'userInfos': router.push('/manage/' + activeTab.value);break;
      default: activeTab.value = 'home';
    }
  }
})

watch(() => route.params.tab, async (newTab) => {
  if (newTab) {
    switch (newTab) {
      case 'questions':
      case 'answers':
      case 'userInfos': activeTab.value = newTab;break;
      default: activeTab.value = 'home';
    }
  }
  else {
    activeTab.value = 'home';
  }
});

watch(activeTab, (newTab) => {
  loadComponent(newTab);
});

loadComponent(activeTab.value);

</script>

<template>
  <div v-if="display">
    <h1>
      WELCOME, ADMIN!
    </h1>
    <div class="shows">
      <div class="tabs">
        <el-tabs class="custom-tabs" tab-position="right" type="border-card" v-model="activeTab">
          <el-tab-pane label="首页" name="home"></el-tab-pane>
          <el-tab-pane label="问题" name="questions"></el-tab-pane>
          <el-tab-pane label="解答" name="answers"></el-tab-pane>
          <el-tab-pane label="用户" name="userInfos"></el-tab-pane>
        </el-tabs>
      </div>

      <div class="component">
        <div class="component-box">

          <component :is="currentComponent"/>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

.shows {
  display: flex;
  height: 742px;
  overflow: hidden;
}

.tabs {
  height: 742px;
  width: 68px;
  margin-left: 30px;
  margin-right: 10px;
  border-radius: 10px;
  box-shadow: 0 0 0 1px #eee;
}

.tabs :deep(.el-tabs__item):hover {
  color: #FAAFBE !important;
}

.tabs :deep(.el-tabs__item.is-active) {
  color: #FAAFBE;
}

.custom-tabs {
  height: 742px;
  border-radius: 10px;
}

.custom-tabs :deep(.el-tabs__item) {
  width: 68px;
  height: 187px;
}

.component {
  display: flex;
  justify-content: center;
  height: 740px;
  width: 1500px;
}

.component-box {

  padding: 34px 20px 20px 20px;
  overflow: auto;
  width: 1440px;
  background-color: white;
  max-height: 740px;
  border-radius: 10px;
  box-shadow: 0 0 0 1px #eee;
  position: relative;
}

</style>