<script setup>

import {onMounted, ref} from "vue";
import router from "@/router/index.js";

const count = ref(5);
function countdown()
{
  setInterval(() => {
    if (count.value > 0) {
      count.value -= 1;
    } else {
      goHome();
    }
  }, 1000);
}

function goHome() {
  window.location.href = '/';
}

onMounted(() => {
  countdown();
})
</script>

<template>
  <div style="display: flex;flex-direction: column;margin: 200px 200px 0 200px;align-items: center;justify-content: center;">
    <div style="display: flex;width: 1000px;justify-content: space-between;align-items: center">
      <div class="emoticon" style="font-size: 30px;width: 345px;margin-left: 110px;font-family: 'ＭＳ Ｐゴシック', 'ＭＳＰゴシック', 'MSPゴシック', 'MS Pゴシック',serif;line-height: 1.2">
        ∧＿∧<br>
        (　･ω･)＝つ≡つ <br>
        (っ　≡つ＝つ <br>
        /　　　) ﾊﾞﾊﾞﾊﾞﾊﾞ <br>
        ( /￣∪
      </div>
      <div style="font-size: 200px;font-family:Papyrus,fantasy;width: 345px;">404</div>
    </div>
    <div style="font-family: YouYuan,serif;width: 192px">糟糕!页面被みくみく掉了~</div>
    <div style="font-family: YouYuan,serif;font-size: 14px;margin: 5px;display: flex;width: 300px;justify-content: space-between;align-items: center">
      <div>{{ count }}秒后自动跳转到首页</div>
      <div><el-button text type="primary" @click="goHome">立即跳转到首页</el-button></div>
    </div>
  </div>
</template>

<style scoped>

</style>