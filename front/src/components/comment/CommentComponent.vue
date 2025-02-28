<script setup>
import { computed, onMounted, ref } from "vue";
import {getCommentsByStartId} from "@/api/comment.js";
import { More } from "@element-plus/icons-vue";

const comments = ref([]);
const startId = ref(0);
const loading = ref(false);
const noMore = ref(false);
const disabled = computed(() => loading.value || noMore.value);
const userInfo = defineModel();
const load = async () => {
  if (userInfo.value.userId !== 0) {
    loading.value = true;
    let response = await getCommentsByStartId(startId.value);
    if (response && response.data.length) {
      response.data.forEach(data => {
        data.time = new Date(data.time).toLocaleDateString('zh-CN', {
          timeZone: 'Asia/Shanghai',
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit'
        });
        data.visiable = false;
      })
      comments.value = comments.value.concat(response.data);
      startId.value = response.data[response.data.length - 1].id;
    }
    else
      noMore.value = true;
    loading.value = false;
  }
};
const hoveredCommentId = ref(0);

onMounted(async () => {
  await load();
});

</script>

<template>
  <div class="comment-list-wrapper" v-infinite-scroll="load" :infinite-scroll-disabled="disabled">
    <ul class="comment-list">
      <li
          v-for="comment in comments"
          :key="comment.id"
          class="comment"
          @mouseenter="hoveredCommentId = comment.id"
          @mouseleave="hoveredCommentId = 0;comment.visiable = false"
      >
        <div style="display: flex; align-items: start;">
          <el-link type="info" :underline="false" :href="`/user/${comment.authorId}`">
            <el-avatar :size="50" :src="comment.icon !== null ? comment.icon : '/api/sys_images/default.webp'" />
          </el-link>
          <div style="display: block; padding-left: 10px">
            <div>
              <el-link type="info" :underline="false" :href="`/user/${comment.authorId}`">
                {{ comment.nickName }}
              </el-link>
            </div>
            <div>
              {{ comment.content }}
            </div>
            <div style="font-size: 13px; color: #9499A0">
              {{ comment.time }}
            </div>
          </div>
          <div class="settings">
            <div class="more" v-if="comment.authorId === userInfo.userId && hoveredCommentId === comment.id">
              <el-popover :visible="comment.visiable" placement="bottom" :width="10" :teleported="false" trigger="hover">
                <div>
                  <el-button style="width: 100%" text @click="comment.visiable = false">删除留言</el-button>
                </div>
                <template #reference>
                  <el-button text :icon="More" @click="comment.visiable = true"></el-button>
                </template>
              </el-popover>
            </div>
          </div>
        </div>
      </li>
    </ul>
    <p v-if="loading" v-loading="loading"></p>
    <p v-if="noMore">没有更多了 ┑(￣Д ￣)┍</p>
  </div>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

.comment-list {
  border-radius: 5px;
  padding: 0;
  margin: 0;
  background: #ffffff;
}

.comment {
  border-radius: 5px;
  display: block;
  background: #ffffff;
  margin: 0 auto;
  padding: 20px;
  border-bottom: 1px solid #E3E5E7;
}

.settings {
  margin: auto 0 auto auto;
}

.more :deep(.el-popper) {
  padding: 0;
}

</style>
