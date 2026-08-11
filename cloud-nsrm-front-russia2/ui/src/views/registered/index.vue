<template>
  <el-container class="registered-page">
    <el-main style="position: relative;">
      <div class="reg-header">
        <div class="inner-content">
          <!-- <ThemeConf
            class="reg-logo"
            themeType="img"
            imgType="mainLogo"
          /> -->
          <div class="reg-title">
            <!-- 欢迎注册供应商账号 -->
            {{ $t('cusEntry.login.welcomeRegister')}}
          </div>
        </div>
      </div>
      <div :class="['reg-main-info', 'inner-content', [3,4,5].includes(activeStep) ? 'company-info-out' : '']">
        <div class="reg-steps">
          <el-steps
            :active="activeStep"
            finish-status="success"
            :align-center="true"
          >
            <el-step :title="$t('vendorMod.registrationPolicy')" />
            <el-step :title="$t('vendorMod.registerAccount')" />
            <el-step :title="$t('vendorMod.registrationType')" />
            <el-step :title="$t('vendorMod.authenticationInformation')" />
            <el-step :title="$t('vendorMod.pendingApproval')" />
            <el-step :title="$t('vendorMod.informationAuthentication')" />
          </el-steps>
        </div>
        <div class="reg-fill-content">
          <!-- 注册须知 -->
          <RegistrationNotes
            v-if="activeStep==0"
            :stepBack="stepBack"
            @agreeChange="agreeChangeHandle"
          />
          <!-- 注册账号 -->
          <RegistrationAccount
            v-if="activeStep==1 && !regSuccess"
            :verifyType="openConfig.verifyType"
            @regSuccess="regSuccessHandle"
            @preStep="preStepHandle"
          />
          <!-- 注册成功 -->
          <RegistrationSuccess v-if="regSuccess && !autoLogon" />
          <!-- 企业信息-->
          <CompanyInfo
            v-if="[2,3,4,5].includes(activeStep) && regSuccess && autoLogon"
            type="registered"
            @goToWhere="goToWhere"
            @companyInfoIsSuccess="isSuccess"
          />
        </div>
      </div>
    </el-main>
  </el-container>
</template>

<script>
import ThemeConf from '@/components/themeConf'
import RegistrationNotes from './components/registrationNotes'
import RegistrationAccount from './components/registrationAccount'
import RegistrationSuccess from './components/registrationSuccess'
import CompanyInfo from 'modcc@/userManage/views/companyInfoMaintainEngine'

export default {
  name: 'Registered',
  components: {
    ThemeConf,
    RegistrationNotes,
    RegistrationAccount,
    RegistrationSuccess,
    CompanyInfo
  },
  data () {
    return {
      stepBack: false,
      activeStep: 0,
      regSuccess: false,
      openConfig: this.$store.getters.sysOpenConfig, // 未登录的配置
      autoLogon: true, // 判断是否需要自动登录，如果不是自动登录的话需要跳到登录页手动登录
      isCompanySuccess: 'N'// 判断是否跳到企业信息组件中的提交成功的界面，主要是修改样式判断用的
    }
  },
  created () {
    if (this.$route?.query?.regType === 'invite') { // 如果是邀请供应商的时候
      this.$store.commit('user/SET_USER_INFO', null)
      this.$store.commit('user/SET_COMPANYID', null)
    }
    const userInfo = this.$store.getters?.user?.userInfo
    if (userInfo?.companyId) {
      this.regSuccess = true
      this.activeStep = 3
    } else if (userInfo?.userId) {
      this.regSuccess = true
      this.activeStep = 2
    }
  },
  methods: {
    isSuccess (val) {
      this.isCompanySuccess = val
      //提交成功后，更新用户表中的公司名称
      if(val === 'Y'){ 
        this.$http({
          url: '/api-pj/pj/user/updateUserCompanyName',
          method: 'POST',
          data: {}
        }).then(res => {
        }).catch(() => {
        })
      }
    },
    // 从企业信息获取跳到第几步了
    goToWhere (where) {
      console.log(where, 'where')
      this.activeStep = where
    },
    // 下一步
    agreeChangeHandle (isAgree) {
      // 已勾选
      if (isAgree) {
        this.activeStep = 1
      } else {
        this.$message.warning(this.$t('appRegister.checkBeforeNextStep'))
        this.activeStep = 0
        return false
      }
    },
    // 注册回调
    async regSuccessHandle (val, resData) {
      if (val) {
        // 注册成功保持在当前不走，切换到注册成功组件
        this.regSuccess = true
        // 暂时注释掉自动登录获取token的方案，现IAM不支持
        // if (this.openConfig.supplierAutoAuth) {
        //   let keyToken = resData.KeyToken
        //   await this.$store.dispatch('user/loginWithToken', keyToken)
        //   this.$store.dispatch('user/initSystem')
        //   this.activeStep = 2
        //   this.autoLogon = true
        // } else {
        this.activeStep = 1
        this.autoLogon = false
        // }
      }
    },
    // 上一步 回到协议
    preStepHandle (val) {
      if (val) {
        this.activeStep = 0
        this.stepBack = true
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.registered-page{
  height: 100%;
  background-image: url('./img/reg-bulletinBoard.png');
  background-repeat: no-repeat;
  background-size: 100%;
  background-color: #edeff2;
  .inner-content{
    width: 100%;
    max-width: 1200px;
    position: relative;
    margin: auto;
  }
  .company-info-out{
    width: 80%;
    max-width: 1100px !important;
    left: -62px;
  }
  .reg-header{
    height: 120px;
    position: relative;
    z-index: 1;
    img{
      border: none;
      &.reg-logo{
        position: absolute;
        left: 2px;
        top: 20px;
      }
    }
    .reg-title{
      position: absolute;
      width: 100%;
      text-align: center;
      left: 0;
      right: 0;
      top: 45px;
      font-size: 24px;
      line-height: 32px;
      color: #FFFFFF;
      font-weight: bold;
    }
  }
  .reg-main-info{
    background: #FFFFFF;
    position: relative;
    padding: 16px;
    padding-bottom: 80px;
    z-index: 3px;
    .reg-steps{
      padding: 30px 100px;
    }
  }
}
</style>
