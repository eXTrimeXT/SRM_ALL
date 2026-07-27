<template>
  <div :class="classObj" class="app-wrapper">
    <!--布局方式为左Aside、右上Header、右下Main方式-->

    <div
      v-if="device === 'device-xs' && sidebar.opened"
      class="drawer-bg"
      @click="handleClickOutside"
    />

    <!--左侧菜单栏-->
    <Sidebar class="sidebar-container" />

    <!--右侧内容区-->
    <div :class="['main-container',{ hasTagsView: needTagsView && device !== 'device-xs' }]">
      <!--右上Header-->
      <div ref="sysHeader" :class="['sys-header', { 'fixed-header': fixedHeader }]">
        <!-- 头部信息 -->
        <AppHeader pageType="home" @themeConfig="themeConfigFn" />
      </div>

      <section
        class="app-main"
        :style="appMainStyle"
      >
        <AppMain ref="appMain" />
      </section>
    </div>
    <!-- 主题配置 -->
    <el-theme-select
      ref="themeSelect"
      v-model="themeSelectVisible"
      :defaultConfig="themeData"
      :extendFunc="extendFunc"
      @confirm="themeConfirm"
      @reset="setThemeHandle"
    />
    <!-- 智能助手 -->
    <div v-if="assistantFlag" ref="assistantBox" class="assistant-box">
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
        :class="{ 'show': showMyFrame, 'expand': expand, 'showPreview': showPreview, 'hide': !showMyFrame, 'fullSize': fullSize }"
      />
    </div>
  </div>
</template>

<script>
import Vue from 'vue'
import { AppMain, Navbar, Sidebar, Header } from './components'
import { mapState, mapGetters } from 'vuex'
import { themeDefault } from '@/config/logo-config'
import { getAssistantToken, getDictList } from '@/api/user'
import Postmate from 'postmate'
import { STORE_COMMON_CACHE } from '@/config/store-config'

