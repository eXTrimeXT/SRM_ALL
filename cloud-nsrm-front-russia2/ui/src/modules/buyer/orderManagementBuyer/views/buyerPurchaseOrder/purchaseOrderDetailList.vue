<template>
  <el-container
    class="flex-container the_vendorPurchaseOrderList_wrapper"
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
          <!-- 订单行关闭 -顾问说先注释 -linyk7 -->
          <!-- <AuthorityButton
            code="po:buyerPurchaseOrder:close"
            type="primary"
            @click="handleClose"
          >
            {{ $t('orderMod.orderRowClose') }}
          </AuthorityButton> -->
          <!-- 导出 -->
          <ExportExcel
            :page-url="tableUrl"
            :filter-params="queryParam"
            :table-header="formDetailListData"
            :dict-codes="dictCodes"
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
        :open-custom-table="true"
        :reserve-selection="true"
        row-key="orderDetailId"
        :comActive="$attrs['changeTab']"
      />
    </el-main>

    <!-- 关闭说明 -->
    <srm-dialog
      size="middle"
      :visible.sync="closeVisibleDialog"
    >
      <template slot="header">
        <em class="toRequired">*</em>
        {{ $t('orderMod.closeDes') }}
      </template>
      <el-input
        v-model="closedCause"
        type="textarea"
        :rows="3"
        show-word-limit
        :maxlength="200"
      />
      <div slot="footer">
        <el-button @click="closeVisibleDialog = false">
          {{ $t('common.cancel') }}
        </el-button>
        <el-button type="primary" @click="handleCloseConfirm">
          {{ $t('common.confirm') }}
        </el-button>
      </div>
    </srm-dialog>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import ExportExcel from 'lib@/components/export-excel'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import MainHeader from 'lib@/components/Table/MainHeader'
import TableView from 'lib@/components/Table/TableView'
import purchaseOrderDetail from './purchaseOrderDetail'
import purchaseApplicationDetail from 'modb@/purchasingDemand/views/purchaseApplication/purchaseApplicationDetail'
import { formDetailSearchList, formDetailListData } from './data/detail'

export default {
  name: 'PurchaseOrderDetailList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    ExportExcel,
    purchaseApplicationDetail
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      closedCause: '',
      closeVisibleDialog: false,
      currentRows: [],
      tableUrl: '/api-sup-ce/po/orderDetail/getOrderDetailList',
      queryParam: {},
      formDetailSearchList: formDetailSearchList(this),
      formDetailListData: formDetailListData(this),
      dictCodes: {
        orderType: 'ORDER_TYPE',
        orderStatus: 'PURCHASE_ORDER',
        orderDetailStatus: 'OrderDetailStatus',
        ceeaIfSupplierConfirm: 'YES_OR_NO',
        sourceSystem: 'SOURCE_SYSTERM'
      }
    }
  },
  created () {
    this.getQuerydata()
  },
  methods: {
    getQuerydata (obj) {
      const params = {}
      const { dateList, ...rest } = obj || this.queryParam
      if (dateList) {
        params.startTime = dateList[0]
        params.endTime = dateList[1]
      }
      let fields = 'o.ORDER_ID,o.ORDER_NUMBER,o.BUDGET_MANAGEMENT_NUM,o.CEEA_PURCHASE_ORDER_DATE,o.ORDER_TYPE,o.ORDER_STATUS,o.CEEA_ORG_NAME,o.ORGANIZATION_NAME,o.VENDOR_CODE,o.VENDOR_NAME,o.BUYER_NAME,od.REFUSED_REASON,o.SOURCE_SYSTEM,o.CREATED_FULL_NAME,o.CREATION_DATE,o.CEEA_IF_SUPPLIER_CONFIRM,od.CEEA_REQUIREMENT_HEAD_NUM,od.MATERIAL_CODE,od.MATERIAL_NAME,od.CEEA_IF_REQUIREMENT,od.ORDER_NUM,od.STORAGE_NUM,od.RETURN_NUM,od.CLOSED_CAUSE,od.REQUIREMENT_DATE,od.UNIT,od.CEEA_UNIT_TAX_PRICE,od.CEEA_UNIT_NO_TAX_PRICE,od.CEEA_TAX_RATE,od.CURRENCY_NAME,od.CEEA_TAX_AMOUNT,od.CEEA_AMOUNT_INCLUDING_TAX,od.CEEA_AMOUNT_EXCLUDING_TAX,od.CATEGORY_NAME,od.REQUIREMENT_QUANTITY,od.ORDER_DETAIL_STATUS,od.LINE_NUM,od.CEEA_PLAN_RECEIVE_DATE,od.CEEA_PROMISE_RECEIVE_DATE,od.DELIVERY_NOTICE_QUANTITY,od.CEEA_ROW_NUM,od.ORDER_DETAIL_ID'
      this.queryParam = { ...rest, ...params, fields }
      this.$nextTick(() => {
        this.$refs.listDetailRef.query()
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
          flag: 'approveNumber',
          row,
          ctrlHeight: true,
          showType: 'readOnly',
          activeWorkflowTab: this.integrationMode !== 'None' && this.integrationMode !== 'Push'
        },
        title: row.orderNumber,
        name: 'purchaseOrderDetail' + row.orderNumber
      }
      this.$emit('tab-add', tab)
    },
    handleCurrentChange (val) {
      this.currentRows = val
    },
    handleClose () {
      // 请先选择订单行！
      if (this.currentRows.length < 1) return this.$message.warning(this.$t('orderMod.selectOrderRow'))
      let isAccept = this.currentRows.every(row => row.orderDetailStatus === 'ACCEPT')
      // 请选择订单行状态为接受的行！
      if (!isAccept) return this.$message.warning(this.$t('orderMod.selectStatusAcceptRow'))
      this.closeVisibleDialog = true
    },
    // 订单行关闭
    handleCloseConfirm () {
      // 请先输入关闭说明
      if (!this.closedCause) return this.$message.warning(this.$t('orderMod.inputCloseDes'))
      const ids = this.currentRows.map(item => item.orderDetailId)
      this.$http({
        url: '/api-sup-ce/order/orderDetail/batchCloseOrderDetail',
        method: 'POST',
        data: {
          closedCause: this.closedCause,
          orderDetailIds: ids
        },
        loading: true
      }).then(_ => {
        this.closeVisibleDialog = false
      })
    }
  }
}
</script>
<style scoped lang="scss">
</style>
