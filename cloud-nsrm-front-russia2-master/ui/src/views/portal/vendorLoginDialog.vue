<template>
  <el-dialog
    width="380px"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
    center
  >
    <div slot="title">
      <div class="login-title">
        <ThemeConf
          themeType="text"
          textType="webName"
        />
      </div>
    </div>
    <div class="single-point-login">
      <!-- 美擎SRM管理平台 -->
      <!-- <div class="login-title">
        <ThemeConf
          themeType="text"
          textType="webName"
        />
      </div> -->
      <div class="iamLogin">
        <!-- <div class="splitline" /> -->
        <div style="width:86%; margin:20px auto">
          <el-form
            ref="vendorLoginForm"
            :model="vendorLoginForm"
            :rules="vendorLoginFormRules"
            class="login-form"
            autocomplete="off"
            label-position="left"
          >
            <!-- 用户名 -->
            <el-form-item prop="username" class="form-user">
              <el-input
                ref="username"
                v-model="vendorLoginForm.username"
                :placeholder="$t('vendorMod.userName')"
                name="username"
                type="text"
                tabindex="1"
                autocomplete="off"
                :showWordLimit="false"
              >
                <template #prefix>
                  <img
                    src="../../assets/logo/user/icon-user.svg"
                    class="input-icon__user"
                  >
                </template>
              </el-input>
            </el-form-item>
            <!-- 密码 -->
            <el-tooltip
              v-model="capsTooltip"
              content="Caps lock is On"
              placement="right"
              manual
            >
              <el-form-item prop="password" class="form-pwd">
                <el-input
                  :key="passwordType"
                  ref="password"
                  v-model="vendorLoginForm.password"
                  :type="passwordType"
                  :placeholder="$t('vendorMod.pass')"
                  name="password"
                  tabindex="2"
                  autocomplete="off"
                  @keyup.native="checkCapslock"
                  @blur="capsTooltip = false"
                  @keyup.enter.native="vendorLogin"
                >
                  <template #prefix>
                    <img
                      src="../../assets/logo/password/icon-password.svg"
                      class="input-icon__password"
                    >
                  </template>
                </el-input>
                <span
                  class="show-pwd"
                  @click="showPwd"
                >
                  <svg-icon
                    :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'"
                  />
                </span>
              </el-form-item>
            </el-tooltip>
            <el-button
              :loading="loading"
              type="primary"
              class="loginBtn"
              @click.native.prevent="vendorLogin"
            >
              {{ $t("vendorMod.login") }}
            </el-button>
          </el-form>
          <div class="btns">
            <div class="btn" @click="forgetPassword">
              {{ $t("vendorMod.forgotPass") }}
            </div>
            <div class="btn" @click="handleReg">
              {{ $t("vendorMod.register") }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script>
/**
 * 供应商登录
 */
import { mapMutations, mapActions, mapGetters, mapState } from 'vuex'
import { getToken, removeRedirectUrl, setRedirectUrl, getRedirectUrl, setEntranceType, setToken } from '@/utils/auth'
import ThemeConf from '@/components/themeConf'
import * as path from '@/utils/path'
import { dataEncryption } from '@/utils/secret'
import { getPassPublicKey } from '@/api/user'
import qs from 'qs'
export default {
  name: 'VendorLoginDialog',

  components: {
    ThemeConf
  },

  props: {
    visible: {
      type: Boolean,
      default: false
    },
    projectInfo: {
      type: Object,
      default: () => {}
    }
  },

  data () {
    return {
      vendorLoginForm: {
        username: '',
        password: ''
      },
      vendorLoginFormRules: {
        username: [{ required: true, message: this.$t('vendorMod.enterUserName') }],
        password: [{ required: true, message: this.$t('vendorMod.enterPass') }]
      },
      loading: false,
      passwordType: 'password',
      capsTooltip: false
    }
  },

  computed: {
    ...mapGetters(['userInfo', 'languageList', 'sysOpenConfig']),
    ...mapState({
      // 注册码
      appRegisterCode: state => {
        return state.app.appRegisterCode
      }
    }),
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },

  methods: {
    forgetPassword () {
      if (!this.checkAppRegisterCode()) return
      this.$router.push({ path: '/forgetPassword' })
    },
    changePassword () {
      this.showChangePasswordDialog = true
    },
    checkCapslock ({ shiftKey, key } = {}) {
      if (key && key.length === 1) {
        if (
          (shiftKey && key >= 'a' && key <= 'z') ||
          (!shiftKey && key >= 'A' && key <= 'Z')
        ) {
          this.capsTooltip = true
        } else {
          this.capsTooltip = false
        }
      }
      if (key === 'CapsLock' && this.capsTooltip === true) {
        this.capsTooltip = false
      }
    },
    showPwd () {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.password.focus()
      })
    },
    // 检查应用是否过期
    checkAppRegisterCode () {
      // 有appRegisterCode 说明应用过期
      if (this.appRegisterCode) {
        // 请检查授权信息是否正确', '产品授权失败
        this.$confirm(this.$t('cusEntry.login.checkAuth'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'error',
          showClose: false,
          showCancelButton: false,
          closeOnClickModal: false
        }).then(() => {
          location.reload()
        })
        return false
      } else {
        return true
      }
    },
    // 供应商登录跳转
    vendorLogin () {
      let href = window.location.href.split('#')[1]
      setRedirectUrl(decodeURI(href))

      setEntranceType('singlePoint') // 设置类型
      let _this = this
      this.$refs.vendorLoginForm.validate(async (valid) => {
        if (valid) {
          const { data } = await getPassPublicKey()
          this.passPublicKey = data
          let resData = await this.$http({
            url: '/api-rbac/rbac-anon/sys/pj/login',
            method: 'POST',
            data: qs.stringify({
              language: this.$i18n.locale,
              username: this.vendorLoginForm.username.trim(),
              password: dataEncryption(this.vendorLoginForm.password, this.passPublicKey)
            }),
            loading: true
          })
          if (resData) {
            // 设置语言
            this.$store.dispatch('app/setLanguage', this.$i18n.locale)
            let token = resData.data?.value
            setToken(token)
            this.$store.commit('user/SET_TOKEN', token)
            await this.$store.dispatch('app/getServeLang')
            await this.$store.dispatch('user/initSystem')
            _this.dialogVisible = false
            _this.$router.push({ path: path.resolve('/login') })
          }
          // if (resData.data.userType) {
          //   await this.$store.dispatch('user/initSystem')
          //   _this.dialogVisible = false
          //   _this.$router.push({ path: path.resolve('/login') })

            // if (this.projectInfo.flag == 'sign') {
            //   // 跳转到报名详情页
            //   this.$router.push({
            //     name: 'sourcingCooperation',
            //     params: {
            //       from: 'portal',
            //       row: {
            //         id: this.projectInfo.id,
            //         formNo: this.projectInfo.formNo
            //       }
            //     }
            //   })
            // } else if (this.projectInfo.flag == 'bid') {
            //   // 跳转到质疑澄清页面
            //   this.$router.push({
            //     name: 'biddingQas',
            //     params: {
            //       from: 'portal', // 来源路由name
            //       flag: 'bid',
            //       row: {
            //         row: {
            //           souName: this.projectInfo.souName,
            //           projectId: this.projectInfo.projectId
            //         },
            //         extType: 'REQ'
            //       }
            //     }
            //   })
            // }
          // }
        }
      })
    },
    iamForgetPassword () {
      if (!this.checkAppRegisterCode()) return
      // this.$router.push({ path: '/forgetPassword' })
      let singleBaseUrl = this.$systemUrl + '/'
      let service = encodeURI(singleBaseUrl)
      window.location.href = `${this.sysOpenConfig.iamSysBaseUrl}/portal/index.html#/forgetpassword?redirectUri=${service}`
    },
    // 注册账号
    handleReg (regType) {
      // 弹框模式(旧模式) 对应系统参数配置里面 REGISTER_VERSION
      if (this.sysOpenConfig.registerVersion == 'old') {
        this.showDialog = true
      } else { // 页面跳转
        if (regType == 'invite') { // 如果是邀请供应商的时候
          this.$router.push({ path: '/registered', query: { regType: 'invite' } })
        } else {
          this.$router.push({ path: '/registered' })
        }
      }
    }
  }
}
</script>
<style lang="scss" scoped>
::v-deep .el-dialog .el-dialog__header {
  padding: 55px 40px 55px 40px !important;
}
::v-deep .el-dialog__body {
  padding: 10px 20px !important;
}
.login-form {
  ::v-deep .el-input--prefix .el-input__inner {
    height: 38px !important;
    padding-left: 48px !important;
  }
}
.single-point-login{
  // position: absolute;
  margin: auto;
  width: 340px;
  // height: 316px;
  max-width: 100%;
  overflow: hidden;
  right: 104px;
  // padding: 0 0 10px;
  box-sizing: border-box;
  background-color: rgba(255,255,255,0.8);
  border-radius: 4px;
  // transform: translate(-0%,-50%);
  top: 50% !important;
  z-index: 10;
  .pd-30 {
    padding: 0 30px;
  }
  .iamLogin{
    padding-bottom: 8px;
    .tabs_box {
      ::v-deep .el-tabs__item {
        font-size: 16px;
        color: #51555B;
      }
      ::v-deep .el-tabs__item.is-active {
        color: #0077FF;
      }
    }
    .splitline {
      height: 1px;
      background: #4B517A;
    }
    .btns {
      display: flex;
      display: -webkit-flex;
      align-items: center;
      justify-content: space-between;
      margin-top: 16px;
      // padding: 0 7%;
      .btn {
        font-size: 14px;
        height: 22px;
        line-height: 22px;
        color: #51555B;
        cursor: pointer;
      }
    }
    .btn-login {
      padding:0 30px;
    }
    .el-button{
      width: 100%;
    }
  }
}
.form-user {
  margin-bottom: 16px !important;
  .input-icon__user {
    width: 16px;
    height: 16px;
    margin-top: 8px;
    box-sizing: content-box;
    padding: 3px 10px 3px 7px;
    border-right: 1px solid  rgba(0,0,0,0.15);
  }
}
.form-pwd {
  margin-bottom: 16px !important;
  .input-icon__password {
    width: 16px;
    height: 16px;
    margin-top: 8px;
    box-sizing: content-box;
    padding: 3px 10px 3px 7px;
    border-right: 1px solid  rgba(0,0,0,0.15);
  }
}
.form-code {
  margin-bottom: 16px;
}
.form-account {
  .el-form-item__content{
    overflow: hidden;
  }
  span {
    font-size: 12px;
    &.spanBtn{
      float:right;
      cursor: pointer;
      color: #0077FF;
      line-height: 30px;
    }
    &.division-line {
      float: right;
      width:1px;
      height:14px;
      background:rgba(0,0,0,0.15);
      margin:8px 16px 0;
    }
    &.division-line2 {
      float: right;
      width:1px;
      height:14px;
      background:rgba(0,0,0,0.15);
      margin:8px 2px 0;
    }
  }
  .vendor-account {
    color:#393E45;
    float: left;
    :deep(span) {
      font-size: 12px;
      font-weight: normal;
    }
  }
}
.login-title {
  font-size: 24px;
  line-height: 26px;
  // margin-bottom: 16px;
  color:#393E45;
  .backHone{
    font-size: 14px;
    float: right;
    line-height: 26px;
    cursor: pointer;
    &:hover{
      color: #1890ff;
    }
  }
}
.login-top-sec{
  zoom: 1;
  width: 100%;
}
.login-contain{
  position: relative;
  width: 85%;
  height: 95%;
  max-width: 1252px;
  margin: 0 auto;
  z-index: 3;
}
.show-pwd {
  position: absolute;
  right: 10px;
  top: 10px;
  font-size: 16px;
  cursor: pointer;
  user-select: none;
  width: 16px;
  height: 16px;
  line-height: 16px;
}
.loginBtn {
  display: block;
  width: 100%;
  padding: 11px 15px;
  font-size: 16px;
  margin-bottom: 10px;
  border-radius: 4px;
}
</style>
