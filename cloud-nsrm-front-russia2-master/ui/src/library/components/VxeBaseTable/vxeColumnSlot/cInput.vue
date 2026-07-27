<template>
  <el-input
    :value="value"
    :type="col.inputType || 'string'"
    clearable
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
            default: () => { }
        }
    }
}
</script>