export default {
  name: 'Layout',
  components: {
    AppMain,
    Navbar,
    Sidebar,
    AppHeader: Header
  },
  data () {
    return {
      appMainStyle: null,
      screenWidth: null,
      themeSelectVisible: false,
      themeDataBak: themeDefault,
      themeData: {},
      systemStyleId: null,
      iframeShow: false,
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
      dictList: [],
      userType: '',
      assistantFlag: false,
      hideTips: false,
      contentMap: [
        'hi，朋友',
        '我是慧采精灵',
        '有什么问题都可以问我哦',
        ''
      ],
      typeContent: '有什么问题都可以问我哦'
    }
  },
  computed: {
    ...mapState({
      sidebar: state => state.app.sidebar,
      device: state => state.app.device,
      showSettings: state => state.settings.showSettings,
      needTagsView: state => state.settings.tagsView,
      fixedHeader: state => state.settings.fixedHeader,
      showMyFrame: state => state.user.showMyFrame,
      isFirstClick: state => state.user.isFirstClick,
      loadingFlag: state => state.user.loadingFlag,
      showPreview: state => state.user.showPreview,
      expand: state => state.user.expand,
      fullSize: state => state.user.fullSize
    }),
    ...mapGetters(['navCollapse']),
    classObj () {
      return {
        hideSidebar: !this.sidebar.opened,
        openSidebar: this.sidebar.opened,
        withoutAnimation: this.sidebar.withoutAnimation
        // mobile: this.device === 'mobile'
      }
    }
  },
  watch: {
    device: {
      // immediate: true,
      handler (newValue) {
        if (newValue) {
          this.getStyle()
        }
      }
    },
    navCollapse: {
      // immediate: true,
      deep: true,
      handler (newValue) {
        this.getStyle()
      }
    },
    'elementPosition.y' (newVal, oldVal) {
      if (newVal > 179) {
        this.elementPosition.y = 180
      } else if (newVal < 310 - this.screenHeight) {
        this.elementPosition.y = 300 - this.screenHeight
      }
    }
  },
  async created () {
    this.themeData = this.themeDataBak
    this.getSystemStyle() // 查询用户主题配置
  },
  mounted () {
    this.getStyle()
    // const _that = this
    // window.addEventListener('resize', function () {
    //   return (() => {
    //     window.screenWidth = document.body.clientWidth
    //     _that.screenWidth = window.screenWidth
    //   })()
    // })
    // 监听鼠标事件
    this.$nextTick(() => {
      this.userType = this.$store.state.user.userType
      this.getDictListFun()
      this.typingArr(this.contentMap)
      this.restore()
      // this.$refs.assistant.addEventListener('mousedown', this.onMousedown, true)
      this.screenHeight = window.innerHeight
    })
  },
  destroyed () {
    // 移除监听鼠标事件
    if (this.$refs.assistant && this.$refs.assistant.removeEventListener) {
      this.$refs.assistant.removeEventListener('mousedown', this.onMousedown, true)
    }
  },
  methods: {
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
    getDictListFun () {
      getDictList({ code: 'AI_HELPER' }).then(res => {
        const data = res.data
        if (this.userType === 'BUYER') {
          this.assistantFlag = data.filter(item => item.dictItemCode === 'BUYER_HELPER').map(item => item.dictItemMark)[0] === 'Y'
        } else if (this.userType === 'VENDOR') {
          this.assistantFlag = data.filter(item => item.dictItemCode === 'VENDOR_HELPER').map(item => item.dictItemMark)[0] === 'Y'
        }
      })
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
    handleClick () {
      if (Vue.prototype.embedHelper) {
          setTimeout(() => {
              Vue.prototype.embedHelper.call('navTabIndex', 0)
          }, 300)
      }
      this.$handleFrameClick()
    },
    handleClickOutside () {
      this.$store.dispatch('app/closeSideBar', { withoutAnimation: false })
    },
    getStyle () {
      setTimeout(() => {
        let sysHeaderH = this.$refs.sysHeader.clientHeight // 系统头高度
        const height = `calc(100vh - ${sysHeaderH}px)`
        this.appMainStyle = { height }
        this.$store.dispatch('app/setHeaderHeight', sysHeaderH)
      }, 500)
    },
    // 主题开始
    // 接口查询主题
    async getSystemStyle () {
      const { data } = await this.$api.base.themeConfig.getSystemStyle()
      const { theme, leftMenuContent, pageContent, systemStyleId } = data
      this.systemStyleId = systemStyleId
      this.themeData = {
        theme: theme || this.themeDataBak.theme,
        leftMenuContent: leftMenuContent || this.themeDataBak.leftMenuContent,
        pageContent: pageContent || this.themeDataBak.pageContent
      }
      console.log(this.themeData)
    },
    /* 主题配置 */
    themeConfigFn (visible) {
      this.themeSelectVisible = visible
    },
    // 更新主题
    updateSystemStyle (themeData = {}) {
      let systemStyleId = this.systemStyleId
      if (systemStyleId) { // 更新主题
        let paramsData = {
          systemStyleId: systemStyleId,
          ...themeData
        }
        this.$api.base.themeConfig.updateSystemStyle(paramsData).then((res) => {
          this.$message({
            type: 'success',
            message: res.message
          })
        })
      } else { // 新增主题
        this.$api.base.themeConfig.addSystemStyle(themeData).then((res) => {
          this.$message({
            type: 'success',
            message: res.message
          })
        })
      }
    },
    /* 主题配置确认 */
    themeConfirm (themeData) {
      this.updateSystemStyle(themeData) // 数据库更新
    },
    setThemeHandle () {
      this.themeData = this.themeDataBak
    },

    /* 返回待补充样式 */
    extendFunc (config) {
      return this.generateCssText(config)
    },
    generateCssText (config) {
      const { theme, pageContent, leftMenuContent } = config || {}
      let str = ''
      let themeStyle = `
        .user-menu-dropdown .el-dropdown-menu__item:hover {color: ${theme};}
        .setting-button:hover {color: ${theme};}
        .custom-table-header .custom-table-header__btn:hover {color: ${theme};}
        .custom-table-header__button .custom-table-header .custom-table-header__btn:hover{color: ${theme};}
        .quick-search-btn.quick-edit:hover .iconfont{color: ${theme};}
        .srm-body .themeColor{color: ${theme}}
        .srm-body .themeLink:hover{color: ${theme}}
        .srm-body .vxe-table .vxe-sort--asc-btn.sort--active,
        .srm-body .vxe-table .vxe-sort--desc-btn.sort--active {
          color: ${theme};
        }
        .srm-body .status-list-block .status-active .status-btn{
          border-color: ${theme};
          background: ${theme};
        }
        .srm-body .status-list-block .status-active .status-text{
          color: ${theme};
        }
        .srm-body .is--checked.vxe-custom--option .vxe-checkbox--icon:before,
        .srm-body .is--checked.vxe-export--panel-column-option .vxe-checkbox--icon:before,
        .srm-body .is--checked.vxe-table--filter-option .vxe-checkbox--icon:before,
        .srm-body .is--indeterminate.vxe-custom--option .vxe-checkbox--icon:before,
        .srm-body .is--indeterminate.vxe-export--panel-column-option .vxe-checkbox--icon:before,
        .srm-body .is--indeterminate.vxe-table--filter-option .vxe-checkbox--icon:before,
        .srm-body .vxe-table--render-default .is--checked.vxe-cell--checkbox .vxe-checkbox--icon:before,
        .srm-body .vxe-table--render-default .is--indeterminate.vxe-cell--checkbox .vxe-checkbox--icon:before,
        .srm-body .vxe-table--render-default .is--checked.vxe-cell--radio .vxe-radio--checked-icon:before {
          border-color: ${theme};
        }
        .srm-body .is--checked.vxe-checkbox,
        .srm-body .is--checked.vxe-checkbox .vxe-checkbox--icon,
        .srm-body .is--checked.vxe-custom--option,
        .srm-body .is--checked.vxe-custom--option .vxe-checkbox--icon,
        .srm-body .is--checked.vxe-export--panel-column-option,
        .srm-body .is--checked.vxe-export--panel-column-option .vxe-checkbox--icon,
        .srm-body .is--checked.vxe-table--filter-option,
        .srm-body .is--checked.vxe-table--filter-option .vxe-checkbox--icon,
        .srm-body .is--indeterminate.vxe-checkbox,
        .srm-body .is--indeterminate.vxe-checkbox .vxe-checkbox--icon,
        .srm-body .is--indeterminate.vxe-custom--option,
        .srm-body .is--indeterminate.vxe-custom--option .vxe-checkbox--icon,
        .srm-body .is--indeterminate.vxe-export--panel-column-option,
        .srm-body .is--indeterminate.vxe-export--panel-column-option .vxe-checkbox--icon,
        .srm-body .is--indeterminate.vxe-table--filter-option,
        .srm-body .is--indeterminate.vxe-table--filter-option .vxe-checkbox--icon,
        .srm-body .vxe-table--render-default .is--checked.vxe-cell--checkbox,
        .srm-body .vxe-table--render-default .is--checked.vxe-cell--checkbox .vxe-checkbox--icon,
        .srm-body .vxe-table--render-default .is--indeterminate.vxe-cell--checkbox,
        .srm-body .vxe-table--render-default .is--indeterminate.vxe-cell--checkbox .vxe-checkbox--icon {
          color: ${theme};
        }
        .srm-body .vxe-checkbox:not(.is--disabled):hover .vxe-checkbox--icon,
        .srm-body .vxe-custom--option:not(.is--disabled):hover .vxe-checkbox--icon,
        .srm-body .vxe-export--panel-column-option:not(.is--disabled):hover .vxe-checkbox--icon,
        .srm-body .vxe-table--filter-option:not(.is--disabled):hover .vxe-checkbox--icon,
        .srm-body .vxe-table--render-default .vxe-cell--checkbox:not(.is--disabled):hover .vxe-checkbox--icon {
          color: ${theme};
        }
        .srm-body .vxe-table--render-default .vxe-cell--radio:not(.is--disabled):hover .vxe-radio--icon:before,
        .srm-body .vxe-custom--option:not(.is--disabled):hover .vxe-checkbox--icon:before,
        .srm-body .vxe-export--panel-column-option:not(.is--disabled):hover .vxe-checkbox--icon:before,
        .srm-body .vxe-table--filter-option:not(.is--disabled):hover .vxe-checkbox--icon:before,
        .srm-body .vxe-table--render-default .vxe-cell--checkbox:not(.is--disabled):hover .vxe-checkbox--icon:before{
          border-color: ${theme};
        }
        .srm-body .vxe-loading>.vxe-loading--chunk{
          color: ${theme};
        }
        .srm-body .vxe-loading .vxe-loading--spinner:after,
        .srm-body .vxe-loading .vxe-loading--spinner:before{
          background-color: ${theme};
        }
        `
      for (let item of [pageContent, leftMenuContent]) {
        Object.keys(item).forEach(k => {
          let v = item[k] || '#fff'
          switch (k) {
          case 'activeBgColor':
            str += `
                .srm-body .el-menu--vertical >.el-menu--popup >.menu-wrapper.nest-menu .el-submenu.is-active .el-submenu__title {
                  background-color: ${v};
                }
              `
            break
          case 'tableBorderColor':
            str += `
                .srm-body .vxe-table--render-default.border--full .vxe-body--column,
                .srm-body .vxe-table--render-default.border--full .vxe-footer--column,
                .srm-body .vxe-table--render-default.border--full .vxe-header--column {
                  background-image: linear-gradient(${v}, ${v}), linear-gradient(${v}, ${v})
                }
              `
            break
          case 'tableHeaderBgColor':
            str += `
                .srm-body .vxe-table--render-default.border--default .vxe-table--header-wrapper,
                .srm-body .vxe-table--render-default.border--full .vxe-table--header-wrapper,
                .srm-body .vxe-table--render-default.border--outer .vxe-table--header-wrapper,
                .srm-body .vxe-table--render-default.border--full .render-table__table--header-sticky {
                  background-color: ${v}
                }
              `
            break
          case 'tableTitleColor':
            str += `
                .srm-body .vxe-table .vxe-table--header-wrapper {
                  color: ${v}
                }`
            break
          case 'tableRowCurrentBgColor': // 当前选中背景色
            str += `
                .srm-body .vxe-table--render-default .vxe-body--row.row--current,
                .srm-body .vxe-table--render-default .vxe-body--row.row--current .render-table__table--sticky-left,
                .srm-body .vxe-table--render-default .vxe-body--row.row--current .render-table__table--sticky-right,
                .srm-body .vxe-table--render-default .vxe-body--row.row--stripe.row--current,
                .srm-body .vxe-table--render-default .vxe-body--row.row--stripe.row--current .render-table__table--sticky-left,
                .srm-body .vxe-table--render-default .vxe-body--row.row--stripe.row--current .render-table__table--sticky-right {
                  background-color: ${v}
                }`
            break
          case 'tableRowHoverBgColor': // hover颜色
            str += `
                .srm-body .vxe-table--render-default .vxe-body--row.row--hover,
                .srm-body .vxe-table--render-default .vxe-body--row.row--hover .render-table__table--sticky-left,
                .srm-body .vxe-table--render-default .vxe-body--row.row--hover .render-table__table--sticky-right,
                .srm-body .vxe-table--render-default .vxe-body--row.row--hover.row--stripe,
                .srm-body .vxe-table--render-default .vxe-body--row.row--hover.row--stripe .render-table__table--sticky-left,
                .srm-body .vxe-table--render-default .vxe-body--row.row--hover.row--stripe .render-table__table--sticky-right{
                  background-color: ${v}
                }`
            break
          case 'tableRowStripedBgColor': // 隔行颜色
            str += `
                .srm-body .vxe-table--render-default .vxe-body--row.row--stripe {
                  background-color: ${v}
                }`
            break
          case 'bgColor': // 菜单背景色
            str += `
                .srm-body #app .sidebar-container {
                  background: ${v}
                }`
            break
          case 'unfoldColor': // 菜单展开二级背景色
            str += `
                .srm-body #app .sidebar-container .el-submenu .el-menu{
                  background: ${v} !important
                }`
            break
          case 'inputColor': // 表单输入框边框
            str += `
                .srm-body .vxe-checkbox .vxe-checkbox--icon,
                .srm-body .vxe-custom--option .vxe-checkbox--icon,
                .srm-body .vxe-export--panel-column-option
                .srm-body .vxe-checkbox--icon,
                .srm-body .vxe-table--filter-option .vxe-checkbox--icon,
                .srm-body .vxe-table--render-default .vxe-cell--checkbox .vxe-checkbox--icon{
                  color: ${v}
                }`
            break
          }
        })
      }
      let resStr = str + themeStyle
      return resStr.replace(/[\r\n]|\s{2,}/g, '')
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/mixin.scss';
@import "@/styles/variables.scss";

.app-wrapper {
  @include clearfix;
  position: relative;
  height: 100%;
  width: 100%;
  .drawer-bg {
    background: #000;
    opacity: 0.3;
    width: 100%;
    top: 0;
    height: 100%;
    position: absolute;
    z-index: 999;
  }
  .fixed-header {
    position: fixed;
    top: 0;
    right: 0;
    z-index: 9;
    width: calc(100% - #{$sideBarWidth});
    transition: width 0.28s;
  }
  // 隐藏侧边栏
  &.hideSidebar{
    .fixed-header {
      width: calc(100% - 54px);
    }
  }
  .sys-header{
    //line-height: 1;
    height: 48px;
    box-shadow: 0 1px 8px 0 rgba(42,64,89,0.10);
    position: relative;
    z-index: 3;
  }
}

.smartHelper {
  position: fixed;
  right: 15px;
  bottom: 100px;
  z-index: 10000;
}
.app-main {
  background-color: #edeff2;
  overflow: auto;
  width: 100%;
  position: relative;
  font-size: 12px;
  box-sizing: border-box;
}
.hasTagsView {
  .fixed-header + .app-main {
    padding-top: 99px;
  }
}
// 移动小屏幕
.device-xs {
  .app-wrapper{
    .openSidebar {
      position: fixed;
      top: 0;
    }
    .fixed-header {
      width: 100%;
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
    background: url(./../assets/images/zhushou.gif) center center no-repeat;
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
    &.fullSize {
      width: 100%;
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
