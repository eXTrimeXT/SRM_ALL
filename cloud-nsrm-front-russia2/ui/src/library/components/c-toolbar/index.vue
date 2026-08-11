<template>
  <div
    ref="toolbar"
    :class="['c-toolbar',{collapsed: sideBarCollapsed}]"
    :style="style"
  >
    <slot />
    <div
      v-if="$slots.center"
      ref="toolbarCenter"
      class="center"
    >
      <slot name="center" />
    </div>
    <div
      v-if="$slots.right"
      ref="toolbarRight"
      class="right"
    >
      <slot name="right" />
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  name: 'CToobar',
  props: {
    isNested: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      ctrlShow: false
    }
  },
  computed: {
    ...mapState({
      sideBarCollapsed: state => state.app.sideBarCollapsed
    }),
    style () {
      const style = {}
      if (this.isNested) {
        style.position = 'absolute'
        style.bottom = 0
        style.left = 0
      }
      return style
    }
  },
  mounted () {
    this.$nextTick(() => {
      // this.handleResize()
    })
  },
  methods: {
    /* 由于按钮有权限控制，导致底部有一个阴影线条 */
    handleResize () {
      const toolBar = this.$refs.toolbar
      const toolBarCenter = this.$refs.toolbarCenter
      const toolBarRight = this.$refs.toolbarRight
      if (toolBar) {
        let childHeightCenter = 0
        let childHeightRight = 0
        if (toolBarCenter) {
          childHeightCenter = toolBarCenter.clientHeight
        }
        if (toolBarRight) {
          childHeightRight = toolBarRight.clientHeight
        }
        this.ctrlShow = childHeightCenter == 0 && childHeightRight == 0
      } else {
        this.ctrlShow = true
      }
    }
  }
}
</script>

<style lang="scss">
@import "@/styles/variables.scss";
.c-toolbar {
  position: fixed;
  width: 100%;
  // min-height: 50px;
  padding: 9px 24px;
  // line-height: 40px;
  padding-left: $sideBarWidth + 16;
  left: 0;
  bottom: 0;
  transition: padding-left 0.28s;
  box-sizing: border-box;
  background-color: #ffffff;
  box-shadow: 0 -1px 2px 0 rgba(182, 182, 182, 0.5);
  overflow: hidden;
  z-index: 999;

  &.collapsed {
    padding-left: $collapseWidth + 16;
  }
  &.ctrlShow{
    padding-top:0 ;
    padding-bottom:0 ;
    box-shadow:none;
  }

  .right {
    float: right;
    display: flex;
    align-items: center;
  }

  .center {
    display: flex;
    align-items: center;
    justify-content: center;
  }
}
</style>
