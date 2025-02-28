<script setup>
import {ref, onMounted, onUnmounted, computed} from "vue";
import { useUserInfoStore } from "@/stores/userInfo.js";
import {addComment, deleteCommentById, getCommentsByStartId} from "@/api/comment.js";
import {ElMessage} from "element-plus";
import {More} from "@element-plus/icons-vue";
import {forbiddenWords} from "@/constant/forbiddenWords.js";

const userInfoStore = useUserInfoStore();
const userInfo = userInfoStore.userInfo;
const textarea = ref('');
const input = ref(false);
const panelRef = ref(null);

// 检测点击是否在 comment-panel 之外
const handleClickOutside = (event) => {
  if (panelRef.value && !panelRef.value.contains(event.target)) {
    input.value = false;
  }
};

// 挂载和卸载时添加和移除事件监听器
onMounted(() => {
  document.addEventListener("mousedown", handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener("mousedown", handleClickOutside);
});

const comments = ref([]);
const startId = ref(2147483648);
const loading = ref(false);
const noMore = ref(false);
const disabled = computed(() => loading.value || noMore.value);
const load = async () => {
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
};
const hoveredCommentId = ref(0);
const sendButtonLoading = ref(false);

onMounted(async () => {
  await load();
});

async function send() {
  sendButtonLoading.value = true;
  if (forbiddenWordsFilter(textarea.value.trim())) {
    ElMessage.error("留言中含有违禁词，禁止发布!")
    sendButtonLoading.value = false;
    return;
  }
  let response = await addComment(textarea.value.trim());
  if (response.code === 1) {
    ElMessage.success('发布成功~');
    let newComment = {
      id: response.data,
      authorId: userInfo.userId,
      content: textarea.value,
      time: new Date((Date.now())).toLocaleDateString('zh-CN', {
        timeZone: 'Asia/Shanghai',
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      }),
      icon: userInfo.icon,
      nickName: userInfo.nickName,
    }
    comments.value.unshift(newComment);
    textarea.value = '';
  }
  else if (response.code === 0) {
    ElMessage.error(response.msg);
  }
  sendButtonLoading.value = false;
}

async function deleteComment(id) {
  let response = await deleteCommentById(id);
  if (response.code === 1) {
    comments.value = comments.value.filter(comment => comment.id !== id);
    ElMessage.success('删除成功~');
  }
  else {
    ElMessage.error(response.msg);
  }
}

function forbiddenWordsFilter(content) {
  return forbiddenWords.some(word => {
    const regex = new RegExp(word, 'g'); // 创建正则表达式，进行全局匹配
    return regex.test(content); // 如果匹配上，则返回 true
  });
}


</script>

<template>
  <div class="container">
    <div class="comments-wrapper">
      <div class="comments">
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
                        <el-button type="danger" style="width: 100%" text @click="comment.visiable = false;deleteComment(comment.id)">删除留言</el-button>
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
        </div>
        <div style="height: 100px"></div>
      </div>

      <div class="comment-panel" ref="panelRef">
        <div style="display:flex;">
          <el-avatar :size="50" :src="userInfo.icon ? userInfo.icon : '/api/sys_images/default.webp'" style=""/>
          <el-input
              v-model="textarea"
              :rows="2"
              type="textarea"
              placeholder="留下你的足迹吧~"
              style="padding: 0 20px"
              @focus="input = true"
          />
        </div>
        <div style="display:flex;justify-content: end;margin-top: 10px;padding: 0 20px" v-if="input">
          <el-button type="primary" :disabled="textarea === '' || textarea.length > 100" @click="send" :loading="sendButtonLoading">发布</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

.container {
  display: flex;
  justify-content: center;
}

.comments-wrapper {
  width: 680px;
}

.comment-panel {
  border-radius: 5px;
  border-top: 0.5px solid #e3e5e7;
  width: 680px;
  bottom: 0;
  z-index: 1;
  position: fixed;
  background-color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 20px;
}

.comment-panel :deep(.el-textarea__inner) {
  resize: none;
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
