<template>
  <el-container
    direction="vertical"
    style="height:100%"
    class="forget-password-page"
  >
    <el-main class="forget-password-main account-form-content" style="position:relative;height:100%">
      <div class="forget-header">
        <div class="inner-content">
          <!-- <ThemeConf
            class="reg-logo"
            themeType="img"
            imgType="mainLogo"
          /> -->
          <div class="reg-title">
            {{ $t('route.forgotPass') }}
          </div>
        </div>
      </div>
      <div class="forget-content inner-content">
        <!-- 步骤条 -->
        <el-steps class="steps" :active="activeSteps" align-center finish-status="success">
          <el-step :title="$t('login.validAccount')" />
          <el-step :title="$t('common.verifyType')" />
          <el-step :title="$t('common.resetPass')" />
        </el-steps>
        <div class="account-sec">
          <!-- 验证账号 -->
          <div v-if="activeSteps == 0">
            <el-form
              :model="accountForm"
              :label-position="labelPosition"
              label-width="80px"
              :rules="accoutRules"
              class="forget-password-form"
            >
              <!-- 账号 -->
              <el-form-item prop="username" :label="$t('vendorMod.account')">
                <el-input
                  v-model="accountForm.username"
                  :placeholder="$t('vendorMod.pleaseEnter')"
                  type="text"
                  clearable
                />
              </el-form-item>
              <!-- 验证码滑块 -->
              <el-form-item prop="slideCode" :label="$t('vendorMod.verifyCode')">
                <Slider
                  ref="slider"
                  :identifier="accountForm.username"
                  @success="slideSuccess"
                />
              </el-form-item>
            </el-form>
          </div>
          <!-- 验证方式 -->
          <div v-if="activeSteps == 1">
            <el-form
              :model="validTypeForm"
              :label-position="labelPosition"
              label-width="0px"
              :rules="validTypeRules"
              class="forget-password-form"
            >
              <div v-if="verifyTypeList.length == 2" class="verify-type-style">
                <el-radio-group
                  v-model="validTypeForm.verifyType"
                  class="verify-type-style-radio"
                  @input="changeContactType"
                >
                  <el-radio-button
                    v-for="(item, index) in verifyTypeList"
                    :key="index + '_' + item.value"
                    :label="item.value"
                  >
                    {{ item.label }}
                  </el-radio-button>
                </el-radio-group>
              </div>
              <!-- 账号 -->
              <el-form-item prop="username" label=" ">
                <div class="account-name">
                  {{ $t('vendorMod.account') }} {{ validTypeForm.username }}
                </div>
              </el-form-item>
              <el-form-item prop="contactNo" label="">
                <el-input v-model="validTypeForm.contactNo" disabled />
              </el-form-item>
              <!-- 验证码 -->
              <el-form-item prop="validCode" label="" class="valid-code">
                <el-row class="verify-type-tow" :gutter="0">
                  <el-col :span="16">
                    <el-input
                      v-model="validTypeForm.validCode"
                      name="validCode"
                      type="text"
                      :placeholder="$t('vendorMod.enterCode')"
                    />
                  </el-col>
                  <el-col :span="8" style="padding-left: 8px;" class="send-reg-code">
                    <el-button
                      :disabled="hasSentFlag"
                      type="primary"
                      :class="{ 'btn-active': !hasSentFlag, 'btn-inactive': hasSentFlag }"
                      @click="sendVerifyCode"
                    >
                      {{ hasSentFlag ? $t('common.resend', { countNum }) : $t('common.sendVerificationCode') }}
                    </el-button>
                  </el-col>
                </el-row>
              </el-form-item>
            </el-form>
          </div>
          <!-- 重置密码 -->
          <div v-show="activeSteps == 2">
            <el-row class="updata-pass">
              <el-col
                :xs="24"
                :sm="24"
                :md="14"
                :lg="14"
                :xl="14"
              >
                <el-form
                  :model="resetPassForm"
                  :label-position="labelPosition"
                  label-width="0px"
                  :rules="resetPassRules"
                  class="forget-password-form reset-pass-form"
                >
                  <!-- 账号 -->
                  <el-form-item prop="username" label=" ">
                    <div class="account-name">
                      {{ $t('vendorMod.account') }} {{ resetPassForm.username }}
                    </div>
                  </el-form-item>
                  <el-form-item prop="newPass" label="">
                    <PasswordInput v-model="resetPassForm.newPass" :placeholder="$t('vendorMod.enterPass')" />
                  </el-form-item>
                  <el-form-item prop="newPassCopy" label="">
                    <PasswordInput v-model="resetPassForm.newPassCopy" :placeholder="$t('login.confirmPwd')" />
                  </el-form-item>
                  <!-- 密码规则 -->
                  <div class="pwd-check-config">
                    <!-- 包含数字 -->
                    <p
                      v-if="pwdCheckConfig.containDigit=='Y'"
                      :class="{'errorStyle': passCheckErr.containDigit && passCheckErr.containDigit == 'N', 'successStyle': passCheckErr.containDigit && passCheckErr.containDigit == 'Y'}"
                    >
                      <i :class="{'el-icon-warning': ['','N'].includes(passCheckErr.containDigit), 'el-icon-success': passCheckErr.containDigit == 'Y'}" />
                      {{ $t('passwordManagement.containDigit') }}
                    </p>
                    <!-- 包含小写字母 -->
                    <p
                      v-if="pwdCheckConfig.containLowerLetter=='Y'"
                      :class="{'errorStyle': passCheckErr.containLowerLetter && passCheckErr.containLowerLetter == 'N', 'successStyle': passCheckErr.containLowerLetter && passCheckErr.containLowerLetter == 'Y'}"
                    >
                      <i :class="{'el-icon-warning': ['','N'].includes(passCheckErr.containLowerLetter), 'el-icon-success': passCheckErr.containLowerLetter == 'Y'}" />
                      {{ $t('passwordManagement.containLowerLetter') }}
                    </p>
                    <!-- 包含大写字母 -->
                    <p
                      v-if="pwdCheckConfig.containUpperLetter=='Y'"
                      :class="{'errorStyle': passCheckErr.containUpperLetter && passCheckErr.containUpperLetter == 'N', 'successStyle': passCheckErr.containUpperLetter && passCheckErr.containUpperLetter == 'Y'}"
                    >
                      <i :class="{'el-icon-warning': ['','N'].includes(passCheckErr.containUpperLetter), 'el-icon-success': passCheckErr.containUpperLetter && passCheckErr.containUpperLetter == 'Y'}" />
                      {{ $t('passwordManagement.containUpperLetter') }}
                    </p>
                    <!-- 包含特殊字符 -->
                    <p
                      v-if="pwdCheckConfig.containSpecialLetter=='Y'"
                      :class="{'errorStyle': passCheckErr.containSpecialLetter && passCheckErr.containSpecialLetter == 'N', 'successStyle': passCheckErr.containSpecialLetter && passCheckErr.containSpecialLetter == 'Y'}"
                    >
                      <i :class="{'el-icon-warning': ['','N'].includes(passCheckErr.containSpecialLetter), 'el-icon-success': passCheckErr.containSpecialLetter == 'Y'}" />
                      {{ $t('passwordManagement.containSpecialLetter') }}
                    </p>
                    <!-- 密码长度范围 -->
                    <p :class="{'errorStyle': passCheckErr.notMathLength && passCheckErr.notMathLength == 'N', 'successStyle': passCheckErr.notMathLength && passCheckErr.notMathLength == 'Y'}">
                      <i :class="{'el-icon-warning': ['','N'].includes(passCheckErr.notMathLength), 'el-icon-success': passCheckErr.notMathLength == 'Y'}" />
                      {{ $t('passwordManagement.passLangth') }}{{ pwdCheckConfig.minLength }} - {{ pwdCheckConfig.maxLength }}
                    </p>
                  </div>
                </el-form>
              </el-col>
              <el-col
                :xs="24"
                :sm="24"
                :md="8"
                :lg="8"
                :xl="8"
              >
                <div class="pass-rules">
                  <div class="pass-rules-title">
                    <!-- 密码设置规则： -->
                    {{ $t('cusEntry.supplement20250205.passwordRules') }}
                  </div>
                  <p v-if="pwdCheckConfig.containDigit=='Y'">
                    {{ $t('passwordManagement.containDigit') }}
                  </p>
                  <p v-if="pwdCheckConfig.containLowerLetter=='Y'">
                    {{ $t('passwordManagement.containLowerLetter') }}
                  </p>
                  <p v-if="pwdCheckConfig.containUpperLetter=='Y'">
                    {{ $t('passwordManagement.containUpperLetter') }}
                  </p>
                  <p v-if="pwdCheckConfig.containSpecialLetter=='Y'">
                    {{ $t('passwordManagement.containSpecialLetter') }}
                  </p>
                  <p>{{ $t('passwordManagement.passLangth') }}{{ pwdCheckConfig.minLength }}-{{ pwdCheckConfig.maxLength }}</p>
                  <p>{{ $t('passwordManagement.notContainArr') }}</p>
                  <p>{{ $t('passwordManagement.noeUseBeforNo') }}{{ pwdCheckConfig.cannotUsedAmount }}{{ $t('passwordManagement.usedPass') }}</p>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
        <!-- 重置成功 -->
        <div v-if="activeSteps == 3" class="resetSuccess">
          <p>
            <span class="username-span">
              {{ $t('vendorMod.account') }}
            </span>
            {{ resetPassForm.username }}
          </p>
          <p class="success-icon">
            <i class="el-icon-success" />
          </p>
          <p>{{ $t('vendorMod.resetSuccess') }}</p>
          <p>{{ $t('login.resetSuccess') }}</p>
        </div>
      </div>
    </el-main>
    <div class="forget-password-footer">
      <div class="footer-inner inner-content">
        <el-button :type="activeSteps == 3 ? 'primary' : 'default'" @click="goBack">
          {{ $t('registered.backToLogin') }}
        </el-button>
        <el-button v-if="activeSteps === 1" @click="switchAccount">
          {{ $t('login.changeAccount') }}
        </el-button>
        <el-button
          v-if="activeSteps === 0"
          :disabled="!accountForm.username || !slideFlag"
          type="primary"
          @click="nextToValid"
        >
          {{ $t('common.nextOne') }}
        </el-button>
        <el-button v-if="activeSteps === 1" type="primary" @click="nextToSetPass">
          {{ $t('common.nextOne') }}
        </el-button>
        <el-button v-if="activeSteps === 2" type="primary" @click="confirmPass">
          {{ $t('common.submit') }}
        </el-button>
      </div>
    </div>
  </el-container>
