<template>
  <SrmDialog
    title="意向金开票详情"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-table
      border
      stripe
      :data="tableData"
    >
      <el-table-column
        type="index"
        label="序号"
        fixed="left"
        width="60"
      />
      <el-table-column
        prop="invoiceNo"
        label="意向金开票单号"
        minWidth="150"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <el-button type="text" @click="viewDepositNo(scope.row)">
            {{ scope.row.invoiceNo }}
          </el-button>
        </template>
      </el-table-column>
      <el-table-column
        prop="creationDate"
        label="申请时间"
        minWidth="150"
        show-overflow-tooltip
      />
      <el-table-column
        prop="invoiceType"
        label="发票类型"
        minWidth="150"
        show-overflow-tooltip
        :formatter="(row,column,cellValue) => $getDictLabel('SOU_INVOICE_TYPE',cellValue)"
      />
      <el-table-column
        prop="status"
        label="单据状态"
        minWidth="150"
        show-overflow-tooltip
        :formatter="(row,column,cellValue) => $getDictLabel('SOU_INT_DEPOSIT_INVOICE_STATUS',cellValue)"
      />
    </el-table>

    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t("common.close") }}
      </el-button>
    </div>
  </SrmDialog>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { transformMQL } from 'lib@/utils/util'
import souHttp from '../../../../api'

export default {
  name: 'InvoiceDialog',
  components: {
    TableView,
    FormWrapper
  },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    readonly: {
      type: Boolean,
      default: false
    },
    editRows: {
      type: Object,
      default () {
        return {}
      }
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
    visible: {
      handler (nVal) {
        if (nVal) {
          this.getFormDetail()
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    viewDepositNo (row) {
      this.$emit('offertoFun', row)
    },
    async getFormDetail () {
      let applyId = this.editRows.applyId
      let transfromParams = transformMQL.save('SouIntDepositInvoiceBuyer', {
        filter: {
          applyId: {
            eq: applyId
          }
        }
      }, 'query')
      const response = await souHttp.invocieQuery(transfromParams)
      if (response) {
        this.tableData = response.data.records
      }
    }
  }
}
</script>
