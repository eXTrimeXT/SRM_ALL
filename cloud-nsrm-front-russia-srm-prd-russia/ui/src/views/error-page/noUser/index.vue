<template>
  <!-- 抱歉，您的页面找不到了！ -->
  <div style="padding-top: 90px;">
    <c-exception
      type="401"
      title="该用户不存在或被禁用"
      sub-title="请联系管理员处理账号问题"
    />
    <p style="text-align:center">
      <el-button type="primary" @click="backHome">返回首页</el-button>
    </p>
  </div>
</template>

<script>
import CException from 'lib@/components/c-exception'
import { singlePointLogoutUrl } from '@/config/sysConfig'

export default {
  name: 'NoUser',
  components: {
    CException
  },
  methods: {
    backHome () {
      // 单点登录退出
      this.$store.dispatch('user/resetToken').then(() => {
        window.location.href = singlePointLogoutUrl() // 调用单点登录的退出接口
      })
    }
  }
}
</script>
