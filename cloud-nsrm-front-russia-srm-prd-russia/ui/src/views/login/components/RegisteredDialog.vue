<template>
  <div class="regFormInput-dialog">
    <el-form
      ref="regForm"
      :model="regForm"
      :rules="rules"
      label-width="78px"
      :status-icon="false"
    >
      <el-form-item prop="username" :label="$t('vendorMod.account')">
        <el-input v-model="regForm.username" placeholder="只支持数字、英文" @change="setFormatValue" />
      </el-form-item>
      <el-form-item prop="nickname" label="姓名">
        <el-input v-model="regForm.nickname" />
      </el-form-item>
      <!-- 密码【 -->
      <!-- <el-form-item
        prop="password"
        :label="$t('vendorMod.pass')"
      >
        <el-input
          v-model="regForm.password"
          type="password"
          :placeholder="$t('vendorMod.pass')"
          autocomplete="off"
          show-password
        />
      </el-form-item> -->
      <!-- <el-form-item
        prop="checkPass"
        :label="$t('vendorMod.confirmPass')"
      >
        <el-input
          v-model="regForm.checkPass"
          type="password"
          :placeholder="$t('vendorMod.confirmPass')"
          autocomplete="off"
          show-password
        />
      </el-form-item> -->
      <!-- 密码】 -->

      <el-form-item
        prop="email"
        label="邮箱"
        :rules="[
          { validator: validateEmailFn, trigger: ['blur', 'change'] },
          { required: regForm.verifyType=='email' ? true : true, message: '请输入邮箱'}
        ]"
      >
        <el-input v-model="regForm.email" />
      </el-form-item>
      <el-form-item
        v-if="verifyType != '1'"
        prop="phone"
        label="手机号"
        :rules="[
          { required: regForm.verifyType=='phone' ? true : true, message: '请输入手机号' },
          { validator: validatePhoneFn, trigger: ['blur', 'change'] },
        ]"
      >
        <el-input v-model="regForm.phone" />
      </el-form-item>
      <el-form-item prop="verifyType" label="验证方式">
        <el-row class="verify-type-tow" :gutter="0">
          <el-col :span="12">
            <el-select v-model="regForm.verifyType" :disabled="verifyTypeIsDisabled" placeholder="请选择验证方式">
              <el-option
                v-for="item in verifyTypeList"
                :key="'option' + item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-col>
          <el-col :span="12" style="padding-left: 5px;" class="send-reg-code">
            <el-button v-if="!hasSentFlag" type="primary" class="reg-code-img" @click="sendVerifyCode">
              发送验证码
            </el-button>
            <el-button
              v-else
              type="primary"
              :loading="countNum > 0"
              :class="{'reg-code-img' : countNum === 0 ,'reg-code-img-repeat' : countNum > 0 }"
              @click="sendVerifyCode"
            >
              {{ countNum > 0 ? `重新发送（${countNum}）S` : '重新发送' }}
            </el-button>
          </el-col>
        </el-row>
      </el-form-item>
      <el-form-item prop="verifyCode" :label="$t('vendorMod.verifyCode')">
        <el-input v-model="regForm.verifyCode" class="validCodeInpu">
          <!-- <template slot="append">
            <img
              :src="verifyCode.src"
              class="reg-code-img"
              @click="refreshCode"
            />
          </template> -->
        </el-input>
      </el-form-item>

      <el-form-item class="doAgrrent-div">
        <el-checkbox
          v-model="regForm.agreement"
          true-label="Y"
          false-label="N"
          style="margin-right: 10px;"
        />
        {{ $t('vendorMod.doAgree') }}
        <span class="doAgreement" @click="toUserProtocol">{{ $t('vendorMod.userDeal') }}</span>
        <!-- {{$t('vendorMod.and')}}
        <span class="doAgreement" @click="toPrivacyProtocol">{{$t('vendorMod.privacyDeal')}}</span> -->
      </el-form-item>
      <el-form-item>
        <el-button
          id="loginBtn"
          type="primary"
          :disabled="!regForm.username || !regForm.nickname || !regForm.email || !regForm.verifyCode"
          :loading="loading"
          @click="submitRegForm('regForm')"
        >
          {{ $t('vendorMod.submitReg') }}
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { randomLenNum } from 'lib@/utils/util'
import http from '@/utils/axios/http'
import sha1 from 'js-sha1'
import { isSinglePoint } from '@/config/sysConfig'
import { validEmail, validatePhone } from '@/utils/validate'
import { sysPrefix } from '@/config/ipConfig'

