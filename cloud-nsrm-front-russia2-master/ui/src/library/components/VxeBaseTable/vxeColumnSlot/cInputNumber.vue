<template>
  <el-input-number
    :value="value"
    v-bind="col.attrs || {}"
    :controls="typeof col.controls === 'boolean' ? col.controls : true"
    :controls-position="col.controlsPosition || 'right'"
    :min="col.min ? (typeof col.min === 'function' ? col.min(scope.row) : col.min) : 0"
    :max="col.max ? (typeof col.max === 'function' ? col.max(scope.row) : col.max) : Infinity"
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
