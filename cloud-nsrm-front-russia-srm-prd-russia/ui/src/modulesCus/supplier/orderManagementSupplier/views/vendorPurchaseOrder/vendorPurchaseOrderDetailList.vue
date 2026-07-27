<template>
  <el-container
    class="flex-container the_vendorPurchaseOrderDetailList_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="formDetailSearchList"
        form-label-width="120px"
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
            :table-header="formDetailListData"
            :dict-codes="dictCodes"
            fileName="采购订单明细"
            timeout="1000000"
            export-mode="front"
          />
        </template>
      </MainHeader>
      <TableView
        ref="listDetailRef"
        customTableKey="vendorPurchaseOrderDetailList"
        :bigData="true"
        :page-size="15"
        :pre-query-data="queryParam"
        :table-header="formDetailListData"
        :auto-query="false"
        :url="tableUrl"
        :adeptMeiQl="true"
        :open-custom-table="true"
        :reserve-selection="true"
        row-key="orderDetailId"
        :comActive="$attrs['changeTab']"
      >
        <!-- 订单附件 -->
        <template #extAttachId="{ scope }">
          <SrmCommonFile
            :default-file="{
              fileId: scope.row.extAttachId,
              fileName: scope.row.extAttachName
            }"
            readonly
          />
        </template>
      </TableView>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import ExportExcel from 'lib@/components/export-excel'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import TableView from 'lib@/components/Table/TableView'
import vendorPurchaseOrderDetail from './vendorPurchaseOrderDetail'
import { formDetailSearchList, formDetailListData } from './data/detail'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'VendorPurchaseOrderDetailList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      filterParams: {},
      queryParam: {},
      tableUrl: '/api-sup-ce/api-ql/OrderDetailVendor/listDetailForVendor',
      formDetailSearchList: formDetailSearchList(this),
      formDetailListData: formDetailListData(this),
      dictCodes: {
        orderType: 'NPM_ORDER_TYPE',
        orderStatus: 'PURCHASE_ORDER',
        extStatus: 'PURCHASE_ORDER',
        orderDetailStatus: 'OrderDetailStatus',
        extDetailStatus: 'OrderDetailStatus',
        ceeaIfSupplierConfirm: 'YES_OR_NO',
        sourceSystem: 'SOURCE_SYSTERM'
      }
    }
  },
  created () {
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    getQuerydata (params) {
      // orderNumber、ceeaOrgId、orderStatus、buyerName、vendorName、ceeaPurchaseOrderDate 查询关联头表，materialCode查询这张明细表
      let filter = {
        '$condition': {
          '$strictQuery': true,
          filter: {
            vendorId: { eq: this.$store.getters.user.companyId },
            orderStatus: { notIn: ['DRAFT', 'UNDER_APPROVAL', 'REJECT', 'WITHDRAW'] }
          }
        }
      }
      const { orderNumber, ceeaOrgName, orderStatus, buyerName, materialCode, ceeaPurchaseOrderDate = [] } = params || {}
      if (orderNumber) {
        filter['$condition'].filter.orderNumber = { contains: orderNumber }
      }
      if (ceeaOrgName) {
        filter['$condition'].filter.ceeaOrgName = { contains: ceeaOrgName }
      }
      if (orderStatus && ['ONGOING', 'FINISHED'].includes(orderStatus)) {
        filter['$condition'].filter.extStatus = { eq: orderStatus }
      } else if (orderStatus) {
        filter['$condition'].filter.orderStatus = { eq: orderStatus }
        filter['$condition'].filter.extStatus = { isNull: true }
      }
      if (buyerName) {
        filter['$condition'].filter.buyerName = { contains: buyerName }
      }
      if (ceeaPurchaseOrderDate.length != 0) {
        filter['$condition'].filter.ceeaPurchaseOrderDate = { between: ceeaPurchaseOrderDate }
      }

      let queryFilter = {}
      if (materialCode) {
        queryFilter.materialCode = { contains: materialCode }
      }

      this.queryParam = {
        type: 'OrderDetailVendor',
        action: 'listDetailForVendor',
        payload: {
          filter: { ...queryFilter },
          page: {
            pageNum: 1,
            pageSize: 15,
            sort: 'lastUpdateDate desc'
          }
        },
        query: {
          '*': {},
          'orderId': {
            '*': {},
            ...filter
          }
        },
        lang: 'zh-cn',
        tree: true
      }
      this.filterParams = { meiqlPayload: this.queryParam }
      this.$nextTick(() => {
        this.$refs.listDetailRef.query()
      })
    },
    readOne (row) {
      const tab = {
        component: vendorPurchaseOrderDetail,
        params: {
          flag: 'view',
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
