<template>
  <el-container
    v-if="notwindowOpener"
    :class="['login-container',{'isPortalSourcing': portalSourcing === 'Y' || portalBidding === 'Y'}]"
  >
    <el-main :class="{ 'login-main-body': isPC, 'login-main-body-mobile': !isPC }">
      <!-- <AppHeader v-if="userInfo && userInfo.userId" pageType="gateway" /> -->
      <div class="login-top-sec">
        <div v-if="!isPC" class="bg-blur" />
        <!-- loginBanner 图片 -->
        <el-carousel
          v-else
          class="loginBanner"
          :interval="5000"
        >
          <el-carousel-item
            v-for="(item, index) in logoInfo.loginBanner"
            :key="'banner_'+ index"
            :style="{'backgroundImage': 'url('+ item +')'}"
          />
        </el-carousel>
        <div class="login-contain">
          <!-- logo | 汇聚全球商机，缔造供需共赢 -->
          <div class="logo">
            <ThemeConf
              :lang="$i18n.locale"
              themeType="img"
              imgType="mainLogo"
            />
            <!-- PC端且存在文字描述时才显示  && logoInfo.webDes-->
            <template v-if="isPC">
              <div class="vertical" />
              <!-- <ThemeConf
                :lang="$i18n.locale"
                class="font"
                themeType="text"
                textType="webDes"
              /> -->
            </template>
          </div>
          <!--切换语言-->
          <el-dropdown
            trigger="hover"
            class="language-dropdown-wrap"
            placement="bottom"
            @command="handleSetLanguage"
          >
            <div class="iconfont-button">
              <i class="iconfont iconearth themeLink" />
              <span style="float:right;line-height:18px;margin-left:7px;">{{ newLang }}</span>
            </div>
            <el-dropdown-menu slot="dropdown" class="header-menu-dropdown">
              <el-dropdown-item
                v-for="item in languageList"
                :key="item.value"
                :command="item.value"
                :disabled="item.value === language"
                :class="{'currentLang': item.value === language}"
              >
                {{ item.label }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <div v-if="(singlePoint==='N' || showLoginBox === 'Y' ) && (loginType === 'innerLogin' || loginType === 'workChatLogin')" class="form-div">
            <div class="login-title">
              <!-- {{ $t('route.login') }} -->
              <ThemeConf
                :lang="$i18n.locale"
                themeType="text"
                textType="webName"
              />
            </div>
            <el-form
              v-if="loginType === 'innerLogin'"
              ref="loginForm"
              :model="loginForm"
              :rules="loginRules"
              class="login-form"
              autocomplete="off"
              label-position="left"
            >
              <!-- 用户名 -->
              <el-form-item prop="username" class="form-user">
                <el-input
                  ref="username"
                  v-model="loginForm.username"
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
                    v-model="loginForm.password"
                    :type="passwordType"
                    :placeholder="$t('vendorMod.pass')"
                    name="password"
                    tabindex="2"
                    autocomplete="off"
                    @keyup.native="checkCapslock"
                    @blur="capsTooltip = false"
                    @keyup.enter.native="handleLogin"
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

              <el-form-item prop="verifyCode" class="form-code">
                <el-input
                  v-model="loginForm.verifyCode"
                  :placeholder="$t('vendorMod.enterCode')"
                  class="validCodeInpu"
                  :showWordLimit="false"
                  @keyup.enter.native="handleLogin"
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
              <el-button
                :loading="loading"
                type="primary"
                class="loginBtn"
                @click.native.prevent="handleLogin"
              >
                {{ $t("vendorMod.login") }}
              </el-button>
              <el-form-item prop="isRememberUser" class="form-account">
                <el-checkbox v-model="isRememberUser" class="vendor-account" :label="$t('common.rememberMe')" />
                <span class="spanBtn" @click="handleReg">{{ $t("vendorMod.register") }}</span>
                <span :class="divisionLine" />
                <!--division-line-->
                <span class="spanBtn" @click="forgetPassword">{{ $t("vendorMod.forgotPass") }}</span>
              </el-form-item>
            </el-form>
            <div v-else-if="loginType === 'workChatLogin'">
              <vue-qr :text="qrCode" :size="220" />
            </div>
            <!-- 其他登录方式 -->
            <div class="other-login-type">
              <el-divider class="other-type" content-position="center">
                {{ $t('cusEntry.login.otherMethods') }}
              </el-divider>
              <div class="type-content">
                <span v-if="loginType === 'workChatLogin'" type="text" @click="switchType">{{ $t('cusEntry.login.srmLogin') }}</span>
                <span type="text" @click="singlePointLogin">{{ $t('cusEntry.login.iamLogin') }}</span>
                <span v-if="loginType === 'innerLogin' && isWorkchatLogin === 'Y'" type="text" @click="workChatLogin">{{ $t('cusEntry.login.weChatLogin') }}</span>
                <span class="verify-face" @click="verifyFaceDialogClick">{{ $t("vendorMod.verifyFace") }}</span>
              </div>
            </div>
          </div>
          <div v-else class="single-point-login">
            <!-- 长城慧采云管理平台 -->
            <div class="login-title">
              <ThemeConf
                :lang="$i18n.locale"
                themeType="text"
                textType="webName"
              />
            </div>
            <!-- <div v-if="userInfo && userInfo.userId" class="pd-30">
              <p class="user-wellcom">
                {{ userInfo.nickname || '' }} 欢迎您！
              </p>
              <p>
                <el-button type="primary" @click="intoDashboard">
                  进入工作台
                </el-button>
              </p>
            </div> -->
            <div class="iamLogin">
              <!-- <div class="splitline" /> -->
              <!-- <el-tabs v-model="activeName" class="tabs_box" :stretch="true" @tab-click="tabHandleClick">
                <el-tab-pane :label="$t('login.supplierLogin')" name="first" />
                <el-tab-pane :label="$t('login.buyerLogin')" name="second" />
              </el-tabs> -->
              <div style="width:86%; margin:10px auto">
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
                    {{ $t('vendorMod.forgotPass') }}
                  </div>
                  <div class="btn" @click="handleReg">
                    {{ $t("vendorMod.register") }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- 公开寻源组件位置 -->
      <div v-if="portalSourcing === 'Y' || portalBidding === 'Y'" class="portal-sourcing-sec">
        <!-- <img v-if="userInfo && userInfo.userId" src="./img/bulletinBoard.jpg" style="display:block;width:100%;height:148px;" alt=""> -->
        <section class="ditialTitalAll" :class="{top:userInfo && userInfo.userId}">
          <div :class="bolTab == 0 ? 'blue' : ''" @click="tabChange(0)">
            {{ $t('common.publicSourcing') }}
          </div>
          <!-- 招募公示大厅 -->
          <!-- <div :class="bolTab == 1 ? 'blue' : ''" @click="tabChange(1)">
            {{ $t('cusEntry.dashboard.recruitmentHall') }}
          </div> -->
        </section>
        <PortalSourcing v-if="portalSourcing === 'Y' && bolTab == 0" :userInfo="userInfo" />
        <!-- 公开招标组件位置 -->
        <!-- <PortalBidding v-if="portalBidding === 'Y' && bolTab == 1" /> -->
      </div>
      <!-- 系统版本 -->
      <div class="login-footer">
        <div class="copy-right">
          <!-- Copyright © 2016-2023 | 长城汽车股份有限公司版权所有 | 冀ICP备05008632号 | 版本信息：V2.0 -->
          <!-- <span>{{ $t('cusEntry.dashboard.copyright') }}</span> -->
          <!-- <span>{{ logoInfo.copyright }}</span> -->
          <!-- <span class="version">{{ $t("vendorMod.copyrightVersion") }}</span> -->
        </div>
      </div>
    </el-main>
    <srm-dialog
      :title="$t('vendorMod.register')"
      :visible.sync="showDialog"
      size="small"
      :destroy-on-close="true"
      :close-on-click-modal="false"
    >
      <RegisteredDialog
        :isShow="showDialog"
        :verifyType="sysOpenConfig.verifyType"
        @show="regShowFun()"
        @regsuccess="regsuccessHandle"
      />
    </srm-dialog>
    <srm-dialog
      :title="$t('vendorMod.changePassword')"
      :visible.sync="showChangePasswordDialog"
      size="middle"
    >
      <ChangePassword @show="passShowFun()" />
    </srm-dialog>
    <srm-dialog
      :title="$t('vendorMod.forgotPass')"
      :visible.sync="showForgetPasswordDialog"
      :close-on-click-modal="false"
      size="middle"
    >
      <ForgetPassword @visible="forgetVisibleChange" />
    </srm-dialog>
    <srm-dialog
      :title="$t('vendorMod.welcomeWord')"
      append-to-body
      :visible.sync="tipDialogVisible"
      :show-close="false"
      size="middle"
      class="tipDialog"
      :close-on-click-modal="false"
    >
      <div class="text">
        {{ $t("vendorMod.hereYouOwn") }}
      </div>
      <div class="text textP">
        {{ $t("vendorMod.moreMajor") }}
      </div>
      <div class="text textP">
        {{ $t("vendorMod.moreEfficient") }}
      </div>
      <div class="text textP">
        {{ $t("vendorMod.moreSmart") }}
      </div>

      <div class="doAgrrent-div">
        <el-checkbox
          v-model="agreement"
          true-label="Y"
          false-label="N"
          style="margin-right: 10px;"
        />
        {{ $t("vendorMod.doAgree") }}
        <span
          class="doAgreement"
          @click="toUserProtocol"
        >{{
          $t("vendorMod.userSassDeal")
        }}</span>
        {{ $t("vendorMod.and") }}
        <span
          class="doAgreement"
          @click="toPrivacyProtocol"
        >{{
          $t("vendorMod.privacyDeal")
        }}</span>
      </div>
      <span
        slot="footer"
        class="dialog-footer"
      >
        <el-button @click="cancelHandel">{{ $t("common.cancel") }}</el-button>
        <el-button
          type="primary"
          @click="comfirmHandel"
        >{{
          $t("common.confirm")
        }}</el-button>
      </span>
    </srm-dialog>
    <srm-dialog
      :title="$t('vendorMod.verifyFace')"
      :visible.sync="showVerifyFaceDialog"
      size="middle"
    >
      <VerifyFaceDialog @visible="verifyFaceDialogVisibleChange" />
    </srm-dialog>
    <srm-dialog
      :title="$t('common.tips')"
      :visible.sync="pwdDialogVisible"
      size="small"
    >
      <span>{{ $t('common.passwordExpired') }}</span>
      <span
        slot="footer"
        class="dialog-footer"
      >
        <el-button
          type="primary"
          @click="handlePwdClose"
        >
          {{ $t('route.updatePassword') }}
        </el-button>
      </span>
    </srm-dialog>
    <!-- 注册成功弹窗 -->
    <srm-dialog
      :title="$t('vendorMod.register')"
      :visible.sync="regSucDialogVisible"
      size="small"
      class="register-dialog"
    >
      <div class="register-wrapper">
        <div class="img">
          <img class="icon-success" src="../../assets/images/success.png">
        </div>
        <div class="des">
          {{ $t('registered.regSuccess') }}
        </div>
        <div class="info">
          {{ $t('registered.regSuccessInfo') }}
        </div>
        <el-button v-if="sysOpenConfig.supplierAutoAuth" type="primary" class="btn" @click="regSuccessJump('companyInfoMaintain')">
          {{ $t('registered.regSuccessToEdit') }}
        </el-button>
        <el-button v-else type="primary" class="btn" @click="regSuccessJump('singlePointLogin')">
          {{ $t('registered.regSuccessToLogin') }}
        </el-button>
      </div>
    </srm-dialog>
    <!-- 智能助手 -->
    <!-- <div class="assistant-box">
      <div
        id="assistant"
        ref="assistant"
        v-loading="loadingFlag"
        class="assistant"
        :style="{ 'transition-duration': '0.1s','transform': `translate(${elementPosition.x}px, ${elementPosition.y}px)`,'cursor':`${isGrab}` }"
        @click.prevent="handleClick"
        @mousedown="onMousedown"
        @mouseup="onMouseup"
      >
        <div class="icon-tips-box" :class="{ 'hide': hideTips }">
          {{ typeContent }}<span class="blink-tag"> |</span>
        </div>
      </div>
      <div
        id="helperFrame"
        ref="helperFrame"
        class="frame-wrap"
        :class="{ 'show': showMyFrame, 'expand': expand, 'showPreview': showPreview, 'hide': !showMyFrame }"
      />
    </div> -->
  </el-container>
</template>

<script>
// eslint-disable-next-line no-unused-vars
import { mapMutations, mapActions, mapGetters, mapState } from 'vuex'
import { trimField } from '@/utils'
import RegisteredDialog from './components/RegisteredDialog'
import ChangePassword from './components/ChangePasswordDialog'
import ForgetPassword from './components/ForgetPassword'
import VerifyFaceDialog from './components/VerifyFaceDialog'
import LangSelect from '@/components/LangSelect'
import PortalSourcing from './components/portalSourcing'
import PortalBidding from './components/portalBidding'
import ThemeConf from '@/components/themeConf'
import { confirmDeal, genScanCode, getScanCode, getPassPublicKey, getAssistantToken } from '@/api/user'
import { randomLenNum } from 'lib@/utils/util'
import {getToken, removeRedirectUrl, getRedirectUrl, setEntranceType, setToken} from '@/utils/auth'
import config from '@/config/user.env'
import http from '@/utils/axios/http'
import sha1 from 'js-sha1'
import { isPortalSourcing, isSinglePoint, isPortalBidding, singlePointLoginUrl } from '@/config/sysConfig'
import vueQr from 'vue-qr'
import Header from '@/layout/components/Header/index.vue'
import { sysPrefix } from '@/config/ipConfig'
import { STORE_COMMON_CACHE } from '@/config/store-config'
import { getSystemTheme } from '@/config/logo-config'
import { dataEncryption } from '@/utils/secret'
import Postmate from 'postmate'
import qs from 'qs'
export default {
  name: 'Login',
  components: {
    RegisteredDialog,
    ChangePassword,
    ForgetPassword,
    LangSelect,
    VerifyFaceDialog,
    PortalSourcing,
    PortalBidding,
    vueQr,
    AppHeader: Header,
    ThemeConf
  },
  data () {
    const validateUsername = (rule, value, callback) => {
      if (!trimField(value)) {
        callback(new Error(this.$t('vendorMod.enterUserName')))
      } else {
        callback()
      }
    }
    const validatePassword = (rule, value, callback) => {
      const patrn = /[A-Za-z0-9][`~!@#$%^&*()_\-+=<>?:"{}|,.\\/;'\\[\]·~！@#￥%……&*（）——\-+={}|《》？：“”【】、；‘'，。、]/im
      if (!value) {
        callback(new Error(this.$t('vendorMod.enterPass')))
      } else if (!patrn.test(value) || value.length < 8 || value.length > 24) {
        callback(new Error(this.$t('vendorMod.errorPass')))
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
      loadingFlag: false,
      firstTime: '',
      lastTime: '',
      screenHeight: 0,
      isGrab: 'pointer',
      originalPosition: {
        x: 0,
        y: 0
      },
      mousedownOffset: {
        x: 0,
        y: 0
      },
      elementPosition: {
        x: 0,
        y: 0
      },
      tempElementPosition: {
        x: 0,
        y: 0
      },
      iframeShow: false,
      showMyFrame: false,
      showPreview: false,
      expand: false,
      isFirstClick: true,
      hideTips: false,
      contentMap: [
        this.$t('cusEntry.tipMessage.contentTip1'),
        this.$t('cusEntry.tipMessage.contentTip2'),
        this.$t('cusEntry.tipMessage.contentTip3'),
        ''
      ],
      typeContent: this.$t('cusEntry.tipMessage.contentTip3'),
      activeName: 'first',
      vendorLoginForm: {
        username: '',
        password: ''
      },
      newLang: '', // 多语言文字
      divisionLine: 'division-line', // 多语言样式class
      bolTab: 0,
      portalSourcing: isPortalSourcing,
      portalBidding: isPortalBidding,
      singlePoint: isSinglePoint,
      loginType: 'innerLogin', // 登录方式 innerLogin/singlePoint
      logoInfo: getSystemTheme(),
      isRememberUser: false,
      configData: config,
      isWorkchatLogin: config.isWorkchatLogin,
      pwdDialogVisible: false,
      isDemoEnv: false, // 是否在demo环境下面
      isVerifyCodeTrue: null,
      loginForm: {
        username: '',
        password: '',
        verifyCode: ''
      },
      loginRules: {
        username: [{ required: true, validator: validateUsername }],
        // password: [{ required: true, validator: validatePassword }],
        password: [{ required: true, message: this.$t('vendorMod.enterPass') }],
        verifyCode: [{ required: true, message: this.$t('vendorMod.enterCode') }] // validator: validateCode
      },
      passwordType: 'password',
      capsTooltip: false,
      loading: false,
      redirect: undefined,
      otherQuery: {},
      showDialog: false,
      showForgetPasswordDialog: false,
      showChangePasswordDialog: false,
      tipDialogVisible: false,
      showVerifyFaceDialog: false,
      agreement: '',
      verifyCode: {
        src: `${this.$systemUrl}${sysPrefix()}/api-sup/register/getHomeVerifyCode`,
        value: '',
        len: 4,
        message: this.$t('vendorMod.enterFourDigitsCode')
      },
      notwindowOpener: true,
      scanCode: '',
      qrCode: '',
      loginTimer: null,
      regSucDialogVisible: false,
      iamKeyToken: ''
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
    language () {
      return this.$store.getters.language
    },
    vendorLoginFormRules () {
      return {
        username: [{ required: true, validator: this.validateUsername }],
        password: [{ required: true, message: this.$t('vendorMod.enterPass') }]
      } 
    },
    isPC () {
      return this.$store.getters.isPC && this.$store.getters.device !== 'device-xs'
    },
    showLoginBox () { // url参数方式控制是否显示登录框
      return this.$route.query.loginBox || 'N'
    }
  },
  watch: {
    'elementPosition.y' (newVal, oldVal) {
      if (newVal > 179) {
        this.elementPosition.y = 180
      } else if (newVal < 310 - this.screenHeight) {
        this.elementPosition.y = 300 - this.screenHeight
      }
    }
  },
  created () {
    // 显示登录框再设置
    if (this.showLoginBox == 'Y') {
      this.loginForm.username = this.getLocalData('username') || ''
      this.isRememberUser = JSON.parse(this.getLocalData('isRememberUser')) || false
    }
    // 如果开启单点就默认类型为单点
    // if (this.singlePoint === 'Y') {
    //   this.loginType = 'singlePoint'
    // }
    // 获取登录页里面的重定向连接
    if ('redirect' in this.$route.query) {
      console.log('redirect')
      console.log(decodeURI(this.$route.query.redirect))
      this.redirect = this.$route.query.redirect
    }
    // 通过邀请链接打开页面
    let regType = this.$route.query.regType
    let code = this.$route.query.code
    let me = this
    if (regType && code && regType === 'invite') {
      this.$http({
        url: '/api-sup/register/isValidLink',
        method: 'GET',
        params: { verifyCode: code },
        loading: true
      })
        .then(res => {
          if (res.data) {
            // 打开注册页面
            me.handleReg(regType)
          } else {
            // 提示无权限
            this.$router.push({ name: 'page401' })
          }
        })
        .catch(err => {
          console.log(err)
        })
    } else if (regType != 'portal') {
      this.intoDashboard()
    }
    let token = getToken()
    if (!token) { // token不存在的情况设置为 inside
      setEntranceType('inside') // 设置类型
    }

    // 国际化初始化
    this.$i18n.locale = 'ru_RU'
    this.handleSetLanguage(this.$i18n.locale)
  },
  mounted () {
    // 监听鼠标事件
    this.$nextTick(() => {
      this.typingArr(this.contentMap)
      this.restore()
      // this.$refs.assistant.addEventListener('mousedown', this.onMousedown, true)
      this.screenHeight = window.innerHeight
    })
  },
  destroyed () {
    this.isFirstClick = true
    this.showMyFrame = false
    this.clearScanLogin()
    // 移除监听鼠标事件
    if (this.$refs.assistant && this.$refs.assistant.removeEventListener) {
      this.$refs.assistant.removeEventListener('mousedown', this.onMousedown, true)
    }
  },
  methods: {
    validateUsername (rule, value, callback) {
      if (!trimField(value)) {
        callback(new Error(this.$t('vendorMod.enterUserName')))
      } else {
        callback()
      }
    },
    typingStr (str) {
      const _arr = str.split('')
      if (!_arr.length) {
        this.hideTips = true
        return false
      }
      let count = 0
      return new Promise((resolve, reject) => {
        let myTimer = setInterval(() => {
          this.typeContent += _arr[count]
          count++
          if (count >= _arr.length) {
            clearInterval(myTimer)
            let timerout = setTimeout(() => {
              clearTimeout(timerout)
              resolve()
            }, 2000)
          }
        }, 0)
      })
    },
    async typingArr (arr) {
      for (let i = 0; i < arr.length; i++) {
        const str = arr[i]
        this.typeContent = ''
        await this.typingStr(str)
      }
    },
    restore () {
      this.elementPosition.x = this.originalPosition.x
      this.elementPosition.y = this.originalPosition.y
    },
    // 拖拽
    onMousedown (event) {
      const _t = this
      event.stopPropagation()
      this.firstTime = new Date().getTime()
      this.mousedownOffset.x = event.clientX - this.originalPosition.x
      this.mousedownOffset.y = event.clientY - this.originalPosition.y
      document.addEventListener('mousemove', this.onMousemove, true)
      document.addEventListener('mouseup', _t.onMouseup, true)
      this.isGrab = 'grabbing'
    },
    onMousemove (event) {
      event.stopPropagation()
      this.lastTime = new Date().getTime()
      if (this.lastTime - this.firstTime > 200) {
        document.getElementById('assistant').setAttribute('drag-flag', true)
      }
      // this.elementPosition.x = event.clientX - this.mousedownOffset.x + this.tempElementPosition.x
      this.elementPosition.y = event.clientY - this.mousedownOffset.y + this.tempElementPosition.y
    },
    onMouseup (event) {
      const _t = this
      event.stopPropagation()
      this.tempElementPosition.x = this.elementPosition.x
      this.tempElementPosition.y = this.elementPosition.y
      document.removeEventListener('mousemove', _t.onMousemove, true)
      document.removeEventListener('mouseup', _t.onMouseup, true)
      this.isGrab = 'pointer'
      setTimeout(() => {
        document.getElementById('assistant').setAttribute('drag-flag', false)
      }, 300)
    },
    // 获取知识问答助手检验token
    getAssistantToken () {
      if (this.isFirstClick) {
        this.loadingFlag = true
        getAssistantToken().then(res => {
          if (res.code + '' === '0') {
            this.handleHandshake(res.data)
            this.isFirstClick = false
          }
        })
      } else {
        this.loadingFlag = false
        this.showMyFrame = true
      }
    },
    handleClick () {
      const isDrag = document.getElementById('assistant').getAttribute('drag-flag')
      if (isDrag === 'true') {
        return
      }
      this.getAssistantToken()
    },
    handleHandshake (token) {
      const handshake = new Postmate({
        container: document.getElementById('helperFrame'), // Element to inject frame into
        // url: 'http://10.246.100.57:8299/#/embed', // Page to load, must have postmate.js. This will also be the origin used for communication.
        // url: 'https://gwkb-helper-test.gwm.cn/#/embed',
        url: 'https://gwkb-helper.gwm.cn/#/embed',
        name: 'my-iframe-name', // Set Iframe name attribute. Useful to get `window.name` in the child.
        classListArray: ['helper-frame']
      })
      handshake.then(child => {
        this.loadingFlag = false
        this.showMyFrame = true
        setTimeout(() => {
          child.frame.style.height = '100%'
          child.frame.style.width = '100%'
          child.frame.style.border = 'none'
          child.frame.style.background = '#fff'
          child.frame.style.borderRadius = '20px'
          child.frame.style.boxShadow = '0px 10px 20px 0px rgba(143, 115, 156, 0.13)'
        })
        child.get('msg').then(msg => { })
        child.on('expand', status => { // 接收 关闭折叠展开消息
          console.log('接收 折叠展开消息', status)
          this.expand = status
        })
        child.on('close', data => { // 接收 关闭
          console.log('接收 关闭消息')
          this.showMyFrame = false
        })
        child.on('preview', status => { // 接收 关闭打开预览展开消息
          console.log('接收 打开关闭预览消息', status)
          this.showPreview = status
        })
        child.on('authExpire', status => {
          getAssistantToken().then(res => {
            if (res.code + '' === '0') {
              child.call('token', res.data)
            }
          })
          console.log('接收 token失效消息')
        })
        child.call('token', token)
        child.call('embedClient', 'SRMclient')
      })
    },
    tabHandleClick (tab) {
      if (this.activeName === 'second') {
        this.buyerLogin()
      }
    },
    /* 语言切换 */
    handleSetLanguage (val) {
      this.$i18n.locale = val

      // 设置语言
      this.$store.dispatch('app/setLanguage', val)

      // 清除字典缓存
      this.$store.commit(STORE_COMMON_CACHE.RESET)

      this.newLang = this.languageList.filter(i => i.value == val)[0].label

      if (val == 'ru_RU') {
        this.divisionLine = 'division-line2'
      } else {
        this.divisionLine = 'division-line'
      }
    },
    tabChange (num) {
      this.bolTab = num
    },
    handlePwdClose () {
      this.pwdDialogVisible = false
      location.href =
        '/#/userManage/resetPwd?userName=' + this.loginForm.username
    },
    iamForgetPassword () {
      if (!this.checkAppRegisterCode()) return
      // this.$router.push({ path: '/forgetPassword' })
      let singleBaseUrl = this.$systemUrl + '/'
      let service = encodeURI(singleBaseUrl)
      window.location.href = `${this.sysOpenConfig.iamSysBaseUrl}/portal/index.html#/forgetpassword?redirectUri=${service}`
    },
    forgetPassword () {
      // this.showForgetPasswordDialog = true
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
    // 设置本地缓存过期时间
    setLocalExpirse (key, val, n) {
      let nowTime = new Date().getTime()
      let setDate = 1000 * 60 * 60 * 24 * n
      let data = { value: val, expirse: nowTime + setDate } // 设置n天过期
      window.localStorage.setItem(key, JSON.stringify(data))
    },
    // 获取本地缓存
    getLocalData (key) {
      let data = JSON.parse(window.localStorage.getItem(key))
      if (data !== null) {
        if (data.expirse != null && data.expirse < new Date().getTime()) {
          window.localStorage.removeItem(key)
        } else {
          return data.value
        }
      }
      return null
    },
    // 记住账号
    setRememberUser () {
      if (this.isRememberUser) {
        this.setLocalExpirse('username', this.loginForm.username, 7)
        this.setLocalExpirse('isRememberUser', this.isRememberUser, 7)
      } else {
        localStorage.removeItem('username')
        localStorage.removeItem('isRememberUser')
      }
    },
    handleLogin () {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          const _this = this
          _this.loading = true
          let pass = sha1(_this.loginForm.password)
          let userInfo = {
            username: _this.loginForm.username,
            password: pass,
            verifyCode: _this.loginForm.verifyCode
          }
          // 用户登录 _this.loginForm
          _this.$store
            .dispatch('user/login', userInfo) // _this.loginForm
            .then(res => {
              if (res && res.data) {
                // 获取用户信息
                _this.$store.dispatch('user/initSystem').then(
                  res => {
                    if (res) {
                      // 记住账号
                      this.setRememberUser()

                      let mainType = res.data.mainType
                      let isConfirm = res.data.isConfirm
                      let userType = res.data.userType
                      // 主账号 提示语
                      if (userType === 'BUYER' && mainType === 'Y' && (!isConfirm || isConfirm === 'N')) {
                        _this.tipDialogVisible = true
                      }

                      // if (this.portalSourcing !== 'Y') {
                      // 先去拿cookie的redirectUrl，有的话，就跳转这个地址，
                      let redirectUrl = getRedirectUrl()
                      if (redirectUrl) {
                        location.href = redirectUrl
                        removeRedirectUrl()
                      } else {
                        _this.$router.push({
                          path: _this.redirect || '/',
                          query: _this.otherQuery
                        })
                      }
                      // }

                      _this.loading = false
                    }
                  },
                  err => {
                    _this.loading = false
                    console.log(err)
                  }
                )
              }
            })
            .catch(err => {
              if (err.message === this.$t('cusEntry.login.passwordExpired')) { // 密码过期
                this.pwdDialogVisible = true
              }
              this.refreshCode() // 刷新验证码
              _this.loading = false
            })
        } else {
          console.log('error submit!!')
          this.refreshCode() // 刷新验证码
          return false
        }
      })
    },
    getOtherQuery (query) {
      return Object.keys(query).reduce((acc, cur) => {
        if (cur !== 'redirect') {
          acc[cur] = query[cur]
        }
        return acc
      }, {})
    },
    // 切换内部登录
    switchType () {
      this.clearScanLogin()
      this.loginType = 'innerLogin'
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
      // setEntranceType('singlePoint') // 设置类型
      // this.$store.commit('user/SET_ENTRANCE', 'singlePoint')
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
            // 清除字典缓存
            this.$store.commit(STORE_COMMON_CACHE.RESET)
            let token = resData.data?.value
            setToken(token)
            this.$store.commit('user/SET_TOKEN', token)
            await this.$store.dispatch('app/getServeLang')
            await this.$store.dispatch('user/initSystem')
            _this.$router.push({ path: '/dashboard' })
          }
        }
      })
    },
    // 采购商登录跳转
    buyerLogin () {
      const pathname = window.location.pathname
      const systemUrl = window.location.origin + pathname.substring(0, pathname.length - 1)
      let singleBaseUrl = systemUrl
      console.log(2, singleBaseUrl) // 'https://localhost:7899/srm'
      // let sUrl = `${singleBaseUrl}/cloud-srm/sys/sso/transfer?redirectUri=${singleBaseUrl}/`
      // let service = encodeURI(sUrl)
      // window.location.href = `http://iam-cnp-bdtest.gwmit.cn/iam/auth/proxy.login/go?sceneCode=changcheng&clientId=PyjQhAYzcbfX&service=${service}`
      // window.location.href = `${this.$store.getters?.sysOpenConfig?.iamBaseUrl}/iam/auth/proxy.login/go?sceneCode=changcheng&clientId=PyjQhAYzcbfX&service=${service}`
      // window.location.href = 'http://platform.test.paas.gwm.cn/login?mode=TOKEN&redirect_url=http://srm-cnp-bdtest.gwmit.cn/cloud-srm/api-pj/external/bpm/viewSrm?redirectUri=http://srm-cnp-bdtest.gwmit.cn/#/flowTaskToken/ZnJvbT1mcm9tRnVuJmZ1bk5hbWU9ZGFzaGJvYXJk'
      // window.location.href = 'http://platform.test.paas.gwm.cn/login?mode=TOKEN&redirect_url=http%3A%2F%2Fsrm-cnp-bdtest.gwmit.cn%2Fcloud-srm%2Fapi-pj%2Fexternal%2Fbpm%2FviewSrm%3FredirectUri%3Dhttp%253A%252F%252Fsrm-cnp-bdtest.gwmit.cn%252F%2523%252FflowTaskToken%252FZnJvbT1mcm9tRnVuJmZ1bk5hbWU9ZGFzaGJvYXJk'

      let ssoUrl = 'http://platform.test.paas.gwm.cn' // dev、uat
      if (singleBaseUrl == 'https://srm.gwm.cn') {
        ssoUrl = 'https://platform.gwm.cn' // prd
      }

      let redirectUri = `${singleBaseUrl}/#/flowTaskViewBase/ZnJvbT1mcm9tRnVuJmZ1bk5hbWU9ZGFzaGJvYXJk`
      // console.log(3, redirectUri, encodeURIComponent(redirectUri))
      // 3 'https://localhost:7899/srm/#/flowTaskToken/ZnJvbT1mcm9tRnVuJmZ1bk5hbWU9ZGFzaGJvYXJk'
      // https%3A%2F%2Flocalhost%3A7899%2Fsrm%2F%23%2FflowTaskToken%2FZnJvbT1mcm9tRnVuJmZ1bk5hbWU9ZGFzaGJvYXJk

      let redirectUrl = `${singleBaseUrl}/cloud-srm/api-pj/external/bpm/viewSrm?redirectUri=${encodeURIComponent(redirectUri)}`
      // console.log(4, redirectUrl, encodeURIComponent(redirectUrl))
      // https://localhost:7899/srm/cloud-srm/api-pj/external/bpm/viewSrm?redirectUri=https://localhost:7899/srm/#/flowTaskToken/ZnJvbT1mcm9tRnVuJmZ1bk5hbWU9ZGFzaGJvYXJk
      // https%3A%2F%2Flocalhost%3A7899%2Fsrm%2Fcloud-srm%2Fapi-pj%2Fexternal%2Fbpm%2FviewSrm%3FredirectUri%3Dhttps%253A%252F%252Flocalhost%253A7899%252Fsrm%252F%2523%252FflowTaskToken%252FZnJvbT1mcm9tRnVuJmZ1bk5hbWU9ZGFzaGJvYXJk'
      window.location.href = `${ssoUrl}/login?mode=TOKEN&redirect_url=${encodeURIComponent(redirectUrl)}`
      // console.log('window.location.href', window.location.href)
    },
    // 单点登录跳转
    singlePointLogin () {
      if (!this.checkAppRegisterCode()) return
      // this.$router.push({ path: '/find' })
      setEntranceType('singlePoint') // 设置类型
      window.location.href = singlePointLoginUrl()
    },
    // 企业微信登录跳转
    workChatLogin () {
      this.clearScanLogin()
      this.loginType = 'workChatLogin'
      // 获取二维码，并启动轮询任务
      genScanCode().then(genCodeRes => {
        this.scanCode = genCodeRes.data
        const redirectSrm = window.location.protocol + '//' + window.location.host + '/#/mobile?routeName=dashboard'
        this.qrCode = 'https://sccdemosyy.meicloud.com/cloudmobile/workchat-anon/guest/srmIndex?scanCode=' + genCodeRes.data + '&redirectSrm=' + encodeURI(redirectSrm)

        this.loginTimer = setInterval(() => {
          getScanCode(this.scanCode).then(scanCodeRes => {
            console.log(scanCodeRes.data)
            const scanCodeStatus = scanCodeRes.data.scanCodeStatus
            if (scanCodeStatus === '-1') {
              // 未扫码确认
            } else if (scanCodeStatus === '-2') {
              // 已过期，重新生成二维码
              this.workChatLogin()
            } else if (scanCodeStatus === '1') {
              this.$store
                .dispatch('user/scanLogin', scanCodeRes) // _this.loginForm
                .then(() => {
                  this.$store.dispatch('user/initSystem').then(
                    res => {
                      if (res) {
                        let mainType = res.data.mainType
                        let isConfirm = res.data.isConfirm
                        let userType = res.data.userType
                        // 主账号 提示语
                        if (userType === 'BUYER' && mainType === 'Y' && (!isConfirm || isConfirm === 'N')) {
                          this.tipDialogVisible = true
                        }
                        // 先去拿cookie的redirectUrl，有的话，就跳转这个地址，
                        let redirectUrl = getRedirectUrl()
                        if (redirectUrl) {
                          location.href = redirectUrl
                          removeRedirectUrl()
                        } else {
                          this.$router.push({
                            path: this.redirect || '/',
                            query: this.otherQuery
                          })
                        }
                        this.loading = false
                      }
                    },
                    err => {
                      this.loading = false
                      console.log(err)
                    }
                  )
                })
            }
          })
        }, 2000)
      })
    },
    // 进入工作台
    intoDashboard () {
      this.$router.push({ path: '/dashboard' })
    },
    passShowFun (val) {
      this.showChangePasswordDialog = val
    },
    regShowFun (val) {
      this.showDialog = val
    },
    forgetVisibleChange (visible) {
      this.showForgetPasswordDialog = visible
    },
    verifyFaceDialogVisibleChange (visible) {
      this.showVerifyFaceDialog = visible
    },
    cancelHandel () {
      this.$store.dispatch('user/getLogout').then(
        res => {
          if (res) {
            this.tipDialogVisible = false
          }
        },
        err => {
          console.log(err)
        }
      )
    },
    comfirmHandel () {
      let agree = this.agreement
      if (agree !== 'Y') {
        this.$message({
          message: this.$t('vendorMod.toDoAgree'),
          type: 'error'
        })
      } else {
        confirmDeal(agree).then(res => {
          if (res) {
            this.$router.push({
              path: this.redirect || '/',
              query: this.otherQuery
            })
          }
        })
      }
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
      this.loginForm.verifyCode = ''
      let randomStr = randomLenNum(this.verifyCode.len)
      this.verifyCode.src =
        `${this.$systemUrl}${sysPrefix()}/api-sup/register/getHomeVerifyCode?randomStr=` +
        randomStr
    },
    // 跳到用户使用协议
    toUserProtocol () {
      const { href } = this.$router.resolve({ name: 'saasUserProtocol' })
      window.open(href, '_blank', 'noopener,noreferrer')
    },
    // 跳到隐私协议
    toPrivacyProtocol () {
      const { href } = this.$router.resolve({ name: 'saasPrivacyProtocol' })
      window.open(href, '_blank', 'noopener,noreferrer')
    },
    // 人脸识别
    verifyFaceDialogClick () {
      this.showVerifyFaceDialog = true
    },
    backHone () {
      this.loginType = 'singlePoint'
    },
    clearScanLogin () {
      if (this.loginTimer) {
        clearInterval(this.loginTimer)
      }
    },
    regsuccessHandle (data) {
      // 成功弹框
      this.regSucDialogVisible = true
      if (this.sysOpenConfig.supplierAutoAuth) {
        this.iamKeyToken = data.KeyToken
      }
    },
    async regSuccessJump (type) {
      this.regSucDialogVisible = false
      if (type == 'companyInfoMaintain') {
        // 注册后自动登录
        if (this.sysOpenConfig.supplierAutoAuth) {
          await this.$store.dispatch('user/loginWithToken', this.iamKeyToken)
          await this.$store.dispatch('user/initSystem')
          this.$router.push({
            path: '/userManage/companyInfoMaintain'
          })
        }
      } else {
        this.singlePointLogin()
      }
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
    font-size: 12px;
    color: #51555B;
    line-height: 22px;
    height: 22px;
  }
  .btn {
    margin-top:32px;
  }
}
.language-dropdown-wrap{
  position: absolute;
  top: 26px;
  right: 0px;
  color: #515558;
  cursor: pointer;
  .iconearth{
    color: #515558;
  }
}
.portal-sourcing-sec{
  position: relative;
  width: 100%;
  min-height: 435px;
  background-image: url("./img/portal.jpg");
  background-repeat: no-repeat;
  background-color: #e6f0f7;
  background-size: 100% 100%;
  overflow: hidden;
  .ditialTitalAll{
    width: 85%;
    max-width: 1252px;
    position: absolute;
    top: 25px;
    left: 50%;
    transform: translate(-50%,0);
    display: flex;
    &.top {
      top: 166px;
    }
    div{
      font-size: 22px;
      color: #161C24;
      line-height: 30px;
      font-weight: 500;
      height: 41px;
      cursor: pointer;
      margin-right: 32px;
    }
    .blue{
      color: #0077FF;
      border-bottom: 2px solid #0077FF;
    }
  }
}
.login-container {
  height: 100%;
  width: 100%;
  overflow: hidden;
  :deep(.el-input--prefix .el-input__inner) {
    padding-left: 50px;
  }
  .el-input{
    .el-input__inner {
      height: 40px !important;
      line-height: 40px !important;
      min-height: 40px !important;
      font-size: 14px;
      background-color: #fff !important;
      color: #51555B;
    }
  }
  .input-icon__password,
  .input-icon__user {
    width: 16px;
    height: 16px;
    margin-top: 10px;
    box-sizing: content-box;
    padding: 3px 13px 3px 9px;
    border-right: 1px solid  rgba(0,0,0,0.15);
  }
  .logo-icon {
    width: 79px;
    height: 30px;
  }
  .bottom-group {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .el-form-item {
    border-radius: 5px;
    color:#51555B;
    margin-bottom: 0;
    .el-form-item__error {
      padding-top: 6px;
    }
  }
  .form-div {
    position: absolute;
    width: 340px;
    height: 389px;
    max-width: 100%;
    padding: 30px 30px 8px;
    overflow: hidden;
    top: 50%;
    transform: translateY(-50%);
    background: #fff;
    box-sizing: border-box;
    border-radius: 4px;
    z-index: 15;
  }
  .single-point-login{
    position: absolute;
    width: 380px;
    max-width: 100%;
    overflow: hidden;
    right: 104px;
    padding: 30px 0 20px;
    box-sizing: border-box;
    background-color: rgba(255,255,255,0.8);
    border-radius: 4px;
    transform: translate(-0%,-50%);
    top: 50% !important;
    z-index: 10;
    .login-title{
      font-size: 24px;
      height: 42px;
      line-height: 32px;
      color: #161C24;
      text-align: center;
      font-weight: 500;
    }
    .pd-30 {
      padding: 0 30px;
    }
    .iamLogin{
      padding-top: 8px;
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
    .vendor-reg-btn{
      background: transparent;
      color: #fff;
    }
    .user-wellcom{
      color: #fff;
    }
  }
  .form-user {
    margin-bottom: 16px;
  }
  .form-pwd {
    margin-bottom: 16px;
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
    margin-bottom: 16px;
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
    top: 11px;
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
}

// 欢迎弹框样式
.tipDialog{
  .text {
    font-size: 16px;
    line-height: 26px;
    padding: 0 20px;
    &.textP {
      text-indent: 1em;
      position: relative;
      height: 26px;
      &::before {
        position: absolute;
        top: 10px;
        left: 22px;
        width: 6px;
        height: 6px;
        border-radius: 50%;
        content: " ";
        font-size: 0px;
        background: #1890ff;
      }
    }
  }
  .doAgrrent-div {
    margin-top: 15px;
    padding: 0 20px;
    span {
      color: #1890ff;
      cursor: pointer;
    }
  }
}

</style>
<style lang='scss'>
/* 系统底部版本 */
.login-footer {
  font-size: 12px;
  position: fixed;
  width: 100%;
  bottom: 34px;
  padding: 10px;
  .copy-right {
    text-align: center;
    color: #fff;
    span{
      display: inline-block;
      vertical-align: middle;
      padding: 0 8px;
      &.version{
        padding-left: 0px;
        &::before{
          content: "|";
          padding-right: 8px;
        }
      }
    }
  }
}

// 开启显示寻源信息
.login-container {
  // PC端
  .login-main-body{
    height: 100%;
    overflow-y: auto;
    position: relative;
    background-color: #EDEFF2;
    .login-top-sec{
      height: 100%;
      // background-image: url("./img/bg-2880-1560.png");
      background-position: center center;
      background-repeat: no-repeat;
      background-color: #e6f0f7;
      background-size: 100% 100%;
      .loginBanner{
        position: absolute;
        z-index: 2;
        width: 100%;
        height: 100%;
        .el-carousel__container{
          height: 100%;
          .el-carousel__item{
            height: 100%;
            background-position: center center;
            background-repeat: no-repeat;
            background-color: #e6f0f7;
            background-size: cover;
          }
        }
      }
    }
    .logo {
      width: 80%;
      position:absolute;
      left:0px;
      top:16px;
      color: #393E45;
      z-index: 11;
      display: flex;
      img {
        width: 180px;
      }
      .vertical{
        width: 0;
        height: 32px;
        background-color: #FFFFFF;
        margin: 24px 33px 0;
      }
      .font{
        font-size: 18px;
        color: #FFFFFF;
        margin-top: 16px;
      }
    }
    .mainImg{
      width: 330px;
      height: 330px;
      position: absolute;
      top: 52%;
      left: 11%;
      -webkit-transform: translate(0, -50%);
      transform: translate(0, -50%);
      z-index: 10;
    }
    .validCodeInpu{
      .el-input-group__append {
        padding: 0 !important;
        width: 100px;
        height: 38px;
        border-radius: 0px 4px 4px 0;
        overflow: hidden;
        .reg-code-img {
          display: block;
          width: 100%;
          height: 36px;
        }
        .reg-code-img {
          display: block;
          width: 100%;
          height: 36px;
        }
      }
    }
    .el-input{
      .el-input__icon {
        line-height: 40px !important;
        color: #a8a8a8;
      }
    }
    .form-div {
      //margin: 0 auto 0 64%;
      top: 47% !important;
      right: 102px;
      transform: translate(-0%,-50%);
      .login-qrcode {
        position: absolute;
        width: 50px;
        height: 50px;
        right: 14px;
        top: 14px;
        background: skyblue;
      }
    }
  }

  /* 移动登录样式 */
  .login-main-body-mobile {
    height: 100%;
    position: relative;
    display: flex;
    justify-content: center;
    .login-contain{
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
    }
    .form-div{
      top: 51% !important;
    }
    .portal-main-li{
      width: 100% !important;
    }
    .portal-sourcing-sec .ditialTitalAll{
      position: relative;
    }
    .portal-sourcing-sec .top{
      top: 25px;
    }
    .page-portal-sourcing .portal-header .search{
      width: 100%;
    }
    .search{
      margin-top: 15px;
    }
    .logo {
      width: 80px;
      position: absolute;
      top: 20px;
      left: 20px;
      color: #393E45;
    }
    .mainImg{
      display: none;
    }
    .bg-blur {
      height: 100%;
      width: 100%;
      overflow-y: auto;
      background-image: url("./img/bg@2x.jpg");
      background-position: top left;
      background-repeat: no-repeat;
      background-color: #e6f0f7;
      background-size: cover;
      filter: blur(5px);
    }
    .el-input{
      .el-input__icon {
        line-height: 40px !important;
        color: #a8a8a8;
      }
    }
    .form-div{
      margin: 0  auto ;
      left: 50%;
      margin-left: -170px;
      .title{
        color: #fff;
      }
    }
    .single-point-login{
      width: 80%;
      text-align: center;
      margin: 0 auto;
      right: 30px;
      left: 30px;
      padding: 30px 30px 50px;
    }
  }
  // 开启显示公开寻源
  &.isPortalSourcing{
    .login-footer{
      position: static;
      .copy-right{
        color: #393E45;
      }
    }
    @media screen and (min-width: 1500px){
      .login-top-sec{
        height: 520px !important;
      }
    }
    .login-top-sec{
      height: 520px;
      position: relative;
      .mainImg{
        top: 52% !important;
      }
    }
    .login-main-body{
      .mainImg{
        width: 380px;
        height: 380px;
        top: 43%;
        left: 11%;
      }
      .form-div{
        top: 50% !important;
      }
    }
    .login-main-body-mobile{
      display: block;
      .bg-blur{
        filter: blur(2px);
      }
      .login-top-sec{
        height: 540px;
      }
      .logo {
        width: 80px;
        position: absolute;
        top: 20px;
        left: 20px;
        color: #393E45;
      }
      .form-div{
        top: 42%;
        height: 389px;
        padding: 30px 30px 8px;
      }
      .single-point-login{
        width: 80%;
        text-align: center;
        margin: 0 auto;
        right: 30px;
        left: 30px;
        padding: 30px 30px 50px;
      }
    }
  }
  .other-login-type{
    .other-type{
      margin: 20px 0 14px;
      .el-divider__text{
        color: #96999c;
        font-size: 12px;
      }
    }
    .type-content{
      span{
        color:#393E45;
        font-size: 12px;
        line-height: 20px;
        margin-right: 10px;
        cursor: pointer;
        &:last-child{
          margin-right: 0;
        }
        &:hover{
          color:#1890ff;
        }
      }
    }
  }
}
.el-main {
  padding: 0 !important;
}
// 登录页面
.login-container .el-input .el-input__inner {
  height: 38px !important;
  line-height: 38px !important;
  min-height: 38px !important;
  font-size: 14px;
  background-color: #fff !important;
}
html{
  font-size: 107%;
}
@media screen and (min-width: 1500px){
  html{
    font-size: 135%;
  }
}
.register-dialog .srm-dialog-content {
  padding: 46px 30px !important;
}
.header-menu-dropdown {
  .el-dropdown-menu__item{
    line-height: 32px !important;
    padding-left: 16px !important;
    padding-right: 16px !important;
    // 多语言当前选中
    &.currentLang {
      color: #161C24;
      background-color: #F6F6F6;
    }
    &:hover {
      cursor: pointer;
      background: #E6F6FF;
      color: #0077FF;
    }
  }
}
.assistant-box {
  .el-loading-mask{
    border-radius: 50%;
  }
  .assistant{
    position: absolute;
    width: 60px;
    height: 60px;
    border-radius: 50%;
    // overflow: hidden;
    z-index: 9998;
    bottom: 180px;
    right: 20px;
    user-select: none;
    cursor: pointer;
    background: url(./../../assets/images/zhushou.gif) center center no-repeat;
    background-size: contain;
    .icon-tips-box {
      background: #A9EFFF;
      color: #00495D;
      position: absolute;
      top: -50px;
      right: 100%;
      text-align: left;
      padding: 5px 20px;
      border-radius: 20px;
      white-space: nowrap;
      font-weight: 600;
      font-size: 14px;

      &::after,
      &::before {
        content: '';
        width: 15px;
        height: 15px;
        position: absolute;
        bottom: -13px;
        right: -10px;
        background: #A9EFFF;
        opacity: .6;
        border-radius: 100%;
      }

      &::before {
        background: #A9EFFF;
        opacity: 0.3;
        bottom: -22px;
        right: -15px;
        width: 8px;
        height: 8px;
      }

      .blink-tag {
        animation: blinks 1.5s infinite steps(1, start);
        position: relative;
        bottom: 1px;
      }

      &.hide {
        display: none;
      }
    }
  }
  .frame-wrap {
    position: fixed;
    top: 0;
    right: 0;
    height: 100%;
    z-index: 9999;
    top: 0;
    right: 0;
    height: 100%;
    transition: all .3s ease-in-out;
    .helper-frame {
      width: 100%!important;
      height: 100%!important;
      border: 0!important;
      background-color: #fff;
      border-radius: 20px;
    }

    &.show {
      transform: translateX(0);
      width: 560px;
    }

    &.showPreview {
      width: 1070px;
    }

    &.expand {
      // transform: translateX(0);
      width: 850px;
    }

    &.expand.showPreview {
      width: 1350px;
    }

    &.hide {
      width: 560px !important;
      transform: translateX(560px) !important;
    }
  }
}
@keyframes blinks {
  0% {
    opacity: 1;
  }

  50% {
    opacity: 0;
  }

  100% {
    opacity: 1;
  }
}
</style>
