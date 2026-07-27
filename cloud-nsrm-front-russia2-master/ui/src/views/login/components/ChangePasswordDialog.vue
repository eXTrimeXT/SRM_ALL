<template>
  <div class="changeFormInput">
    <el-form
      ref="changeForm"
      :model="changeForm"
      :rules="rules"
      label-width="120px"
      status-icon
    >
      <el-form-item
        prop="username"
        :label="$t('vendorMod.account')"
      >
        <el-input
          v-model="changeForm.username"
          @change="setFormatValue"
        />
      </el-form-item>
      <!-- 旧密码【 -->
      <el-form-item
        prop="oldPass"
        :label="$t('vendorMod.oldPass')"
      >
        <el-input
          v-model="changeForm.oldPass"
          type="password"
          :placeholder="$t('vendorMod.oldPass')"
          autocomplete="off"
          show-password
        />
      </el-form-item>
      <!-- 新密码【 -->
      <el-form-item
        prop="newPass"
        :label="$t('vendorMod.newPass')"
      >
        <el-input
          v-model="changeForm.newPass"
          type="password"
          :placeholder="$t('vendorMod.newPass')"
          autocomplete="off"
          show-password
        />
      </el-form-item>
      <el-form-item
        prop="confirmNewPass"
        :label="$t('vendorMod.confirmNewPass')"
      >
        <el-input
          v-model="changeForm.confirmNewPass"
          type="password"
          :placeholder="$t('vendorMod.confirmNewPass')"
          autocomplete="off"
          show-password
        />
      </el-form-item>
      <!-- 确认新密码】 -->
      <el-form-item>
        <el-button
          id="loginBtn"
          type="primary"
          :loading="loading"
          @click="submitChangeForm('changeForm')"
        >
          {{ $t('common.confirm') }}
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { randomLenNum } from 'lib@/utils/util'
import http from '@/utils/axios/http'
import sha1 from 'js-sha1'
import { sysPrefix } from '@/config/ipConfig'

export default {
  name: 'RegisteredDialog',
  components: {}, // Message
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
    var validateConfirmNewPass = (rule, value, callback) => {
      if (!value) {
        callback(new Error(this.$t('vendorMod.enterConfirmPass')))
      } else if (value !== this.changeForm.newPass) {
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
        this.validCodeHandle(value, callback)
      }
    }
    return {
      isVerifyCodeTrue: null,
      loading: false,
      changeForm: {
        username: '',
        oldPass: '',
        newPass: '',
        confirmNewPass: '',
        nickname: '',
        email: '',
        verifyCode: '',
        agreement: 'Y'
      },
      rules: {
        username: [{ required: true, validator: checkUsername }],
        newPass: [{ required: true, validator: validatePass }],
        confirmNewPass: [{ required: true, validator: validateConfirmNewPass }]
      },
      verifyCode: {
        src: '/api-sup/register/getVerifyCode',
        value: '',
        len: 4,
        message: this.$t('vendorMod.enterFourDigitsCode')
      }
    }
  },
  methods: {
    // 只允许允许输入数字和字母
    setFormatValue () {
      this.changeForm.username = this.changeForm.username.replace(/[\W]/g, '')
      this.changeForm.nickname = this.changeForm.username
    },
    // 提交注册
    submitChangeForm (formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.loading = true
          let oldPass = sha1(this.changeForm.oldPass)
          let newPass = sha1(this.changeForm.newPass)
          return http({
            url: '/sys/changePassword', // 调用修改密码接口
            method: 'POST',
            data: {
              username: this.changeForm.username,
              oldPass: oldPass,
              newPass: newPass // this.changeForm.newPass
            }
          }).then(res => {
            this.loading = false
            console.log('res', res)
            if (res.success) {
              this.$message({
                message: res.message,
                type: 'success'
              })
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
      this.changeForm.verifyCode = ''
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
.changeFormInput {
  width: 100%;
  max-width: 520px;
  margin: 0 auto;

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
    margin-bottom: 28px;

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
}
</style>
<style>
.login-container .changeFormInput .el-input--mini .el-input__inner {
  height: 38px !important;
  line-height: 38px !important;
  min-height: 38px !important;
  color: #666;
}

.changeFormInput .el-button--small {
  padding: 12px 15px;
}

.changeFormInput .validCodeInpu .el-input-group__append {
  padding: 0 !important;
  width: 6rem;
  height: 38px;
}

.changeFormInput .validCodeInpu .el-input-group__append .reg-code-img {
  display: block;
  width: 100%;
  height: 36px;
  text-align: center;
  margin-left: 1px;
  background-color: #1890ff;
  color: #fff;
}

.changeFormInput .doAgrrent-div {
  margin-bottom: 12px;
}
</style>
