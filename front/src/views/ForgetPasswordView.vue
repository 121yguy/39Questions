<script setup>
import {ref} from "vue";
import {ElMessage} from "element-plus";
import {forgetPassword, updatePassword} from "@/api/user.js";

const form = ref({
  email: '',
  password: '',
  passwordConfirm: '',
  verify: ''
});

const validatePasswordConfirm = (rule, value, callback) => {
  if (value === "") {
    callback(new Error("请再次输入密码"));
  } else if (value !== form.value.passwordConfirm) {
    callback(new Error("两次输入的密码不一致"));
  } else {
    callback();
  }
}

const rules = {
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 8, max: 16, message: '请确保密码长度在8-16个字符', trigger: 'blur'}
  ],
  passwordConfirm: [
    {required: true, message: '请再次输入密码', trigger: 'blur'},
    {validator: validatePasswordConfirm, trigger: 'blur'}
  ],
  verify: [
    {required: true, message: '请输入验证码', trigger: 'blur'},
    {min: 4, max: 4, message: '验证码格式有误', trigger: 'blur'},
  ]
}

async function submit() {
  let response = await updatePassword(form.value.email, form.value.password, form.value.verify);
  if (response.data) {
    ElMessage.success('修改成功');
  }
  else {
    ElMessage.error(response.msg);
  }
}

async function sendVerify() {
  if (form.value.email !== '') {
    let response = await forgetPassword(form.value.email);
    if (response.data) {
      ElMessage({
        message: '发送成功!',
        type: "success"
      })
    }
    else {
      ElMessage.error('邮箱不存在!')
    }
  }
  else {
    ElMessage.error('邮箱格式错误!')
  }
}

</script>

<template>
  <div class="container">
    <div class="form">
      <el-form class="form" :rules="rules" label-width="100px" :model="form" label-position="right">
        <el-form-item>
          <div class="title">重置密码</div>
        </el-form-item>
        <el-form-item prop="email" label="邮箱">
          <el-input v-model="form.email"></el-input>
        </el-form-item>
        <el-form-item prop="verify" label="验证码">
          <el-input v-model="form.verify">
            <template #append>
              <el-button @click="sendVerify">获取验证码</el-button>
            </template>
          </el-input>
        </el-form-item>
       <el-form-item prop="password" label="新密码">
         <el-input type="password" v-model="form.password"></el-input>
       </el-form-item>
        <el-form-item prop="passwordConfirm" label="确认密码">
          <el-input type="password" v-model="form.passwordConfirm"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit">提交</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>

.container {
  display: flex;
  justify-content: center;
}

.title {
  text-align: center;
  font-size: 32px;
}

.form {
  padding: 20px;
  display: flex;
  align-items: center;
  flex-direction: column;
  width: 700px;
  height: 90vh;
  background: #ffffff;
}

.form :deep(.el-input) {
  width: 300px;
}

</style>