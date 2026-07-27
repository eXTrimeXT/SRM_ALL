<template>
  <!-- <SrmDialog
    title="查看评价结果"
    size="middle"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  > -->
  <SrmDialog
    :title="$t('cusEntry.supplement20250205.viewEvaluationResults')"
    size="middle"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-table
      ref="table"
      border
      stripe
      :data="tableData"
    >
      <!-- <el-table-column
        type="index"
        label="序号"
        width="60"
      /> -->
      <el-table-column
        type="index"
        :label="$t('components.common.sort')"
        width="60"
      />
      <!-- <el-table-column
        prop="nickname"
        label="评价人"
        showOverflowTooltip
      /> -->
      <el-table-column
        prop="nickname"
        :label="$t('cusEntry.supplement20250205.evaluator')"
        showOverflowTooltip
      />
      <!-- <el-table-column
        prop="score"
        label="评价结果"
        showOverflowTooltip
      /> -->
      <el-table-column
        prop="score"
        :label="$t('vendorMod.indicatorLineDes')"
        showOverflowTooltip
      />
    </el-table>

    <div slot="footer" class="dialog-footer" />
  </SrmDialog>
</template>
<script>
import { transformMQL } from 'lib@/utils/util'

export default {
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    editRows: {
      type: Array,
      default: () => []
    }
  },
  data () {
    return {
      tableData: []
    }
  },
  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },
  watch: {
    editRows: {
      handler (nVal) {
        if (nVal) {
          this.tableData = nVal
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    doLayout () {
      this.$nextTick(() => {
        this.$refs.table.doLayout()
      })
    }
  }
}
</script>
<style style="scss" scoped>
.red {
  color:red;
}
.risk-info {
  margin-bottom: 10px;
}
.abnormal-info {
  font-size: 14px;
  font-weight: bold;
  margin: 10px 0;
}
</style>
<style style="scss">
.red .cell {
  color:red;
}
</style>
