<template>
  <transition
    name="fade-transform"
    mode="out-in"
  >
    <keep-alive
      :include="cachedViews"
      :max="10"
    >
      <router-view
        :key="key"
        class="app-main-container water-copy"
      />
    </keep-alive>
  </transition>
</template>

<script>
import { mapGetters } from 'vuex'
import watermark from './watermark.js'
import { getDictItem } from '@/api/common'
export default {
  name: 'AppMain',
  provide () {
    return { context: this }
  },
  data () {
    return {
    }
  },
  computed: {
    ...mapGetters(['navCollapse']),
    cachedViews () {
      return this.$store.state.tagsView.cachedViews
    },
    key () {
      return this.$route.fullPath
    }
  },
  async mounted () {
    const { username, nickname } = this.$store.state.user.userInfo
    const text = `${nickname}(${username})`
    // 是否启用 水印 字典
    const { data } = await getDictItem('ENABLE_Watermark')
    let isEnableWatermark = data[0] ? data[0].dictItemCode : ''
    if (isEnableWatermark == 'Y') { // 判断是否设置开启水印
      watermark.set(text)
    } else {
      watermark.set('')
    }
  }
}
</script>

<style lang="scss" scoped>
.app-main-container {
  position: relative;
  padding: 0;
  background-color: #fff;
}

.fixed-header + .app-main {
  padding-top: 65px;
}

</style>

<style lang="scss">
// fix css style bug in open el-dialog
.el-popup-parent--hidden {
  .fixed-header {
    padding-right: 15px;
  }
}
</style>
