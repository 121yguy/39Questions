<script setup>
import {onMounted, ref} from "vue";
import {addPendingUserInfo, getUserInfo} from "@/api/user.js";
import {CameraFilled, EditPen} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";
const userInfo = ref({
  userId: 0,
  nickName: "",
  about: "",
  icon: "",
  background: ""
});

const oldUserInfo = ref({
  userId: 0,
  nickName: "",
  about: "",
  icon: "",
  background: ""
});

const input = ref({
  nickName: '',
  about: ''
})

const editNickName = ref(false);

const editAbout = ref(false);

onMounted( async () => {
  userInfo.value = (await getUserInfo()).data;
  oldUserInfo.value = {...userInfo.value};
  input.value.nickName = userInfo.value.nickName;
  input.value.about = userInfo.value.about;
});

function handleUploadAvatarSuccess(response) {
  if (response.code === 1) {
    userInfo.value.icon = '/api' + response.data;
  }
  else
    ElMessage.error(response.msg);
}

function handleUploadBackgroundSuccess(response) {
  if (response.code === 1) {
    userInfo.value.background = '/api' + response.data;
  }
  else
    ElMessage.error(response.msg);
}

function handleUploadError(response) {
  ElMessage.error(response.msg);
}

async function submit() {
  if (JSON.stringify(userInfo) === JSON.stringify(oldUserInfo)) {
    ElMessage.error("用户信息未更改");
    return;
  }
  let newUserInfo = {
    userId: userInfo.value.userId,
    nickName: userInfo.value.nickName === oldUserInfo.value.nickName ? null : userInfo.value.nickName,
    about: userInfo.value.about === oldUserInfo.value.about ? null : userInfo.value.about,
    icon: userInfo.value.icon === oldUserInfo.value.icon ? null : userInfo.value.icon,
    background: userInfo.value.background === oldUserInfo.value.background ? null : userInfo.value.background,
  }
  let response = await addPendingUserInfo(newUserInfo);
  if (response.code === 1) {
    ElMessage.success("上传成功，请耐心等待审核~");
  }
  else {
    ElMessage.info(response.msg);
  }
}

function rollBack() {
  userInfo.value = {...oldUserInfo.value};
  input.value.nickName = oldUserInfo.value.nickName;
  input.value.about = oldUserInfo.value.about;
}

function handleEditNickname() {
  userInfo.value.nickName = input.value.nickName;
  editNickName.value = false;
}

function handleEditAbout() {
  userInfo.value.about = input.value.about;
  editAbout.value = false;
}

</script>

