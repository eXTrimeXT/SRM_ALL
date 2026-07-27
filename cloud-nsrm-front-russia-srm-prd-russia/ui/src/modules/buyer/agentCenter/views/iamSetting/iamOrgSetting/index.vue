<template>
  <el-container class="flex-container" direction="vertical">
    <el-main style="position:relative;height:100%">
      <iframe
        name="iframeModel"
        style="border:none;height:100%;position:absolute;width:100%;"
        :width="iframeWidth"
        :src="iframeUrl"
        title=""
      />
    </el-main>
  </el-container>
</template>

<script>
import { getDictItem } from '@/api/common'
import config from '@/config/user.env'

export default {
  name: 'IAMOrgSetting',

  data () {
    return {
      iframeUrl: '',
      iframeWidth: '100%',
      iamHost: '',
      configData: config
    }
  },
  async created () {
    const env = import.meta.env.VUE_APP_ENV
    const { data = [] } = await getDictItem('IAM_HOST')
    let envUrl = data.find(item => (item.dictItemCode == env)) // 'DEV'
    if (envUrl) {
      this.iamHost = envUrl.dictItemName
      this.loadIframe()
    } else {
      this.iamHost = this.configData.iamUrl
    }
  },
  methods: {
    // DEV SIT UAT PROD
    loadIframe () {
      this.iframeUrl = `${this.iamHost}/manage/#/user/organization?menuVisible=true`
    }
  }
}
</script>
