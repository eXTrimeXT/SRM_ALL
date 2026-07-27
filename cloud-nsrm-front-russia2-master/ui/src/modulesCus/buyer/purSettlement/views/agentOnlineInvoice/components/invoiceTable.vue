<template>
  <!-- 选择发票 -->
  <SrmDialog
    size="xLarge"
    :title="$t('cusEntry.supplement20250205.selectInvoice')"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <!-- <FormWrapper :form-array="preArr" @getFormData="getQuerydata" /> -->
    <el-table
      border
      max-height="300px"
      :data="tableData"
      :row-key="row => row.uuid"
      @selection-change="checkChange"
    >
      <el-table-column
        type="selection"
        width="55"
        :reserve-selection="true"
      />
      <el-table-column
        align="center"
        :label="$t('purSettlementMod.tabindex')"
        type="index"
        fixed="left"
        width="60"
      />
      <el-table-column
        align="center"
        prop="invoiceTypeCode"
        :label="$t('components.ocr.invoiceType')"
        width="120"
        :formatter="(row, column, cellValue) => $getDictLabel('ONLINE_INVOICE_TYPE', cellValue)"
        :show-overflow-tooltip="true"
      />
      <!-- <el-table-column
        align="center"
        prop="buyerTaxNo"
        :label="$t('components.ocr.fillPurchaserRegisterNum')"
        width="120"
        :show-overflow-tooltip="true"
      /> -->
      <el-table-column
        align="center"
        prop="invoiceCode"
        :label="$t('components.ocr.invoiceCode')"
        width="120"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        align="center"
        prop="invoiceNo"
        :label="$t('components.ocr.invoiceNum')"
        width="120"
        :show-overflow-tooltip="true"
      />
      <!-- <el-table-column
        align="center"
        prop="uuid"
        :label="$t('contractMod.invocieNumber')"
        width="120"
        :show-overflow-tooltip="true"
      /> -->
      <!-- 数电号码 -->
      <el-table-column
        align="center"
        prop="verifyCode"
        :label="$t('cusEntry.supplement20250205.checkCode')"
        width="120"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        align="center"
        prop="invoiceDate"
        :label="$t('components.ocr.invoiceDate')"
        width="120"
        :formatter="(row, column, cellValue) => $parseTime(cellValue)"
        :show-overflow-tooltip="true"
      />
      <!-- 购买方名称 -->
      <!-- <el-table-column
        align="center"
        prop="buyerName"
        :label="$t('cusEntry.supplement20250205.buyerName')"
        width="120"
        :show-overflow-tooltip="true"
      /> -->
      <!-- <el-table-column
        align="center"
        prop="salerTaxNo"
        :label="$t('components.ocr.sellerRegisterNum')"
        width="150"
        :show-overflow-tooltip="true"
      /> -->
      <el-table-column
        align="center"
        prop="totalAmount"
        :label="$t('accountMod.invoiceAmount')"
        width="150"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        align="center"
        prop="taxAmount"
        :label="$t('contractMod.taxQuota')"
        width="150"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        align="center"
        prop="invoiceAmount"
        :label="$t('contractMod.unAmount')"
        width="120"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        align="center"
        prop="salerName"
        :label="$t('common.companyName')"
        width="120"
        :show-overflow-tooltip="true"
      />
    </el-table>
    <CPagination
      ref="queryPagination"
      class="c-query-table-pagination"
      :total="pageInfo.total"
      :page-num="pageInfo.pageNum"
      :page-size="pageInfo.pageSize"
      @current-change="handleCurrentChange"
      @size-change="handleSizeChange"
    />
    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>
      <el-button type="primary" @click="handleConfirm">
        {{ $t('common.confirm') }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import CPagination from 'lib@/components/c-pagination'

export default {
  name: 'InvoiceTable',
  components: {
    FormWrapper,
    CPagination,
    TableView
  },

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    invoiceParams: {
      type: Object,
      default: () => {}
    }
  },

  data () {
    return {
      gridId: 'list',
      queryParam: {},
      preArr: [],
      tableData: [],
      pageInfo: {
        total: 0,
        pageNum: 1,
        pageSize: 15
      },
      selection: []
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

  created () {
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },

  methods: {
    handleCurrentChange (pageNum) {
      this.pageInfo.pageNum = pageNum
      this.getQuerydata()
    },
    handleSizeChange (pageSize) {
      this.pageInfo.pageSize = pageSize
      this.getQuerydata()
    },
    // 查询发票
    getQuerydata (v) {
      let params = {
        page: this.pageInfo.pageNum, // 页码
        rows: this.pageInfo.pageSize, // 条数
        enterpriseCode: this.invoiceParams.extPrincipalCode, // 开票主体编码 '5000'
        userId: this.invoiceParams.username, // 创建人工号
        reimburseState: 0 // 0 未核销  1 已核销
      }
      if (this.invoiceParams.extBehalfInvoice !== 'Y') { // 是否代开发票=是, 不传供应商名称
        params.salerName = this.invoiceParams.vendorName // 销售方名称
      }
      this.$http({
        url: '/api-pj/invoiceApi/search',
        method: 'POST',
        data: params,
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.tableData = res.data.invoice
          this.pageInfo.total = res.data.total
        }
      })
    },
    checkChange (selected) {
      this.selection = selected
    },
    handleConfirm () {
      if (this.selection.length == 0) {
        return this.$message.warning(this.$t('common.msgSelectData'))
      }
      this.dialogVisible = false
      // 处理数据
      let selection = this.selection.map(item => {
        return {
          invoiceType: item.invoiceTypeCode,
          purchaserRegisterNum: item.buyerTaxNo,
          invoiceCode: item.invoiceCode,
          invoiceNum: item.invoiceNo,
          invoiceName: item.uuid,
          invoiceDate: item.invoiceDate,
          checkCode: item.verifyCode,
          purchaserName: item.buyerName,
          sellerName: item.salerName,
          sellerRegisterNum: item.salerTaxNo,
          noTaxTotalAmount: parseFloat(Number(item.invoiceAmount || 0).toFixed(8)), // 未税金额
          totalTax: parseFloat((Number(item.totalAmount || 0) - Number(item.invoiceAmount || 0)).toFixed(8)), // 税额 = 含税 - 未税
          totalAmount: parseFloat(Number(item.totalAmount || 0).toFixed(8)), // 含税金额
          amountInFigures: parseFloat(Number(item.totalAmount || 0).toFixed(8))
        }
      })
      this.$emit('after-confirm', selection)
    }
  }
}
</script>