<template>
  <div class="container">

    <div class="background-image">
      <el-image :src="userInfo.background ? userInfo.background : '/api/sys_images/defaultBackground.webp'" fit="cover" style="width: 1000px;height: 300px;"/>
      <el-upload
          class="upload-demo"
          action="/api/pics/upload"
          :show-file-list="false"
          :on-success="handleUploadBackgroundSuccess"
          :on-error="handleUploadError"
      >
        <div class="upload-background-image">
          <el-button :icon="CameraFilled" color="rgba(255, 255, 255, 0.5)">上传封面图片</el-button>
        </div>
      </el-upload>
    </div>

      <div class="userinfo">

        <div class="uploader">
          <el-upload
              class="avatar-uploader"
              action="/api/pics/upload"
              :show-file-list="false"
              :on-success="handleUploadAvatarSuccess"
              :on-error="handleUploadError"
          >
            <div class="avatar-and-icon">
              <el-image class="avatar" fit="cover" v-if="true" :src="userInfo.icon ? userInfo.icon : '/api/sys_images/default.webp'"/>
              <el-icon class="avatar-uploader-icon"><CameraFilled/></el-icon>
            </div>
          </el-upload>
        </div>

        <div class="others">

          <div class="nickname">
            <span class="nickname-text" v-if="!editNickName">{{userInfo.nickName}}</span>
            <el-button v-if="!editNickName" @click="editNickName = !editNickName" text :icon="EditPen" type="primary">编辑</el-button>
            <div v-if="editNickName">
              <div style="display: flex;align-items: baseline">
                <div style="flex: 1;font-weight: bold">用户名</div>
                <div style="flex: 5">
                  <el-input class="edit-input" v-model="input.nickName"></el-input>
                  <div>
                    <el-button class="edit-ensure-button" @click="handleEditNickname" color="#1772f6" :disabled="input.nickName < 1 || input.nickName > 35">确定</el-button>
                    <el-button class="edit-cancel-button" @click="editNickName = false;input.nickName = userInfo.nickName">取消</el-button>
                  </div>
                </div>
              </div>
            </div>
            <div class="go-back-link">
              <el-link :href="`/user/${userInfo.userId}`" type="primary" :underline="false">返回个人资料</el-link>
            </div>
          </div>

          <div class="about">
            <div style="display: flex;align-items: baseline">
              <div style="flex: 1;font-weight: bold">我的简介</div>
              <div style="flex: 5;">
                <span v-if="!editAbout" style="padding: 0">
                  {{userInfo.about}}
                  <el-button v-if="!editAbout" @click="editAbout = !editAbout" text :icon="EditPen" type="primary">编辑</el-button>
                </span>
                <el-input v-if="editAbout" class="edit-input" v-model="input.about"></el-input>
                <div v-if="editAbout">
                  <el-button class="edit-ensure-button" @click="handleEditAbout" color="#1772f6" :disabled="input.about < 1 || input.about > 80">确定</el-button>
                  <el-button class="edit-cancel-button" @click="editAbout = false;input.about = userInfo.about">取消</el-button>
                </div>
              </div>
            </div>
          </div>

          <div class="buttons">
            <div class="save-button">
              <el-button type="primary" :disabled="JSON.stringify(userInfo) === JSON.stringify(oldUserInfo)" @click="submit">保存更改</el-button>
            </div>
            <div class="cancel-button">
              <el-button type="danger" :disabled="JSON.stringify(userInfo) === JSON.stringify(oldUserInfo)" @click="rollBack">撤销更改</el-button>
            </div>
          </div>
        </div>

        </div>
      </div>
</template>

<style scoped>
.container {
  width: 1000px;
  margin: auto;
  background-color: #f7f8fa;
}
.background-image {
  height: 300px;
  background-color: white;
  position: relative;
}

.upload-background-image {
  position: absolute;
  top: 0;
  right: 90px;
  z-index: 2;
  width: 50px;
  margin: 20px;
}

.userinfo {
  display: flex;
  width: 1000px;
  margin: 10px auto;
  background-color: white;
  border-radius: 5px;
  box-shadow: 0 0 0 1px #eee;
}

.uploader {
  width: 178px;
  padding: 0;
  margin: 10px;
  flex: 1;
}

.avatar-uploader {
  width: 178px;
  height: 178px;
}

.avatar-and-icon {
  position: relative;
}

.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}

.avatar-uploader .avatar-uploader-icon {
  position: absolute;
  top: 0;
  left: 0;
  background: #191b1f;
  opacity: .4;
  font-size: 28px;
  color: white;
  width: 178px;
  height: 178px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.others {
  width: 500px;
  flex: 5;
  height: 53vh;
}

.nickname {
  position: relative;
  padding: 10px;
}

.nickname .nickname-text {
  font-size: 20px;
  font-weight: bold;
}
.edit-input {
  margin: 0;
  width: 300px;
}

.edit-ensure-button {
  margin: 10px 10px 0 0;
}

.edit-cancel-button {
  margin: 10px 0 0 10px;
}

.go-back-link {
  position: absolute;
  top: 10px;
  right: 20px;
}

.about {
  padding: 10px;
}

.buttons {
  margin-top: 100px;
  display: flex;
  justify-content: stretch;
}

.save-button {
  margin-left: 50px;
  margin-right: 50px;
}

.cancel-button {
  margin-left: 50px;
}

</style>