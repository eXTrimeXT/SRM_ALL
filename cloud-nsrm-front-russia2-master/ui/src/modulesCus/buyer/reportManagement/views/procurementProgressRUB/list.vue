<template>
  <el-container
    class="flex-container procurementProgressRUBList"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @synchronous-value="syncFilterParams"
        @getFormData="getQuerydata"
      />
      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <ExportExcel
            type="default"
            page-url="/api-sup-ce/purchaseOrder/process/import/rubListPage"
            :filter-params="filterParams"
            :table-header="tableHeader"
            :dict-codes="{}"
            timeout="1000000"
            export-mode="front"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :table-header="tableHeader"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :url="tableUrl"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import ExportExcel from 'lib@/components/export-excel'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'

export default {
  name: 'ProcurementProgressRUBList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      dictCodes: {
        extBuyType: 'PR_BUY_TYPE',
        auditStatus: 'APPROVAL_STATUS',
        extIsApproved: 'YES_OR_NO',
        extIsInq: 'YES_OR_NO',
        extIsCreatedOrder: 'YES_OR_NO',
        extIsReceive: 'YES_OR_NO',
        extIsChCoo: 'YES_OR_NO',
        extErpPaymentStatus: 'EXT_ERP_PAYMENT_STATUS'
      },
      tableUrl: '/api-sup-ce/purchaseOrder/process/rubListPage',
      gridId: 'list',
      pageSize: 15,
      queryParam: {},
      filterParams: {},
      tableData: [],
      tableHeader: [],
      preArr: [
        {
          prop: 'applyFullName',
          label: this.$t('components.approvalHead.headers.applicant')
        },
        // 使用人
        {
          prop: 'extUserName',
          label: this.$t('cusEntry.orderMod.extUserName')
        },
        // 使用部门
        {
          prop: 'extUseDepartmentName',
          label: this.$t('cusEntry.orderMod.extUseDepartmentName')
        },
        // 申请单位
        {
          prop: 'orgName',
          label: this.$t('cusEntry.bidSuperviseReport.extOrgOuName')
        },
        {
          prop: 'materialId',
          label: this.$t('common.materialCode'),
          showKey: 'materialCode',
          propKey: 'materialId',
          type: 'quicksearch',
          name: 'scc_base_material_item_contract'
        },
        // 物料购买类型
        {
          prop: 'extBuyType',
          label: this.$t('cusEntry.supplement20250205.materialPurchaseType'),
          type: 'dict',
          code: 'PR_BUY_TYPE'
        },
        {
          prop: 'extOrderProcess',
          label: this.$t('bidMod.quotePurchasor')
        },
        {
          prop: 'orderNumber',
          label: this.$t('logisticsMod.orderNum')
        },
        {
          prop: 'vendorName',
          label: this.$t('common.companyName')
        },
        {
          prop: 'dateList',
          label: this.$t('purchaseDemand.applyDate'),
          type: 'daterange'
        },
        {
          prop: 'requirementHeadNum',
          label: this.$t('orderMod.requirementHeadNum')
        },
        {
          prop: 'materialName',
          label: this.$t('common.materialName')
        },
        // 需求状态
        {
          prop: 'auditStatus',
          label: this.$t('cusEntry.supplement20250121.extPoolStatusValue'),
          type: 'dict',
          code: 'APPROVAL_STATUS'
        },
        // 是否询价
        {
          prop: 'extIsInq',
          label: this.$t('cusEntry.supplement20250314.extIsInq'),
          type: 'dict',
          code: 'YES_OR_NO'
        },
        // 是否创建订单
        {
          prop: 'extIsCreatedOrder',
          label: this.$t('cusEntry.supplement20250314.extIsCreatedOrder'),
          type: 'dict',
          code: 'YES_OR_NO'
        },
        // 是否收货
        {
          prop: 'extIsReceive',
          label: this.$t('cusEntry.supplement20250314.extIsReceive'),
          type: 'dict',
          code: 'YES_OR_NO'
        },
        // 是否中国寻源
        {
          prop: 'extIsChCoo',
          label: this.$t('cusEntry.dataConfMod.extIsHome'),
          type: 'dict',
          code: 'YES_OR_NO'
        }
      ]
    }
  },
  created () {
    this.tableHeader = [
      {
        prop: 'requirementHeadNum',
        label: this.$t('orderMod.requirementHeadNum'),
        minWidth: 150
      },
      {
        prop: 'applyDate',
        label: this.$t('purchaseDemand.applyDate'),
        dataType: 'dateTime',
        minWidth: 150
      },
      // 审批结束日期
      {
        prop: 'extApproveTime',
        label: this.$t('cusEntry.supplement20250205.extApproveTime'),
        dataType: 'dateTime',
        minWidth: 150
      },
      {
        prop: 'requirementDate',
        label: this.$t('bidMod.ceeaDemandDate'),
        dataType: 'dateTime',
        minWidth: 150
      },
      {
        prop: 'applyFullName',
        label: this.$t('components.approvalHead.headers.applicant'),
        minWidth: 150
      },
      // 使用人
      {
        prop: 'extUserName',
        label: this.$t('cusEntry.orderMod.extUserName'),
        minWidth: 150
      },
      // 使用部门
      {
        prop: 'extUseDepartmentName',
        label: this.$t('cusEntry.orderMod.extUseDepartmentName'),
        minWidth: 150
      },
      // 需求状态
      {
        prop: 'auditStatus',
        label: this.$t('cusEntry.supplement20250121.extPoolStatusValue'),
        minWidth: 150,
        dataType: 'dict',
        code: 'APPROVAL_STATUS'
      },
      {
        prop: 'extClosedCause',
        label: this.$t('qualitySynergy.closeReason'),
        minWidth: 150
      },
      {
        prop: 'receiveAddress',
        label: this.$t('purchaseDemand.ceeaDeliveryPlaceOut'),
        minWidth: 150
      },
      // 收货人
      {
        prop: 'extReceiver',
        label: this.$t('cusEntry.orderMod.extReceiveContact'),
        minWidth: 150
      },
      // 收货人联系方式
      {
        prop: 'receiveTelephone',
        label: this.$t('cusEntry.orderMod.extReceiveTelephone'),
        minWidth: 150
      },
      // 区域
      {
        prop: 'extAreaName',
        label: this.$t('vendorMod.area1'),
        minWidth: 150
      },
      // 申请单位
      {
        prop: 'orgName',
        label: this.$t('cusEntry.bidSuperviseReport.extOrgOuName'),
        minWidth: 150
      },
      {
        prop: 'materialCode',
        label: this.$t('common.materialCode'),
        minWidth: 150
      },
      {
        prop: 'materialName',
        label: this.$t('common.materialName'),
        minWidth: 150
      },
      {
        prop: 'extMaterialModel',
        label: this.$t('vendorMod.specification'),
        minWidth: 150
      },
      // 计量单位
      {
        prop: 'unit',
        label: this.$t('cusEntry.competition.measurementUnit'),
        minWidth: 150
      },
      {
        prop: 'requirementQuantity',
        label: this.$t('bidMod.demandQuantity2'),
        minWidth: 150
      },
      // 询价单号
      {
        prop: 'extInqSouNo',
        label: this.$t('bidMod.inquiryNo'),
        minWidth: 150
      },
      // 物料购买类型
      {
        prop: 'extBuyType',
        label: this.$t('cusEntry.supplement20250205.materialPurchaseType'),
        dataType: 'dict',
        code: 'PR_BUY_TYPE',
        minWidth: 150
      },
      // 质保期（自然日）
      {
        prop: 'extWarrantyPeriod',
        label: this.$t('cusEntry.orderMod.extWarrantyPeriod'),
        minWidth: 150
      },
      // 交货日期 改成 供方承诺到货日期
      {
        prop: 'deliveryDate',
        label: this.$t('purchaseDemand.promiseReceiveDate'),
        dataType: 'dateTime',
        minWidth: 150
      },
      // 到货周期
      {
        prop: 'extDeliveryCycle',
        label: this.$t('cusEntry.inq.extLeadTime'),
        minWidth: 150
      },
      {
        prop: 'orderNumber',
        label: this.$t('logisticsMod.orderNum'),
        minWidth: 150
      },
      {
        prop: 'vendorName',
        label: this.$t('common.vendorName'),
        minWidth: 150
      },
      {
        prop: 'vendorCode',
        label: this.$t('common.vendorCode'),
        minWidth: 150
      },
      {
        prop: 'extOrderProcess',
        label: this.$t('bidMod.quotePurchasor'),
        minWidth: 150
      },
      // 采购单位
      {
        prop: 'extPurchaserOrgName',
        label: this.$t('cusEntry.reportManagement.createUserOrgOuName'),
        minWidth: 150
      },
      // 采购员联系方式
      {
        prop: 'extPurchaserPhone',
        label: this.$t('cusEntry.supplement20250205.extPurchaserPhone'),
        minWidth: 160
      },
      // 实际订单数量
      {
        prop: 'orderNum',
        label: this.$t('cusEntry.supplement20250205.actualOrderQuantity'),
        minWidth: 160
      },
      {
        prop: 'ceeaPurchaseOrderDate',
        label: this.$t('oneStopShopping.orderDate'),
        dataType: 'dateTime',
        minWidth: 160
      },
      {
        prop: 'comments',
        label: this.$t('reApproval.formRemark'),
        minWidth: 160
      },
      // 订单取消原因
      {
        prop: 'closedCause',
        label: this.$t('cusEntry.supplement20250205.closedCause'),
        minWidth: 160
      },
      // 送货单单号
      // {
      //   prop: 'deliveryNumber',
      //   label: this.$t('cusEntry.supplement20250205.deliveryNumber'),
      //   minWidth: 160
      // },
      // // 发货数量
      // {
      //   prop: 'deliveryQuantity',
      //   label: this.$t('cusEntry.supplement20250205.deliveryQuantity'),
      //   minWidth: 160
      // },
      // // 物流单号
      // {
      //   prop: 'extExpressNo',
      //   label: this.$t('vendorMod.expressNum'),
      //   minWidth: 160
      // },
      // // 发货日期
      // {
      //   prop: 'deliveryTime',
      //   label: this.$t('cusEntry.supplement20250205.deliveryTime'),
      //   dataType: 'dateTime',
      //   minWidth: 160
      // },
      // {
      //   prop: 'receivedTime',
      //   label: this.$t('orderMod.buyerOrderSynergy.receivedTime'),
      //   dataType: 'dateTime',
      //   minWidth: 160
      // },
      // {
      //   prop: 'receivedNum',
      //   label: this.$t('orderMod.buyerOrderSynergy.receivedNum'),
      //   minWidth: 160
      // },
      {
        prop: 'warehouseQuantity',
        label: this.$t('orderMod.buyerOrderSynergy.warehouseReceiptQuantity'),
        minWidth: 160
      },
      {
        prop: 'extStorageTime',
        label: this.$t('orderMod.warehouseDate'),
        dataType: 'dateTime',
        minWidth: 160
      },
      // 未入库数量
      {
        prop: 'withoutWarehouseQuantity',
        label: this.$t('cusEntry.supplement20250205.unstockedQuantity'),
        minWidth: 160
      },
      // 是否审批
      {
        prop: 'extIsApproved',
        label: this.$t('cusEntry.supplement20250314.extIsApproved'),
        dataType: 'dict',
        code: 'YES_OR_NO',
        minWidth: 120
      },
      // 是否询价
      {
        prop: 'extIsInq',
        label: this.$t('cusEntry.supplement20250314.extIsInq'),
        dataType: 'dict',
        code: 'YES_OR_NO',
        minWidth: 120
      },
      // 是否创建订单
      {
        prop: 'extIsCreatedOrder',
        label: this.$t('cusEntry.supplement20250314.extIsCreatedOrder'),
        dataType: 'dict',
        code: 'YES_OR_NO',
        minWidth: 120
      },
      // 是否收货
      {
        prop: 'extIsReceive',
        label: this.$t('cusEntry.supplement20250314.extIsReceive'),
        dataType: 'dict',
        code: 'YES_OR_NO',
        minWidth: 120
      },
      // 是否中国寻源
      {
        prop: 'extIsChCoo',
        label: this.$t('cusEntry.dataConfMod.extIsHome'),
        dataType: 'dict',
        code: 'YES_OR_NO',
        minWidth: 120
      },
      {
        prop: 'extErpPaymentStatus',
        label: this.$t('cusEntry.orderMod.extErpPaymentStatus'), // ERP付款状态
        dataType: 'dict',
        code: 'EXT_ERP_PAYMENT_STATUS',
        minWidth: 120
      },
      // 定价单号
      {
        prop: 'fixPriceNo',
        label: this.$t('cusEntry.inq.priceOrderNo'),
        minWidth: 150
      },
      // 未税单价
      {
        prop: 'notaxPrice',
        label: this.$t('bid_mod.untaxedPrice'),
        minWidth: 120
      },
      // 近期最低价格(未税)
      {
        prop: 'latestMinNotaxPrice',
        label: this.$t('cusEntry.inq.latestMinNotaxPrice'),
        minWidth: 150
      },
      // 预估价格
      {
        prop: 'extPredictPrice',
        label: this.$t('cusEntry.inq.estimatePrice'),
        minWidth: 120
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    syncFilterParams (values) {
      const { dateList = [], ...rest } = values || {}
      let params = { ...rest }
      if (dateList.length > 0) {
        params.applyDateFrom = dateList[0]
        params.applyDateTo = dateList[1]
      }
      this.filterParams = params
    },
    getQuerydata (v) {
      const { dateList = [], ...rest } = v || {}
      let params = { ...rest }
      if (dateList.length > 0) {
        params.applyDateFrom = dateList[0]
        params.applyDateTo = dateList[1]
      }
      this.queryParam = params
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    }
  }
}
</script>
<style scoped lang="scss">
</style>
