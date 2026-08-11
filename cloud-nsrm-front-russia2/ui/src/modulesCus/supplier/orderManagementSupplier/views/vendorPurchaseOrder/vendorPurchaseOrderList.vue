<template>
  <el-container
    class="flex-container the_vendorPurchaseOrderList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        :pre-form-obj="preFormObj"
        @getFormData="getQuerydata"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <!-- 导出 -->
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
        :bigData="true"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :auto-query="false"
        :url="tableUrl"
        :open-custom-table="true"
        :reserve-selection="true"
        :adeptMeiQl="true"
        row-key="orderId"
        customTableKey="vendorPurchaseOrderList"
        :comActive="$attrs['changeTab']"
        @afterQuery="afterQuery"
      />
    </el-main>
  </el-container>
</template>
<script>
import { parseTime } from '@/utils'
import { tabTodoMixin, tabTodoWatch } from '@/utils/mixins'
import ExportExcel from 'lib@/components/export-excel'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import TableView from 'lib@/components/Table/TableView'
import { transformMQL } from 'lib@/utils/util'
import vendorPurchaseOrderDetail from './vendorPurchaseOrderDetail'

export default {
  name: 'VendorPurchaseOrderList',
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
      filterParams: {},
      dictCodes: {
        orderType: 'NPM_ORDER_TYPE',
        extStatus: 'PURCHASE_ORDER',
        orderStatus: 'PURCHASE_ORDER',
        extOrderProperty: 'ORDER_PROPERTY'
      },
      preArr: [
        {
          prop: 'orderNumber',
          label: this.$t('orderMod.buyerOrderSynergy.orderNumber')
        },
        {
          prop: 'ceeaPurchaseOrderDate',
          label: this.$t('oneStopShopping.orderDate'),
          type: 'daterange'
        },
        {
          prop: 'orderStatus',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderStatus'), // 订单状态
          type: 'dict',
          filterItem: () => ['DRAFT'],
          code: 'PURCHASE_ORDER'
        }
      ],
      tableHeader: [
        {
          prop: 'orderNumber',
          showType: 'button',
          btnStyle: 'text',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber'),
          width: 150,
          callback: row => this.readOne(row, 'view')
        },
        {
          prop: 'ceeaPurchaseOrderDate',
          label: () => this.$t('oneStopShopping.orderDate'),
          width: 100,
          formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : ''
        },
        {
          prop: 'orderType',
          // label: '订单类型',
          label: () => this. $t('orderMod.buyerOrderSynergy.orderType'),
          width: 100,
          formattor: val => this.$getDictLabel('NPM_ORDER_TYPE', val)
        },
        {
          prop: 'extStatus',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderStatus'),
          width: 100,
          dataType: 'dict',
          code: 'PURCHASE_ORDER'
        },
        // 业务实体
        {
          prop: 'ceeaOrgName',
          label: () => this.$t('purchaseDemand.businessEntity'),
          width: 150
        },
        {
          prop: 'extOrderProperty',
          // label: '订单性质',
          label: () => this.$t('cusEntry.supplement20250121.orderNature'),
          formattor: val => this.$getDictLabel('ORDER_PROPERTY', val),
          width: 100
        },
        {
          prop: 'vendorCode',
          label: () => this.$t('purchaseDemand.vendorCode'),
          width: 120
        },
        {
          prop: 'vendorName',
          label: () => this.$t('purchaseDemand.vendorName'),
          minWidth: 150
        },
        {
          prop: 'ceeaEmpUsername',
          label: () => this.$t('orderMod.buyerOrderSynergy.buyerName'),
          width: 100
        },
        {
          prop: 'refuseReason',
          label: () => this.$t('orderMod.buyerOrderSynergy.refuseReason'),
          width: 130
        },
        {
          prop: 'createdFullName',
          label: () => this.$t('purchaseDemand.createdBy1'),
          width: 100
        },
        {
          prop: 'creationDate',
          label: () => this.$t('orderMod.buyerOrderSynergy.creationDate'),
          width: 100,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          prop: 'operation',
          label: () => this.$t('common.operation'),
          width: 150,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              callback: row => this.readOne(row, 'edit'),
              formattor: () => this.$t('purchaseDemand.manage'),
              // 待供应商确认
              show: row => ['APPROVED_INVALID'].includes(row.orderStatus)
            }
          ]
        }
      ],
      gridId: 'list',
      pageSize: 15,
      queryParam: {},
      preFormObj: {},
      tableUrl: '/api-sup-ce/api-ql/OrderVendor/query'
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler (nVal) {
        const { from, funName, listName } = this.$route.params
        if (from === 'workCount' && listName === 'ORDER') { // 待确认订单
          this.preFormObj = { orderStatus: 'APPROVED_INVALID' }
          this.getQuerydata(this.preFormObj)
        } else if (from === 'workCount' && listName === 'CREATE_DELIVERY_ORDER') { // 待创建送货单
          this.preFormObj = { ifCreatDelivery: 'Y' }
          this.getQuerydata(this.preFormObj)
        }
      }
    }
  },
  created () {
    this.$nextTick(() => {
      this.getQuerydata(this.preFormObj)
    })
  },
  methods: {
    afterQuery (data) {
      this.tableData = data.map(item => {
        item.orderStatus = item.extStatus || item.orderStatus
        return item
      })
    },
    getQuerydata (v) {
      // 执行中、已完成状态 使用 extStatus 查询, 其他状态使用 orderStatus 查询(此时 extStatus为空)
      let params = {}
      params.vendorId = { eq: this.$store.getters.user.companyId }
      params.orderStatus = { notIn: ['DRAFT', 'UNDER_APPROVAL', 'REJECT', 'WITHDRAW'] }
      const { orderNumber, ceeaPurchaseOrderDate, orderStatus, ifCreatDelivery } = v || {}
      if (orderNumber) {
        params.orderNumber = { contains: orderNumber }
      }
      if (ceeaPurchaseOrderDate) {
        params.ceeaPurchaseOrderDate = { between: ceeaPurchaseOrderDate }
      }
      if (orderStatus && ['ONGOING', 'FINISHED'].includes(orderStatus)) {
        params.extStatus = { eq: orderStatus }
      } else if (orderStatus) {
        params.orderStatus = { eq: orderStatus }
        params.extStatus = { isNull: true }
      }
      if (ifCreatDelivery) {
        params.ifCreatDelivery = ifCreatDelivery === 'Y' ? { eq: 'Y' } : { NE: 'Y' }
      }

      this.queryParam = {
        type: 'OrderVendor',
        action: 'query',
        payload: {
          filter: { ...params },
          page: {
            pageNum: 1,
            pageSize: 15,
            sort: 'lastUpdateDate desc'
          }
        },
        query: { '*': {} },
        lang: 'zh-cn',
        tree: true
      }
      this.filterParams = { meiqlPayload: this.queryParam }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    readOne (row, type) {
      const tab = {
        component: vendorPurchaseOrderDetail,
        params: {
          flag: type,
          row
        },
        title: row.orderNumber,
        name: 'vendorPurchaseOrderDetail' + row.orderNumber
      }
      this.$emit('tab-add', tab)
    }
  }
}
</script>
<style scoped lang="scss">
</style>
