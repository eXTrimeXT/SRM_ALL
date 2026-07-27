<template>
  <el-container class="login-container">
    <el-header style="height: 50px">
      <!-- <div class="login-header-logo">单点登录</div> -->
    </el-header>
  </el-container>
</template>

<script>
import { singlePointLoginUrl } from '@/config/sysConfig'
export default {
  name: 'Find',
  data () {
    return {}
  },
  created () {
    window.location.href = singlePointLoginUrl()
  },
  methods: {

  }
}
</script>
