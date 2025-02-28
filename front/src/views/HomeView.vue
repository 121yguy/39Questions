<script lang="js" setup>
import {ref, computed, onMounted, markRaw, defineAsyncComponent} from "vue";
import {
  addPendingQuestion,
  addUnsubmittedPendingQuestion,
  getHomePageQuestions
} from "@/api/quetion.js";
import {CaretTop, Comment, DataBoard, Document, Reading, Share, StarFilled} from "@element-plus/icons-vue";
import {cancelLike, like} from "@/api/answer.js";
import {ElMessage} from "element-plus";
import QuestionIconComponent from "@/components/icon/QuestionIcon.vue";
import GithubIcon from "@/components/icon/GithubIcon.vue";
import FeedbackIcon from "@/components/icon/FeedbackIcon.vue";
import router from "@/router/index.js";
import { v4 as uuidv4 } from 'uuid';
import {useUserInfoStore} from "@/stores/userInfo.js";

const questions = ref([{
  question: {
    id: 0,
    title: ''
  },
  popularAnswer: {
    id: 0,
    text: '',
  },
  image: {
    src: ''
  },
  buttons: {
    like: 'like',
    heartVisible: false,
    favor: 'favor'
  }
}]);
const page = ref(0);
const loading = ref(false);
const noMore = ref(false);
const disabled = computed(() => loading.value || noMore.value);
const userInfoStore = useUserInfoStore();

const load = async () => {
  loading.value = true;
  // let response = await getQuestionsByPage(page.value);

  if (!sessionStorage.key("HomePageSessionId")) {
    sessionStorage.setItem("HomePageSessionId", uuidv4());
  }
  let response = await getHomePageQuestions(sessionStorage.getItem("HomePageSessionId"));

  let data = [];
  if (response && response.data.length) {
    for (let i = 0; i < response.data.length;i++) {
      let item = {
        question: {
          id: 0,
          title: ''
        },
        popularAnswer: {
          id: 0,
          text: '',
        },
        image: {
          src: ''
        },
        buttons: {
          like: 'like',
          heartVisible: false,
          favor: 'favor'
        }
      }
      item.question = response.data[i].question;
      let txt = response.data[i].popularAnswer.text;
      response.data[i].popularAnswer.text = strippedText(txt);
      item.popularAnswer = response.data[i].popularAnswer;
      item.image.src = getFirstImage(txt);
      item.buttons.like = response.data[i].liked ? 'liked' : 'like';
      item.buttons.favor = response.data[i].favored ? 'favored' : 'favor';
      data.push(item)
    }
    if (questions.value.length !== 1)
      questions.value.push(...data);
    else
      questions.value = data;
    page.value++;
  } else
    noMore.value = true;
  loading.value = false;
};

onMounted(async () => {
  if (!sessionStorage.key("HomePageSessionId")) {
    sessionStorage.setItem("HomePageSessionId", uuidv4());
  }
  await load();
});

function strippedText(text) {
  return text.replace(/<[^>]+>/g, '');
}

function getFirstImage(text) {
  const imgTagPattern = /<img\s+[^>]*src\s*=\s*"([^"]+)"/;
  const match = imgTagPattern.exec(text);
  return match ? match[1] : null;
}

async function handleLike(question) {
  if (question.buttons.like === 'liked') {
    let response = await cancelLike(question.popularAnswer.id);
    if (response.code === 1 || response.code === 0) {
      question.buttons.like = 'like';
    }
    else if (response.data.code !== 401) {
      ElMessage.error(response.msg);
    }
  }
  else {
    let response = await like(question.popularAnswer.id);
    if (response.code === 1 || response.code === 0) {
      question.buttons.like = 'liked';
      question.buttons.heartVisible = true;
      setTimeout(() => {
        question.buttons.heartVisible = false;
      }, 1000);
    }
    else if (response.data.code !== 401) {
      ElMessage.error(response.msg);
    }
  }
}

const centerDialogVisible = ref(false)
const favorAnswerId = ref(0);
const favorComponent = ref(null);
const favoringQuestion = ref();

