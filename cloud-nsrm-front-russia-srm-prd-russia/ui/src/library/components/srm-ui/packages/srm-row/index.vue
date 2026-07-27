<template>
  <el-row
    style="display: flex;flex-wrap: wrap;"
    :gutter="$attrs.gutter || ($attrs.gutter === 0 ? 0 : 32)"
    v-bind="$attrs"
    v-on="$listeners"
  >
    <slot />
  </el-row>
</template>

<script>
export default {
  name: 'SrmRow',
  props: {}
}
</script>
