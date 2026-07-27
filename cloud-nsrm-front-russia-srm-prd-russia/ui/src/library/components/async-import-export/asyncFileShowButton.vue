<template>
  <span>
    <AuthorityButton
      :type="type"
      :code="code"
      @click="showDialog"
    >{{ buttonText }}</AuthorityButton>
    <el-dialog
      v-if="show"
      :title="titleText"
      :visible.sync="show"
      width="1000px"
      destroy-on-close
      append-to-body
    >
      <!-- 这里弹窗查看异步上传、下载的进度 -->
      <AsyncFileList :model="model" />
    </el-dialog>
  </span>
</template>

<script>
import AsyncFileList from './asyncFileList.vue'
export default {
  name: 'AsyncFileShowButton',
  components: {
    AsyncFileList
  },
  props: {
    // 弹窗标题
    title: {
      type: String,
      default: ''
    },
    // 指定业务模块(必填。查看异步上传、下载文件时，只能看对应模块的，不应该看到所有)
    model: {
      type: String
    },
    button: {
      type: String,
      default: ''
    },
    // 按钮权限编码
    code: {
      type: String,
      default: ''
    },
    // 按钮颜色
    type: {
      type: String,
      default: 'primary'
    }
  },
  data () {
    return {
      show: false // 显示弹窗
    }
  },
  computed: {
    titleText () {
      return this.title || this.$t('components.export.dowloadList')
    },
    buttonText () {
      return this.button || this.$t('components.export.dowloadList')
    }
  },
  methods: {
    // 展示弹窗
    showDialog () {
      this.show = true
    }
  }
}
</script>
<style>
.the_expert_pro {
  height: 400px;
}
</style>
