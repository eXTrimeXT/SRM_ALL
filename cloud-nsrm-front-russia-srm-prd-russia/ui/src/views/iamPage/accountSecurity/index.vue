<template>
  <el-container direction="vertical" style="height:100%;padding:0 !important;">
    <el-main
      :id="iframeName+'_wrap'"
      style="position:relative;height:100%"
    />
  </el-container>
</template>

<script>
import { mapState } from 'vuex'
import { singlePointLogoutUrl } from '@/config/sysConfig'
import { getOpenConfigAfterLogin } from '@/api/common'
export default {
  name: 'IAMOrgSetting',

  data () {
    return {
      iframeUrl: '',
      iframeWidth: '100%',
      iamHost: '',
      iframeName: 'accountSecurity'
    }
  },
  computed: {
    ...mapState({
      iamSecurityPageType: state => {
        return state.pageCtrl.iamSecurityPageType
      },
      sysOpenConfig: state => {
        return state.app.sysOpenConfig
      }
    })
  },
  watch: {
    iamSecurityPageType: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler (val) {
        // let tabName = this.$route.params.tab
        console.log('url-tabName', val)
        if (val) {
          // this.loadIframe()
        }
      }
    }
  },
  created () {

  },
  mounted () {
    this.loadIframe()
    // 监听iframe返回的事件
    this.handleMessageListener()
  },
  destroyed () {
    window.removeEventListener('message', this.handleMessageListener)
  },
  methods: {
    handleMessageListener () {
      window.addEventListener('message', (res) => {
        debugger
        console.log('accountSecurityMessage')
        console.log(res.data)
        if (res.data) {
          if (res.data.success) {
            this.$message.success('修改成功!')
            let resData = res.data
            if (resData.success) {
              // 单点登录退出
              this.$store.dispatch('user/resetToken').then(() => {
                window.location.href = singlePointLogoutUrl() // 调用单点登录的退出接口
              })
            }
          }
          if (res.data.code == '10302') {
            let errorData = res.data.errorData
            getOpenConfigAfterLogin().then(res =>{
              if(res.code=='0') {
                this.loadIframe(errorData)
              } else {
                // 单点登录退出
                this.$store.dispatch('user/resetToken').then(() => {
                  window.location.href = singlePointLogoutUrl() // 调用单点登录的退出接口
                })
              }
            })
          }
        }
      })
    },
    async loadIframe (iamUrl) {
      let tabName = this.iamSecurityPageType
      let singleBaseUrl = this.$systemUrl + '/'
      let service = encodeURI(singleBaseUrl)
      if (iamUrl) {
        this.iframeUrl = iamUrl
      } else {
        this.iframeUrl = `${this.sysOpenConfig.iamSysBaseUrl}/portal/index.html#/portalPage/personSetting?headerVisible=true&tab=${tabName}&redirectUri=${service}`
      }
      // iframe 加载
      let flowIframeWrap = document.getElementById(this.iframeName + '_wrap')
      let flowIframe = document.getElementById(this.iframeName)
      let iframeNode = document.createElement('iframe')
      iframeNode.setAttribute('id', this.iframeName)
      iframeNode.setAttribute('name', this.iframeName)
      iframeNode.setAttribute('class', 'accountSecurityIframeStyle')
      iframeNode.setAttribute('src', this.iframeUrl)
      if (!flowIframe) {
        if (flowIframeWrap) {
          document.getElementById(this.iframeName + '_wrap').appendChild(iframeNode)
        }
      } else {
        if (flowIframeWrap) {
          document.getElementById(this.iframeName + '_wrap').removeChild(flowIframe)
          document.getElementById(this.iframeName + '_wrap').appendChild(iframeNode)
          document.getElementById(this.iframeName).src = this.iframeUrl // 设置iframe url
        }
      }
    }
  }
}
</script>
<style>
.accountSecurityIframeStyle{
  border:none;
  height:100%;
  position:absolute;
  width:100%;
}
</style>
