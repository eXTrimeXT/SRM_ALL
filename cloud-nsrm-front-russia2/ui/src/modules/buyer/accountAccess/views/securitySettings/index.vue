<template>
  <el-container
    class="flex-container-notab securitySettings-page"
    direction="vertical"
    style="
      flex-grow: 1;
      display: flex;
      flex-direction: row;
      height: 100%;
      padding: 0 0 0px !important;
    "
  >
    <el-aside class="left-page">
      <div class="left-page-title">
        配置目录
      </div>
      <ul class="left-page-menu">
        <!-- 密码管理 -->
        <li
          :class="{'current': activeTabName=='passwordManagement'}"
          @click="securitySettingsMenuSelect('passwordManagement')"
        >
          {{ $t('securitySettings.passwordManagement') }}
        </li>
        <!-- 防爆力破解 -->
        <li
          :class="{'current': activeTabName=='explosionProofForceCracking'}"
          @click="securitySettingsMenuSelect('explosionProofForceCracking')"
        >
          {{ $t('securitySettings.explosionProofForceCracking') }}
        </li>
        <!-- 二次认证 -->
        <li
          :class="{'current': activeTabName=='secondaryAuthenticationConfig'}"
          @click="securitySettingsMenuSelect('secondaryAuthenticationConfig')"
        >
          二次认证
        </li>
        <!-- 认证异常管理 -->
        <li
          :class="{'current': activeTabName=='authenticationExceptionManagement'}"
          @click="securitySettingsMenuSelect('authenticationExceptionManagement')"
        >
          <!-- 认证异常管理 -->
          {{ $t("cusEntry.supplement20250211.authExceptionManagement") }}
        </li>
      </ul>
    </el-aside>
    <el-main class="right-page">
      <!-- 密码管理 -->
      <PasswordManagement v-if="activeTabName=='passwordManagement'" />
      <!-- 防爆力破解 -->
      <AntiBruteForceCracking
        v-if="activeTabName=='explosionProofForceCracking'"
        :configDate="violenceConfigDate"
        @saveSuccess="saveSuccessFn"
      />
      <!-- 二次认证 -->
      <SecondaryAuthenticationConfig
        v-if="activeTabName=='secondaryAuthenticationConfig'"
      />
      <!-- 认证异常管理 -->
      <AuthenticationExceptionManagement
        v-if="activeTabName=='authenticationExceptionManagement'"
      />
    </el-main>
  </el-container>
</template>

<script>
import CToolbar from 'lib@/components/c-toolbar'
import PasswordInput from 'lib@/components/passwordInput'
import PasswordManagement from './passwordManagement'
import AntiBruteForceCracking from './antiBruteForceCracking'
import AuthenticationExceptionManagement from './authenticationExceptionManagement'
import SecondaryAuthenticationConfig from './secondaryAuthenticationConfig'
import { securitySettingsApi } from 'modb@/accountAccess/api'

export default {
  name: 'SecuritySettings',
  components: {
    CToolbar,
    PasswordManagement,
    AntiBruteForceCracking,
    AuthenticationExceptionManagement,
    SecondaryAuthenticationConfig,
    PasswordInput
  },
  data () {
    return {
      activeTabName: 'passwordManagement',
      activeCollapsePass: ['1', '2', '3', '4', '5', '6', '7', '8'],
      activeCollapseForce: ['1', '2', '3', '4', '5', '6', '7', '8'],
      configForm: {
        useDefault: 'Y', // 启用默认配置
        userType: 'BUYER', // BUYER | VENDOR
        pwdCreated: { // 创建密码
          sourceType: 'DEFAULT', // 用户来源类型: DEFAULT | REGISTER | VENDOR_GREEN | VENDOR_OPENAPI
          sourceTypeObj: { // 对应用户来源类型的设置值
            switcher: 'RANDOM', // 策略: RANDOM 随机 | SOLID 固定
            value: '', // 固定密码的值
            length: 8, // 随机密码时指定的密码长度
            containDigit: 'Y', // 随机密码包含数字: Y | N
            containLowerLetter: 'Y', // 随机密码包含小写字母: Y | N
            containUpperLetter: 'Y', // 随机密码包含大写字母: Y | N
            containSpecialLetter: 'Y' // 随机密码包含特殊字符:Y | N
          }
        },
        pwdCheck: { // 密码校验策略
          cannotUsedAmount: 1, // 不能使用最近的几个密码
          minLength: 8, // 最小长度，包含：>=minLength
          maxLength: 16, // 最大长度，包含：<=maxLength
          containDigit: 'Y', // 随机密码包含数字:Y/N
          containLowerLetter: 'Y', // 随机密码包含小写字母:Y/N
          containUpperLetter: 'Y', // 随机密码包含大写字母:Y/N
          containSpecialLetter: 'Y', // 随机密码包含特殊字符:Y/N
          notContainColumn: '' // 不能包含的字段
        },
        loginCheck: { // 密码校验
          forceChangeInitPwd: 'N', // 是否强制修改初始密码
          pwdTimeoutDays: 30 // 密码过期天数
        },
        autoExpiredCheck: { // 自动过期设置
          pwdTimeoutDays: 30, // 密码过期天数
          smsReminder: 'Y', // 短信提醒
          emialReminder: 'Y' // 邮件提醒
        },
        loginViolenceCheck: { // 锁定类型：TIME:锁定时间 | ACCOUNT:锁定账号
          // 'TIME': {},
          // 'ACCOUNT': {},
          lockObj: {
            lessTimeSecond: 600, // 600秒以内认证失败，单位秒
            failAmount: 5, // 认证失败次数
            lockSecond: 300 // lockType=TIME时有效 认证失败达到次数后锁定秒数，单位秒
          }
        }
      },
      violenceConfigDate: {}
    }
  },
  async created () {
  },
  methods: {
    securitySettingsMenuSelect (item) {
      this.activeTabName = item
      if (item === 'explosionProofForceCracking') {
        this.getViolenceConf()
      }
    },
    //  获取防爆力配置数据
    async getViolenceConf () {
      const { data } = await securitySettingsApi.getViolence()
      this.violenceConfigDate = data.loginViolenceCheck
    },
    saveSuccessFn (isSuccess, type) {
      if (isSuccess) {
        if (type === 'violence') {
          this.getViolenceConf()
        }
      }
    }
  }
}
</script>

<style lang="scss">
.securitySettings-page{
  .left-page{
    width: 180px !important;
    height: 100%;
    background: #fff;
    border-right: 1px solid #dedede;
    margin: 0;
    padding: 0;
    overflow: hidden;
    .left-page-title{
      font-size: 14px;
      color: #161c24;
      line-height: 22px;
      font-weight: 600;
      margin: 0 0 10px;
      padding: 8px 16px 8px;
      text-align: left;
      background-color: #f4f5f7;
    }
    .left-page-menu{
      padding: 0;
      list-style: none;
      margin: 0;
      li{
        margin: 0;
        padding: 8px 16px;
        font-size: 14px;
        line-height: 24px;
        color: #161C24;
        cursor: pointer;
        &:hover{
          background-color: #F6F6F6;
        }
        &.current{
          background-color: #ebf4ff;
          color: #0077ff;
        }
      }
    }
  }
  .right-page{
    padding: 0x !important;
    width: calc(100% - 180px) !important;
    height: 100% !important;
    overflow: hidden;
  }
}

</style>
