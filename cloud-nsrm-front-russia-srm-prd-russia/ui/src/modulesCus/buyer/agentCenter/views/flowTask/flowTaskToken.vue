<template>
  <el-container class="login-container">
    <el-header style="height: 50px">
      <!-- <div class="login-header-logo">单点登录</div> -->
    </el-header>
  </el-container>
</template>

<script>
// import {singlePointLoginUrl, systemUrl} from '@/config/sysConfig'
export default {
  name: 'FlowTaskToken',
  data () {
    return {}
  },
  computed: {
    base64Param () {
      return this.$route.params.id
    }
  },
  created () {
  },
  mounted () {
    const pathname = window.location.pathname
    const systemUrl = window.location.origin + pathname.substring(0, pathname.length - 1)

    let singleBaseUrl = systemUrl
    let redirectUrl = systemUrl + '/%23/flowTaskViewBase/' + this.base64Param
    let sUrl = singleBaseUrl + '/cloud-srm/sys/sso/transfer?redirectUri=' + encodeURI(redirectUrl)
    let service = encodeURI(sUrl)
    let url = singleBaseUrl + '/cloud-srm/sys/sso/forward?service=' + service
    window.location.href = url
  },
  methods: {

  }
}
</script>
