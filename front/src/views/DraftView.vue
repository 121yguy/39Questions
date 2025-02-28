<script setup>
import {defineAsyncComponent, markRaw, onMounted, ref, watch} from "vue";

const activeName = ref('');
const component = ref(null);

onMounted(() => {
  activeName.value = 'questions';
})

watch(activeName, () => {
  if (activeName.value === 'questions') {
    component.value = markRaw(defineAsyncComponent(() =>
        import('@/components/draft/QuestionsDraftComponent.vue')
    ));
  }
  else {
    component.value = markRaw(defineAsyncComponent(() =>
        import('@/components/draft/AnswersDraftComponent.vue')
    ));
  }
})

</script>

<template>
  <div class="main-contain">
    <div class="tabs">
      <el-tabs v-model="activeName" type="card">
        <el-tab-pane label="问题草稿" name="questions"></el-tab-pane>
        <el-tab-pane label="解答草稿" name="answers"></el-tab-pane>
      </el-tabs>
    </div>

    <div class="component">
      <component :is="component"/>
    </div>
  </div>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

.main-contain {
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
  height: 30px;
}

.component {
  width: 700px;
}

</style>