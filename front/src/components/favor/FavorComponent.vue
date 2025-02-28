<script setup>
import {computed, onMounted, ref} from "vue";
import {addFavoritesFolders, doFavor, getPersonalFavoritesFolders} from "@/api/favorite.js";
import {Plus} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";

const favoriteFolders = ref([]);
const page = ref(1);
const loading = ref(false);
const noMore = ref(false);
const disabled = computed(() => loading.value || noMore.value);
const aid = defineModel("answerId");
const centerDialogVisible = defineModel("dialogVisible");
const favoringQuestion = defineModel("question");
let favoredFoldersIds = [];
let newlyFavorFolderIds = [];
let newlyCancelFavorFolderIds = [];
const submitDisable = ref(true);
const inputRef = ref();
const inputValue = ref('')
const inputText = ref("新建收藏夹");
const inputIcon = ref(Plus);
const inputWordLimitVisible = ref(false);

const load = async () => {
  loading.value = true;
  let response = await getPersonalFavoritesFolders(page.value, aid.value);
  if (response && response.data.length) {
    favoriteFolders.value.push(...response.data);
    favoriteFolders.value.forEach(folder => {
      if (folder.favoredStatus) {
        favoredFoldersIds.push(folder.favoritesFolder.id);
      }
    })
    page.value++;
  }
  else
    noMore.value = true;
  loading.value = false;
};

onMounted( async () => {
  await load();
});

async function submit() {
  if (newlyFavorFolderIds.length !== 0)
    await doFavor(aid.value, newlyFavorFolderIds, true);
  if (newlyCancelFavorFolderIds.length !== 0)
    await doFavor(aid.value, newlyCancelFavorFolderIds, false);
  if (newlyCancelFavorFolderIds.length === favoredFoldersIds.length) {
    favoringQuestion.value.buttons.favor = 'favor';
  }
  if (newlyFavorFolderIds.length !== 0)
    favoringQuestion.value.buttons.favor = 'favored';
  centerDialogVisible.value = false;
}

function selectCheckBox(folder) {
  let id = folder.favoritesFolder.id;
  if (folder.favoredStatus) { //选择
    favoredFoldersIds.indexOf(id) === -1 ? newlyFavorFolderIds.push(id)
        :
        newlyCancelFavorFolderIds = newlyCancelFavorFolderIds.filter(fid => {
      return fid !== id;
    });

  }
  else { //取消选择
    favoredFoldersIds.indexOf(id) === -1 ? newlyFavorFolderIds = newlyFavorFolderIds.filter(fid => {
          return fid !== id;
        }) :
        newlyCancelFavorFolderIds.push(id);
  }
  submitDisable.value = newlyFavorFolderIds.length === 0 && newlyCancelFavorFolderIds.length === 0;
}

function focusInput() {
  inputWordLimitVisible.value = true;
  inputText.value = "不超过20个字符";
  inputIcon.value = null;
}

function blurInput() {
  inputWordLimitVisible.value = false;
  inputText.value = "新建收藏夹";
  inputIcon.value = Plus;
  inputValue.value = '';
}

async function addFolder() {
  if (inputValue.value.trim().length === 0) {
    ElMessage.error("请输入新文件名");
    inputValue.value = '';
    return;
  }
  let response = await addFavoritesFolders(inputValue.value.trim());
  if (response.code === 1) {
    let newFolder = {
      favoritesFolder: {
        id: response.data,
        name: inputValue.value,
      },
      favoredStatus: false
    }
    favoriteFolders.value.push(newFolder);
    ElMessage.success("新建成功")
    inputValue.value = '';
  }
  else if (response.code === 0) {
    ElMessage.error(response.msg);
  }
}
</script>

<template>
  <div class="favorite-folders-wrapper" v-infinite-scroll="load" :infinite-scroll-disabled="disabled">
    <ul class="favorite-folders">
      <li v-for="favoritesFolder in favoriteFolders" :key="favoritesFolder" class="favorite-folder">
        <el-checkbox v-model="favoritesFolder.favoredStatus" @change="selectCheckBox(favoritesFolder)">
          {{favoritesFolder.favoritesFolder.name}}
        </el-checkbox>
      </li>
    </ul>
    <p v-if="loading" v-loading="loading"></p>
    <div style="margin: 10px" v-if="noMore">
      <el-input
          class="input"
          ref="inputRef"
          v-model="inputValue"
          :placeholder="inputText"
          :prefix-icon="inputIcon"
          @focus="focusInput"
          @blur="blurInput"
          maxlength="20"

      >
        <template #suffix>
          <el-button class="input-button" v-if="inputWordLimitVisible" @click="addFolder">新建</el-button>
        </template>
      </el-input>
    </div>
  </div>
  <div class="submit-button">
    <el-button @click="submit" type="primary" :disabled="submitDisable">确定</el-button>
  </div>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

.favorite-folders-wrapper {
  max-height: 400px;
  overflow: auto;
}

.favorite-folders {
  padding: 0;
  margin: 0;
  background: #ffffff;
}

.favorite-folder {
  display: block;
  background: #ffffff;
  margin: 0 auto;
  padding: 20px;
}

.submit-button {
  display: flex;
  margin: 20px;
  justify-content: center;
}

.input :deep(.el-input__wrapper) {
  padding: 1px 1px 1px 11px;
}

.input-button {
  margin: 0;
  border-color: white;
  background-color: #d9f1f9;
  color: #00aeec;
  width: 90px
}

</style>