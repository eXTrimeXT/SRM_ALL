<template>
  <el-container direction="vertical" style="height:100%">
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
import { mapGetters } from 'vuex'
export default {
  name: 'IAMOrgSetting',

  data () {
    return {
      iframeUrl: '',
      iframeWidth: '100%',
      iamHost: ''
    }
  },
  computed: {
    ...mapGetters(['sysOpenConfig']),
  },
  mounted () {
    this.loadIframe()
  },
  methods: {
    loadIframe () {
      let singleBaseUrl = this.$systemUrl + '/'
      let service = encodeURI(singleBaseUrl)
      this.iframeUrl = `${this.sysOpenConfig.iamSysBaseUrl}/portal/index.html#/forgetpassword?redirectUri=${service}`
    }
  }
}
</script>
