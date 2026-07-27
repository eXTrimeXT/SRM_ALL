<template>
  <!-- <SrmDialog
    title="伴随投标"
    size="middle"
    :visible.sync="newVisible"
    :close-on-click-modal="false"
  > -->
  <SrmDialog
    :title="$t('cusEntry.supplement20250121.accompanyingBidding')"
    size="middle"
    :visible.sync="newVisible"
    :close-on-click-modal="false"
  >
    <el-table
      border
      :data="tableData"
    >
      <!-- <el-table-column
        type="index"
        label="序号"
        width="60"
        align="center"
      /> -->
      <el-table-column
        type="index"
        :label="$t('components.common.sort')"
        width="60"
        align="center"
      />
      <!-- <el-table-column
        align="center"
        label="伴随投标"
      > -->
      <el-table-column
        align="center"
        :label="$t('cusEntry.supplement20250121.accompanyingBidding')"
      >
        <template slot-scope="scope">
          <!-- {{ `${scope.row.beforeVendorName}与${scope.row.afterVendorName}` }} -->
          {{ `${scope.row.beforeVendorName}${$t('cusEntry.supplement20250121.with')}${scope.row.afterVendorName}` }}
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

