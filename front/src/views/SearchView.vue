<script lang="js" setup>
import {ref, onMounted, watch, onUnmounted, markRaw, defineAsyncComponent} from "vue";
import {useRoute} from "vue-router";

const route = useRoute();
const keyword = ref(route.query.q);
const activeName = ref(route.query.type || 'questions');
let isPopState = false;
const component = ref(null);

onMounted( async () => {
  window.addEventListener('popstate', handlePopState);
  if (activeName.value === 'questions') {
    component.value = markRaw(defineAsyncComponent(() =>
        import('@/components/search/MainSearchComponent.vue')
    ));
  } else {
    component.value = markRaw(defineAsyncComponent(() =>
        import('@/components/search/UsersSearchComponent.vue')
    ));
  }
});

watch(activeName,  () => {
  if (!isPopState) {
    window.history.pushState("", "", "/search?type=" + activeName.value + "&q=" + route.query.q);
  }
  else {
    isPopState = false;
  }
});

watch(activeName, () => {
  if (activeName.value === 'questions') {
    component.value = markRaw(defineAsyncComponent(() =>
        import('@/components/search/MainSearchComponent.vue')
    ));
  }
  else {
    component.value = markRaw(defineAsyncComponent(() =>
        import('@/components/search/UsersSearchComponent.vue')
    ));
  }
})

const handlePopState = () => {
  isPopState = true;
  activeName.value = route.query.type;
};

onUnmounted(() => {
  window.removeEventListener('popstate', handlePopState);
});

</script>

<template>

  <div class="container">
    <div class="tabs">
      <el-tabs v-model="activeName" type="card">
        <el-tab-pane label="综合" name="questions"></el-tab-pane>
        <el-tab-pane label="用户" name="users"></el-tab-pane>
      </el-tabs>
    </div>

    <div class="component">
      <component :is="component" v-model="keyword"/>
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
  width: 100%;
  align-items: center;
}

.tabs {
  background-color: white;
  width: 700px;
  box-shadow: 0 1px 0 rgba(26, 26, 26, .1);
  border-radius: 5px 5px 0 0;
}

.tabs :deep(.el-tabs) {
  margin: 0;
  height: 40px;
}

.component {
  width: 700px;
}
</style>
