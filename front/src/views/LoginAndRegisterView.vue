<script setup lang="js">
import {onMounted, ref} from "vue";
import {User,Lock} from '@element-plus/icons-vue'
import {register, login, checkRegisterAccount, checkToken, sendVerify} from "@/api/loginAndRegister.js"
import {ElMessage} from "element-plus";
import {useRouter} from "vue-router";
import { ElLoading } from 'element-plus'
import {useUserInfoStore} from "@/stores/userInfo.js";
import { v4 as uuidv4 } from 'uuid';

// 常量
const router = useRouter();
const userInfoStore = useUserInfoStore();
// 模型
const title = ref("登录");
const changeButtonText = ref("没有账号?点击注册。");
const loginData = ref({
  account: '',
  pass: '',
});
const registerData = ref({
  account: '',
  pass: '',
  rePass: '',
  Verify: '',
});
const loginRules = {
  account: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    {
      pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/,
      message: '请输入有效的邮箱地址',
      trigger: 'blur'
    }
  ],
  pass: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 16, message: '请确保密码长度在8-16个字符', trigger: 'blur' }
  ]
}
const loginFormRef = ref();
const registerFormRef = ref();
const getVerifyButtonLoading = ref(false);

const validateRePass = (rule, value, callback) => {
  if (value === "") {
    callback(new Error("请再次输入密码"));
  } else if (value !== registerData.value.pass) {
    callback(new Error("两次输入的密码不一致"));
  } else {
    callback();
  }
}

const validateAccount = async (rule, value, callback) => {
  let response = await checkRegisterAccount(value);
  if (response.code === 1) {
    if (!response.data)
      callback(new Error("用户名已经被使用"));
    else
      callback();
  }
  else {
    callback();
    ElMessage.error(response.msg);
  }
}

const registerRules = {
  account: [
    {required: true, message: '请输入邮箱', trigger: 'blur'},
    {
      pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/,
      message: '请输入有效的邮箱地址',
      trigger: 'blur'
    },
    {validator: validateAccount, trigger: 'blur'}
  ],
  pass: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 8, max: 16, message: '请确保密码长度在8-16个字符', trigger: 'blur'}
  ],
  rePass: [
    {required: true, message: '请再次输入密码', trigger: 'blur'},
    {validator: validateRePass, trigger: 'blur'}
  ],
  Verify: [
    {required: true, message: '请输入验证码', trigger: 'blur'},
    {min: 4, max: 4, message: '验证码格式有误', trigger: 'blur'},
  ]
}
function registerUser(){
  registerFormRef.value.validate(async valid => {
    if (valid) {
      let user = {
        account: registerData.value.account,
        pass: registerData.value.pass
      };
      let data = await register(user, registerData.value.Verify);
      if (data.code === 1){
        ElMessage({
          message: '注册成功',
          type: 'success',
        });
        registerData.value = {
          account: '',
          pass: '',
          rePass: '',
          Verify: ''
        };
        loginData.value = {
          account: '',
          pass: '',
        }
        change();
      }else {
        ElMessage.error('注册失败')
      }
    }else {
      return false;
    }
  })
}

function getVerify() {
  registerFormRef.value.validateField('account', async valid => {
    if (valid) {
      getVerifyButtonLoading.value = true;
      let response = await sendVerify(registerData.value.account);
      if (response.code === 1) {
        startCountdown();
        getVerifyButtonLoading.value = false;
        ElMessage.success("验证码已发送");
      }
      else {
        ElMessage.error(response.msg);
      }
    }
  });
}

function loginUser() {

  const loading = ElLoading.service({
    lock: true,
    text: 'Loading',
    background: 'rgba(0, 0, 0, 0.7)',
  })

  loginFormRef.value.validate(async valid => {
    if (valid) {
      // 执行登录操作
      let deviceId = uuidv4();
      localStorage.setItem('deviceId', deviceId);
      let response = await login(loginData.value.account, loginData.value.pass, deviceId);
      if (response.code === 1){
        ElMessage({
          message: '登录成功',
          type: 'success',
        })
        userInfoStore.setUserInfo(response.data)
        loading.close()
        await router.replace('/');
      }
      else {
        loading.close()
        ElMessage.error(response.msg);
      }
    }
    else {
      loading.close()
      return false;
    }
  });
}
function change() {
  if (title.value === "登录") {
    title.value = "注册";
    document.getElementById("login").style.display = "none";
    document.getElementById("register").style.display = "block";
    changeButtonText.value = "已有账号?点击登录。";
  } else {
    title.value = "登录";
    document.getElementById("login").style.display = "block";
    document.getElementById("register").style.display = "none";
    changeButtonText.value = "没有账号?点击注册。";
  }
}

