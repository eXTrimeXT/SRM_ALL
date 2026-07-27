<template>
  <span>
    <AuthorityButton
      type="primary"
      :code="code"
      @click="exportFile"
    >{{ $t('components.export.shishidaochu') }}</AuthorityButton>
    <el-dialog
      v-if="show"
      :title="title || $t('components.export.dowloadList')"
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
  name: 'AsyncFileExportButton',
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
    // 导出接口
    url: {
      type: String
    },
    // 接口查询条件
    queryParam: {
      type: Object
    },
    // 接口请求是否MQL方式
    adaptMql: {
      type: Boolean,
      default: false
    },
    transformQueryParams: {
      type: Object,
      default: () => {}
    },
    // 按钮权限编码
    code: {
      type: String,
      default: ''
    }
  },
  data () {
    return {
      show: false
    }
  },
  methods: {
    exportFile () {
      let transformParams = this.queryParam ?? {}
      if (this.adaptMql) {
        transformParams = this.transformQueryParams
      }
      this.$http({
        url: this.url,
        method: 'POST',
        data: transformParams,
        loading: true
      })
        .then((data) => {
          this.show = true
        })
        .catch((err) => {
          console.log(err)
        })
    }
  }
}
</script>
