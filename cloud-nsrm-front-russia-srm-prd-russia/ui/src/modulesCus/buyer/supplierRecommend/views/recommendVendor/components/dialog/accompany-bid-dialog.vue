<template>
  <SrmDialog
    title="伴随投标"
    size="middle"
    :visible.sync="newVisible"
    :close-on-click-modal="false"
  >
    <el-table
      border
      :data="tableData"
    >
      <el-table-column
        type="index"
        label="序号"
        width="60"
        align="center"
      />
      <el-table-column
        align="center"
        label="伴随投标"
      >
        <template slot-scope="scope">
          {{ `${scope.row.beforeVendorName}与${scope.row.afterVendorName}` }}
        </template>
      </el-table-column>
    </el-table>
  </SrmDialog>
</template>

<script>
export default {
  name: 'AccompanyBidDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    tableData: {
      type: Array,
      default: () => []
    }
  },
  data () {
    return {}
  },
  computed: {
    newVisible: {
      get () {
        return this.visible
      },
      set (newValue) {
        this.$emit('update:visible', newValue)
      }
    }
  }
}
</script>

