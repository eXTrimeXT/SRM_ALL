<template>
  <el-switch
    :value="value"
    :inactive-value="col.switchValues.inactive"
    :active-value="col.switchValues.active"
    @input="(val) => { $emit('input', val) }"
    @change="(val) => { $emit('change', val) }"
  />
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
