<script setup>
import {computed, onMounted, ref} from "vue";
import {updateOperateTime, getNotices, readAllNotices} from "@/api/notice.js";
import {CircleCheckFilled} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";

const notices = ref([]);
const page = ref(1);
const loading = ref(false);
const noMore = ref(false);
const disabled = computed(() => loading.value || noMore.value);
const userInfo =  defineModel("userInfo");
const unreadMessage =  defineModel("unreadMessage");
const load = async () => {
  if (userInfo.value.userId !== 0) {
    loading.value = true;
    let response = await getNotices(page.value);
    if (response && response.data.length) {
      notices.value = notices.value.concat(response.data);
      page.value++;
    }
    else
      noMore.value = true;
    loading.value = false;
  }
};

onMounted(async () => {
  await load();
})

async function handleReadMessage(notice) {
  notice.readTime = 1;
  let response = await updateOperateTime(notice.id);
  if (response.code === 1) {
    unreadMessage.value--;
    notice.read = true;
  }
  else {
    ElMessage.error(response.msg);
  }
}

async function handleReadAllNotices() {
  let response = await readAllNotices();
  if (response.code === 1) {
    unreadMessage.value = 0;
    notices.value.forEach(notice => notice.read = true)
    ElMessage.success("消息已经全部已读~");
  }
  else {
    ElMessage.error(response.msg);
  }
}

</script>

<template>
  <div class="notices-list-wrapper" v-infinite-scroll="load" :infinite-scroll-disabled="disabled">
    <ul class="notice-list" style="padding: 0">
      <div style="display: flex;justify-content: center;">
        <el-button style="width: 500px" text @click="handleReadAllNotices">一键已读</el-button>
      </div>
      <li v-for="notice in notices" :key="notice" class="notice">
        <div style="display: flex;align-items: center">
          <div style="width: 30px;margin-right: 10px;justify-content: center;align-items: center;display: flex;">
            <div class="red-dot" v-if="!notice.read"></div>
          </div>
          <div v-html="notice.content" style="max-width: 360px;padding: 10px">
          </div>
        </div>
        <div style="width: 41px;height: 32px">
          <el-button v-if="!notice.read" @click="handleReadMessage(notice)" type="success" :icon="CircleCheckFilled" text></el-button>
        </div>
      </li>
    </ul>
  </div>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

.notices-list-wrapper {
  width: 480px;
  max-height: 400px;
  overflow-y: auto;
}


.notice-list {
  margin-bottom: 0;
}

.notice {
  display: flex;
  width: 460px;
  background: #ffffff;
  margin: 0 auto;
  padding: 8px 8px 8px 2px;
  border-bottom: #ebeced solid 1px;
  word-break: break-all;
  justify-content: space-between;
  align-items: center;
}

.red-dot {
  width: 10px;
  height: 10px;
  background-color: red;
  border-radius: 50%;
  display: inline-block;
  border: 1px solid white;
}

</style>