function handleFavor(question) {
  if (!userInfoStore.userInfo.userId) {
    ElMessage.error("未登录，请登录后再试");
    return;
  }
  favoringQuestion.value = question;
  let answerId = question.popularAnswer.id;
  favorComponent.value = markRaw(defineAsyncComponent(() =>
      import('@/components/favor/FavorComponent.vue')
  ));
  centerDialogVisible.value = true;
  favorAnswerId.value = answerId;
}

function goToQuestions(question) {
  window.open(`/question/${question.question.id}`, '_blank');
}

function share(question) {
  navigator.clipboard.writeText('http://localhost:5173' +`/question/${question.question.id}/${question.popularAnswer.id}`);
  ElMessage({
    type: "success",
    message: "已复制链接"
  })
}

const addQuestionDialogVisible = ref(false);
const questionTitle = ref('');
const questionText = ref('');
const questionButtonsLoading = ref(false);

async function handleSubmitQuestion(title, text, isSubmit) {
  questionButtonsLoading.value = true;
  if (title.trim().length < 5) {
    ElMessage.error('标题字数过少');
    questionButtonsLoading.value = false;
    return;
  }
  let response = isSubmit ? await addPendingQuestion(title, text) : await addUnsubmittedPendingQuestion(title, text);
  if (response.code === 1 && isSubmit) {
    cleanQuestionTextAndTitle();
    ElMessage.success('提问成功，耐心等待审核哦~');
  }
  else if (response.code === 1) {
    cleanQuestionTextAndTitle();
    ElMessage.success('已保存到稿件中心');
  }
  else if (response.code === 0) {
    ElMessage.error(response.msg);
  }
  addQuestionDialogVisible.value = false;
  questionButtonsLoading.value = false;
}

function cleanQuestionTextAndTitle() {
  questionText.value = "";
  questionTitle.value = "";
}

function handleQuestionTitle() {
  if (questionTitle.value.trim().length === 0) return;
  let c = questionTitle.value.trim().charAt(questionTitle.value.length - 1);
  if (c !== '?' && c !== '？') questionTitle.value = questionTitle.value.trim() + '？';
}

function handleAddQuestion() {
  if (!userInfoStore.userInfo.userId) {
    ElMessage.error("未登录，请登录后再试");
    return;
  }
  addQuestionDialogVisible.value = true;
}

function handleGoToDraftCenter() {
  if (!userInfoStore.userInfo.userId) {
    ElMessage.error("未登录，请登录后再试");
    return;
  }
  router.push('/draft');
}

function handleAnswerQuestions() {
  if (!userInfoStore.userInfo.userId) {
    ElMessage.error("未登录，请登录后再试");
    return;
  }
  router.push('/question/waiting');
}

function gotoGithubRepositories() {
  window.open('https://github.com/121yguy/39Questions', '_blank');
}

function gotoFeedbackPage() {
  window.open('https://wj.qq.com/s2/18174551/8e24', '_blank');
}

</script>

