<template>
  <SrmDialog
    size="xLarge"
    title="选择对账单明细"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    append-to-body
    class="source-order-wrapper"
  >
    <FormWrapper
      :form-array="preArr"
      @getFormData="getQuerydata"
    />
    <TableView
      :ref="gridId"
      :table-data="tableData"
      :table-header="tableHeader"
      :pre-query-data="queryParam"
      :page-size="15"
      :adeptMeiQl="true"
      :checkbox="true"
      :checkChange="checkChange"
      :comActive="$attrs['changeTab']"
      url="/api-sup-ce/api-ql/InvoiceNoticeDetail/query"
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
import { transformMQL } from 'lib@/utils/util'
import TableView from 'lib@/components/Table/TableView'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import { parseTime } from '@/utils'

export default {
  name: 'StatementTable',
  components: {
    TableView,
    FormWrapper
  },

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    statementParams: {
      type: Object,
      default: () => {}
    }
  },

  data () {
    return {
      pageSize: 15,
      gridId: 'list',
      queryParam: {},
      preArr: [
        {
          prop: 'invoiceNoticeNumber',
          label: () => this.$t('purSettlementMod.statementNumber')
        },
        {
          prop: 'orderNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber2')
        },
        {
          prop: 'receiveOrderNo',
          label: () => this.$t('purSettlementMod.inboundReturnNumber')
        }
      ],
      tableData: [],
      tableHeader: [
        {
          prop: 'invoiceNoticeNumber',
          label: this.$t('purSettlementMod.statementNumber'),
          formattor: (val, row) => {
            return row.invoiceNoticeId.invoiceNoticeNumber
          },
          minWidth: 120
        },
        {
          prop: 'receiveOrderNo',
          label: this.$t('accountMod.inboundReturnOrderNo'),
          minWidth: 120
        },
        {
          prop: 'receiveOrderLineNo',
          label: this.$t('accountMod.inboundReturnLineNo'),
          minWidth: 120
        },
        {
          prop: 'type',
          label: this.$t('purSettlementMod.type'),
          formattor: val => val ? this.$getDictLabel('WAREHOURING_RETURN_DETAIL', val) : null,
          minWidth: 120
        },
        {
          prop: 'receiveDate',
          label: this.$t('orderMod.transactionDate'),
          formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : null,
          minWidth: 120
        },
        {
          prop: 'orderNumber',
          label: this.$t('purSettlementMod.orderNumber'),
          minWidth: 120
        },
        {
          prop: 'lineNum',
          label: this.$t('orderMod.orderLineNum'),
          minWidth: 120
        },
        {
          prop: 'itemCode',
          label: this.$t('common.materialCode'),
          minWidth: 120
        },
        {
          prop: 'itemName',
          label: this.$t('common.materialName'),
          minWidth: 120
        },
        {
          prop: 'unit',
          label: this.$t('dataConfMod.unit'),
          minWidth: 120
        },
        {
          prop: 'notInvoiceQuantity',
          label: this.$t('purSettlementMod.invoicesAvailable'),
          minWidth: 120
        },
        {
          prop: 'unitPriceExcludingTax',
          label: this.$t('purSettlementMod.unitPriceNoTax'),
          minWidth: 120
        },
        {
          prop: 'taxRate',
          label: this.$t('bidMod.taxRate2'),
          minWidth: 120
        },
        {
          prop: 'unitPriceContainingTax',
          label: this.$t('purchaseDemand.taxPrice'),
          minWidth: 120
        }
      ],
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
    getQuerydata (v) {
      // 默认查出所选 业务实体 + 供应商 + 币种 + 未开票数量>0 + 单据状态：供应商已确认
      let params = {
        orgId: { eq: this.statementParams.orgId },
        vendorId: { eq: this.statementParams.vendorId },
        currencyCode: { eq: this.statementParams.currencyCode },
        notInvoiceQuantity: { gt: 0 }
      }
      let filter = {
        '$condition': {
          '$strictQuery': true,
          filter: {
            extStatus: { eq: 'CONFIRM' }
          }
        }
      }
      // invoiceNoticeNumber 查询关联头表
      const { invoiceNoticeNumber, orderNumber, receiveOrderNo } = v || {}
      if (invoiceNoticeNumber) {
        filter['$condition'].filter.invoiceNoticeNumber = { contains: invoiceNoticeNumber }
      }
      if (orderNumber) {
        params.orderNumber = { contains: orderNumber }
      }
      if (receiveOrderNo) {
        params.receiveOrderNo = { contains: receiveOrderNo }
      }

      this.queryParam = {
        type: 'InvoiceNoticeDetail',
        action: 'query',
        payload: {
          filter: { ...params },
          page: {
            pageNum: 1,
            pageSize: 15,
            sort: 'lastUpdateDate desc'
          }
        },
        query: {
          '*': {},
          'invoiceNoticeId': {
            '*': {},
            ...filter
          }
        },
        lang: 'zh-cn',
        tree: true
      }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    checkChange (selected) {
      this.selection = selected
    },
    handleConfirm () {
      if (this.selection.length == 0) {
        return this.$message.warning(this.$t('common.msgSelectData'))
      }
      // 处理数据
      let selection = this.selection.map(item => {
        const { invoiceNoticeId = {}, ...rest } = item
        // 未税总额 = 本次开票数量*未税单价
        let noTaxAmount = parseFloat(
          (Number(item.notInvoiceQuantity || 0) * Number(item.unitPriceExcludingTax || 0)).toFixed(8)
        )
        // 含税金额 = 未税总额*（1+税率）
        let taxAmount = parseFloat(
          (Number(noTaxAmount || 0) * (1 + Number(item.taxRate || 0) / 100)).toFixed(8)
        )
        // 税额 = 含税金额 - 未税总额
        let tax = parseFloat(
          (taxAmount - noTaxAmount).toFixed(8)
        )
        return {
          ...rest,
          invoiceNoticeNumber: invoiceNoticeId.invoiceNoticeNumber, // 对账单号
          invoiceQuantity: item.notInvoiceQuantity, // 本次开票数量 默认取可开票数量
          extAdjustAmount: 0, // 尾差调整
          noTaxAmount,
          taxAmount,
          tax,
          extSource: 'INVOICE_NOTICE',
          extInputTaxAmount: null,
          extSapCostCode: null,
          extSapCostContent: null,
          extInvoiceUsage: null
        }
      })
      this.$emit('after-confirm', selection)
      this.dialogVisible = false
    }
  }
}
</script>
<style>
.source-order-wrapper .the_TableView .table-wrapper {
  display: block !important;
  flex: none;
}
</style>
