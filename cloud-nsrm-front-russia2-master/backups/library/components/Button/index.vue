<template>
  <el-tooltip
    :effect="effect"
    :content="tip"
    :placement="placement"
  >
    <el-button
      :type="type"
      :icon="icon"
      :loading="loading"
      :disabled="disabled"
      :style="buttonStyle"
      @click="bttonClick"
    >
      <slot />
    </el-button>
  </el-tooltip>
</template>

<script>
export default {
  name: 'IconTipButton',
  props: {
    effect: { // tooltip主题dark/light
      type: String,
      default: 'dark'
    },
    tip: { // tooltip显示的内容
      type: String,
      default: ''
    },
    placement: { // Tooltip的出现位置
      type: String,
      default: 'top'
    },
    type: { // 按钮类型，primary / success / warning / danger / info / text
      type: String,
      default: 'primary'
    },
    icon: { // 按钮字体图标
      type: String,
      default: ''
    },
    authCode: { // 按钮授权码
      type: String,
      default: ''
    },
    loading: { // 是否加载状态
      type: Boolean,
      default: false
    },
    disabled: { // 是否禁用
      type: Boolean,
      default: false
    },
    buttonStyle: { // 按钮样式可传，例如修改字体图标颜色color:red
      type: String,
      default: ''
    }
  },
  methods: {
    bttonClick () {
      this.$emit('click')
    }
  }
}
</script>

<style scoped>
</style>
