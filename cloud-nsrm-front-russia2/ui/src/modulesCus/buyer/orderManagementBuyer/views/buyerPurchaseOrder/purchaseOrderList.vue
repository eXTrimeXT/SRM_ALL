<template>
  <el-container
    class="flex-container the_purchaseOrderListBuyer_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        form-label-width="120px"
        @getFormData="getQuerydata"
      />

      <MainHeader
        :l-span="22"
        :r-span="2"
      >
        <template slot="left">
          <AuthorityButton
            code="po:buyerPurchaseOrder:souResult"
            type="primary"
            @click="addSourceOrder"
          >
            <!-- 依据寻源结果创建订单 -->
            {{ $t("cusEntry.supplement20250121.createOrderFromSourcingResult") }}
          </AuthorityButton>
          <AuthorityButton
            code="po:buyerPurchaseOrder:price"
            type="primary"
            @click="addPriceOrder"
          >
            <!-- 依据协议价格创建订单 -->
             {{ $t("cusEntry.supplement20250121.createOrderFromAgreementPrice") }}
          </AuthorityButton>
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
        customTableKey="purchaseOrderList"
        :comActive="$attrs['changeTab']"
        @afterQuery="afterQuery"
      />
      <CancleDialog
        v-if="canclDialogVisible"
        :visible.sync="canclDialogVisible"
        :currentRow="currentRow"
        @after-cancle="getQuerydata"
      />
      <!-- 选择定价单 -->
      <SourceOrderList
        v-if="sourceOrderVisible"
        :visible.sync="sourceOrderVisible"
        @afterCreatOrder="getQuerydata"
      />
      <!-- 选择协议 -->
      <SourcePriceOrderList
        v-if="priceOrderVisible"
        :visible.sync="priceOrderVisible"
        @afterCreatOrder="getQuerydata"
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
import purchaseOrderDetail from './purchaseOrderDetail'
import CancleDialog from './components/cancleDialog'
import SourcePriceOrderList from './components/sourcePriceOrderList'
import SourceOrderList from './components/sourceOrderList'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'PurchaseOrderListBuyer',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    CancleDialog,
    SourcePriceOrderList,
    SourceOrderList,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      dictCodes: {
        orderType: 'NPM_ORDER_TYPE',
        extStatus: 'PURCHASE_ORDER',
        orderStatus: 'PURCHASE_ORDER',
        extOrderProperty: 'ORDER_PROPERTY',
        extErpPaymentStatus: 'EXT_ERP_PAYMENT_STATUS'
      },
      integrationMode: '',
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
          code: 'PURCHASE_ORDER'
        },
        {
          prop: 'ceeaOrgId',
          label: this.$t('purchaseDemand.businessEntity'),
          type: 'OUorganizationSelector'
        },
        {
          prop: 'createdFullName',
          label: this.$t('purchaseDemand.createdBy1')
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
          width: 130,
          formattor: val => val ? parseTime(val, '{y}-{m}-{d}') : ''
        },
        {
          prop: 'orderType',
          // label: '订单类型',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderType'),
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
          prop: 'extSapOrderNumber',
          label: () => this.$t('cusEntry.orderMod.erpOrderNumber'),
          width: 130
        },
        {
          prop: 'extErpPaymentStatus',
          label: () => this.$t('cusEntry.orderMod.extErpPaymentStatus'), // ERP付款状态
          formattor: val => this.$getDictLabel('EXT_ERP_PAYMENT_STATUS', val),
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
          width: 130,
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
              code: 'po:buyerPurchaseOrder:confirmDelivery',
              formattor: () => this.$t('common.edit'),
              // orderStatus: 已撤回、拟定
              // extStatus 只有2种状态: ONGOING 执行中（在途）、FINISHED 已完成
              show: row => ['WITHDRAW', 'DRAFT'].includes(row.orderStatus)
            },
            {
              callback: row => this.deleteRow(row),
              code: 'po:buyerPurchaseOrder:delete',
              formattor: () => this.$t('common.delete'),
              // 已撤回、已驳回、拟定
              show: row => ['WITHDRAW', 'DRAFT'].includes(row.orderStatus)
            },
            {
              callback: row => this.withdrawOrder(row),
              code: 'po:buyerPurchaseOrder:withdraw',
              formattor: () => this.$t('bidMod.withdraw'),
              // 待供方确认
              show: row => row.orderStatus === 'APPROVED_INVALID'
            },
            {
              callback: row => this.cancelOrder(row),
              code: 'po:buyerPurchaseOrder:cancel',
              formattor: () => this.$t('common.cancel'),
              // 已生效
              show: row => row.orderStatus === 'APPROVED'
            }
          ]
        }
      ],
      gridId: 'list',
      pageSize: 15,
      tableUrl: '/api-sup-ce/api-ql/Order/query',
      filterParams: {},
      queryParam: {},
      currentRow: {},
      canclDialogVisible: false,
      sourceOrderVisible: false,
      priceOrderVisible: false
    }
  },
  computed: {
    userId () {
      return this.$store.getters.userInfo.userId || null
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
          this.$route.params.funName === 'buyerPurchaseOrder'
        ) {
          const orderId = Number(this.$route.params.formId)
          const formNo = this.$route.params.formNo // 流程标题
          const row = {
            ...this.$route.params,
            orderId,
            orderNumber: formNo // tab 标题显示
          }
          this.readOne(row, 'manage')
        }
        // 来源：采购订单变更
        if (this.$route.params.from === 'purchaseOrderChangeList') {
          this.readOne(this.$route.params.row, 'view')
        }
      }
    }
  },
  created () {
    this.getQuerydata()
  },
  methods: {
    addSourceOrder () {
      this.sourceOrderVisible = true
    },
    addPriceOrder () {
      this.priceOrderVisible = true
    },
    cancelOrder (row) {
      if (row.extStatus == 'ONGOING') {
        // this.$message.error('当前数据行存在送货单，请取消送货单后再取消订单')
        this.$message.error(this.$t('cusEntry.supplement20250121.cancelDeliveryNoteBeforeCancelOrder'))
        return
      }
      this.currentRow = row
      this.canclDialogVisible = true
    },
    async adandonOne (row) {
      const isConfirm = await this.$confirm(
        this.$t('purSettlementMod.isDiscarded'),
        this.$t('common.tips'),
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        },
      )
      if (isConfirm !== 'confirm') return
      const saveData = transformMQL.save('Order', [row.orderId], 'abandon')
      this.$http({
        url: '/api-sup-ce/api-ql/Order/abandon',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    // 撤回
    async withdrawOrder (row) {
      // const sign = await this.$confirm('确定撤回此数据？', {
      //   confirmButtonText: this.$t('common.confirm'),
      //   cancelButtonText: this.$t('common.cancel'),
      //   type: 'warning'
      // })
      const sign = await this.$confirm(this.$t('cusEntry.supplement20250121.confirmRevokeData'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
      if (sign !== 'confirm') return
      const saveData = transformMQL.save('Order', [row.orderId], 'extRevoke')
      this.$http({
        url: '/api-sup-ce/api-ql/Order/extRevoke',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    // 删除
    async deleteRow (row) {
      const sign = await this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
      if (sign !== 'confirm') return
      const saveData = transformMQL.save(
        'Order',
        [{
          'orderId': row.orderId,
          'detailList': [{ $delete: '*' }],
          'attachmentList': [{ $delete: '*' }],
          'paymentProvisionList': [{ $delete: '*' }]
        }],
        'delete'
      )
      this.$http({
        url: '/api-sup-ce/api-ql/Order/delete',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    getQuerydata (v) {
      // 执行中、已完成状态 使用 extStatus 查询, 其他状态使用 orderStatus 查询(此时 extStatus为空)
      let params = {}
      const { orderNumber, ceeaPurchaseOrderDate, orderStatus, ceeaOrgId, createdFullName } = v || {}
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
      if (ceeaOrgId) {
        params.ceeaOrgId = { eq: ceeaOrgId }
      }
      if (createdFullName) {
        params.createdFullName = { contains: createdFullName }
      }

      this.queryParam = {
        type: 'Order',
        action: 'query',
        payload: {
          filter: { ...params },
          page: {
            pageNum: 1,
            pageSize: 15,
            sort: 'ceeaPurchaseOrderDate desc'
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
        component: purchaseOrderDetail,
        params: {
          flag: type,
          row
        },
        title: row.orderNumber,
        name: 'purchaseOrderDetail' + row.orderNumber
      }
      this.$emit('tab-add', tab)
    },
    afterQuery (data) {
      this.tableData = data.map(item => {
        item.orderStatus = item.extStatus || item.orderStatus
        return item
      })
    }
  }
}
</script>
<style scoped lang="scss">
</style>
