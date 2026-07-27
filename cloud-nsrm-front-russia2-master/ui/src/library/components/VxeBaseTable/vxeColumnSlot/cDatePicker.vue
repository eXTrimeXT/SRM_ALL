<template>
  <el-date-picker
    :value="value"
    :type="col.type"
    :placeholder="col.placeholder || $t('common.selectDate')"
    :format="col.format || $formatDatePicker"
    :value-format="col.valueFormat || 'yyyy-MM-dd'"
    :picker-options="col.pickerOptions && typeof col.pickerOptions === 'function' ? col.pickerOptions(scope) : col.pickerOptions"
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