const getVerifyButtonDisabled = ref(false);
const buttonText = ref('获取验证码');
const countdown = ref(120);

function startCountdown() {
  getVerifyButtonDisabled.value = true;
  let timer = setInterval(() => {
    if (countdown.value > 0) {
      countdown.value -= 1;
      buttonText.value = `${countdown.value}秒`;
    } else {
      clearInterval(timer);
      getVerifyButtonDisabled.value = false;
      buttonText.value = '获取验证码';
      countdown.value = 120; // 重置倒计时
    }
  }, 1000);
}

onMounted( async () => {
  let response = await checkToken();
  if (response.code === 1) {
    ElMessage.success("已登录，自动跳转到首页");
    await router.replace('/')
  }
})
</script>

<template>
  <div id="cardContain">
    <div id="card" >
      <el-card id="myCard" >
        <div style="width: 100%;">
          <h1 style="text-align: center;">{{ title }}</h1>
        </div>
        <div class="form" id="login" style="display: block;">
          <el-form label-position="right" :model="loginData" :rules="loginRules" label-width="100px" ref="loginFormRef">
            <el-form-item label="邮箱" prop="account">
              <el-input type="text" :prefix-icon="User" autocomplete="off" v-model="loginData.account" clearable />
            </el-form-item>
            <el-form-item label="密码" prop="pass">
              <el-input type="password" :prefix-icon="Lock" autocomplete="off" v-model="loginData.pass" clearable />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" style="margin-left: 90px" @click="loginUser">登录</el-button>
            </el-form-item>
          </el-form>
        </div>
        <div class="form" id="register" style="display: none;">
          <el-form label-position="right" :model="registerData" label-width="100px" :rules="registerRules" ref="registerFormRef">
            <el-form-item label="邮箱" prop="account">
              <el-input type="text" :prefix-icon="User" autocomplete="off" v-model="registerData.account" clearable />
            </el-form-item>
            <el-form-item label="密码" prop="pass">
              <el-input type="password" :prefix-icon="Lock" autocomplete="off" v-model="registerData.pass" clearable />
            </el-form-item>
            <el-form-item label="确认密码" prop="rePass">
              <el-input type="password" :prefix-icon="Lock" autocomplete="off" v-model="registerData.rePass" clearable />
            </el-form-item>
            <el-form-item label="验证码" prop="Verify">
              <el-input
                  v-model="registerData.Verify"
                  style="max-width: 600px"
                  oninput="value=value.replace(/\D/g,'')"
              >
                <template #append>
                  <el-button @click="getVerify" :disabled="getVerifyButtonDisabled" :loading="getVerifyButtonLoading">{{buttonText}}</el-button>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" style="margin-left: 90px" @click="registerUser">注册</el-button>
            </el-form-item>
          </el-form>
        </div>
        <div class="buttons">
          <div>
            <el-button text type="primary" @click="router.push('/forgetPassword')">忘记密码</el-button>
          </div>
          <div>
            <el-button @click="change" text type="primary">{{ changeButtonText }}</el-button>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>


<style scoped>
#cardContain {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh; /* 使容器占满整个屏幕高度 */
  background-image: url("/39q_logo.png");

  background-attachment: scroll;
}

#myCard {
  display: block;
  margin: auto;
  max-width: 700px;
  width: 700px;
  height: 500px; /* 设置固定高度 */
  padding: 10px;
  box-sizing: border-box;
}


.form{
  margin: auto;
  padding: 10px;
  width: 70%;
  justify-content: center;
  align-items: center;
}

.el-form-item .el-input__inner {
  width: 100%;
}

.buttons {
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
}

</style>