export default {
  name: 'RegisteredDialog',
  components: {}, // Message
  props: {
    isShow: {
      type: Boolean,
      default: false
    },
    verifyType: {
      type: String,
      default: '1' // 默认支持邮箱
    }
  },
  data () {
    // 账号校验
    var checkUsername = (rule, value, callback) => {
      if (!value) {
        return callback(new Error(this.$t('vendorMod.enterAccount')))
      } else {
        callback()
      }
    }
    // 密码校验
    var validatePass = (rule, value, callback) => {
      const patrn = /[A-Za-z0-9][`~!@#$%^&*()_\-+=<>?:"{}|,.\/;'\\[\]·~！@#￥%……&*（）——\-+={}|《》？：“”【】、；‘'，。、]/im
      if (!value) {
        callback(new Error(this.$t('vendorMod.enterPass')))
      } else if (!patrn.test(value) || value.length < 8 || value.length > 24) {
        callback(
          new Error(this.$t('vendorMod.errorPass'))
        )
      } else {
        callback()
      }
    }
    // 确认密码校验
    var validatecheckPass = (rule, value, callback) => {
      if (!value) {
        callback(new Error(this.$t('vendorMod.enterConfirmPass')))
      } else if (value !== this.regForm.password) {
        callback(new Error(this.$t('vendorMod.confirmPassError')))
      } else {
        callback()
      }
    }
    // 验证码校验
    var validateCode = (rule, value, callback) => {
      if (!value) {
        return callback(new Error(this.$t('vendorMod.enterCode')))
      } else {
        // this.validCodeHandle(value, callback)
        callback()
      }
    }

    return {
      singlePoint: isSinglePoint,
      isVerifyCodeTrue: null,
      loading: false,
      regForm: {
        username: '',
        password: '',
        checkPass: '',
        nickname: '',
        email: '',
        phone: '',
        verifyCode: '',
        verifyType: '',
        agreement: 'Y'
      },
      rules: {
        username: [{ required: true, validator: checkUsername }],
        password: [{ required: true, validator: validatePass }],
        checkPass: [{ required: true, validator: validatecheckPass }],
        nickname: [{ required: true, message: '请输入姓名' }],
        // email: [{ required: true, message: '请输入邮箱' }],
        // phone: [{ required: true, message: '请输入手机号' }],
        verifyType: [{ required: true, message: '请选择验证方式' }],
        verifyCode: [{ required: true, validator: validateCode }]
      },
      verifyCode: {
        src: `${this.$systemUrl}${sysPrefix()}/api-sup/register/getVerifyCode`,
        value: '',
        len: 4,
        message: this.$t('vendorMod.enterFourDigitsCode')
      },
      hasSentFlag: false,
      countNum: 60,
      timerId: null,
      regSucDialog: false,
      verifyTypeIsDisabled: false
    }
  },
  computed: {
    verifyTypeList () {
      let list = []
      if (this.verifyType == '1') {
        list = [{ value: 'email', label: '邮箱验证' }]
      }
      if (this.verifyType == '2') {
        list = [{ value: 'phone', label: '手机号验证' }]
      }
      if (this.verifyType == '3') {
        list = [
          { value: 'phone', label: '手机号验证' },
          { value: 'email', label: '邮箱验证' }
        ]
      }
      return list
    }
  },
  watch: {
    isShow: {
      deep: true,
      immediate: true,
      handler (value) {
        if (value) {
          for (let i in this.regForm) {
            this.regForm[i] = ''
            this.regForm.agreement = 'Y'
          }
          if (this.verifyType == '1') {
            this.verifyTypeIsDisabled = true
            this.regForm.verifyType = 'email'
          } else if (this.verifyType == '2') {
            this.verifyTypeIsDisabled = true
            this.regForm.verifyType = 'phone'
          } else {
            this.verifyTypeIsDisabled = false
            this.regForm.verifyType = ''
          }
        }
      }
    }
  },
  beforeDestroy () {
    clearTimeout(this.timerId)
    this.timerId = null
  },
  methods: {
    // 验证邮箱
    validateEmailFn (rule, value, callback) {
      if (!value) {
        return callback(new Error('请输入邮箱'))
      } else {
        if (!validEmail(value)) {
          return callback(new Error('请输入正确的邮箱地址'))
        } else {
          return callback()
        }
      }
    },
    // 验证手机
    validatePhoneFn (rule, value, callback) {
      if (!value) {
        return callback(new Error(this.$t('vendorMod.enterCode')))
      } else {
        if (!validatePhone(value)) {
          return callback(new Error('请输入正确的手机号码'))
        } else {
          return callback()
        }
      }
    },
    // 发送邮箱
    sendVerifyCode () {
      if (!this.regForm.verifyType) {
        this.$message.warning('请选择验证方式!')
        return
      }
      if (this.regForm.verifyType == 'email' && !validEmail(this.regForm.email)) {
        this.$message.warning('请输入格式正确的邮箱账号!')
        return
      }
      if (this.regForm.verifyType == 'phone' && !validatePhone(this.regForm.phone)) {
        this.$message.warning('请输入格式正确的手机号码!')
        return
      }
      let sendVerifyUrl = ''
      let paramsData = {}
      let typeText = ''
      // 邮箱接收方式
      if (this.regForm.verifyType == 'email') {
        sendVerifyUrl = '/api-sup/register/sendVerifyCodeToEmailNew'
        paramsData = { email: this.regForm.email }
        typeText = '邮箱'
      } else {
        // 手机接收方式
        sendVerifyUrl = '/api-sup/register/sendVerifyCodeToPhone'
        paramsData = { phone: this.regForm.phone }
        typeText = '手机'
      }
      http({
        url: sendVerifyUrl,
        method: 'GET',
        params: paramsData,
        loading: true
      }).then(res => {
        this.$message({ type: 'success', message: typeText + '验证码发送成功，请注意查收！' })
        this.hasSentFlag = true
        this.countDown()
        this.$forceUpdate()
      })
    },
    countDown () {
      var count = () => {
        this.timerId = setTimeout(() => {
          this.countNum--
          if (this.countNum === 0) {
            clearTimeout(this.timerId)
            this.timerId = null
          } else {
            count()
          }
        }, 1000)
      }
      this.countNum = 60
      count()
    },
    // 只允许允许输入数字和字母
    setFormatValue () {
      this.regForm.username = this.regForm.username.replace(/[\W]/g, '')
      this.regForm.nickname = this.regForm.username
    },
    // 提交注册
    submitRegForm (formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          if (!this.regForm.verifyCode) {
            this.$message.warning('请输入邮箱接收到的验证码！')
            return
          }
          // 发送注册请求
          if (this.regForm.agreement === 'N') {
            this.$message({
              message: this.$t('vendorMod.toDoComAgree'),
              type: 'error'
            })
          } else {
            this.loading = true
            let regPass = sha1(this.regForm.password)
            return http({
              url: '/api-sup/register/registerAccount',
              method: 'POST',
              data: {
                username: this.regForm.username,
                nickname: this.regForm.nickname,
                email: this.regForm.email,
                phone: this.regForm.phone,
                password: regPass,
                verifyCode: this.regForm.verifyCode,
                mainType: this.regForm.verifyType
              }
            }).then(async (res) => {
              this.loading = false
              if (res.code === '0') {
                this.$emit('show', false)
                this.$emit('regsuccess', res.data)
              } else {
                this.$message({
                  message: res.message,
                  type: 'error'
                })
                return false
              }
            }).catch(() => {
              this.loading = false
            })
          }
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    validCodeHandle (value, callback) {
      if (value.length === 4) {
        return http({
          url: '/api-sup/register/checkVerifyCode',
          method: 'POST',
          params: {
            verifyCode: value
          }
        })
          .then(res => {
            this.isVerifyCodeTrue = 'success'
            callback()
          })
          .catch(() => {
            callback(new Error(this.$t('vendorMod.verifyCodeError')))
          })
      } else {
        callback(new Error(this.$t('vendorMod.enterFourDigitsCode')))
      }
    },
    // 刷新验证码
    refreshCode () {
      this.regForm.verifyCode = ''
      let randomStr = randomLenNum(this.verifyCode.len)
      this.verifyCode.src =
        `${this.$systemUrl}${sysPrefix()}/api-sup/register/getVerifyCode?randomStr=` + randomStr
    },
    // 跳到用户协议
    toUserProtocol () {
      const { href } = this.$router.resolve({ name: 'userProtocol' })
      window.open(href, '_blank', 'noopener,noreferrer')
    },
    // 跳到隐私协议
    toPrivacyProtocol () {
      const { href } = this.$router.resolve({ name: 'privacyProtocol' })
      window.open(href, '_blank', 'noopener,noreferrer')
    }
  }
}
</script>

<style lang="scss" scoped>
.register-wrapper {
  text-align: center;
  .img {
    .icon-success {
      display: block;
      width: 80px;
      margin: 0 auto;
    }
  }
  .des {
    margin-top: 12px;
    font-size: 18px;
    color: #52BB26;
    height: 26px;
    line-height: 26px;
    font-weight: 500;
  }
  .info {
    margin-top: 8px;
    font-size: 14px;
    color: #51555B;
    line-height: 22px;
    height: 22px;
  }
  .btn {
    margin-top:32px;
  }
}
.regFormInput-dialog {
  width: 100%;
  max-width: 520px;
  margin: 0 auto;
  padding-right: 20px;
  h1 {
    font-size: 1.6rem;
    text-align: center;
    line-height: 1.8rem;
    margin: 0;
    padding: 2.5rem 0;
  }
  .enterTip {
    margin: 0;
    font-size: 1.125rem;
    line-height: 2rem;
    margin-bottom: 1rem;
  }
  .commonPading {
    padding: 0.5rem 0;
  }
  #loginBtn {
    width: 100%;
  }
  .forgetPswAndReg {
    height: 1.8rem;
    a {
      color: #488cff;
      font-size: 1.125rem;
      text-decoration: none;
      &.register {
        float: right;
      }
    }
  }
  .el-form-item {
    border-radius: 2px;
    margin-bottom: 16px;
    .el-form-item__error {
      padding-top: 6px;
    }
  }
  .doAgreement {
    cursor: pointer;
    color: #1890ff;
  }
  .isVerifyCodeTrue {
    line-height: 40px;
    margin-right: 5px;
    font-size: 16px;
    color: #7dd012;
  }
  .isVerifyCodeError {
    line-height: 40px;
    margin-right: 5px;
    font-size: 16px;
    color: #f63707;
  }
  .verify-type-tow{
    .send-reg-code{
      padding-left: 5px;
      >.el-button{
        width:100%;
      }
    }
  }
}
</style>
<style>
.login-container .regFormInput-dialog .el-input--mini .el-input__inner {
  height: 38px !important;
  line-height: 38px !important;
  min-height: 38px !important;
  color: #666;
}
.regFormInput-dialog .el-button--small {
  padding: 12px 15px;
}
.regFormInput-dialog .el-button.el-button--primary.is-disabled {
  padding: 11px 15px;
}
.regFormInput-dialog .validCodeInpu .el-input-group__append {
  padding: 0 !important;
  width: 6rem;
  height: 38px;
  overflow: hidden;
}
.regFormInput-dialog .validCodeInpu .el-input-group__append .reg-code-img {
  display: block;
  width: 100%;
  height: 36px;
  text-align: center;
  margin-left: 1px;
  background-color: #1890ff;
  color: #fff;
}
.regFormInput-dialog .validCodeInpu .el-input-group__append .reg-code-img-repeat {
  display: block;
  width: 100%;
  height: 36px;
  text-align: center;
  margin-left: 1px;
  background-color: #F6F6F6;
  color: #C5C6C8;
  cursor: not-allowed;
  pointer-events: none;
}
.regFormInput-dialog .doAgrrent-div {
  margin-bottom: 12px;
}
.register-dialog .srm-dialog-content {
  padding: 46px 30px !important;
}
</style>
