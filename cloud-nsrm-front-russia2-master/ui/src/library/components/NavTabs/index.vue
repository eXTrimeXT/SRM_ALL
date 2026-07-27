<!--
 * @Author: tim
 * @LastEditors: linyk7 && linyk7@meicloud.com
 * @Description: 页签
 * @Date: 2019-03-20 15:17:58
 * @LastEditTime: 2022-12-23 16:04:46
-->

<template>
  <div ref="secondMenuTabs" :class="['menu-tabs-nav',{'moreContent': isFixedFirstItem}]">
    <span
      v-if="isFixedFirstItem"
      :class="['el-tabs__item firstTabItem', {'is-active': activeTab == firstItemName,'is-shadow': isOverWidth}]"
      @click="firstTabItemClick"
    >
      {{ firstItemText }}
    </span>
    <el-tabs
      ref="menuTabsNav"
      v-model="activeTab"
      class="tabs-nav"
      type="card"
      @tab-click="tabClick"
      @tab-remove="tabRemove"
    >
      <el-tab-pane
        v-for="item in tabs"
        :key="item.name"
        :label="typeof item.title === 'function' ? item.title() : item.title"
        :name="item.name"
        :closable="item.closable === false ? false : true"
        :lazy="true"
        :style="item.ctrlHeight ? getStyleDetail : getStyleList"
      >
        <keep-alive :include="tabs">
          <component
            :is="item.component"
            :active-tab="activeTab"
            :params="item.params"
            :tabName="item.name"
            :changeTab="changeTab"
            @tab-show="tabShow"
            @tab-add="tabAdd"
            @tab-remove="tabRemove"
          />
        </keep-alive>
      </el-tab-pane>
    </el-tabs>
    <!-- 全屏icon -->
    <span class="screenfull-icon">
      <svg-icon
        :icon-class="isFullscreen ? 'exit-fullscreen':'fullscreen'"
        @click="screenFullClick"
      />
    </span>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Cookies from 'js-cookie'