<template>

  <el-dialog v-model="centerDialogVisible" title="选择收藏夹" width="500" center style="max-height: 600px">
    <component :is="favorComponent" v-model:answerId="favorAnswerId" v-model:dialogVisible="centerDialogVisible" v-model:question="favoringQuestion"/>
  </el-dialog>

  <el-dialog class="add-question-dialog" v-model="addQuestionDialogVisible" title="提问" width="700" center style="max-height: 600px;">
    <div class="dialog-contain">
      <div class="dialog-input">
        <div class="dialog-input-type">问题标题</div>
        <el-input v-model="questionTitle" maxlength="50" show-word-limit placeholder="大于5个字符" @blur="handleQuestionTitle"></el-input>
      </div>
      <div class="dialog-input">
        <div class="dialog-input-type">问题描述</div>
        <el-input v-model="questionText" type="textarea" maxlength="100" show-word-limit placeholder="可以为空"></el-input>
      </div>
    </div>
    <template #footer>
      <el-button type="primary" @click="handleSubmitQuestion(questionTitle, questionText, true)" :disabled="questionTitle.trim().length < 5 || questionTitle.length >= 50" :loading="questionButtonsLoading">提交</el-button>
      <el-button type="primary" @click="handleSubmitQuestion(questionTitle, questionText, false)" :disabled="questionTitle.trim().length < 5 || questionTitle.length >= 50" :loading="questionButtonsLoading">保存到草稿箱</el-button>
    </template>
  </el-dialog>

  <div class="questions-list-wrapper">
    <div class="shows">
      <ul
          class="question-list"
          v-infinite-scroll="load"
          :infinite-scroll-disabled="disabled"
      >
        <li class="question" v-for="question in questions" :key="question.id">
          <div>
            <el-link class="question-title" type="info" :underline="false" :href="`/question/${question.question.id}`"
                     target="_blank" rel="noopener noreferrer">
              <div class="question-title-text">
                {{ question.question.title }}
              </div>
            </el-link>
          </div>

          <div style="display: flex;margin: 5px 0 5px 0;">
              <div class="answer">
                <el-link
                    type="info"
                    :underline="false"
                    :href="`/question/${question.question.id}/${question.popularAnswer.id}`"
                    target="_blank"
                    rel="noopener noreferrer"
                >
                  <div>
                    <el-image class="answer-img" :src="question.image.src" style="width: 100px;height: 100px;margin: 2px;border-radius: 5px;"
                              v-if="question.image.src" fit="cover"/>
                  </div>
                </el-link>
                <el-link
                    type="info"
                    :underline="false"
                    :href="`/question/${question.question.id}/${question.popularAnswer.id}`"
                    target="_blank"
                    rel="noopener noreferrer"
                >
                  <div class="answer-text" v-text="question.popularAnswer.text" style="text-align: inherit"></div>
                </el-link>
              </div>
          </div>
          <div class="toolbar">
            <div :class="question.buttons.like" style="position: relative;display: inline-block;">
              <div v-if="question.buttons.heartVisible" class="heart">❤️</div>
              <el-button class="like-button" :icon="CaretTop" text type="info" @click="handleLike(question)"></el-button>
            </div>
            <div :class="question.buttons.favor">
              <el-button class="favor-button" :icon="StarFilled" text type="info" @click="handleFavor(question)"></el-button>
            </div>
            <div class="comment">
              <el-button :icon="Comment" @click="goToQuestions(question)" text type="info"></el-button>
            </div>
            <div class="share">
              <el-button :icon="Share" text type="info" @click="share(question)"></el-button>
            </div>
          </div>
        </li>
        <p v-if="loading" v-loading="loading" style="text-align: center; margin: 0"></p>
      </ul>
      <div class="workspace">
        <div class="workspace-tab1">
          <div class="workspace-tab-title">创作中心</div>
          <div class="workspace-toolbar">
            <div class="workspace-toolbar-icon-contain">
              <div style="display: flex;flex-direction: column;align-items: center">
                <el-button color="#39c5bb" circle @click="handleAddQuestion">
                  <el-icon size="1.5em"><QuestionIconComponent/></el-icon>
                </el-button>
                <div class="about-button">提问</div>
              </div>
              <div style="display: flex;flex-direction: column;align-items: center">
                <el-button color="#FFA500" circle @click="handleAnswerQuestions">
                  <el-icon size="1.5em"><Reading/></el-icon>
                </el-button>
                <div class="about-button">回答问题</div>
              </div>
              <div style="display: flex;flex-direction: column;align-items: center">
                <el-button color="#FFE211" circle @click="handleGoToDraftCenter">
                  <el-icon size="1.5em"><Document /></el-icon>
                </el-button>
                <div class="about-button">稿件管理</div>
              </div>
              <div style="display: flex;flex-direction: column;align-items: center">
                <el-button color="#FAAFBE" circle @click="router.push('/comments')">
                  <el-icon size="1.5em"><DataBoard /></el-icon>
                </el-button>
                <div class="about-button">留言板</div>
              </div>
            </div>
          </div>
        </div>

        <div class="workspace-tab2">

        </div>

        <div class="workspace-tab3">
          <div class="workspace-tab-title">关于本站</div>
          <div class="workspace-toolbar">
            <div class="workspace-toolbar-icon-contain">
              <div style="display: flex;flex-direction: column;align-items: center">
                <el-button color="#D80000" circle @click="gotoGithubRepositories">
                  <el-icon size="1.5em"><GithubIcon/></el-icon>
                </el-button>
                <div class="about-button">github</div>
              </div>
              <div style="display: flex;flex-direction: column;align-items: center">
                <el-button color="#0000FF" circle @click="gotoFeedbackPage">
                  <el-icon size="1.5em"><FeedbackIcon /></el-icon>
                </el-button>
                <div class="about-button">问题反馈</div>
              </div>
            </div>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

