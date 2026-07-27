<template>
  <div class="registration-account">
    <div class="account-form-content">
      <el-form
        ref="regForm"
        :model="regForm"
        :rules="rules"
        label-width="80px"
        :status-icon="false"
      >
        <el-form-item
          prop="phone"
          label="手机号"
          :rules="[
            { required: regForm.verifyType=='phone' ? true : true, message: '请输入手机号' },
            { validator: validatePhoneFn, trigger: ['blur', 'change'] },
          ]"
        >
          <el-input
            v-model="regForm.phone"
            :placeholder="$t('vendorMod.pleaseEnter')"
            @change="setFormatValue"
          />
        </el-form-item>
        <el-form-item prop="username" :label="$t('vendorMod.account')">
          <el-input
            v-model="regForm.username"
            disabled
          />
        </el-form-item>
        <el-form-item prop="nickname" label="姓名">
          <el-input v-model="regForm.nickname" :placeholder="$t('vendorMod.pleaseEnter')" />
        </el-form-item>
        <el-form-item
          prop="email"
          label="邮箱"
          :rules="[
            { validator: validateEmailFn, trigger: ['blur', 'change'] },
            { required: regForm.verifyType=='email' ? true : true, message: '请输入邮箱'}
          ]"
        >
          <el-input v-model="regForm.email" :placeholder="$t('vendorMod.pleaseEnter')" />
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
          <el-input v-model="regForm.verifyCode" :placeholder="$t('vendorMod.pleaseEnter')" class="validCodeInpu">
            <!-- <template slot="append">
              <img
                :src="verifyCode.src"
                class="reg-code-img"
                @click="refreshCode"
              />
            </template> -->
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button @click="preStepHandle">
            上一步
          </el-button>
          <el-button
            id="loginBtn"
            type="primary"
            :disabled="!regForm.username || !regForm.nickname || !regForm.email || !regForm.verifyCode"
            :loading="loading"
            @click="submitRegForm('regForm')"
          >
            {{ $t('vendorMod.submitReg') }}
          </el-button>
          <el-button
            type="text"
            class="to-login"
            @click="toLoginHandle"
          >
            已有账号，去登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { randomLenNum } from 'lib@/utils/util'
import http from '@/utils/axios/http'
import sha1 from 'js-sha1'
import * as path from '@/utils/path'
import { validEmail, validatePhone } from '@/utils/validate'
import { sysPrefix } from '@/config/ipConfig'
export default {
  name: 'RegistrationAccount',
  props: {
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
    const validateCode = (rule, value, callback) => {
      if (!value) {
        return callback(new Error(this.$t('vendorMod.enterCode')))
      } else {
        // this.validCodeHandle(value, callback)
        callback()
      }
    }
    return {
      isVerifyCodeTrue: null,
      loading: false,
      verifyTypeIsDisabled: false,
      regForm: {
        username: '',
        password: '',
        checkPass: '',
        nickname: '',
        email: '',
        phone: '',
        verifyCode: '',
        verifyType: 'phone',
        agreement: 'Y'
      },
      rules: {
        username: [{ required: true, validator: checkUsername }],
        password: [{ required: true, validator: validatePass }],
        checkPass: [{ required: true, validator: validatecheckPass }],
        nickname: [{ required: true, message: '请输入昵称' }],
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
      regSucDialog: false
    }
  },
  computed: {
    verifyTypeList () {
      let list = []
      if (this.verifyType == '1') {
        list = [{ value: 'email', label: '邮箱验证' }]
      }
      if (this.verifyType == '2') {
        list = [{ value: 'phone', label: '短信验证' }]
      }
      if (this.verifyType == '3') {
        list = [
          { value: 'phone', label: '短信验证' },
          { value: 'email', label: '邮箱验证' }
        ]
      }
      return list
    }
  },
  watch: {
    verifyType: {
      deep: true,
      immediate: true,
      handler (value) {
        if (value) {
          if (value == '1') {
            this.verifyTypeIsDisabled = true
            this.regForm.verifyType = 'email'
          } else if (value == '2') {
            this.verifyTypeIsDisabled = true
            this.regForm.verifyType = 'phone'
          } else {
            this.verifyTypeIsDisabled = false
            this.regForm.verifyType = 'phone'
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
        this.$message.warning('请输入格式正确的手机账号!')
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
    setFormatValue (value) {
      this.regForm.username = value
      // this.regForm.username = this.regForm.username.replace(/[\W]/g, '')
      // this.regForm.nickname = this.regForm.username
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
                password: regPass, // this.regForm.password
                verifyCode: this.regForm.verifyCode,
                mainType: this.regForm.verifyType
              }
            }).then(res => {
              this.loading = false
              if (res.code === '0') {
                this.$message({
                  message: res.message,
                  type: 'success'
                })
                this.regSuccess(true, res.data)
              } else {
                this.$message({
                  message: res.message,
                  type: 'error'
                })
                this.regSuccess(false)
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
    // 注册成功
    regSuccess (isSuccess, resData) {
      this.$emit('regSuccess', isSuccess, resData)
    },
    preStepHandle () {
      this.$emit('preStep', true)
    },
    // 已有账号，跳转登录
    toLoginHandle () {
      this.$router.push({ path: path.resolve('/login') })
    }
  }
}
</script>

<style lang="scss">
  .registration-account{
    .el-button{
      border-radius: 4px;
    }
    .el-input__inner{
      border-radius: 4px;
    }
    .account-form-content{
      padding: 45px 0;
      .el-form{
        width: 420px;
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
  }
</style>