export default {
  name: 'NavTabs',
  components: {},
  props: {
    // 属性传值时用法：cur-tab 或 curTab
    curTab: {
      type: String,
      default: 'list'
    },
    tabsList: {
      type: Array,
      default: function () {
        return []
      }
    },
    tabClick: {
      type: Function,
      default: function () {
        return null
      }
    },
    // 设置固定项
    isFixedFirstItem: {
      type: Boolean,
      default: true
    }
  },
  data () {
    return {
      activeTab: '',
      tabs: [],
      changeTab: 0,
      isOverWidth: false, // 是否超过95%
      isFullscreen: false
    }
  },
  computed: {
    ...mapGetters(['navCollapse', 'language']),
    getStyleDetail () {
      // 顶部导航50 + 标签页 39 + 52
      // const height = this.navCollapse.opened
      //   ? 'calc(100vh - 141px)'
      //   : 'calc(100vh - 91px)'
      // return { height }
      // 顶部导航固定 48 + 40 + 28
      return { height: 'calc(100vh - 116px)', paddingBottom: '40px' }
    },
    getStyleList () {
      return { height: 'calc(100vh - 116px)' }
    },
    firstItemText () {
      let firstItem = this.tabs[0]
      return typeof firstItem.title === 'function' ? firstItem.title() : firstItem.title
    },
    firstItemName () {
      let firstItem = this.tabs[0]
      return firstItem.name
    }
  },
  watch: {
    activeTab: {
      handler (tab) {
        this.changeTab += 1
        this.$emit('tab-change', tab)
      }
    },
    language: {
      handler (lang) {
        this.setTabHeaderPd()
      }
    }
  },
  created () {
    this.activeTab = this.curTab
    this.tabs = this.tabsList
  },
  mounted () {
    let that = this
    this.$nextTick(() => {
      that.setTabHeaderPd()
    })
    window.addEventListener('fullscreenchange', () => {
      if (!that.checkFull()) {
        that.isFullscreen = false
        that.exitFullscreenStyle() // 退出全屏后样式处理
      }
    })
  },
  destroy () {
    window.removeEventListener('fullscreenchange', null)
  },
  methods: {
    // 判断浏览器是否处于全屏状态
    checkFull () {
      // 火狐浏览器、谷歌浏览器及Webkit内核浏览器
      let isFull = document.mozFullScreen ||
                  document.fullScreen ||
                  document.webkitIsFullScreen ||
                  document.webkitRequestFullScreen ||
                  document.mozRequestFullScreen ||
                  document.msFullscreenEnabled
      if (isFull === undefined) {
        isFull = false
      }
      return isFull
    },
    // 控制浏览器是否全屏
    screenFullClick () {
      let element = document.documentElement
      if (this.isFullscreen) { // 全屏状态下面退出全屏
        if (document.exitFullscreen) { // W3C
          document.exitFullscreen()
        } else if (document.mozCancelFullScreen) { // FireFox
          document.mozCancelFullScreen()
        } else if (document.webkitCancelFullScreen) { // Chrome等
          document.webkitCancelFullScreen()
        } else if (document.msExitFullscreen) { // IE11
          document.msExitFullscreen()
        }
        this.exitFullscreenStyle() // 退出后样式处理
      } else { // 非全屏则切换全屏
        if (element.requestFullscreen) { // W3C
          element.requestFullscreen()
        } else if (element.webkitRequestFullScreen) { // Chrome等
          element.webkitRequestFullScreen()
        } else if (element.mozRequestFullScreen) { // FireFox
          element.mozRequestFullScreen()
        } else if (element.msRequestFullscreen) { // IE11
          element.msRequestFullscreen()
        }
        document.querySelector('.sidebar-container').style.display = 'none'
        document.querySelector('#app .main-container').style.marginLeft = '0px'
        document.querySelector('.sys-header').style.display = 'none'
      }
      this.isFullscreen = !this.isFullscreen
    },
    // 退出全屏后样式
    exitFullscreenStyle () {
      // 判断左边菜单是否有回缩
      const sidebarStatus = Cookies.get('sidebarStatus') // 1伸展，0缩进
      document.querySelector('.sidebar-container').style.display = 'block'
      document.querySelector('#app .main-container').style.marginLeft = sidebarStatus == 0 ? '52px' : '190px'
      document.querySelector('.sys-header').style.display = 'block'
    },
    // 获取第一个tab 的文字宽度
    getFirstItemLength (str) {
      // 文字宽度 + padding + 预留宽度 第一个item 不可删除
      return this.getTextWidth(str) + 30 + 16 + 8
    },
    getTextWidth (str) {
      let canvas = document.createElement('canvas')
      let context = canvas.getContext('2d')
      context.font = '12px Arial'
      let metrics = context.measureText(str)
      return metrics.width
    },
    // 动态设置tab header的padding
    setTabHeaderPd () {
      if (this.isFixedFirstItem) {
        let firstItemLength = this.getFirstItemLength(this.firstItemText)
        let navHeaderDom = this.$refs.menuTabsNav.$el.children[0]
        navHeaderDom.style = 'padding-left:' + firstItemLength + 'px'
        // console.log(navHeaderDom)
        // console.log('文字长度')
        // console.log(firstItemLength)
      }
    },
    // 判断是否超过长度
    isOverWidthFn () {
      if (this.isFixedFirstItem) {
        let itemLength = this.tabs.length // 打开单据个数
        let allText = ''
        this.tabs.forEach(item => {
          let title = item.title === 'function' ? item.title() : item.title
          allText += title
        })
        let allTextLength = this.getTextWidth(allText)
        let blankWidth = 30 + 45 * (itemLength - 1)
        let allItemLength = allTextLength + blankWidth
        let navHeaderWidth = this.$refs.menuTabsNav.$el.children[0].clientWidth
        let widthPercent = allItemLength / navHeaderWidth
        if (widthPercent > 0.9) {
          this.isOverWidth = true
        } else {
          this.isOverWidth = false
        }
        // console.log('allItemLength', allItemLength)
        // console.log('navHeaderWidth', navHeaderWidth)
        // console.log('widthPercent', widthPercent)
      }
    },
    // 添加标签
    tabAdd ({ title, name, component, closable = true, ctrlHeight = false, params = {} }) {
      let activeTab = this.activeTab

      name = name || [new Date().getTime(), Math.random()].join('-')

      const findTabs = this.tabs.find(v => (v.name === name))
      const tabObj = {
        title,
        name,
        component,
        params,
        closable,
        ctrlHeight
      }
      let tabFlag = params.flag ? params.flag : ''
      if (findTabs) {
        let curTabFlag = findTabs.params && findTabs.params.flag ? findTabs.params.flag : ''
        // 操作类型不一致 关掉在打开，操作类型一直，直接显示
        if (tabFlag !== curTabFlag) {
          this.tabRemove(name)
          setTimeout(() => {
            this.tabs.push(tabObj)
            this.activeTab = name
          }, 200)
        } else {
          this.activeTab = name
        }
      } else {
        this.tabs.push(tabObj)
        this.activeTab = name
      }
      // 计算宽度
      this.$nextTick(() => {
        this.isOverWidthFn()
      })
    },
    // 点击标签
    /* tabClick (tab) {
        this.$emit('tab-click', tab)
      }, */
    // 移除标签
    tabRemove (name) {
      let tabs = this.tabs
      let activeTab = this.activeTab
      if (activeTab === name) {
        tabs.forEach((tab, index) => {
          if (tab.name === name) {
            let nextTab = tabs[index + 1] || tabs[index - 1]
            if (nextTab) {
              activeTab = nextTab.name
            }
          }
        })
      }
      this.activeTab = activeTab
      this.tabs = tabs.filter(tab => tab.name !== name)
      this.$emit('tab-remove', { name, activeTab, tabs })
      // 计算宽度
      this.$nextTick(() => {
        this.isOverWidthFn()
      })
    },
    // 切换当前 tab 页
    tabShow (name) {
      const findTabs = this.tabs.find(v => {
        return v.name === name
      })
      if (findTabs) {
        this.activeTab = name
        this.$emit('tab-show', name)
      } else {
        console.log('cant not find the tab name:' + name)
      }
    },
    firstTabItemClick () {
      this.activeTab = this.firstItemName

      this.$emit('first-tab-active', this.firstItemName)
    }
  }
}
</script>

