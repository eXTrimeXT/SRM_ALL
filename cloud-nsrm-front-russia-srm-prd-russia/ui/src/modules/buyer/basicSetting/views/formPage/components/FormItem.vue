<template>
  <el-col :span="colSpan">
    <el-form-item
      v-bind="$attrs"
      v-on="$listeners"
    >
      <slot />
    </el-form-item>
  </el-col>
</template>
<script>

export default {
  name: 'FormItem',
  props: {
    colSpan: {
      type: Number,
      default: 8
    }
  }
}
</script>
