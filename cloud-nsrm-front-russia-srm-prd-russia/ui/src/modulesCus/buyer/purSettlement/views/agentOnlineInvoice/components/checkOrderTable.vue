<template>
  <SrmDialog
    size="xLarge"
    title="选择验收单明细"
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
      url="/api-sup-ce/api-ql/CheckOrderDetail/query"
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
  name: 'CheckOrderTable',
  components: {
    TableView,
    FormWrapper
  },

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    checkOrderParams: {
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
          prop: 'checkOrderNumber',
          label: '验收单单号'
        },
        {
          prop: 'orderNumber',
          label: '采购订单号'
        },
        {
          prop: 'materialCode',
          label: '物料编码'
        }
      ],
      tableData: [],
      tableHeader: [
        {
          prop: 'orgName',
          label: () => this.$t('oneStopShopping.businessEntity'),
          formattor: (val, row) => row.checkOrderId.orgName,
          minWidth: 150
        },
        {
          prop: 'checkOrderNumber',
          label: '验收单单号',
          formattor: (val, row) => row.checkOrderId.checkOrderNumber,
          minWidth: 150
        },
        {
          prop: 'orderNumber',
          label: this.$t('purSettlementMod.orderNumber'),
          formattor: (val, row) => row.orderDetailId.orderNumber,
          minWidth: 150
        },
        {
          prop: 'lineNum',
          label: this.$t('orderMod.orderLineNum'),
          formattor: (val, row) => row.orderDetailId.lineNum,
          minWidth: 120
        },
        {
          prop: 'materialCode',
          label: this.$t('common.materialCode'),
          formattor: (val, row) => row.orderDetailId.materialCode,
          minWidth: 120
        },
        {
          prop: 'materialName',
          label: this.$t('common.materialName'),
          formattor: (val, row) => row.orderDetailId.materialName,
          minWidth: 120
        },
        {
          prop: 'notInvoiceQuantity',
          label: this.$t('purSettlementMod.invoicesAvailable'),
          // 可开票数量 = 本次验收数量: checkQty - 开票数量: invoiceQty
          formattor: (val, row) => row.checkQty - row.invoiceQty,
          minWidth: 120
        },
        {
          prop: 'ceeaUnitTaxPrice',
          label: this.$t('purchaseDemand.taxPrice'),
          formattor: (val, row) => row.orderDetailId.ceeaUnitTaxPrice,
          minWidth: 120
        },
        {
          prop: 'ceeaUnitNoTaxPrice',
          label: this.$t('purSettlementMod.unitPriceNoTax'),
          formattor: (val, row) => row.orderDetailId.ceeaUnitNoTaxPrice,
          minWidth: 120
        },
        {
          prop: 'ceeaTaxRate',
          label: this.$t('bidMod.taxRate2'),
          formattor: (val, row) => row.orderDetailId.ceeaTaxRate,
          minWidth: 120
        },
        {
          prop: 'currencyName',
          label: this.$t('bid_mod.currencyName'),
          formattor: (val, row) => row.orderDetailId.currencyName,
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
      // 默认查出所选 业务实体 + 供应商 + 币种 + 可开票数量>0 + 单据状态：供应商已确认
      // 可开票数量 > 0 = 本次验收数量: checkQty - 开票数量: invoiceQty
      let params = { 'checkQty': { 'gt': { '$field': 'invoiceQty' } } }
      let filter1 = {
        '$condition': {
          '$strictQuery': true,
          filter: {
            orgId: { eq: this.checkOrderParams.orgId },
            vendorId: { eq: this.checkOrderParams.vendorId },
            checkOrderStatus: { eq: 'CONFIRM' }
          }
        }
      }
      // 因为验收单头表上上没有currencyCode字段, 所以从关联的订单明细表上查询
      let filter2 = {
        '$condition': {
          '$strictQuery': true,
          filter: {
            currencyCode: { eq: this.checkOrderParams.currencyCode }
          }
        }
      }
      // checkOrderNumber 查询关联头表，orderNumber、materialCode关联订单明细表查询
      const { checkOrderNumber, orderNumber, materialCode } = v || {}
      if (checkOrderNumber) {
        filter1['$condition'].filter.checkOrderNumber = { contains: checkOrderNumber }
      }
      if (orderNumber) {
        filter2['$condition'].filter.orderNumber = { contains: orderNumber }
      }
      if (materialCode) {
        filter2['$condition'].filter.materialCode = { contains: materialCode }
      }

      this.queryParam = {
        type: 'CheckOrderDetail',
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
          'checkOrderId': {
            '*': {},
            ...filter1
          },
          'orderDetailId': {
            '*': {},
            ...filter2
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
        const { checkOrderId = {}, orderDetailId = {}, checkQty, invoiceQty, ...rest } = item
        let invoiceQuantity = checkQty - invoiceQty
        // 未税总额 = 本次开票数量*未税单价
        let noTaxAmount = parseFloat(
          (Number(invoiceQuantity || 0) * Number(orderDetailId.ceeaUnitNoTaxPrice || 0)).toFixed(8)
        )
        // 含税金额 = 未税总额*（1+税率）
        let taxAmount = parseFloat(
          (Number(noTaxAmount || 0) * (1 + Number(orderDetailId.ceeaTaxRate || 0) / 100)).toFixed(8)
        )
        // 税额 = 含税金额 - 未税总额
        let tax = parseFloat(
          (taxAmount - noTaxAmount).toFixed(8)
        )
        return {
          ...rest,
          ...orderDetailId,
          extCheckDetailId: item.checkOrderDetailId, // 验收单明细id
          invoiceNoticeNumber: checkOrderId.checkOrderNumber, // 验收单号(没字段，先存到对账单号里)
          itemId: orderDetailId.materialId,
          itemCode: orderDetailId.materialCode,
          itemName: orderDetailId.materialName,
          // 可开票数量 = 本次验收数量: checkQty - 开票数量: invoiceQty
          notInvoiceQuantity: checkQty - invoiceQty,
          invoiceQuantity,
          unitPriceContainingTax: orderDetailId.ceeaUnitTaxPrice,
          unitPriceExcludingTax: orderDetailId.ceeaUnitNoTaxPrice,
          taxRate: orderDetailId.ceeaTaxRate,
          taxKey: orderDetailId.ceeaTaxKey,
          noTaxAmount,
          taxAmount,
          tax,
          extSource: 'CHECK_ORDER'
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