<style lang="scss">
.tabs-nav.el-tabs{
  &.el-tabs--card{
    > .el-tabs__header {
      border: 0;
      padding-left: 16px;
      padding-right: 0px;
      margin: 0;
      height: 40px;
      > .el-tabs__nav-wrap{
        margin-bottom: 0px;
        padding-top: 8px;
        // padding-left: 16px;
        padding-right: 72px;
        &.is-scrollable{
          .el-tabs__nav-next, .el-tabs__nav-prev{
            line-height: 32px;
            font-size: 14px;
          }
          .el-tabs__nav-prev{
            right: 50px;
            left: auto !important;
            .el-icon-arrow-left{
              position: relative;
              top: 2px;
              &:before {
                content: '\e707'!important; // 强制覆盖箭头图标内容
              }
            }
            &::before{
              content: " ";
              position: absolute;
              height: 40px;
              box-shadow: -3px 0 4px 0 rgba(42,64,89,0.10);
              top: -3px;
              left: -4px;
              width: 1px;
              z-index: 200;
              font-size: 0px;
              background:transparent;
            }
            &::after{
              content: " ";
              position: absolute;
              height: 10px;
              width: 70px;
              background: #fff;
              top: -8px;
              left: -4px;
              z-index: 200;
            }
          }
          .el-tabs__nav-next{
            right: 38px;
            .el-icon-arrow-right{
              position: relative;
              top: 2px;
              &:before {
                content: '\e706'!important; // 强制覆盖箭头图标内容
              }
            }
          }
          &::before{
            display: none;
            content: " ";
            position: absolute;
            height: 10px;
            width: 16px;
            background: #fff;
            top: 0px;
            left: 0px;
            z-index: 200;
          }
          &::after{
            display: none;
            content: " ";
            position: absolute;
            height: 40px;
            box-shadow: 3px 0 4px 0 rgba(42,64,89,0.10);
            top: 0px;
            left: 15px;
            width: 1px;
            z-index: 300;
            font-size: 0px;
            background:transparent;
          }
        }
      }
      .el-tabs__nav{
        border: 0;
        border-top-left-radius: 0 !important;
        .el-tabs__item{
          height: 32px;
          line-height: 32px !important;
          background: #fff;
          border: 0;
          padding: 0 15px !important;
          border-radius: 4px 4px 0px 0px;
          margin-right: 0px;
          font-weight: normal;
          color: #51555B;
          font-size: 12px;
          &::after{
            content: " ";
            height: 16px;
            width: 0px;
            font-size: 0;
            border-right: 1px solid #DCDDDE;
            position: absolute;
            top: 8px;
            left: 0px;
          }
          &:first-child{
            &::after{
              display: none;
            }
          }
          &:hover{
            background: #F1F2F2;
            // color: #393E45;
            // color: #0077FF;
            &::after{
              display: none;
            }
            + .el-tabs__item{
              &::after{
                display: none;
              }
            }
          }
          &.is-active{
            background: #edeff2;
            // color: #393E45;
            color: #0077FF;
            font-weight: bold;
            &::after{
              display: none;
            }
            + .el-tabs__item{
              &::after{
                display: none;
              }
            }
          }
          &.is-disabled {
            color: #c0c4cc;
          }
          .el-icon-close{
            font-size: 12px;
            width: 12px;
            height: 12px;
            line-height: 12px;
            color: #B9BABD;
            &:hover {
              color: #161C24;
              // background-color: #63abfd;
            }
          }
        }
      }
    }
    > .el-tabs__content{
      padding: 16px 16px 12px;
      background: #edeff2;
      height: calc(100vh - 88px); // 48 40
      > .el-tab-pane{
        padding: 16px 16px 12px;
        background: #fff;
        height: 100%;
      }
    }
  }
}
.menu-tabs-nav{
  // 内容超过95%
  &.moreContent{
    .firstTabItem{
      font-weight: normal;
      color: #51555B;
      font-size: 12px;
      position: absolute;
      left: 16px;
      top: 8px;
      padding-bottom: 0;
      z-index: 1000;
      height: 32px;
      line-height: 32px !important;
      background: #fff;
      border: 0;
      padding: 0 15px !important;
      border-radius: 4px 4px 0px 0px;
      margin-right: 0px;
      font-weight: normal;
      color: #51555B;
      font-size: 12px;
      &.is-active {
        background: #edeff2;
        // color: #393E45;
        color: #0077FF;
        font-weight: bold;
      }
      &.is-shadow{
        &::before{
          content: " ";
          position: absolute;
          height: 8px;
          background: #fff;
          top: -8px;
          left: -16px;
          right: -3px;
          z-index: 200;
        }
        &::after{
          content: " ";
          position: absolute;
          height: 40px;
          box-shadow: 3px 0 4px 0 rgba(42,64,89,0.10);
          top: -8px;
          right: -3px;
          width: 1px;
          z-index: 300;
          font-size: 0px;
          background:transparent;
        }
      }
      &:hover{
        background: #F1F2F2;
        // color: #393E45;
        // color: #0077FF;
        &.is-shadow{
          &::before{
            display: none;
          }
          &::after{
            display: none;
          }
        }
      }
    }
    .el-tabs__item {
      &.is-active {
        background: #edeff2 !important;
        color: #0077FF !important;
        font-weight: bold !important;
        .el-icon-close {
          color: #0077FF !important;
        }
      }
    }
    .tabs-nav.el-tabs{
      &.el-tabs--card{
        > .el-tabs__header {
          padding-left: 100px;
          .el-tabs__nav{
            .el-tabs__item{
              &:first-child{
                display: none;
              }
            }
          }
        }
      }
    }
  }
  .screenfull-icon{
    position: absolute;
    right: 16px;
    top: 13px;
    padding: 2px;
    cursor: pointer;
    svg{
      font-size: 12px;
      color: #96999C;
    }
  }
}

</style>
