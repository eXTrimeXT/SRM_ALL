<template>
  <el-select
    :value="value"
    clearable
    @input="(val) => { $emit('input', val) }"
    @change="(val) => { $emit('change', val) }"
  >
    <el-option v-for="item in col.options" :key="item.id" :label="item.label" :value="item.value" />
  </el-select>
</template>
<script>
export default {
  props: {
    value: {
      type: [String, Number]
    },
    col: {
      type: Object,
      default: () => {}
    }
  }
}
</script>
