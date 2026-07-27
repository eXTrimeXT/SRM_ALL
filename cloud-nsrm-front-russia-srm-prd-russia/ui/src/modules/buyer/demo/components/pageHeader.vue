<template>
  <div class="sec-page-header">
    <span class="header-title">{{ title }}</span>
    <el-button
      v-if="isReflash"
      @click="reflash()"
    >
      刷新
    </el-button>
    <div class="right-sec">
      <slot name="right" />
    </div>
  </div>
</template>
<script>
export default {
  name: 'PageHeader',
  props: {
    title: {
      type: String,
      default: '页面标题'
    },
    isReflash: {
      type: Boolean,
      default: true
    }
  },
  data () {
    return {
      title: '学习例子'
    }
  },
  watch: {
    isReflash: {
      handler (val_new, val_old) {
        if (val_new) {

        }
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
      reflash () {
        this.$emit('reflash')
      }
  }
}
</script>
<style scoped lang="scss">
.sec-page-header {
  padding: 10px 5px;
  background-color: #f5f5f5;
  .header-title{
    font-size: 18px;
  }
  .right-sec{
      float: right;
      padding-left: 10px;
      max-width: 600px;
  }
}
</style>
