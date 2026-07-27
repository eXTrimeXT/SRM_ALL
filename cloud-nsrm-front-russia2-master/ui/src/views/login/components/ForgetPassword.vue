<template>
  <div class="regFormInput">
    <el-tabs
      v-model="showTab"
      stretch
    >
      <el-tab-pane
        :label="$t('vendorMod.emailType')"
        name="email"
      >
        <el-form
          ref="email"
          :model="regForm"
          :rules="rules"
          status-icon
          class="form"
          label-width="80px"
        >
          <el-row>
            <el-col>
              <el-form-item
                prop="username"
                :label="$t('vendorMod.account')"
                class="accountForm"
              >
                <el-input v-model="regForm.username" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item
            prop="verifyCode"
            :label="$t('vendorMod.verifyCode')"
          >
            <el-input
              v-model="regForm.verifyCode"
              class="validCodeInpu"
            >
              <template slot="append">
                <img
                  :src="verifyCode.src"
                  class="reg-code-img"
                  @click="refreshCode"
                >
              </template>
            </el-input>
          </el-form-item>
          <div class="tip">
            *{{ $t('vendorMod.forgotPassTipEmail') }}
          </div>
        </el-form>
      </el-tab-pane>
      <!-- <el-tab-pane :label="$t('vendorMod.phoneType')" name="phone">
        <el-form
          ref="phone"
          :model="regForm"
          :rules="rules"
          status-icon
          class="form"
          label-width="80px"
        >
          <el-row>
            <el-col>
              <el-form-item prop="username" :label="$t('vendorMod.account')">
                <el-input v-model="regForm.username" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item prop="verifyCode" :label="$t('vendorMod.verifyCode')">
            <el-input v-model="regForm.verifyCode" class="validCodeInpu">
              <template slot="append">
                <img
                  :src="verifyCode.src"
                  class="reg-code-img"
                  @click="refreshCode"
                />
              </template>
            </el-input>
          </el-form-item>
          <div class="tip">*{{$t('vendorMod.forgotPassTipEmail')}}</div>
        </el-form>
      </el-tab-pane> -->
    </el-tabs>
    <el-row
      type="flex"
      justify="center"
      align="middle"
      style="padding-top: 10px;"
    >
      <el-button
        id="loginBtn"
        type="primary"
        :loading="loading"
        @click="submitRegForm"
      >
        {{ $t('common.submit') }}
      </el-button>
      <el-button
        @click="cancle"
      >
        {{ $t('common.cancel') }}
      </el-button>
    </el-row>
  </div>
</template>

<script>
import { randomLenNum } from 'lib@/utils/util'
import http from '@/utils/axios/http'
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
    // 验证码校验
    const validateCode = (rule, value, callback) => {
      if (!value) {
        return callback(new Error(this.$t('vendorMod.enterCode')))
      } else {
        this.validCodeHandle(value, callback)
      }
    }
    return {
      loading: false,
      apiList: {
        email: '/api-rbac/rbac-anon/user/resetUserPwByEmail',
        phone: ''
      },
      showTab: 'email',
      regForm: {
        username: '',
        verifyCode: ''
      },
      rules: {
        username: [{ required: true, validator: checkUsername }],
        verifyCode: [{ required: true, validator: validateCode }]
      },
      verifyCode: {
        src: `${this.$systemUrl}${sysPrefix()}/api-sup/register/getVerifyCode`,
        value: '',
        len: 4,
        message: this.$t('vendorMod.errorCode')
      }
    }
  },
  methods: {
    cancle () {
      this.$emit('visible', false)
    },
    // 提交注册
    submitRegForm () {
      const formName = this.showTab
      this.$refs[formName].validate(valid => {
        this.loading = true
        if (!valid) return false
        http({
          url: this.apiList[this.showTab],
          method: 'POST',
          data: {
            username: this.regForm.username,
            verifyCode: this.regForm.verifyCode
          }
        })
          .then(res => {
            if (res.success) {
              this.$message({
                message: res.message,
                type: 'success'
              })
              this.$emit('visible', false)
            } else {
              this.$message({
                message: res.message,
                type: 'error'
              })
            }
            this.loading = false
          })
          .catch(() => {
            this.loading = false
          })
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
            callback()
          })
          .catch(() => {
            callback(new Error(this.$t('vendorMod.verifyErrorCode')))
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
    }
  }
}
</script>

<style lang="scss" scoped>
.regFormInput {
  padding: 0 0 20px 0;
  .tip {
    display: flex;
    align-items: center;
    justify-content: center;
    padding-top: 10px;
    color: red;
  }
  .form {
    padding: 15px;
    width: 100%;
    :deep(.accountForm){
      .el-form-item__label{
        width: 60px !important;
        text-align: center;
      }
      .el-form-item__content{
        margin-left: 60px !important;
      }
    }
  }
}

</style>
<style>
.regFormInput .el-tabs__header {
  padding: 0 100px;
}
</style>
