<template>
  <el-dialog
    v-el-drag-dialog
    class="srm-dialog"
    :width="width"
    :fullscreen="size=='fullscreen' ? true : false"
    :append-to-body="appendToBody"
    v-bind="$attrs"
    v-on="$listeners"
  >
    <template #title>
      <slot name="header" />
    </template>
    <template #default>
      <div
        class="srm-dialog-content"
        :style="getStyle"
      >
        <slot />
      </div>
    </template>
    <template #footer>
      <slot name="footer" />
    </template>
  </el-dialog>
</template>

<script>
import omit from 'lodash/omit'
import { mapState } from 'vuex'

export default {
  name: 'SrmDialog',
  props: {
    size: String,
    contentMaxHeightLimit: {
      type: Boolean,
      default: () => {
        return true
      }
    },
    appendToBody: {
      type: Boolean,
      default: () => {
        return false
      }
    }
  },
  computed: {
    ...mapState({
      device: state => state.app.device // 当前屏幕类型
    }),
    getStyle () {
      if (!this.contentMaxHeightLimit) {
        return {}
      }
      // 580 - 30 - 56 - 58
      let maxHeight = 430
      if (!this.$slots.footer) {
        maxHeight = 500
      }
      return { 'max-height': `${maxHeight}px` }
    },
    attrs () {
      return omit(this.$attrs, ['width'])
    },
    width () {
      const sizes = {
        small: '420px',
        middle: '650px',
        large: '1080px',
        xLarge: '1200px'
      }
      // const xs = 768 // <768px -> device-xs
      // const sm = 768 // ≥768px -> device-sm
      // const md = 992 // ≥992px -> device-md
      // const lg = 1200 // ≥1200px -> device-lg
      // const xl = 1920 // ≥1920px -> device-xl
      if (this.device !== 'device-xs') {
        if (this.size === 'xLarge' && (['device-md', 'device-sm'].includes(this.device))) {
          return '96%'
        }
        if (this.size === 'large' && (['device-sm', 'device-md'].includes(this.device))) {
          return '96%'
        }
        if (this.size === 'middle' && ['device-sm'].includes(this.device)) {
          return '96%'
        }
        return sizes[this.size]
      } else { // 小屏幕下弹框的宽度为 96%
        return '96%'
      }
    }
  }
}
</script>

<style scoped lang="scss">
.srm-dialog-content {
  overflow-y: auto;
  overflow-x: hidden;
  padding: 24px !important;
}
</style>
