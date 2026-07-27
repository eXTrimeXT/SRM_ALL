<template>
  <el-container class="flex-container" direction="vertical">
    <el-main style="position:relative;height:100%">
      <iframe
        name="iframeExternalScript"
        style="border:none;height:100%;position:absolute;width:100%;"
        :width="iframeWidth"
        :src="iframeUrl"
      />
    </el-main>
  </el-container>
</template>
<script>
import { beforeProcess } from '@/api/workFlow'
import { getEntranceType } from '@/utils/auth'

export default {
  name: 'ExternalScriptList',
  components: {
  },
  data () {
    return {
      iframeUrl: '',
      iframeHeight: 500,
      iframeWidth: 1100,
      dataBeforeProcess: {}
    }
  },
  created () {
    // 流程预处理
    this.beforeProcess()
  },
  methods: {
    // 流程预处理，从后台获取iframe，token等等
    beforeProcess () {
      let param = {} // 查询流程参数
      param.processType = ''
      param.businessId = 0
      beforeProcess(param).then(res => {
        if (res.data) {
          this.dataBeforeProcess = res.data
          // 加载iframe
          this.loadIframeProduct()
        }
      })
    },
    // 加载iframe
    loadIframeProduct () {
      var token = this.dataBeforeProcess.token
      let localeKey = this.dataBeforeProcess.localeKey
      let entranceType = getEntranceType() // 登录方式
      // 从服务器端获取url等配置信息,例如http://10.17.145.72/oasis
      var host = location.origin // this.dataBeforeProcess.iframeUrl
      var url = host + '/oasis/#/oasis/ihr/flow/external_script?token=' + token + '&localeKey=' + localeKey + '&entranceType=' + entranceType
      this.iframeUrl = url
    }
  }
}
</script>
