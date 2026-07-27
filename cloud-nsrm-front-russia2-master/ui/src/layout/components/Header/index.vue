<template>
  <div ref="menuHeader" class="header-comp" :style="{paddingRight:paddingR+'px'}">
    <ThemeConf
      v-if="pageType === 'gateway'"
      class="logo-gateway"
      themeType="img"
      imgType="subLogo"
    />
    <!--切换左侧导航模式-->
    <div v-if="pageType !== 'gateway'" class="toggle-sideBar-wrap">
      <i
        :class="['iconfont themeLink', sidebar.opened ? 'iconmenu-fold' : 'iconmenu-unfold']"
        @click="toggleSideBar"
      />
    </div>

    <!--tabs缓存页面标签列表-->
    <!-- device-xs 小屏幕下不用显示 -->
    <div v-if="device !== 'device-xs' && pageType !== 'gateway'" class="header-menu">
      <div :class="['header-menu-tabs',{'hispreAndNex':navNextIsHidden}]">
        <el-tabs
          class="tab-container"
          :value="activeTab"
          type="card"
          @contextmenu.prevent.native="openContextMenu($event)"
          @tab-click="tabClickHandle"
          @tab-remove="tabRemoveHandle"
        >
          <el-tab-pane
            v-for="item in visitedViewsArrange"
            :key="item.fullPath"
            :name="item.fullPath"
            :closable="!isDashboardTab(item.fullPath)"
          >
            <!--id加tab前缀避免重复-->
            <span
              :id="`tab-${item.fullPath}`"
              slot="label"
              class="right-click-menu"
              :data-item="item"
            >
              {{ item.tabTitle }}
            </span>
          </el-tab-pane>
        </el-tabs>

        <!--右键菜单-->
        <ul
          v-if="contextMenuVisible"
          :style="{
            left: `${left}px`,
            top: `${top}px`,
            width: `${width}px`
          }"
          class="contextmenu"
        >
          <!--关闭-->
          <li @click="tabRemoveHandle(rightClickTabName)">
            {{ $t('base.tagsView.close') }}
          </li>
          <!--关闭所有-->
          <li @click="closeAllTabs()">
            {{ $t('base.tagsView.closeAll') }}
          </li>
          <!--关闭左侧-->
          <li @click="closeLeftOrRightTabs('left')">
            {{ $t('base.tagsView.closeLeft') }}
          </li>
          <!--关闭右侧-->
          <li @click="closeLeftOrRightTabs('right')">
            {{ $t('base.tagsView.closeRight') }}
          </li>
          <!--关闭其他-->
          <li @click="saveCurrentTabs(rightClickTabName)">
            {{ $t('base.tagsView.closeOthers') }}
          </li>
        </ul>
      </div>
      <!--tab下拉菜单 tabs可滚动时候才显示 v-if="headerMenuPopperVisible"-->
      <div class="header-menu-popper">
        <el-popover
          placement="bottom"
          width="80"
          trigger="hover"
          :visible-arrow="false"
          :offset="-10"
          popper-class="header-popper-class"
        >
          <div class="el-popover__content">
            <!--当打开的tab大于5个，出现Y轴滚动条-->
            <div :class="['col-tab', { 'col-tab-y-scroll': visitedViewsArrange.length >= 5 } ]">
              <div
                v-for="item in visitedViewsArrange"
                :key="item.fullPath"
                :name="item.fullPath"
                :class="{ 'tab-class': item.fullPath === activeTab }"
              >
                <span
                  :id="item.fullPath"
                  class="col-tab-rol-title"
                  @click.stop="tabClickHandle({ name: item.fullPath })"
                >
                  {{ item.tabTitle }}
                </span>
                <i
                  v-if="!isDashboardTab(item.fullPath)"
                  class="el-icon-close"
                  @click.stop="tabRemoveHandle(item.fullPath)"
                />
              </div>
            </div>
            <!--关闭所有-->
            <div class="close-all" @click="closeAllTabs">
              {{ $t('base.tagsView.closeAll') }}
            </div>
            <!--保留当前-->
            <div class="save-current" @click="saveCurrentTabs(activeTab)">
              {{ $t('base.tagsView.retainCurrent') }}
            </div>
          </div>

          <el-button slot="reference" icon="el-icon-more" class="popper-reference-button" />
        </el-popover>
      </div>
    </div>
    <!-- 面包屑导航 屏宽小于768 -->
    <!-- <breadcrumb
      v-if="device === 'device-xs'"
      id="breadcrumb-container"
      class="breadcrumb-container"
    /> -->
    <!--右侧功能信息区域-->
    <div ref="rightCont" class="right-container">
      <!--当前用户企业名称-->
      <div class="user-enterprise-name themeLink" :title="userInfo.companyName || ''">
        <div v-if="userInfo && userInfo.userId">{{ userInfo.companyName || '' }}</div>
        <div v-else>未登录</div>
      </div>

      <!--打开信息聊天窗 FIXME 暂时屏蔽-->
      <!--<div class="iconfont-button message" @click="closeOrOpenSrmIM(true)">-->
      <!--  <i class="iconfont iconmessage themeLink" />-->
      <!--</div>-->

      <!--切换语言-->
      <!-- 2023-03-29 放开多语言 -->
      <el-dropdown
        v-if="userInfo.userId"
        trigger="hover"
        class="language-dropdown-wrap"
        placement="bottom"
        @command="handleSetLanguage"
      >
        <div class="iconfont-button">
          <i class="iconfont iconearth themeLink" />
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

      <el-dropdown trigger="hover" @command="clickDropdownHandle">
        <div class="user-dropdown-wrap">
          <div class="avatar">
            <!--用户头像-->
            <img src="../../../assets/images/user.svg" alt="user">
          </div>
          <div class="text themeLink" :title="userInfo.nickname || ''">
            {{ userInfo.nickname || '' }}
          </div>
        </div>

        <el-dropdown-menu slot="dropdown" class="header-menu-dropdown">
          <!--帮助中心 FIXME 暂时屏蔽-->
          <!--<el-dropdown-item command="smartHelper">-->
          <!--  {{ $t('helper.helpCenter') }}-->
          <!--</el-dropdown-item>-->
          <!--用户门户-->
          <!-- <el-dropdown-item
            v-if="pageType === 'home' && isPortalSourcing === 'Y'"
            command="gateway"
          >
            {{ $t('base.navbar.gateway') }}
          </el-dropdown-item> -->
          <!--工作台-->
          <el-dropdown-item
            v-if="pageType === 'gateway' && isPortalSourcing === 'Y'"
            command="home"
          >
            {{ $t('base.navbar.home') }}
          </el-dropdown-item>
          <!--个人资料-->
          <el-dropdown-item v-if="userInfo.userId" command="profile">
            {{ $t('base.navbar.profile') }}
          </el-dropdown-item>
          <!-- 修改密码 -->
          <el-dropdown-item v-if="userInfo.userId" command="updatePassword">
            {{ $t('base.navbar.updatePassword') }}
          </el-dropdown-item>
          <!-- 安全设置 -->
          <!-- <el-dropdown-item v-if="userInfo.userId" command="safety">
            {{ $t('base.navbar.safety') }}
          </el-dropdown-item> -->
          <!-- 第三方账号 -->
          <!-- <el-dropdown-item v-if="userInfo.userId" command="thirdAccount">
            {{ $t('base.navbar.thirdAccount') }}
          </el-dropdown-item> -->
          <!--主题配置-->
          <el-dropdown-item v-if="userInfo.userId && pageType=='home'" command="themeConfig">
            {{ $t('base.navbar.theme') }}
          </el-dropdown-item>
          <!--时区配置-->
          <el-dropdown-item v-if="userInfo.userId" command="timeZone">
            {{ $t('base.navbar.timeZone') }}
          </el-dropdown-item>
          <!--退出登录-->
          <el-dropdown-item class="logout" command="logout">
            {{ $t('base.navbar.logOut') }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>

    <!--即时聊天窗 FIXME 暂时屏蔽-->
    <!--<srmIM-->
    <!--  v-if="chatVisible"-->
    <!--  :visible.sync="chatVisible"-->
    <!--  @closeIm="closeOrOpenSrmIM"-->
    <!--/>-->

    <!--帮助中心 FIXME 暂时屏蔽-->
    <!--<SmartHelper ref="smartHelper" :show-content="false" />-->
  </div>
</template>

<script>
import { mapGetters, mapState } from 'vuex'
import { toTreeArray } from 'xe-utils'
import Sortable from 'sortablejs'
import ThemeConf from '@/components/themeConf'
// import srmIM from 'lib@/components/webIM'
// import SmartHelper from 'lib@/components/smartHelper'
import { getToken } from '@/utils/auth'
import * as path from '@/utils/path'
import getPageTitle from '@/utils/get-page-title'
// import Breadcrumb from '@/components/Breadcrumb'
import { isSinglePoint, singlePointLogoutUrl, isPortalSourcing } from '@/config/sysConfig'
import { STORE_COMMON_CACHE } from '@/config/store-config'
import { getOpenConfigBeforeLogin } from '@/api/common'
import { getSystemTheme } from '@/config/logo-config'

export default {
  name: 'Header',

  components: {
    ThemeConf
    // srmIM,
    // Breadcrumb,
    // SmartHelper
  },

  props: {
    pageType: {
      type: String,
      default: 'home'
    }
  },

  data () {
    return {
      isPortalSourcing: isPortalSourcing,
      contextMenuVisible: false,
      left: 0,
      top: 0,
      width: 120,
      rightClickTabName: '',
      navNextIsHidden: null,
      chatVisible: false,
      affixTags: [],
      sortableTabs: null,
      headerMenuPopperVisible: false,
      paddingR: 250
    }
  },

  computed: {
    ...mapState({
      isPC: (state) => state.settings.isPC,
      device: state => state.app.device,
      visitedViews: (state) => state.tagsView.visitedViews,
      routes: (state) => state.permission.routes,
      entrance: (state) => state.user.entrance,
      // 注册码
      appRegisterCode: state => {
        return state.app.appRegisterCode
      }
    }),
    ...mapGetters(['userInfo', 'language', 'languageList', 'sidebar', 'sysOpenConfig']),
    activeTab () {
      const tab = this.visitedViews.find(tag => tag.fullPath === this.$route.fullPath)
      return tab ? tab.fullPath : ''
    },
    // 将树型的用户菜单转平铺的数组，并筛选需要的数据和属性，减低每次递归遍历树菜单的成本
    userMenusToTreeArray () {
      let menus = JSON.parse(JSON.stringify((this.userInfo || {}).menus || []))
      // 过滤不可跳转的父级菜单
      if (menus && Array.isArray(menus)) {
        // 平铺的数组
        menus = toTreeArray(menus, {
          clear: true,
          children: 'childPermissions'
        })

        // 筛选可点击菜单
        menus = menus.filter(item => item.functionAddress)
          .map(item => {
            // 只取需要用的属性
            return {
              functionAddress: item.functionAddress,
              permissionName: item.permissionName
            }
          })
        return menus
      }
      return []
    },
    // 重新编排冗余数据的缓存tab列表
    visitedViewsArrange () {
      return this.visitedViews.map(item => {
        const viewMenu = this.userMenusToTreeArray.find(menu => menu.functionAddress === item.fullPath)
        return {
          ...item,
          // 找用户菜单名称作tab标题
          tabTitle: (viewMenu || {}).permissionName ? viewMenu.permissionName : this.$t(item.title)
        }
      })
    },
    isHasToken () {
      const token = getToken()
      return !!token
    }
  },

  watch: {
    contextMenuVisible () {
      if (this.contextMenuVisible) {
        document.body.addEventListener('click', this.closeContextMenu)
      } else {
        document.body.removeEventListener('click', this.closeContextMenu)
      }
    },
    $route () {
      // 路由变化，更新tabs view
      this.addTag()
      this.setDocumentTitle()
    },
    sidebar: {
      deep: true,
      handler () {
        this.getStyle()
      }
    }
  },
  mounted () {
    this.tabDrop()
    this.openObserver()

    // 初始化
    this.initTags()
    // 添加当前路由tab
    this.addTag()

    this.getStyle()
    this.setDocumentTitle()
  },

  methods: {
    /* 设置浏览器页签标题 注意document.title全局唯一性，只在当前设置，其他地方需要删除 */
    setDocumentTitle () {
      const menu = this.visitedViewsArrange.find(item => this.$route.fullPath === item.fullPath)
      let systemTheme = getSystemTheme()
      document.title = getPageTitle((menu || {}).tabTitle, systemTheme.webTitle)
    },

    getStyle () {
      let rightHeaderH = this.$refs.rightCont.clientWidth // 系统头宽
      this.paddingR = rightHeaderH + 2
    },
    /* tab拖拽监听 */
    tabDrop () {
      this.$nextTick(() => {
        const that = this
        const el = document.querySelector('.tab-container .el-tabs__nav')
        if (el) {
          this.sortableTabs = Sortable.create(el, {
            // 首页不能拖拽，双右斜杠转义左斜杠
            filter: '#tab-\\/dashboard',
            // 动画时间
            animation: 200,
            // 配置读取DOM属性的标识
            dataIdAttr: 'id',
            onEnd ({ newIndex, oldIndex }) {
              if (newIndex !== oldIndex) {
                // 更新缓存菜单顺序 toArray()返回的是dataIdAttr配置的排序后的数组
                const newTabs = that.sortableTabs.toArray().map(item => {
                  return {
                    ...(that.visitedViewsArrange.find(tab => tab.fullPath === item.replace('tab-/', '/')) || {})
                  }
                })
                if (newTabs) {
                  that.$store.dispatch('tagsView/updateAllVisitedView', newTabs)
                }
              }
            }
          })
        }
        // 判断是否显示左右箭头
        this.$nextTick(() => {
          this.navNextIsHidden = document.querySelector('.tab-container .el-tabs__nav-next')
        })
      })
    },

    /* 监听DOM树变化 */
    openObserver () {
      // 监听tabs容器
      const el = document.querySelector('.tab-container .el-tabs__nav-wrap')
      let that = this
      // 选择浏览器API
      const MutationObserver = window.MutationObserver || window.WebKitMutationObserver || window.MozMutationObserver
      // 实例化
      const observer = new MutationObserver(function (mutations) {
        for (let i = 0; i < mutations.length; i++) {
          if (mutations[i].addedNodes.length > 0 && mutations[i].removedNodes.length === 0) {
            // 存在新增的节点，没有移除的节点
            let arr = mutations[i].addedNodes
            for (let j = 0; j < arr.length; j++) {
              // 找到tabs切换右箭头显示了
              if (arr[j].className === 'el-tabs__nav-next') {
                // 显示右箭头左边阴影条
                that.navNextIsHidden = true
                that.headerMenuPopperVisible = true
                that.$forceUpdate()
              }
            }
          } else if (mutations[i].removedNodes.length > 0 && mutations[i].addedNodes.length === 0) {
            // 存在移除的节点，没有新增的节点
            let arr = mutations[i].removedNodes
            for (let j = 0; j < arr.length; j++) {
              // 找到tabs切换右箭头隐藏了
              if (arr[j].className === 'el-tabs__nav-next') {
                // 隐藏右箭头左边阴影条
                that.navNextIsHidden = false
                that.headerMenuPopperVisible = false
                that.$forceUpdate()
              }
            }
          }
        }
      })
      if (el) {
        // 在监听观察对象中注册DOM节点
        observer.observe(el, {
          // 观察目标节点的所有后代节点
          subtree: true,
          // 观察目标节点的子节点的新增和删除
          childList: true
        })
      }
    },

    /* 切换左侧菜单模式 */
    toggleSideBar () {
      this.$store.dispatch('app/toggleSideBar')
    },

    // 检查应用是否过期
    async checkAppRegisterCode () {
      // 有appRegisterCode 说明应用过期
      if (this.appRegisterCode) {
        this.$confirm('请检查授权信息是否正确', '产品授权失败', {
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
        // 没有 appRegisterCode 请求一个接口判断状态
        const { code } = await getOpenConfigBeforeLogin()
        return code !== 'SRM_COMMON_00060'
      }
    },

    /* 点击用户名称下拉菜单 */
    async clickDropdownHandle (type) {
      // 应用过期提示
      if (!this.checkAppRegisterCode()) return
      switch (type) {
      case 'gateway':
        // 工作台
        this.$router.push({ path: path.resolve('/login?regType=portal') })
        break
      case 'home':
        // 用户门户
        this.$router.push({ path: path.resolve('/dashboard') })
        break
      case 'timeZone':
        this.timeZoneHandle()
        break
      case 'smartHelper':
        // 帮助中心
        this.$refs.smartHelper.popHelpFn()
        break
      case 'profile':
        // 个人资料
        this.$router.push({ path: path.resolve('/userManage/profile') })
        break
      case 'themeConfig':
        // 主题配置
        this.themeConfigHandle()
        break
      case 'logout':
        // 退出登录
        this.logout()
        break
      case 'updatePassword':
        // 密码修改
        // this.iamPersonSetting('updatePassword')
        this.$router.push({ name: type })
        break
      case 'safety':
        // 安全中心
        this.iamPersonSetting('safety')
        break
      case 'thirdAccount':
        // 第三方账号
        this.iamPersonSetting('thirdAccount')
        break
      }
    },
    timeZoneHandle () {
      this.$emit('timeZone', true)
    },
    /* 主题配置 */
    themeConfigHandle () {
      this.$emit('themeConfig', true)
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
      let userType = this.$store.getters.userInfo.userType
      if (this.isHasToken) {
        if (isSinglePoint === 'N') { // 内部页面登录后退出
          this.innerLogout()
        } else { // 单点登录退出 保留原来登录方式 退出的方式从哪里进来就回到哪里
          if (this.entrance === 'inside') {
            this.innerLogout()
          } else {
            this.innerLogout()
            // if (userType === 'VENDOR') { // 供应商退出登录
            //   this.singlePointLogout()
            // } else { // 采购商退出登录
            //   this.$store.dispatch('user/resetToken').then(() => {
            //     const pathname = window.location.pathname
            //     const systemUrl = window.location.origin + pathname.substring(0, pathname.length - 1)
            //     let singleBaseUrl = systemUrl

            //     let ssoUrl = 'http://platform.test.paas.gwm.cn' // dev、uat
            //     if (singleBaseUrl == 'https://srm.gwm.cn') {
            //       ssoUrl = 'https://platform.gwm.cn' // prd
            //     }

            //     let redirectUri = `${singleBaseUrl}/#/flowTaskViewBase/ZnJvbT1mcm9tRnVuJmZ1bk5hbWU9ZGFzaGJvYXJk`
            //     let redirectUrl = `${singleBaseUrl}/cloud-srm/api-pj/external/bpm/viewSrm?redirectUri=${encodeURIComponent(redirectUri)}`
            //     let windowLocationHref = `${ssoUrl}/login?mode=TOKEN&redirect_url=${encodeURIComponent(redirectUrl)}&logout=1`
            //     location.replace(windowLocationHref)
            //   })
            // }
          }
        }
      } else {
        this.$router.push({ name: 'login' })
      }
    },
    // iam密码设置
    async iamPersonSetting (type) {
      // 打开嵌入页面
      this.$store.dispatch('pageCtrl/setIamPage', type)
      this.$router.push({ name: 'accountSecurity', params: { tab: type } })
      // 跳转对应iam页面
      // let singleBaseUrl = this.$systemUrl + '/'
      // let service = encodeURI(singleBaseUrl)
      // let res = await getOpenConfigBeforeLogin()
      // let href = `${this.sysOpenConfig.iamSysBaseUrl}/portal/index.html#/portalPage/personSetting?headerVisible=true&tab=${type}&redirectUri=${service}`
      // location.href = href
    },
    /* tab右键菜单 START */
    /* 打开菜单 */
    openContextMenu (event) {
      const targetId = event?.target?.id || ''
      if (targetId.indexOf('tab-/dashboard') === 0) {
        // 首页tab没有右键菜单
        return false
      }

      // 先关闭旧的
      this.contextMenuVisible = false
      this.$nextTick(() => {
        if (targetId) {
          // 记录打开的标签 裁剪tab-
          this.rightClickTabName = targetId.replace('tab-/', '/')
          const positionData = document.getElementById(targetId).getBoundingClientRect()
          this.contextMenuVisible = true
          this.left = positionData.left - 221
          this.top = 49
          this.width = positionData.width + 59
        }
      })
    },
    /* 监听页面事件，点击空白处关闭右键菜单 */
    closeContextMenu () {
      this.rightClickTabName = ''
      this.contextMenuVisible = false
    },
    /* 关闭当前菜单左侧 or 右侧tab */
    closeLeftOrRightTabs (type) {
      const currentIndex = this.visitedViewsArrange.findIndex(item => item.fullPath === this.rightClickTabName)
      // 当前tab 左侧列表
      const currentLeftList = this.visitedViewsArrange.slice(0, currentIndex)
      // 当前tab 右侧列表
      const currentRightList = this.visitedViewsArrange.slice(currentIndex)
      // 当前删除的列表
      const deleteTabs = type === 'left' ? currentLeftList : currentRightList
      // 需要保留被标记affix固定的tab
      const deleteAffixTabs = deleteTabs.filter(item => item.meta.affix)

      let newTabs = []
      if (type === 'left') {
        // 右侧是删除的列表 保留删除列表中
        newTabs = [...deleteAffixTabs, ...currentRightList]
      } else if (type === 'right') {
        newTabs = [...this.visitedViewsArrange.slice(0, currentIndex + 1), ...deleteAffixTabs]
      }

      // 更新
      if (newTabs) {
        // 判断当前激活的路由不在新的tab列表里，跳转到最后一个tab
        if (!newTabs.find(item => item.fullPath === this.activeTab)) {
          this.toLastTabView(newTabs)
        }
        this.$store.dispatch('tagsView/updateAllVisitedView', newTabs)
      }
    },
    /* tab右键菜单 END */

    /* tab点击 */
    tabClickHandle (tab) {
      if (!tab.name || tab.name === this.activeTab) return

      // 路由跳转
      this.$router.push({ path: tab.name })
    },

    /* 移除当前tab */
    tabRemoveHandle (tabName) {
      // 找到tab对象
      const findTab = this.visitedViewsArrange.find(tag => tag.fullPath === tabName)
      if (findTab) {
        this.$store.dispatch('tagsView/delView', findTab).then(({ visitedViews }) => {
          // 如果不是当前
          if (this.isCurrentTab({ fullPath: tabName })) {
            this.toLastTabView(visitedViews)
          }
        })
      }
    },

    /* 关闭所有tab */
    closeAllTabs () {
      this.navNextIsHidden = false
      // 切换到首页
      this.$store.dispatch('tagsView/delAllViews').then(() => {
        this.$router.push('/dashboard')
      })
    },

    /* 保留当前tab */
    saveCurrentTabs (tab) {
      this.navNextIsHidden = false
      // 删除其他tab，保留当前激活的tab
      if (!this.isCurrentTab({ fullPath: tab })) {
        this.$router.push(tab)
      }
      const findTab = this.visitedViewsArrange.find(tag => tag.fullPath === tab)
      this.$store.dispatch('tagsView/delOthersViews', findTab)
    },

    /* 判断tab是否是当前路由页面 */
    isCurrentTab (tab) {
      return tab.fullPath === this.$route.fullPath
    },

    /* 判断是不是首页 */
    isDashboardTab (tabName) {
      return tabName === '/dashboard'
    },

    /* 移动到最后一个tab */
    toLastTabView (visitedViews) {
      const latestView = visitedViews.slice(-1)[0]
      if (latestView) {
        this.$router.push(latestView.fullPath)
      } else {
        this.$router.push('/dashboard')
      }
    },

    /* 打开关闭即时聊天 */
    closeOrOpenSrmIM (val) {
      this.chatVisible = val
    },

    /* 语言切换 */
    async handleSetLanguage (val) {
      // 设置语言
      await this.$store.dispatch('app/setLanguage', val)

      // 清除字典缓存
      this.$store.commit(STORE_COMMON_CACHE.RESET)

      // 清除缓存页面
      this.$store.dispatch('tagsView/delCachedView', this.$route).then(() => {
        const { fullPath } = this.$route
        // this.$nextTick(() => {
        this.$router.replace({ path: '/redirect' + fullPath })
        this.setDocumentTitle()
        location.reload()
        // })
      })
    },

    /* 添加缓存标签页 */
    addTag () {
      const { name } = this.$route
      if (name) {
        this.$store.dispatch('tagsView/addView', { ...this.$route })
      }
    },

    /* 初始化标签页 START */
    /* 初始化 */
    initTags () {
      const affixTags = (this.affixTags = this.filterAffixTags(this.routes))
      for (const tag of affixTags) {
        if (tag.name) {
          this.$store.dispatch('tagsView/addVisitedView', tag)
        }
      }
    },
    /* 递归筛选affix标记的页签 */
    filterAffixTags (routes, basePath = '/') {
      let tags = []
      routes.forEach(route => {
        if (route.meta && route.meta.affix) {
          const tagPath = path.resolve(basePath, route.fullPath)
          tags.push({
            fullPath: tagPath,
            path: tagPath,
            name: route.name,
            meta: { ...route.meta }
          })
        }
        if (route.children) {
          const tempTags = this.filterAffixTags(route.children, route.fullPath)
          if (tempTags.length >= 1) {
            tags = [...tags, ...tempTags]
          }
        }
      })
      return tags
    }
  }
}
</script>

<style lang="scss" src="./header.scss"></style>

<style lang="scss">
// 全局样式

// 强制覆盖箭头图标内容
.header-comp .tab-container {
  .el-tabs__nav-prev .el-icon-arrow-left:before {
    content: '\e707'!important;
  }
  .el-tabs__nav-next .el-icon-arrow-right:before {
    content: '\e706'!important;
  }
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
    &.logout {
      position: sticky;
      bottom: -5px;
      background: #fff;
      &:hover {
        color: #FF4A4D;
        cursor: pointer;
        background: #FEF5F6;
      }
    }
  }
}
</style>