.questions-list-wrapper {
  display: flex;
  align-items: center;
  margin: auto;
  height: 100%;
  background-color: #f7f8fa;
}

.questions-list-wrapper .shows {
  display: flex;
  margin: auto;
  background-color: #f7f8fa;
}

.questions-list-wrapper .question-list {
  margin: 0;
  display: block;
  flex-shrink: 0;
  padding: 0;
  list-style: none;
  background-color: #f7f8fa;
  width: 650px;
}

.questions-list-wrapper .question {
  display: block;
  margin: 5px auto;
  width: 96%;
  padding: 10px;
  text-align: left;
  max-height: 190px;
  overflow: hidden;
  background: white;
  border-radius: 5px;
  box-shadow: 0 1px 3px rgba(26, 26, 26, .1);

}

.questions-list-wrapper .question + .question {
  margin-top: 4px;
}

.questions-list-wrapper .question .question-title {
  color: black;
  font-size: 18px;
  font-weight: bold;
}

.question-title-text {
  max-width: 600px;
  overflow: hidden; /* 超出部分隐藏 */
  white-space: nowrap; /* 文本不换行 */
  text-overflow: ellipsis; /* 超出部分显示省略号 */
}

.questions-list-wrapper .question .question-title:hover {
  color: #09408e;
  font-size: 18px;
  font-weight: bold;
}

.answer {
  display: flex;
  align-items: flex-start;
}

.answer p {
  margin: 0;
}

.answer-text {
  color: black;
  max-height: 100px;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 5;
  overflow: hidden;
}

.answer-text:hover {
  color: #535861;
}

.question .toolbar {
  display: flex;
}

.question .toolbar .like :hover {
  color: red;
}

.question .toolbar .liked .like-button {
  color: red;
}

.question .toolbar .favored .favor-button {
  color: gold;
}

.question .toolbar .comment :hover {
  color: deepskyblue;
}

.question .toolbar .favor :hover {
  color: gold;
}

.question .toolbar .share :hover {
  color: chartreuse;
}

.question .toolbar .heart {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  font-size: 10px;
  animation: float 1s ease-in-out forwards;
}

@keyframes float {
  0% {
    opacity: 1;
    transform: translate(-50%, 0);
  }
  100% {
    opacity: 0;
    transform: translate(-50%, -50px); /* 向上移动 */
  }
}

.workspace {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  flex-shrink: 0;
  margin: 4px;
  width: 360px;
  background: white;
  border-radius: 5px;
  box-shadow: 0 1px 3px rgba(26, 26, 26, .1);
  height: 90%;
  position: sticky;
  top: 66px;
}

.workspace .workspace-tab1 {
  padding: 20px;
  border-bottom: 1px solid #f8f8fa;
  height: 260px;
}

.workspace .workspace-tab-title {
  font-weight: lighter;
  font-size: 15px;
}

.workspace .workspace-toolbar {
  width: 100%;
  background-color: white;
}

.workspace-toolbar-icon-contain {
  display: flex;
  padding: 16px;
  width: 100%;
  justify-content: space-between;
}

.workspace-toolbar-icon-contain .about-button {
  font-size: 12px;
  font-weight: lighter;
  margin-top: 2px;
}

.workspace-toolbar-icon-contain :deep(.el-button) {
  width: 50px;
  height: 50px;
  opacity: .4;
}

.workspace-toolbar-icon-contain :deep(.el-button:hover) {
  opacity: .8;
}

.workspace .workspace-tab2 {
  padding: 20px;
  border-bottom: 1px solid #f8f8fa;
  height: 340px;
}

.workspace .workspace-tab3 {
  padding: 20px;
}

.add-question-dialog .dialog-contain {
  display: flex;
  align-items: center;
  flex-direction: column;
}

.add-question-dialog .dialog-input {
  margin-top: 10px;
  width: 480px;
  display: flex;
  align-items: baseline;
}

.add-question-dialog .dialog-input-type {
  width: 80px;
}

.add-question-dialog .dialog-input :deep(.el-textarea__inner) {
  max-height: 300px;
}

</style>
