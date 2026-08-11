<template>
  <el-container class="flex-container the_purchaseOrderChangeList" direction="vertical">
    <el-main>
      <FormWrapper :form-array="preArr" form-label-width="120px" @getFormData="getQueryData" />
      <TableView
        :ref="gridId"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :checkbox="true"
        :pre-query-data="queryParam"
        :auto-query="false"
        :comActive="$attrs['changeTab']"
        :url="tableUrl"
        :open-custom-table="true"
      />
    </el-main>
  </el-container>
</template>
<script>
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import purchaseOrderChangeDetail from './purchaseOrderChangeDetail'
import { parseTime } from '@/utils'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import ExportExcel from 'lib@/components/export-excel'

export default {
  name: 'PurchaseOrderChangeList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      tableUrl: '/api-sup-ce/sup/orderchange/listPage',
      currentRows: [],
      gridId: 'list',
      rules: {
        vendorCode: [{ required: true, message: this.$t('bidMod.msgDictCode') }],
        vendorCompanyName: [{ required: true, message: this.$t('bidMod.msgDictName') }]
      },
      preArr: [
        {
          prop: 'orderChangeNumber',
          label: () => this.$t('orderMod.orderChangeNumber')
        },
        {
          prop: 'orderNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber')
        },
        {
          prop: 'dateList',
          label: () => this.$t('orderMod.buyerOrderSynergy.creationDate'),
          type: 'daterange'
        },
        // 业务实体
        {
          prop: 'orgId',
          label: () => this.$t('oneStopShopping.businessEntity'),
          type: 'OUorganizationSelector'
        },
        {
          prop: 'orderChangeStatus',
          label: () => this.$t('quest.changeStatus'), // 变更状态
          type: 'dict',
          code: 'ORDER_CHANGE_STATUS'
        }
      ],
      queryParam: {},
      tableHeader: [
        {
          prop: 'orderChangeNumber',
          showType: 'button',
          btnStyle: 'text',
          label: () => this.$t('orderMod.orderChangeNumber'), // 订单变更编号
          width: 140,
          callback: row => this.readChangeOrderOne(row)
        },
        {
          prop: 'orderNumber',
          showType: 'button',
          btnStyle: 'text',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber'),
          width: 140,
          callback: row => this.readOrderOne(row)
        },
        {
          prop: 'orderType',
          label: () => this.$t('purchaseDemand.purchaseType'),
          width: 100,
          dataType: 'dict',
          code: 'ORDER_TYPE'
        },
        {
          prop: 'orderChangeStatus',
          label: () => this.$t('orderMod.orderChangeStatus'),
          width: 120,
          dataType: 'dict',
          code: 'ORDER_CHANGE_STATUS'
        },
        {
          prop: 'orderChangeVersion',
          label: () => this.$t('orderMod.orderVersion'),
          align: 'center',
          width: 120
        },
        {
          prop: 'orgName',
          label: () => this.$t('purchaseDemand.businessEntity'),
          width: 140
        },
        {
          prop: 'organizationName',
          label: () => this.$t('purchaseDemand.invOrg'),
          width: 140
        },
        {
          prop: 'vendorCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorCode'),
          width: 120
        },
        {
          prop: 'vendorName',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'),
          minWidth: 150
        },
        {
          prop: 'empUsername',
          label: () => this.$t('orderMod.buyerOrderSynergy.buyerName'),
          width: 100
        },
        {
          prop: 'ifSupplierConfirm',
          label: () => this.$t('oneStopShopping.ifSupplierConfirm'),
          width: 120,
          dataType: 'dict',
          code: 'YES_OR_NO'
        },
        {
          prop: 'sourceSystem',
          label: () => this.$t('orderMod.orderSource'),
          width: 130,
          dataType: 'dict',
          code: 'SOURCE_SYSTERM'
        },
        {
          prop: 'createdUserName',
          label: () => this.$t('orderMod.buyerOrderSynergy.createdBy'),
          width: 100
        },
        {
          prop: 'creationDate',
          label: () => this.$t('orderMod.buyerOrderSynergy.creationDate'),
          width: 100,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          prop: 'lastUpdatedBy',
          label: () => this.$t('common.updatePeople'),
          width: 100
        },
        {
          prop: 'lastUpdateDate',
          label: () => this.$t('qualitySynergy.updateDate'),
          width: 100,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          prop: 'orderChangeDate',
          label: () => this.$t('orderMod.buyerOrderSynergy.effectiveDate'),
          width: 120,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'),
          width: 100,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              callback: row => this.editOne(row),
              code: 'spo:supplierPurchaseOrderChange:view',
              formattor: () => this.$t('common.view'),
              show: row => row.orderChangeStatus === 'WAITING_VENDOR_CONFIRM'
            }
          ]
        }
      ]
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        if (
          this.$route.params.from === 'fromFun' &&
          this.$route.params.funName === 'purchaseOrderChange'
        ) {
          const orderChangeId = Number(this.$route.params.formId)
          const formNo = this.$route.params.formNo // 流程标题
          const row = {
            ...this.$route.params,
            orderChangeId,
            orderNumber: formNo // tab 标题显示
          }
          this.readChangeOrderOne(row)
        }

        // 采购订单点击订单变更按钮跳转至采购订单变更详情页面
        if (this.$route.params.from === 'buyerPurchaseOrder') {
          this.editOne(this.$route.params.row)
        }
      }
    }
  },
  created () {
    this.$nextTick(() => {
      this.getQueryData()
    })
  },
  methods: {
    getQueryData (obj = {}) {
      const { dateList, ...rest } = obj
      const params = {}
      if (dateList) {
        params.startCreationDate = dateList[0]
        params.endCreationDate = dateList[1]
      }
      this.queryParam = { ...rest, ...params }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    // 跳转采购订单变更页面 - 只读
    readChangeOrderOne (row) {
      const tab = {
        component: purchaseOrderChangeDetail,
        params: {
          flag: 'readOnly',
          row
        },
        title: row.orderNumber,
        name: 'purchaseOrderChangeDetail' + row.orderNumber
      }
      this.$emit('tab-add', tab)
    },
    // 跳转采购订单页面 - 只读
    readOrderOne (row) {
      this.$router.push({
        name: 'vendorPurchaseOrder',
        params: { from: 'supplierPurchaseOrderChange', row }
      })
    },
    editOne (row) {
      const tab = {
        component: purchaseOrderChangeDetail,
        params: {
          flag: 'edit',
          row
        },
        title: row.orderNumber,
        name: 'purchaseOrderChangeDetail' + row.orderNumber
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>
<style scoped lang="scss"></style>
