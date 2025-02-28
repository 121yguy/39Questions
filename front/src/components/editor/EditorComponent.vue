<script setup lang="js">
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import { i18nChangeLanguage } from '@wangeditor/editor'
import {onBeforeUnmount, onMounted, ref, shallowRef, watch} from 'vue'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import {ElMessage} from "element-plus";

const images = ref([]);
const uploadImages = ref([])

const pendingImages = defineModel("pendingImages");
i18nChangeLanguage('zh-CN')

// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef()

// 内容 HTML
const valueHtml = defineModel();

// 工具栏配置
const toolbarConfig = {
  toolbarKeys: [
    // 菜单 key
    "headerSelect",
    "bold", // 加粗
    "italic", // 斜体
    "through", // 删除线
    "underline", // 下划线
    "color", // 文字颜色
    "|",
    "fontSize", // 字体大小
    "lineHeight", // 行高
    "|",
    "uploadImage", // 上传图片
    "deleteImage", //删除图片
    "insertLink",
    "codeBlock", //代码块
    "insertTable", // 插入表格
    "emotion",
    "|",
    "delIndent", // 缩进
    "indent", // 增进
    "divider", // 分割线
    "justifyCenter", // 居中对齐
    "justifyJustify", // 两端对齐
    "justifyLeft", // 左对齐
    "justifyRight", // 右对齐
    "undo", // 撤销
    "redo", // 重做
    "clearStyle", // 清除格式
    "fullScreen", // 全屏
  ]
};

const editorConfig = {
  placeholder: '请输入内容...',
  MENU_CONF: [],
}


editorConfig.MENU_CONF['uploadImage'] = {
  server: '/api/pics/upload',

  // form-data fieldName ，默认值 'wangeditor-uploaded-image'
  fieldName: 'file',

  // 单个文件的最大体积限制，默认为 2M
  maxFileSize: 1024 * 1024, // 1M

  // 最多可上传几个文件，默认为 100
  maxNumberOfFiles: 10,

  // 选择文件时的类型限制，默认为 ['image/*'] 。如不想限制，则设置为 []
  allowedFileTypes: ['image/*'],

  // 将 meta 拼接到 url 参数中，默认 false
  metaWithUrl: false,


  // 跨域是否传递 cookie ，默认为 false
  withCredentials: true,

  // 超时时间，默认为 10 秒
  timeout: 10 * 1000, // 10 秒

  customInsert(res, insertFn) {
    insertFn("/api" + res.data, "", "");
  },
  onError(file, res) {
    ElMessage.error(`上传失败:${res}`);
  },
}

editorConfig.MENU_CONF['insertImage'] = {
    onInsertedImage(imageNode) {                    // JS 语法
    if (imageNode == null) return;

    const { src } = imageNode;
    images.value.push(src);
    pendingImages.value.push(src);
  },
}

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value;
  if (editor == null) return;
  editor.destroy();
})

const handleCreated = (editor) => {
  editorRef.value = editor // 记录 editor 实例，重要！
}

</script>

<template>
  <div style="width: 650px;margin: 10px auto;box-shadow: 0 1px 3px rgba(26, 26, 26, .1);" id="editor">
    <Toolbar
        class="w-e-toolbar"
        style="border-bottom: 1px solid #ccc"
        :editor="editorRef"
        :defaultConfig="toolbarConfig"
        mode="default"
    />
    <Editor
        style="height: 520px; overflow-y: hidden;"
        v-model="valueHtml"
        :defaultConfig="editorConfig"
        mode="default"
        @onCreated="handleCreated"
    />
  </div>

</template>

<style scoped>

</style>