</template>

<script>
import { mapGetters } from 'vuex'
import forgetPwdApi from './api'
import Slider from './slider'
import ThemeConf from '@/components/themeConf'
import CToolbar from 'lib@/components/c-toolbar'
import PasswordInput from 'lib@/components/passwordInput'
import { getPassPublicKey } from '@/api/user'
import { dataEncryption } from '@/utils/secret'
import { notContainFn, validPatrnObj } from '@/utils/passValid'
export default {
  name: 'ForgetPassword',
  components: {
    Slider,
    ThemeConf,
    CToolbar,
    PasswordInput
  },
  data () {
    const validator = (rule, value, callback) => {
      // eslint-disable-next-line no-useless-escape
      let checkConfig = this.pwdCheckConfig
      let validCount = true
      let resMessage = ''
      if (!value) {
        callback(new Error(this.$t('common.pleaseInput')))
        this.passCheckErr = {
          containDigit: '',
          containLowerLetter: '',
          containSpecialLetter: '',
          containUpperLetter: '',
          notMathLength: '',
          notMathFiled: ''
        }
        return
      } else {
        // 长度判断
        if (value.length < checkConfig.minLength || value.length > checkConfig.maxLength) {
          // '密码长度为' + checkConfig.minLength + '到' + checkConfig.maxLength
          resMessage = this.$t('cusEntry.supplement20250205.passwordValid1', { minLength: checkConfig.minLength, maxLength: checkConfig.maxLength })
          // "密码至少包含数字、大小写字母、特殊字符，长度为8~24位"
          this.passCheckErr.notMathLength = 'N'
        } else {
          this.passCheckErr.notMathLength = 'Y'
        }
        // 判断是否包含数字
        if (checkConfig.containDigit == 'Y') {
          let patrn = validPatrnObj.containDigit
          if (!patrn.test(value)) {
            // 密码需要包含数字
            resMessage = this.$t('cusEntry.supplement20250205.passwordValid2')
            this.passCheckErr.containDigit = 'N'
          } else {
            this.passCheckErr.containDigit = 'Y'
          }
        }
        // 小写字母判断
        if (checkConfig.containLowerLetter == 'Y') {
          let patrn = validPatrnObj.containLowerLetter
          if (!patrn.test(value)) {
            // 密码需要包含小写字母
            resMessage = this.$t('cusEntry.supplement20250205.passwordValid3')
            this.passCheckErr.containLowerLetter = 'N'
          } else {
            this.passCheckErr.containLowerLetter = 'Y'
          }
        }
        // 大写字母判断
        if (checkConfig.containUpperLetter == 'Y') {
          let patrn = validPatrnObj.containUpperLetter
          if (!patrn.test(value)) {
            // 密码需要包含大写字母
            resMessage = this.$t('cusEntry.supplement20250205.passwordValid4')
            this.passCheckErr.containUpperLetter = 'N'
          } else {
            this.passCheckErr.containUpperLetter = 'Y'
          }
        }
        // 特殊字符判断
        if (checkConfig.containSpecialLetter == 'Y') {
          let patrn = validPatrnObj.containSpecialLetter
          if (!patrn.test(value)) {
            // 密码需要特殊字符
            resMessage = this.$t('cusEntry.supplement20250205.passwordValid5')
            this.passCheckErr.containSpecialLetter = 'N'
          } else {
            this.passCheckErr.containSpecialLetter = 'Y'
          }
        }

        if (resMessage) {
          // 请按照密码规则输入
          callback(new Error(this.$t('cusEntry.supplement20250205.passwordValid6')))
          return
        }
      }

      // 新旧密码判断
      if (rule.field == 'newPassCopy') {
        if (value !== this.resetPassForm.newPass) {
          callback(new Error(this.$t('vendorMod.confirmPassError')))// "新密码与确认新密码不一致！"
          return
        }
      }

      callback()
    }

    return {
      openConfig: this.$store.getters.sysOpenConfig, // 未登录的配置
      labelPosition: 'right',
      labelWidth: '120px',
      activeSteps: 0,
      // 账号验证
      accountForm: {
        username: '',
        slideCode: ''
      },
      accoutRules: {
        username: [{ required: true, message: this.$t('vendorMod.enterAccount') }],
        slideCode: [{ required: true, message: this.$t('vendorMod.enterCode') }]
      },
      // 验证方式
      validTypeForm: {
        username: '',
        verifyType: 'EMAIL', // 默认验证方式
        contactNo: '',
        validCode: '',
        email: '',
        phone: ''
      },
      validTypeRules: {
        username: [{ required: true, message: this.$t('common.requiredField') }],
        verifyType: [{ required: true, message: this.$t('common.selectVerifyType') }],
        contactNo: [{ required: true, message: this.$t('common.requiredField') }],
        validCode: [{ required: true, message: this.$t('vendorMod.enterCode') }]
      },
      // 重置密码
      resetPassForm: {
        username: '',
        newPass: '',
        newPassCopy: ''
      },
      resetPassRules: {
        username: [{ required: true }],
        newPass: [
          { required: true, message: this.$t('vendorMod.enterPass') },
          { validator: validator, trigger: 'blur' }
        ],
        newPassCopy: [
          { required: true, message: this.$t('login.confirmPwd') }
        ]
      },
      captchaId: null,
      slideFlag: false,
      slideData: null, // 记录滑块验证成功后，用户相关信息
      countNum: 30, // 倒计时
      timerId: null,
      hasSentFlag: false,
      pwdCheckConfig: { // 密码配置
        cannotUsedAmount: 1,
        containDigit: 'Y',
        containLowerLetter: 'Y',
        containSpecialLetter: 'Y',
        containUpperLetter: 'Y',
        maxLength: 16,
        minLength: 8,
        notContainColumn: {
          username: {
            'total': 'Y',
            'desc': 'Y'
          }
        },
        notContainStr: ''
      },
      passCheckErr: {
        containDigit: '',
        containLowerLetter: '',
        containSpecialLetter: '',
        containUpperLetter: '',
        notMathLength: '',
        notMathFiled: ''
      }
    }
  },
  computed: {
    verifyTypeList () {
      let list = []
      if (this.openConfig.verifyType == '1') {
        list = [{ value: 'EMAIL', label: this.$t('common.emailVerification') }]
      }
      if (this.openConfig.verifyType == '2') {
        list = [{ value: 'PHONE', label: this.$t('common.phoneVerification') }]
      }
      if (this.openConfig.verifyType == '3') {
        list = [
          { value: 'EMAIL', label: this.$t('common.emailVerification') },
          { value: 'PHONE', label: this.$t('common.phoneVerification') }
        ]
      }
      return list
    }
  },
  mounted () {
  },
  methods: {
    slideSuccess (data) {
      this.slideFlag = true // 滑块验证状态
      this.slideData = data
      console.log(data)
    },
    switchAccount () {
      this.activeSteps = 0
    },
    // 跳到验证方式
    nextToValid () {
      this.activeSteps++
      const { email, phone, username, captchaId } = this.slideData
      this.validTypeForm.username = username
      this.validTypeForm.verifyType = this.verifyTypeList[0].value // 'EMAIL'
      this.validTypeForm.phone = phone
      this.validTypeForm.email = email
      this.captchaId = captchaId
      this.changeContactType()
      this.$forceUpdate()
    },
    // 跳到校验方式
    async nextToSetPass () {
      if (!this.validTypeForm.validCode) {
        this.$message.warning(this.$t('vendorMod.enterCode'))
        return
      }
      let identifier = this.validTypeForm.username
      let params = {
        captchaId: this.captchaId,
        identifier,
        answer: {
          value: this.validTypeForm.validCode
        }
      }
      const response = await forgetPwdApi.checkMsgCode(params)
      if (response) {
        this.captchaId = response.data.captchaId
        let question = response.data.question
        this.pwdCheckConfig = question.pwdCheckConfig
        this.pwdCheckConfig.notContainStr = notContainFn(question.pwdCheckConfig.notContainColumn)
        this.resetPassForm.username = this.slideData.username
        this.activeSteps++
        this.$forceUpdate()
      }
    },
    // 确认修改密码
    async confirmPass () {
      if (!this.resetPassForm.newPass || !this.resetPassForm.newPassCopy) {
        this.$message.warning(this.$t('vendorMod.enterPass'))
        return
      }
      if (this.resetPassForm.newPass != this.resetPassForm.newPassCopy) {
        this.$message.warning(this.$t('login.pwdNotSame'))
        return
      }
      const { data } = await getPassPublicKey() // 获取密钥
      let passPublicKey = data
      let identifier = this.slideData.username
      let params = {
        captchaId: this.captchaId,
        identifier,
        pwd: dataEncryption(this.resetPassForm.newPass, passPublicKey) // 密码加密
      }
      const response = await forgetPwdApi.changePwd(params)
      if (response) {
        this.$message.success(this.$t('login.pwdSuccessTip'))
        this.activeSteps++
        // 3S后自动跳回登录页
        setTimeout(() => {
          this.goBack()
        }, 3000)
      }
    },
    countDown () {
      var count = () => {
        this.timerId = setTimeout(() => {
          this.countNum--
          if (this.countNum === 0) {
            clearTimeout(this.timerId)
            this.timerId = null
            this.hasSentFlag = false
          } else {
            count()
          }
        }, 1000)
      }
      this.countNum = 60
      count()
    },
    // 发送验证码
    async sendVerifyCode () {
      if (!this.validTypeForm.verifyType) {
        this.$message.warning(this.$t('common.selectVerifyType'))
        return
      }
      let identifier = this.validTypeForm.username
      let params = {
        msgType: this.validTypeForm.verifyType,
        identifier,
        captchaId: this.captchaId
      }
      try {
        const response = await forgetPwdApi.sendMsgCode(params)
        if (response) {
          this.$message({ type: 'success', message: this.$t('common.sendSuccess') })
          this.hasSentFlag = true
          this.captchaId = response.data.captchaId
          this.countDown()
        }
      } catch {
        this.hasSentFlag = false
        this.clearTimer()
      }
    },
    clearTimer () {
      clearTimeout(this.timerId)
      this.timerId = null
    },
    changeContactType () {
      const { verifyType } = this.validTypeForm
      if (verifyType === 'PHONE') {
        this.validTypeForm.contactNo = this.validTypeForm.phone
      } else if (verifyType === 'EMAIL') {
        this.validTypeForm.contactNo = this.validTypeForm.email
      }
    },
    goBack () {
      this.$router.push('/login')
    }
  }
}
</script>
<style lang="scss" scoped>
.forget-password-page{
  height: 100%;
  background-image: url('./img/reg-bulletinBoard.png');
  background-repeat: no-repeat;
  background-size: 100% 180px;
  background-color: #edeff2;
  position: relative;
  overflow: hidden;
  padding-bottom: 64px;
  .forget-password-main {
    .forget-header{
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
    .forget-content{
      background: #FFFFFF;
      padding: 32px 16px;
    }
  }
  .forget-password-footer{
    position: fixed;
    width: 100%;
    padding: 10px 0px;
    left: 0;
    bottom: 0;
    transition: padding-left 0.28s;
    box-sizing: border-box;
    background-color: #ffffff;
    box-shadow: 0 -1px 2px 0 rgba(182, 182, 182, 0.5);
    overflow: hidden;
    z-index: 999;
    .footer-inner{
      text-align: right;
    }
  }
  .pwd-check-config{
    padding: 5px 0 16px;
    p{
      font-size: 12px;
      color: #96999C;
      line-height: 18px;
      margin: 0;
      i{
        margin-right: 5px;
      }
      &.errorStyle{
        color: #FF4A4D;
      }
      &.successStyle{
        color: #52C718;
      }
    }
  }
  .pass-rules{
    border-left: 1px solid #D8D8D8;
    padding-left: 48px;
    .pass-rules-title{
      margin-bottom: 12px;
      font-size: 16px;
      color: #161C24;
      line-height: 24px;
    }
    p{
      font-size: 12px;
      color: #73777C;
      line-height: 24px;
      margin: 0;
      position: relative;
      padding-left: 10px;
      &::before{
        content: " ";
        font-size: 0px;
        width: 4px;
        height: 4px;
        border-radius: 50%;
        background: #dedede;
        position: absolute;
        left: 2px;
        top: 10px;
      }
    }
  }
}

.inner-content{
  width: 100%;
  max-width: 1200px;
  position: relative;
  margin: auto;
}
.verify-type-style{
  width: 200px;
  height: 36px;
  background: #F6F6F6;
  border-radius: 18px;
  padding: 4px;
  text-align: center;
  margin: 0 auto 28px;
}

.page-title{
  font-size: 24px;
  color: #393e45;
  margin: 48px;
  text-align: center;
}
.steps {
  max-width: 1000px;
  margin: 0 auto;
}
.back {
  padding: 16px 0 0 16px;
  cursor: pointer;
  color: #393e45;
  font-size: 14px;
  span {
    margin-left: 4px;
  }
}

.forget-password-form{
  width: 380px;
  margin: 0 auto;
  &.reset-pass-form{
    margin: 0 48px 0 auto !important;
  }
}
.account-sec{
  margin-top: 30px;
  padding-bottom: 30px;
}
.btn-sec{
  width: 100%;
  text-align: center;
}
.resetSuccess{
  text-align: center;
}
.success-icon{
  width: 80px;
  height: 80px;
  background: rgb(82,187,38,0.2);
  padding: 10px;
  border-radius: 50%;
  margin: 0 auto;
}
.success-icon i {
  font-size: 60px;
  color: #52C718;
}
.account-name {
  height: 32px;
  line-height: 32px;
}
.username-span{
  &::before{
    content: "*";
    font-size: 14px;
    color: #FF4A4D;
    line-height: 22px;
    font-weight: 400;
  }
}
</style>

<style lang="scss">
  .forget-password-page{
    .el-button{
      border-radius: 2px;
    }
    .el-input{
      .el-input__inner {
        height: 32px !important;
        line-height: 32px !important;
        min-height: 32px !important;
        font-size: 14px;
        color: #51555B;
        border-radius: 2px;
        &:focus {
          outline: 0;
          border-color: #0077ff;
        }
        &:hover {
          border-color: #0077ff;
        }
        &::placeholder {
          color: #96999C;
        }
      }
      &.is-disabled{
        .el-input__inner{
          &:hover{
            border-color: #DCDDDE;
          }
        }
      }
    }
    // .valid-code {
    //   .el-input__inner {
    //     border-top-right-radius: 0 !important;
    //     border-bottom-right-radius: 0 !important;
    //   }
    // }
    .account-form-content{
      .el-form{
        width: 380px;
        margin: 0 auto;
        .el-form-item__label{
          line-height: 32px;
        }
        .el-form-item{
          margin-bottom: 24px;
          .el-input{
            &.el-input--small{
              height: 32px;
              .el-input__inner{
                height: 32px;
              }
            }
          }
          .el-button{
            height: 32px;
            height: 32px;
            &.to-login{
              float: right;
            }
          }
        }
      }
    }
    .verify-type-tow{
      .send-reg-code{
        padding-left: 5px;
        >.el-button{
          width:100%;
        }
      }
    }
    .verify-type-style{
      .verify-type-style-radio{
        .el-radio-button{
          border: none;
          background: none;
          border-radius: 16px;
          font-size: 14px;
          &.is-active,&:hover{
            .el-radio-button__inner{
              background: #FFFFFF;
              border-radius: 16px;
              font-weight: 500;
              color: #0077FF;
              font-weight: 500;
            }
          }
          .el-radio-button:not(:first-child):hover{
            border: none;
          }
          .el-radio-button__inner{
            border: 0;
            background: none;
            height: 28px;
            line-height: 20px;
            padding: 4px 20px;
            &:hover{
              border: 0;
              background: none;
            }
          }
        }
        .el-radio-button__orig-radio:checked + .el-radio-button__inner{
          box-shadow: none;
        }

        .el-radio-button:not(:first-child):hover::before{
          display: none;
        }
      }
    }
    .passwordInput .el-input__suffix{
      line-height: 32px;
    }
  }
  .el-form-item .el-input__inner{
    border: 1px solid #DCDDDE;
  }
  @media screen and (max-width: 1199px){
    .forget-password-page {
      .forget-password-main{
        padding: 0 16px;
        .forget-header{
          height: 110px;
        }
      }
      .footer-inner{
        padding: 0px 16px;
      }
    }
  }
  @media screen and (max-width: 500px){
    .forget-password-page {
      .account-form-content .el-form{
        width: 100% !important;
      }
    }
  }
  @media only screen and (max-width: 768px) {
  .forget-password-main{
    .updata-pass{
      .el-col-sm-24{
        .update-pass-form{
          margin: 0px auto 50px !important;
        }
        .pass-rules{
          border: 0;
          padding-bottom: 50px;
        }
      }
    }
  }
}
</style>
