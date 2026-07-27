<template>
  <SrmDialog
    title="查看评价结果"
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
      <el-table-column
        type="index"
        label="序号"
        width="60"
      />
      <!-- <el-table-column
        prop="nickname"
        label="评价人"
        showOverflowTooltip
      /> -->
      <el-table-column
        prop="score"
        label="评价结果"
      >
        <template v-slot="scope">
          <el-input-number v-model="scope.row.score" style="width:100%;" :min="0" :max="100" />
        </template>
      </el-table-column>
    </el-table>

    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button>
      <el-button type="primary" @click="handleConfirm">
        {{ $t("common.confirm") }}
      </el-button>
    </div>
  </SrmDialog>
</template>
<script>
import BaseTable from 'lib@/components/BaseTable'
import { transformMQL } from 'lib@/utils/util'

export default {
  components: {
    BaseTable
  },
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
    },
    handleConfirm () {
      for (let item of this.tableData) {
        if (!item.score) {
          this.$message.warning('评价结果不能为空')
          return
        }
      }
      this.$emit('confirm', this.tableData)
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
