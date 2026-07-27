<template>
  <el-collapse-transition>
    <div
      v-show="navCollapse.opened"
      class="navbar"
    >
      <!-- 切换菜单开合 -->
      <Hamburger
        id="hamburger-container"
        :is-active="sidebar.opened"
        class="hamburger-container"
        @toggleClick="toggleSideBar"
      />
      <!-- 面包屑导航 -->
      <!--    <breadcrumb id="breadcrumb-container" class="breadcrumb-container" />-->
      <!-- <tags-view /> -->
      <!-- 右侧设置信息 -->
      <div class="right-menu">
        <template v-if="device !== 'device-xs'">
          <!-- <search id="header-search" class="right-menu-item" /> -->
          <!-- <error-log class="errLog-container right-menu-item hover-effect" /> -->
          <!-- <screenfull id="screenfull" class="right-menu-item hover-effect" /> -->
          <div class="right-menu-item right-menu-star">
            {{ configData.webName }}
          </div>
          <!-- 即时聊天 -->
          <div class="right-menu-item right-menu-star srmIM">
            <img
              src="../../assets/images/srmIM.png"
              @click="toChatHandel"
            >
          </div>
          <!-- 小助手 -->
          <SmartHelper
            reference-class="helpIconClass"
            class="smartHelper right-menu-item hover-effect"
          >
            <span slot="content" class="referenceName">
              <!-- 帮助中心 -->
              {{ $t("helper.helpCenter") }}
              <i class="el-icon-caret-bottom" style="margin-left: 8px;" />
            </span>
          </SmartHelper>
          <!-- 多语言 -->
          <LangSelect class="right-menu-item hover-effect" />
        </template>
        <!-- trigger="click" -->
        <!-- 用户信息 -->
        <el-dropdown class="avatar-container right-menu-item hover-effect" @command="userHandleCommand">
          <div class="avatar-wrapper">
            <img
              src="../../assets/images/user.png"
              class="userIcon"
            >
            <span class="userName">{{ username }}</span>
            <i class="el-icon-caret-bottom el-icon--right8" />
          </div>
          <el-dropdown-menu
            slot="dropdown"
            class="user-dropdown"
          >
            <el-dropdown-item command="profile">
              <!-- 个人资料 -->
              {{ $t("base.navbar.profile") }}
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <!-- 退出登录 -->
              {{ $t("base.navbar.logOut") }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
        <img
          v-if="device !== 'device-xs'"
          src="../../assets/logo/navClose/navClose@2x.png"
          class="nav-close"
          @click="navClose"
        >
      </div>
      <srmIM
        v-if="chatVisibale"
        :visible.sync="chatVisibale"
        @closeIm="closeImHandel"
      />
    </div>
  </el-collapse-transition>
</template>

<script>
import * as path from '@/utils/path'
import { mapGetters, mapState } from 'vuex'
import SmartHelper from 'lib@/components/smartHelper'
import srmIM from 'lib@/components/webIM'
import Hamburger from '@/components/Hamburger'
import LangSelect from '@/components/LangSelect'
import config from '@/config/user.env'
import { isSinglePoint, singlePointLogoutUrl } from '@/config/sysConfig'

// import TagsView from "./TagsView";

// import Breadcrumb from '@/components/Breadcrumb'
// import ErrorLog from '@/components/ErrorLog'
// import Screenfull from '@/components/Screenfull'
// import SizeSelect from '@/components/SizeSelect'
// import Search from '@/components/HeaderSearch'
export default {
  components: {
    // Breadcrumb,
    // TagsView,
    Hamburger,
    LangSelect,
    SmartHelper,
    srmIM
    // ErrorLog,
    // Screenfull,
    // SizeSelect,
    // Search
  },
  data () {
    return {
      configData: config,
      chatVisibale: false
    }
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'username', // 用户名
      // 'avatar', // 用户头像
      'device',
      'navCollapse'
    ]),
    ...mapState({
      isPC: (state) => state.settings.isPC,
      entrance: (state) => state.user.entrance
    })
  },
  methods: {
    navClose () {
      this.$store.dispatch('app/toggleNav')
    },
    toggleSideBar () {
      this.$store.dispatch('app/toggleSideBar')
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
      if (isSinglePoint === 'N') { // 内部页面登录后退出
        this.innerLogout()
      } else { // 单点登录退出 保留原来登录方式 退出的方式从哪里进来就回到哪里
        if (this.entrance === 'inside') {
          this.innerLogout()
        } else {
          this.singlePointLogout()
        }
      }
    },
    // 启动聊天
    toChatHandel () {
      this.chatVisibale = true
    },
    closeImHandel (data) {
      this.chatVisibale = data
    },
    userHandleCommand (val) {
      if (val === 'profile') {
        this.$router.push({ path: path.resolve('/userManage/profile') })
      }
      if (val === 'logout') {
        this.logout()
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  height: 50px;
  /*overflow: hidden;*/
  position: relative;
  background: #fff;
  border-bottom: 1px solid #d8dce5;
  // box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);

  .nav-close {
    width: 22px;
    height: 22px;
    cursor: pointer;
    margin-right: 18px;
  }

  .hamburger-container {
    line-height: 40px;
    cursor: pointer;
    transition: background 0.3s;
    -webkit-tap-highlight-color: transparent;
    padding: 0 10px !important;
    &:hover {
      background: rgba(0, 0, 0, 0.025);
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .right-menu {
    display: flex;
    align-items: center;
    line-height: 39px;
    height: 100%;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 15px;
      height: 100%;
      border-right: 1px solid #dfe3e6;
      color: #5a5e66;
      vertical-align: top;
      &.hover-effect {
        cursor: pointer;
        transition: background 0.3s;
        &:hover {
          background: rgba(0, 0, 0, 0.025);
        }
      }
    }
    .right-menu-star {
      font-size: 14px;
      display: flex;
      color: #414d55;
      align-items: center;
      font-weight: 400;
      border-right: 1px solid #dfe3e6;
      padding-right: 15px;
    }

    .avatar-container {
      margin-right: 17px;
      display: flex;
      align-items: center;
      .el-icon--right8 {
        margin-left: 8px;
      }
      .avatar-wrapper {
        display: flex;
        align-items: center;
        height: 100%;
        .el-icon-caret-bottom {
        }
      }
    }
    .userIcon {
      width: 24px;
      height: 24px;
    }
    .userName {
      padding-left: 8px;
      font-size: 14px;
      vertical-align: top;
    }
  }
}
.user-dropdown {
  margin-top: 10px;
}
.smartHelper {
  display: inline-block;
  vertical-align: text-bottom;
  font-size: 14px;
  .referenceName {
    font-size: 14px;
  }
}
.srmIM img{
  cursor: pointer;
}
</style>
