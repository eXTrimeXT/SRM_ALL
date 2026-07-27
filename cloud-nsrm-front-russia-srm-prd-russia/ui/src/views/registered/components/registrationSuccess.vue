<template>
  <div class="reg-succes">
    <p>
      <span class="success-icon">
        <i class="el-icon-success" />
      </span>
    </p>
    <p class="success-title">
      账号注册成功
    </p>
    <p class="success-des">
      账号、密码已通过短信发送给您，请注意查收；企业信息认证通过后才可进入准入流程
    </p>
    <div class="success-opt">
      <el-button type="primary" @click="backLoginHandle">
        去企业信息认证
      </el-button>
      <!-- <el-button type="primary" @click="certificationHandle">维护企业信息</el-button> -->
    </div>
  </div>
</template>

<script>
import * as path from '@/utils/path'
export default {
  name: 'RegistrationSuccess',
  data () {
    return {

    }
  },
  computed: {
  },
  created () {

  },
  methods: {
    // 返回登录
    backLoginHandle () {
      this.$router.push({ path: path.resolve('/login') })
    },
    // 企业信息维护
    certificationHandle () {
      // todo
      // this.$router.push({
      //   path: '/userManage/companyInfoMaintain'
      // })
    }
  }
}
</script>
<style lang="scss" scoped>
  .reg-succes{
    width: 365px;
    margin: 50px auto;
    text-align: center;
    .success-title {
      font-size: 18px;
      color: #52BB26;
      line-height: 26px;
      font-weight: 500;
    }
    .success-des {
      font-size: 14px;
      color: #51555B;
      text-align: center;
      line-height: 22px;
      margin-bottom: 30px;
    }
    .success-icon{
      display: inline-block;
      text-align: center;
      width: 80px;
      height: 80px;
      padding: 10px;
      background: rgba(82, 187, 38, 0.2);
      border-radius: 50%;
      .el-icon-success{
        color: #52BB26;
        font-size: 60px;
      }
    }
  }
</style>
