<template>
  <div
    class="sidebar-logo-container"
    :class="{'collapse':collapse}"
  >
    <router-link
      key="homeLink"
      class="sidebar-logo-link"
      to="/"
    >
      <ThemeConf
        class="sidebar-logo"
        themeType="img"
        :imgType="collapse ? 'menuSubLogo':'menuMainLogo'"
      />
    </router-link>
    <!-- <span
      v-if="!collapse && configData.webName !== 'SIT'"
      :style="{ background: envLabelBg[configData.webName] }"
      class="sysEnv"
    >
      {{ configData.webName }}
    </span> -->
  </div>
</template>

<script>
import ThemeConf from '@/components/themeConf'
import config from '@/config/user.env'
export default {
  name: 'SidebarLogo',
  components: {
    ThemeConf
  },
  props: {
    collapse: {
      type: Boolean,
      required: true
    }
  },
  data () {
    return {
      configData: config,
      envLabelBg: {
        DEMO: '#01c68e',
        UAT: '#ff9d4d',
        DEV: '#3bb6e7'
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.sidebar-logo-container {
  position: relative;
  width: 100%;
  height: 76px;
  background: #2A2E40;
  text-align: center;
  overflow: hidden;
  transition: 0.3s width ease-in-out;
  .sysEnv{
    position: absolute;
    top: 0;
    left: 0;
    width: 48px;
    height: 20px;
    line-height: 20px;
    text-align: center;
    font-size: 14px;
    color: #fff;
    background: #ff9d4d;
    border-bottom-right-radius: 10px;
  }
  a{
    display: block;
    height: 100%;
  }
  img{
    border-style: none;
    display: inline-block;
    vertical-align: middle;
  }
  .sidebar-logo-link {
    line-height: 76px;
    .sidebar-logo {
      width: 140px;
      height: 62px;
    }
  }
  &.collapse {
    height: 48px;
    .sidebar-logo-link {
      line-height: 48px;
      .sidebar-logo {
        width: 36px;
        height: 16px;
      }
    }
  }
}
</style>
