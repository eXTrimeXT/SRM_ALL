<template>
  <el-container
    class="flex-container-notab porfile-wap-pass"
    direction="vertical"
  >
    <el-main>
      <div class="sec-page-title">
        {{ $t('base.navbar.updatePassword') }}
      </div>
      <el-row class="updata-pass">
        <el-col
          :xs="24"
          :sm="24"
          :md="14"
          :lg="14"
          :xl="14"
        >
          <el-form
            ref="relModel"
            class="update-pass-form"
            label-width="100px"
            :rules="passwordRules"
            :model="password"
          >
            <el-row :gutter="32" style="margin-bottom:8px;">
              <el-col>
                <!-- 旧密码 -->
                <el-form-item :label="$t('vendorMod.oldPass')" prop="oldItem">
                  <PasswordInput v-model="password.oldItem" type="password" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="32" style="margin-bottom:8px;">
              <el-col>
                <!-- 新密码 -->
                <el-form-item :label="$t('vendorMod.newPass')" prop="newItem">
                  <PasswordInput v-model="password.newItem" type="password" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="32" style="margin-bottom:8px;">
              <el-col>
                <!-- 确认新密码 -->
                <el-form-item :label="$t('vendorMod.newPassConfirm')" prop="copyNewItem">
                  <PasswordInput v-model="password.copyNewItem" type="password" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="32" style="margin-bottom:24px;">
              <el-col>
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
              </el-col>
            </el-row>
            <el-row :gutter="32">
              <el-col style="text-align: left;padding-left: 116px;">
                <el-button type="primary" :loading="loading" @click="modifyUserPassword">
                  {{ $t('common.save') }}
                </el-button>
                <el-button @click="cancelUpdate">
                  {{ $t('common.cancel') }}
                </el-button>
              </el-col>
            </el-row>
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
            <p>{{ $t('passwordManagement.notContainArr') }}{{ pwdCheckConfig.notContainStr }}</p>
            <p>{{ $t('passwordManagement.noeUseBeforNo') }}{{ pwdCheckConfig.cannotUsedAmount }}{{ $t('passwordManagement.usedPass') }}</p>
          </div>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>
<script>
import { mapState } from 'vuex'
import * as path from '@/utils/path'
import { userOptApi } from 'mod@/common/userManage/api'
import { singlePointLogoutUrl } from '@/config/sysConfig'
import { getPassPublicKey, getVendorPwdCheckConfig, getBuyerPwdCheckConfig } from '@/api/user'
import { dataEncryption } from '@/utils/secret'
import PasswordInput from 'lib@/components/passwordInput'
import { notContainFn, validPatrnObj } from '@/utils/passValid'

