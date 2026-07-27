<template>
  <SrmDialog
    title="供应商"
    size="middle"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    destroy-on-close
    append-to-body
  >
    <el-table
      ref="supplierList"
      :data="dataList"
      style="width: 100%"
      border
      :row-height="38"
      max-height="390px"
      highlight-current-row
    >
      <el-table-column prop="vendorName" label="供应商名称" />
    </el-table>
  </SrmDialog>
</template>

<script>
export default {
  name: 'supplierList',
  props: {
    dataList: {
      type: Array,
      default: []
    },
    dialogVisible: {
      type: Boolean,
      default: false
    },
  },
  data () {
    return {

    }
  }
}
</script>

<style scoped>

</style>
