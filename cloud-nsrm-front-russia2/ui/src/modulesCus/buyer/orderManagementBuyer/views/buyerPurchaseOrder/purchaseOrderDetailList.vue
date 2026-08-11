<template>
  <el-container
    class="flex-container the_purchaseOrderDetailList_wrapper"
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
          <el-button
            type="primary"
            @click="cancelOrder"
          >
            {{ $t('common.cancel') }}
          </el-button>
          <!-- <ExportExcel
            type="default"
            :page-url="tableUrl"
            :filter-params="filterParams"
            :table-header="formDetailListData"
            :dict-codes="dictCodes"
            fileName="采购订单明细"
            timeout="1000000"
            export-mode="front"
          /> -->
          <ExportExcel
            type="default"
            :page-url="tableUrl"
            :filter-params="filterParams"
            :table-header="formDetailListDataNew"
            :dict-codes="dictCodes"
            :fileName="$t('orderMod.purchaseOrderDetailList')"
            timeout="1000000"
            export-mode="front"
          />
        </template>
      </MainHeader>
      <TableView
        ref="listDetailRef"
        customTableKey="purchaseOrderDetailList"
        :bigData="true"
        :checkbox="true"
        :table-header="formDetailListData"
        :check-change="handleCurrentChange"
        :page-size="15"
        :pre-query-data="queryParam"
        :auto-query="false"
        :url="tableUrl"
        :adeptMeiQl="true"
        :open-custom-table="true"
        :reserve-selection="true"
        row-key="orderDetailId"
        :comActive="$attrs['changeTab']"
        @afterQuery="afterQuery"
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
    <!-- 取消弹框 -->
    <CancleDialog
      v-if="canclDialogVisible"
      :visible.sync="canclDialogVisible"
      :orderInfoList="orderInfoList"
      cancleFlag="detailList"
      @after-cancle="getQuerydata"
    />
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import ExportExcel from 'lib@/components/export-excel'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import TableView from 'lib@/components/Table/TableView'
import purchaseOrderDetail from './purchaseOrderDetail'
import purchaseApplicationDetail from 'modc@/buyer/purchasingDemand/views/purchaseApplication/purchaseApplicationDetail'
import { formDetailSearchList, formDetailListData } from './data/detail'
import CancleDialog from './components/cancleDialog'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'PurchaseOrderDetailList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel,
    CancleDialog,
    purchaseApplicationDetail
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      filterParams: {},
      queryParam: {},
      currentRows: [],
      tableUrl: '/api-sup-ce/api-ql/OrderDetail/listDetailForBuyer',
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
      },
      canclDialogVisible: false,
      orderInfoList: []
    }
  },
  computed: {
    formDetailListDataNew () {
      return this.formDetailListData.map(item => {
        if (item.prop === 'materialNameShow') {
          item.prop = 'materialName'
        }
        if (item.prop === 'specificationShow') {
          item.prop = 'specification'
        }
        return item
      })
    }
  },
  created () {
    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    cancelOrder () {
      if (this.currentRows.length == 0) {
        // this.$message.error('请先勾选数据')
        this.$message.error(this.$t("cusEntry.supplement20250121.purchaseOrderDetailList"))
        return
      }
      if (this.currentRows.some(row => row.extStatus == 'ONGOING')) {
        // this.$message.error('当前勾选数据行存在送货单，请取消送货单后再取消订单')
        this.$message.error(this.$t("cusEntry.supplement20250121.cancelDeliveryNoteBeforeOrder"))
        return
      }
      this.orderInfoList = this.currentRows.map(item => item.orderDetailId)
      this.canclDialogVisible = true
    },
    getQuerydata (params) {
      // orderNumber、ceeaOrgId、orderStatus、ceeaEmpUsername、vendorName、ceeaPurchaseOrderDate 查询关联头表，materialCode查询这张明细表
      let filter = {}
      const {
        orderNumber,
        ceeaOrgId = [],
        orderStatus,
        ceeaEmpUsername,
        materialCode,
        vendorName,
        extBuyType,
        materialName,
        specification,
        ceeaRequirementHeadNum,
        ceeaPurchaseOrderDate = []
      } = params || {}
      if (orderNumber || ceeaOrgId.length != 0 || orderStatus || ceeaEmpUsername || vendorName || ceeaPurchaseOrderDate.length != 0) {
        filter['$condition'] = {
          '$strictQuery': true,
          filter: {}
        }
      }
      if (orderNumber) {
        filter['$condition'].filter.orderNumber = { contains: orderNumber }
      }
      if (ceeaOrgId.length != 0) {
        filter['$condition'].filter.ceeaOrgId = { in: ceeaOrgId }
      }
      if (orderStatus && ['ONGOING', 'FINISHED'].includes(orderStatus)) {
        filter['$condition'].filter.extStatus = { eq: orderStatus }
      } else if (orderStatus) {
        filter['$condition'].filter.orderStatus = { eq: orderStatus }
        filter['$condition'].filter.extStatus = { isNull: true }
      }
      if (ceeaEmpUsername) {
        filter['$condition'].filter.ceeaEmpUsername = { contains: ceeaEmpUsername }
      }
      if (vendorName) {
        filter['$condition'].filter.vendorName = { contains: vendorName }
      }
      if (ceeaPurchaseOrderDate.length != 0) {
        filter['$condition'].filter.ceeaPurchaseOrderDate = { between: ceeaPurchaseOrderDate }
      }

      let queryFilter = {}
      if (materialCode) {
        queryFilter.materialCode = { contains: materialCode }
      }
      if (extBuyType) {
        queryFilter.extBuyType = { eq: extBuyType }
      }
      if (ceeaRequirementHeadNum) {
        queryFilter.ceeaRequirementHeadNum = { contains: ceeaRequirementHeadNum }
      }
      if (materialName) {
        queryFilter.materialName = { contains: materialName }
      }
      if (specification) {
        queryFilter.specification = { contains: specification }
      }
      this.queryParam = {
        type: 'OrderDetail',
        action: 'listDetailForBuyer',
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
        this.currentRows = []
      })
    },
    afterQuery (data) {
      this.$refs.listDetailRef.setTableData(async tableData => {
        const materialIds = tableData.map(item => item.materialId) || []
        const response = await this.$http({
          url: '/api-base/material/materialItem/ext/multilingual',
          method: 'POST',
          data: { materialIds, language: this.$i18n.locale },
          loading: true
        })
        
        tableData.forEach((item, index) => {
          const data = response.data.find(it => it.materialId === item.materialId)
          item.materialNameShow = data?.materialName
          item.specificationShow = data?.extMaterialModel
          this.$set(tableData, index, item)
        })
      })
    },
    // 跳转采购申请
    async readPurchaseApplication (row) {
      const res = await this.$http({
        url: '/api-sup-ce/pr/requirementHead/getByHeadNum',
        method: 'GET',
        params: { requirementHeadNum: row.ceeaRequirementHeadNum },
        loading: true
      })
      // 查看--只读状态
      const tab = {
        component: purchaseApplicationDetail,
        params: {
          flag: 'readOnly',
          ctrlHeight: true,
          row: {
            requirementHeadId: res.data.requirementHeadId
          },
          showType: 'readOnly',
          tabName: 'purchaseApplicationDetail' + row.ceeaRequirementHeadNum
        },
        title: row.ceeaRequirementHeadNum,
        name: 'purchaseApplicationDetail' + row.ceeaRequirementHeadNum
      }
      this.$emit('tab-add', tab)
    },
    readOne (row) {
      // 查看--只读状态
      const tab = {
        component: purchaseOrderDetail,
        params: {
          flag: 'view',
          row
        },
        title: row.orderNumber,
        name: 'purchaseOrderDetail' + row.orderNumber
      }
      this.$emit('tab-add', tab)
    },
    handleCurrentChange (val) {
      this.currentRows = val
    }
  }
}
</script>
<style scoped lang="scss">
</style>
