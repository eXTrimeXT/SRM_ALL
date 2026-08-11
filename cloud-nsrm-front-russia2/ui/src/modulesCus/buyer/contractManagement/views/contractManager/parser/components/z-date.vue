<template>
  <span :data-key="code">
    <el-date-picker
      v-if="editable"
      clearable
      :value="value"
      :format="$formatDatePicker"
      value-format="yyyy-MM-dd"
      type="date"
      @change="change"
      @input="change"
    />
    <span v-else>{{ value }}</span>
  </span>
</template>
<script>
import minix from './minix'
export default {
  name: 'ZDate',
  mixins: [minix],
  mounted () {
    console.log('code: ', this.code)
  }
}
</script>
