<template>
  <div
    class="sidebar-logo-container"
    :class="{'collapse':collapse}"
  >
    <transition name="sidebarLogoFade">
      <router-link
        v-if="collapse"
        key="collapse"
        class="sidebar-logo-link"
        to="/"
      >
        <h1 class="sidebar-title">
          <div class="zh">
            {{ title }}
          </div>
        </h1>
      </router-link>
      <router-link
        v-else
        key="expand"
        class="sidebar-logo-link"
        to="/"
      >
        <img
          v-if="logo"
          :src="logo"
          class="sidebar-logo"
        >
        <h1 class="sidebar-title">
          <div class="zh">
            {{ title }}
          </div>
          <div class="en">
            {{ titleEn }}
          </div>
        </h1>
      </router-link>
    </transition>
  </div>
</template>

<script>
import logoSrc from '@/assets/logo/logo@2x.png'
export default {
  name: 'SidebarLogo',
  props: {
    collapse: {
      type: Boolean,
      required: true
    }
  },
  data () {
    return {
      title: 'SRM云',
      titleEn: 'SRM Cloud',
      logo: logoSrc // 'iconfont iconscc-quality-ment sidebar-logo'// logoSrc
    }
  }
}
</script>

<style lang="scss" scoped>
.sidebarLogoFade-enter-active {
  transition: opacity 1.5s;
}

.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.sidebar-logo-container {
  position: relative;
  width: 100%;
  height: 60px;
  line-height: 60px;
  background: #2b2f3a;
  text-align: center;
  overflow: hidden;
  border-bottom: 1px solid rgba(255,255,255,0.3);
  & .sidebar-logo-link {
    height: 100%;
    width: 100%;
    & .sidebar-logo {
      width: 79px;
      height: 30px;
      vertical-align: middle;
      margin-right: 12px;
      color: #fff;
    }
    & .sidebar-title {
      display: inline-block;
      margin: 0;
      color: #fff;
      font-weight: normal;
      vertical-align: middle;
      position: relative;
      &::before{
        position: absolute;
        width: 1px;
        height: 14px;
        background: rgba(255,255,255,0.3);
        left: -1px;
        top: 3px;
        content: " ";
      }
      .zh{
        font-size: 12px;
        line-height: 14px;
        text-align: left;
        padding-left: 9px;
      }
      .en{
        font-size: 12px;
        line-height: 12px;
        transform: scale(0.7);
        -webkit-transform: scale(0.7);
      }
    }
  }

  &.collapse {
    .sidebar-logo {
      margin-right: 0px;
    }
    & .sidebar-title{
      &::before{
        display: none;
      }
      .zh{
        padding-left: 0;
      }
    }
  }
}
</style>