export default {
  name: 'Profile',
  components: { PasswordInput },
  data () {
    const validator = (rule, value, callback) => {
      // eslint-disable-next-line no-useless-escape
      let checkConfig = this.pwdCheckConfig
      let validCount = true
      let resMessage = ''
      if (!value) {
        this.passCheckErr = {
          containDigit: '',
          containLowerLetter: '',
          containSpecialLetter: '',
          containUpperLetter: '',
          notMathLength: '',
          notMathFiled: ''
        }
        callback(new Error(this.$t('common.pleaseInput')))
        return
      } else {
        // 长度判断
        if (value.length < checkConfig.minLength || value.length > checkConfig.maxLength) {
          resMessage = this.$t('passwordManagement.passLangth') + checkConfig.minLength + '-' + checkConfig.maxLength
          // "密码至少包含数字、大小写字母、特殊字符，长度为8~24位"
          this.passCheckErr.notMathLength = 'N'
        } else {
          this.passCheckErr.notMathLength = 'Y'
        }
        // 判断是否包含数字
        if (checkConfig.containDigit == 'Y') {
          let patrn = validPatrnObj.containDigit
          if (!patrn.test(value)) {
            resMessage = this.$t('cusEntry.supplement20250205.passwordValid2') // 密码需要包含数字
            this.passCheckErr.containDigit = 'N'
          } else {
            this.passCheckErr.containDigit = 'Y'
          }
        }
        // 小写字母判断
        if (checkConfig.containLowerLetter == 'Y') {
          let patrn = validPatrnObj.containLowerLetter
          if (!patrn.test(value)) {
            resMessage = this.$t('cusEntry.supplement20250205.passwordValid3') // 密码需要包含小写字母
            this.passCheckErr.containLowerLetter = 'N'
          } else {
            this.passCheckErr.containLowerLetter = 'Y'
          }
        }
        // 大写字母判断
        if (checkConfig.containUpperLetter == 'Y') {
          let patrn = validPatrnObj.containUpperLetter
          if (!patrn.test(value)) {
            resMessage = this.$t('cusEntry.supplement20250205.passwordValid4') // 密码需要包含大写字母
            this.passCheckErr.containUpperLetter = 'N'
          } else {
            this.passCheckErr.containUpperLetter = 'Y'
          }
        }
        // 特殊字符判断
        if (checkConfig.containSpecialLetter == 'Y') {
          let patrn = validPatrnObj.containSpecialLetter
          if (!patrn.test(value)) {
            resMessage = this.$t('cusEntry.supplement20250205.passwordValid5') // 密码需要特殊字符
            this.passCheckErr.containSpecialLetter = 'N'
          } else {
            this.passCheckErr.containSpecialLetter = 'Y'
          }
        }

        if (resMessage) {
          callback(new Error(this.$t('cusEntry.supplement20250205.passwordValid6'))) // 请按照密码规则输入
          return
        }
      }

      // 新旧密码判断
      if (rule.field == 'copyNewItem') {
        if (value !== this.password.newItem) {
          callback(new Error(this.$t('vendorMod.confirmPassError')))// "新密码与确认新密码不一致！"
          return
        }
      }

      callback()
    }

    return {
      sysOpenConfig: this.$store.getters.sysOpenConfig,
      activeName: 'updatePassword',
      loading: false,
      userInfo: {},
      password: {
        oldItem: null,
        newItem: null,
        copyNewItem: null
      },
      passwordRules: {
        oldItem: [{ required: true, message: this.$t('vendorMod.msgOldPass') }], // "请填旧密码"
        newItem: [
          { required: true, message: this.$t('vendorMod.msgNewPass') }, // "请填写新密码"
          { validator: validator, trigger: 'blur' }
        ],
        copyNewItem: [
          { required: true, message: this.$t('vendorMod.msgNewPassConfirm') } // "请填写确认新密码"
        ]
      },
      pwdCheckConfig: {
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
    ...mapState({
      entrance: (state) => state.user.entrance,
      visitedViews: (state) => state.tagsView.visitedViews
    })
  },
  async created () {
    const info = this.$store.state.user.userInfo || {}
    this.userInfo = {
      ...info,
      rolePermissions: (info.rolePermissions || []).map(i => i.roleName).join(',')
    }
    await this.getPassRules()
  },
  methods: {
    // 获取密码规则
    //  采购商和供应商用不同的查询规则接口
    async getPassRules () {
      let config = {}
      if (this.userInfo.userType == 'BUYER') {
        const { data } = await getBuyerPwdCheckConfig()
        config = data
      } else {
        const { data } = await getVendorPwdCheckConfig()
        config = data
      }
      this.pwdCheckConfig = config
      this.pwdCheckConfig['notContainStr'] = notContainFn(config.notContainColumn)
    },
    // 修改密码
    async modifyUserPassword () {
      const { data } = await getPassPublicKey()
      const { userId } = this.userInfo
      const { newItem, oldItem, copyNewItem } = this.password
      let sha1OldItem = dataEncryption(oldItem, data)
      let sha1CopyNewItem = dataEncryption(copyNewItem, data)
      this.$refs.relModel.validate(status => {
        if (status) {
          this.loading = true
          userOptApi.modifyPassword({
            oldPass: sha1OldItem,
            newPass: sha1CopyNewItem
          }).then(res => {
            this.loading = false
            this.$message({
              type: 'success',
              message: this.$t('vendorMod.successResetPass')// "密码重置成功，请重新登录！"
            })
            this.logout()
          }).catch(() => (this.loading = false))
        }
      })
    },
    // 内部退出
    innerLogout () {
      this.$store.dispatch('user/getLogout').then(res => {
        if (res) {
          this.$router.push({ path: path.resolve('/login') })
        }
      })
    },
    // 单点登录退出
    singlePointLogout () {
      this.$store.dispatch('user/resetToken').then(() => {
        window.location.href = singlePointLogoutUrl() // 调用单点登录的退出接口
      })
    },
    /* 退出登录 */
    logout () {
      if (this.sysOpenConfig.loginSystem == 'srm') { // 内部页面登录后退出
        this.innerLogout()
      } else if (this.sysOpenConfig.loginSystem == 'iam') { // 单点登录退出 保留原来登录方式 退出的方式从哪里进来就回到哪里
        this.singlePointLogout()
      }
    },
    handleClick (tab, event) {
      console.log(tab, event)
    },
    cancelUpdate () {
      this.password.oldItem = null
      this.password.newItem = null
      this.password.copyNewItem = null
      this.tabRemoveHandle(this.$route.fullPath)
    },
    tabRemoveHandle (tabName) {
      // 找到tab对象
      const findTab = this.visitedViews.find(tag => tag.fullPath === tabName)
      if (findTab) {
        this.$store.dispatch('tagsView/delView', findTab).then(({ visitedViews }) => {
          // 如果不是当前
          if (findTab.fullPath === this.$route.fullPath) {
            const latestView = visitedViews.slice(-1)[0]
            if (latestView) {
              this.$router.push(latestView.fullPath)
            } else {
              this.$router.push('/dashboard')
            }
          }
        })
      }
    }
  }
}
</script>
<style lang="scss">
.porfile-wap-pass {
  margin: 16px;
  padding: 0px !important;
  .sec-page-title{
    font-size: 16px;
    color: #161C24;
    line-height: 24px;
    height: 48px;
    font-weight: 600;
    border-bottom: 1px solid #E8E9EA;
    padding: 12px 16px;
    margin-bottom: 40px;
  }
  .el-input.el-input--small{
      .el-input__inner {
        height: 32px !important;
        line-height: 32px !important;
        min-height: 32px !important;
        font-size: 14px;
        color: #51555B;
        border: 1px solid #DCDDDE;
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
    }
  .update-pass-form{
    width: 420px;
    margin: 0px 48px 50px auto;
  }
  .pwd-check-config{
    padding-left: 100px;
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
@media only screen and (max-width: 768px) {
  .porfile-wap-pass{
    .updata-pass{
      .el-col-sm-24{
        .update-pass-form {
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
