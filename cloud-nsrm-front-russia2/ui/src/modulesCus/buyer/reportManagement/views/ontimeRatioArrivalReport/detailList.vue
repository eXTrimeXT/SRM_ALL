<template>
  <el-container
    class="flex-container ontimeRatioArrivalReportDetailList"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        :pre-form-obj="preFormObj"
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
            :page-url="tableUrl"
            :filter-params="filterParams"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
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
  name: 'OntimeRatioArrivalReportDetailList',
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
        orderDetailStatus: 'OrderDetailStatus',
        unit: 'unit',
        extBuyType: 'PR_BUY_TYPE'
      },
      tableUrl: '/api-sup-ce/pj/order/report/receiveOnTimeRatioDetail',
      gridId: 'list',
      pageSize: 15,
      preFormObj: {},
      queryParam: {},
      filterParams: {},
      tableData: [],
      preArr: [
        {
          prop: 'ceeaPurchaseOrderDate',
          label: this.$t('oneStopShopping.orderDate'),
          type: 'daterange'
        },
        {
          prop: 'orderNumber',
          label: this.$t('logisticsMod.orderNum')
        },
        {
          prop: 'ceeaEmpNo',
          label: this.$t('reduce.buyerName'),
          type: 'quicksearch',
          showKey: 'nickname',
          propKey: 'username',
          name: 'scc_rbac_user_display'
        },
        // 申请单位
        {
          prop: 'ceeaOrgId',
          label: this.$t('cusEntry.bidSuperviseReport.extOrgOuName'),
          type: 'OUorganizationSelector'
        },
        {
          prop: 'vendorCode',
          label: this.$t('common.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyCode',
          name: 'scc_sup_company_info_all'
        },
        {
          prop: 'materialCode',
          label: this.$t('common.materialCode'),
          type: 'quicksearch',
          showKey: 'materialCode',
          propKey: 'materialCode',
          name: 'scc_base_material_item_contract'
        },
        {
          prop: 'deliveryDate',
          label: this.$t('contractMod.deliveryDate'),
          type: 'daterange'
        },
        {
          prop: 'categoryName',
          label: this.$t('common.category'),
          type: 'catSelect',
          showKey: 'categoryName'
        }
      ],
      tableHeader: [
        {
          prop: 'vendorCode',
          label: this.$t('common.vendorCode'),
          minWidth: 150
        },
        {
          prop: 'vendorName',
          label: this.$t('common.vendorName'),
          minWidth: 150
        },
        {
          prop: 'extVendorContacts',
          label: this.$t('vendorMod.vendorContact'),
          minWidth: 150
        },
        // 供应商联系人电话
        {
          prop: 'extVendorPhone',
          label: this.$t('cusEntry.orderMod.extVendorPhone'),
          minWidth: 150
        },
        {
          prop: 'extBuyType',
          label: this.$t('bid_mod.purchaseType'),
          minWidth: 150,
          dataType: 'dict',
          code: 'PR_BUY_TYPE'
        },
        // 采购单位
        {
          prop: 'extPurchaserOrgName',
          label: this.$t('cusEntry.reportManagement.createUserOrgOuName'),
          minWidth: 160
        },
        // 使用部门
        {
          prop: 'extUseDepartmentName',
          label: this.$t('cusEntry.orderMod.extUseDepartmentName'),
          minWidth: 160
        },
        // 使用人
        {
          prop: 'extUserName',
          label: this.$t('cusEntry.orderMod.extUserName'),
          minWidth: 150
        },
        // 使用人工号
        {
          prop: 'extUserCode',
          label: this.$t('cusEntry.supplement20250205.extUserCode'),
          minWidth: 150
        },
        // EAS收货时间
        {
          prop: 'easReceiveTime',
          label: this.$t('cusEntry.supplement20250205.easReceiveTime'),
          minWidth: 150,
          dataType: 'dateTime'
        },
        // 申请单位
        {
          prop: 'ceeaOrgName',
          label: this.$t('cusEntry.bidSuperviseReport.extOrgOuName'),
          minWidth: 160
        },
        // 申请日期
        {
          prop: 'extApplyDate',
          label: this.$t('purchaseDemand.applyDate'),
          dataType: 'dateTime',
          minWidth: 160
        },
        // 订单日期
        {
          prop: 'ceeaPurchaseOrderDate',
          label: this.$t('oneStopShopping.orderDate'),
          dataType: 'dateTime',
          minWidth: 160
        },
        // 订单编号
        {
          prop: 'orderNumber',
          label: this.$t('logisticsMod.orderNum'),
          minWidth: 160
        },
        // 送货单编号
        {
          prop: 'deliveryNumber',
          label: this.$t('cusEntry.supplement20250205.deliveryNumber'),
          minWidth: 160
        },
        // 交货日期
        {
          prop: 'deliveryDate',
          label: this.$t('contractMod.deliveryDate'),
          dataType: 'dateTime',
          minWidth: 160
        },
        // 到货周期
        {
          prop: 'extDeliveryCycle',
          label: this.$t('cusEntry.inq.extLeadTime'),
          minWidth: 160
        },
        {
          prop: 'materialCode',
          label: this.$t('common.materialCode'),
          minWidth: 160
        },
        {
          prop: 'materialName',
          label: this.$t('common.materialName'),
          minWidth: 160
        },
        {
          prop: 'specification',
          label: this.$t('vendorMod.specification'),
          minWidth: 160
        },
        {
          prop: 'categoryName',
          label: this.$t('common.category'),
          minWidth: 160
        },
        // 基本计量单位
        {
          prop: 'unit',
          label: this.$t('cusEntry.inq.baseMeasurmentUnit'),
          minWidth: 160
        },
        {
          prop: 'extBrand',
          label: this.$t('dataConfMod.band'),
          minWidth: 160
        },
        {
          prop: 'ceeaEmpUsername',
          label: this.$t('bidMod.quotePurchasor'),
          minWidth: 160
        },
        {
          prop: 'requirementQuantity',
          label: this.$t('bidMod.demandQuantity2'),
          minWidth: 160
        },
        {
          prop: 'orderNum',
          label: this.$t('orderMod.buyerOrderSynergy.orderNum'),
          minWidth: 160
        },
        {
          prop: 'storageNum',
          label: this.$t('orderMod.buyerOrderSynergy.warehouseReceiptQuantity'),
          minWidth: 160
        },
        {
          prop: 'receiveNum',
          label: this.$t('orderMod.buyerOrderSynergy.receivedNum'),
          minWidth: 160
        },
        // 采购订单取消原因
        {
          prop: 'orderCancelReason',
          label: this.$t('cusEntry.supplement20250205.orderCancelReason'),
          minWidth: 160
        },
        {
          prop: 'orderDetailStatus',
          label: this.$t('orderMod.buyerOrderSynergy.orderDetailStatus'),
          dataType: 'dict',
          code: 'OrderDetailStatus',
          minWidth: 160
        },
        // 送货单行状态
        {
          prop: 'deliveryNoteDetailStatus',
          label: this.$t('cusEntry.orderMod.extDetailStatus'),
          minWidth: 160
        }
      ]
    }
  },
  created () {
    // this.$nextTick(() => {
    //   this.getQuerydata()
    // })
  },
  methods: {
    syncFilterParams (values) {
      this.filterParams = values
    },
    getQuerydata (v) {
      if (typeof (v) === 'string') {
        const arr = v.split(',')
        this.preFormObj = {
          vendorCode: arr[0],
          ceeaEmpNo: arr[1],
          ceeaOrgId: Number(arr[2]),
          ceeaOrgCode: arr[3],
          ceeaPurchaseOrderDate: [arr[4], arr[5]]
        }
        this.queryParam = this.preFormObj || {}
      } else {
        this.queryParam = v || {}
      }
      this.filterParams = this.queryParam
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    }
  }
}
</script>
<style scoped lang="scss">
</style>
