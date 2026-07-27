<template>
  <span :data-key="code">
    <el-select
      v-if="editable"
      clearable
      :value="value"
      @change="change"
    >
      <el-option
        v-for="item in componentInfo.elemRanges"
        :key="item.elemRangeId"
        :label="item.elemValue"
        :value="item.elemValue"
      />
    </el-select>
    <span v-else>{{ value }}</span>
  </span>
</template>
<script>
import minix from './minix'
export default {
  name: 'ZSelect',
  mixins: [minix]
}
</script>
