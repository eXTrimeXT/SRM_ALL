<template>
  <!-- 意向金开票详情 -->
  <SrmDialog
    :title="$t('cusEntry.supplement20250121.invoiceDetailsForEarnestMoney')"
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
        :label="$t('common.sort')"
        fixed="left"
        width="60"
      />
      <!-- 意向金开票单号 -->
      <el-table-column
        prop="invoiceNo"
        :label="$t('cusEntry.supplement20250121.intentionDepositInvoiceNumber')"
        minWidth="110"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <el-button type="text" @click="viewDepositNo(scope.row)">
            {{ scope.row.invoiceNo }}
          </el-button>
        </template>
      </el-table-column>
      <!-- 申请时间 -->
      <el-table-column
        prop="creationDate"
        :label="$t('components.stratProcess.headers.docCreateTime')"
        minWidth="150"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $parseTime(cellValue)"
      />
      <el-table-column
        prop="invoiceType"
        :label="$t('components.ocr.invoiceType')"
        minWidth="100"
        show-overflow-tooltip
        :formatter="(row,column,cellValue) => $getDictLabel('SOU_INVOICE_TYPE',cellValue)"
      />
      <el-table-column
        prop="status"
        :label="$t('bidMod.billstatus')"
        minWidth="100"
        show-overflow-tooltip
        :formatter="(row,column,cellValue) => $getDictLabel('SOU_INT_DEPOSIT_INVOICE_STATUS',cellValue)"
      />
      <!-- 开票失败原因 -->
      <el-table-column
        prop="applyInvoiceFailReason"
        :label="$t('cusEntry.supplement20250205.applyInvoiceFailReason')"
        minWidth="280"
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
import soucHttp from '../../../api/soucHttp.js'
import edit from '../edit'

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
      this.tableData = []
      let reqHeadId = this.editRows.reqHeadId
      let transfromParams = transformMQL.save('SouIntDepositInvoice', {
        filter: {
          reqHeadId: {
            eq: reqHeadId
          },
          vendorId: {
            eq: this.$store.getters.companyId
          }
        }
      }, 'query')
      const response = await soucHttp.queryInvoice(transfromParams)
      if (response?.data?.records.length) {
        this.tableData = response.data.records
      } else {
        // 您尚未发起意向金开票申请
        this.$message.warning(this.$t('cusEntry.supplement20250205.applyInvoiceTip'))
        this.dialogVisible = false
      }
    }
  }
}
</script>
