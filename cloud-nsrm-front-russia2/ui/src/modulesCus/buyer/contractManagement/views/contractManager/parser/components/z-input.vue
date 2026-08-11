<template>
  <span :data-key="code">
    <el-input
      v-if="editable"
      :style="{ width: `${componentInfo.width}px` }"
      clearable
      :value="value"
      @input="change"
    />
    <span v-else>{{ value }}</span>
  </span>
</template>
<script>
import minix from './minix'
export default {
  name: 'ZInput',
  mixins: [minix]
}
</script>
