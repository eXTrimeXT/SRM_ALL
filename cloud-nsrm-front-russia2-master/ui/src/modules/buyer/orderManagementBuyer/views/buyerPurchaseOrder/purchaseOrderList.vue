<template>
  <el-container
    class="flex-container the_vendorPurchaseOrderList_wrapper"
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
          <!-- 新增 -->
          <AuthorityButton
            code="po:buyerPurchaseOrder:addOrder"
            type="primary"
            @click="addOrder"
          >
            {{ $t('orderMod.buyerOrderSynergy.add') }}
          </AuthorityButton>
          <!-- 删除 -->
          <AuthorityButton
            code="po:buyerPurchaseOrder:delete"
            :disabled="currentRows.length <= 0"
            @click="delOrder('mutil')"
          >
            {{ $t('common.delete') }}
          </AuthorityButton>
          <!-- 导出 -->
          <ExportExcel
            type="default"
            :page-url="tableUrl"
            :filter-params="queryParam"
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
        :checkbox="true"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :auto-query="false"
        :url="tableUrl"
        :open-custom-table="true"
        :reserve-selection="true"
        row-key="orderNumber"
        customTableKey="purchaseOrderList"
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
import purchaseOrderDetail from './purchaseOrderDetail'

export default {
  name: 'PurchaseOrderListBuyer',
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
      dictCodes: {
        orderType: 'ORDER_TYPE',
        orderStatus: 'PURCHASE_ORDER',
        storageStatus: 'STORAGE_STATUS',
        ceeaIfSupplierConfirm: 'YES_OR_NO',
        sourceSystem: 'SOURCE_SYSTERM'
      },
      integrationMode: '',
      getFooterNum: 1,
      gridId: 'list',
      currentRows: [],
      tableUrl: '/api-sup-ce/po/order/listPage',
      pageSize: 15,
      preArr: [
        {
          prop: 'orderNumber',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber')
        },
        {
          prop: 'orderType',
          label: () => this.$t('bid_mod.purchaseType'), // 采购类型
          type: 'dict',
          code: 'ORDER_TYPE'
        },
        // 业务实体
        {
          prop: 'orgIds',
          label: () => this.$t('oneStopShopping.businessEntity'),
          type: 'OUorganizationSelector',
          multiple: true,
          collapseTags: true
        },
        {
          prop: 'organizationIds',
          parentId: 'orgIds',
          label: () => this.$t('purchaseDemand.invOrg'),
          type: 'INVorganizationSelector', // 库存组织
          multiple: true,
          collapseTags: true
        },
        // 创建日期
        {
          prop: 'dateList',
          label: () => this.$t('common.creationDate'),
          type: 'daterange'
        },
        {
          prop: 'vendorId',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorName'),
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyId',
          name: 'scc_sup_company_info_all'
        },
        {
          prop: 'orderStatus',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderStatus'), // 订单状态
          type: 'dict',
          code: 'PURCHASE_ORDER'
        },
        {
          prop: 'ceeaIfSupplierConfirm',
          label: () => this.$t('oneStopShopping.ifSupplierConfirm'),
          type: 'dict',
          code: 'YES_OR_NO'
        },
        {
          prop: 'budgetManagementId',
          label: this.$t('purchaseDemand.budgetNumber'), // 预算编号
          type: 'quicksearch',
          showKey: 'budgetManagementNumber',
          propKey: 'budgetManagementId',
          name: 'scc_pb_budget_management_effective'
        },
        {
          prop: 'sourceSystem',
          label: this.$t('orderMod.buyerOrderSynergy.sourceSystem'), // 来源系统
          type: 'dict',
          code: 'SOURCE_SYSTERM'
        }
      ],
      tableHeader: [
        {
          prop: 'orderNumber',
          showType: 'button',
          btnStyle: 'text',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderNumber'),
          width: 150,
          callback: row => this.readOne(row)
        },
        {
          prop: 'budgetManagementNum',
          label: this.$t('purchaseDemand.budgetNumber'), // 预算编号
          width: 150
        },
        {
          prop: 'ceeaPurchaseOrderDate',
          label: () => this.$t('oneStopShopping.orderDate'),
          width: 100,
          formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
        },
        {
          prop: 'orderType',
          label: () => this.$t('bid_mod.purchaseType'), // 采购类型
          width: 100,
          formattor: val => this.$getDictLabel('ORDER_TYPE', val)
        },
        {
          prop: 'orderStatus',
          label: () => this.$t('orderMod.buyerOrderSynergy.orderStatus'), // 订单状态
          width: 100,
          formattor: val => this.$getDictLabel('PURCHASE_ORDER', val)
        },
        {
          prop: 'storageStatus',
          label: () => this.$t('orderMod.buyerOrderSynergy.warehouseReceiptStatus'), // 入库状态
          width: 100,
          formattor: (val) => this.$getDictLabel('STORAGE_STATUS', val)
        },
        // 业务实体
        {
          prop: 'ceeaOrgName',
          label: () => this.$t('purchaseDemand.businessEntity'),
          width: 150
        },
        // 库存组织
        {
          prop: 'organizationName',
          label: () => this.$t('purchaseDemand.invOrg'),
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
          prop: 'ceeaIfSupplierConfirm',
          label: () => this.$t('oneStopShopping.ifSupplierConfirm'), // 是否供方确认
          width: 130,
          formattor: val => this.$getDictLabel('YES_OR_NO', val)
        },
        {
          prop: 'refusedReason',
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
          prop: 'sourceSystem',
          label: () => this.$t('orderMod.buyerOrderSynergy.sourceSystem'), // 来源系统
          width: 130,
          formattor: val => this.$getDictLabel('SOURCE_SYSTERM', val)
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
              callback: row => this.confirmDelivery(row),
              code: 'po:buyerPurchaseOrder:confirmDelivery',
              formattor: () => this.$t('common.edit'),
              show: row =>
                ['WITHDRAW', 'REJECT', 'DRAFT'].includes(row.orderStatus)
            },
            {
              callback: row => this.approvalOne(row),
              code: 'po:buyerPurchaseOrder:approval',
              formattor: _ => this.$t('common.approve'), // 审批
              show: row => this.isShowApprove(row)
            },
            {
              callback: row => this.approvalOneItem(row),
              formattor: () => this.$t('purchaseDemand.approved'), // 审批通过
              code: 'po:buyerPurchaseOrder:pass',
              show: row =>
                // None为本地
                this.srmFlowMode.includes(row.integrationMode) &&
                row.orderStatus === 'SUBMITTED' &&
                row.createdBy === this.globalNickname
            },
            {
              callback: row => this.delOrder('one', [row]),
              code: 'po:buyerPurchaseOrder:delete',
              formattor: () => this.$t('common.delete'),
              show: row =>
                row.orderStatus === 'DRAFT'
            },
            {
              callback: row => this.changeOrder(row),
              code: 'po:buyerPurchaseOrder:orderChange',
              formattor: () => this.$t('orderMod.changeOrder'),
              show: row =>
                ['APPROVED'].includes(row.orderStatus)
            },
            {
              callback: row => this.adandonOne(row),
              code: 'po:buyerPurchaseOrder:adandon',
              formattor: () => this.$t('common.cancelled'), // 作废
              show: row =>
                row.orderStatus === 'REFUSED' &&
                row.createdBy === this.globalNickname
            }
          ]
        }
      ],
      formLabelWidth: '100px',
      globalNickname: null,
      queryParam: {}
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
          this.readOne(row)
        }
        // 来源：采购订单变更
        if (this.$route.params.from === 'purchaseOrderChangeList') {
          this.readOne(this.$route.params.row)
        }
      }
    }
  },
  created () {
    this.globalNickname = this.$store.getters.userInfo
      ? this.$store.getters.userInfo.username
      : null
    this.getQuerydata()
    this.getFlowIntegrationMode()
  },
  methods: {
    async adandonOne (row) {
      const isConfirm = await this.$confirm(
        this.$t('purSettlementMod.isDiscarded'), // 单据废弃后不可撤回，请确认后继续！
        this.$t('common.tips'),
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        },
      )

      if (isConfirm !== 'confirm') {
        // 非确认则返回
        return
      }
      this.$http({
        url: '/api-sup-ce/po/order/abandon',
        method: 'GET',
        params: { orderId: row.orderId },
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success'))
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    // 审批通过
    async approvalOneItem (row) {
      const isConfirm = await this.$confirm(
        this.$t('orderMod.supplierConfirm'), // 确认后审批通过！
        this.$t('common.tips'),
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        },
      )

      if (isConfirm !== 'confirm') {
        // 非确认则返回
        return
      }
      this.$http({
        url: '/api-sup-ce/po/order/approval',
        method: 'POST',
        data: { order: { orderId: row.orderId } }
      }).then((res) => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    // 订单变更
    async changeOrder (row) {
      const { code, data } = await this.$http({
        url: '/api-sup-ce/po/orderchange/startOrderChange',
        method: 'GET',
        params: { orderId: row.orderId },
        loading: true
      })

      if (code === '0') {
        this.$router.push({
          name: 'purchaseOrderChange',
          params: {
            from: 'buyerPurchaseOrder',
            row,
            data
          }
        })
      }
    },
    // 判断是否显示审批按钮；区分开启关闭审批流能否指定审批人
    isShowApprove (row) {
      // tab审批流模式开启下
      const isOpen = this.flowWithTabMode.includes(row.integrationMode)
      const isSubmit = ['SUBMITTED'].includes(row.orderStatus)
      const needStatus = ['UNDER_APPROVAL'].includes(row.orderStatus)
      const isApprove = !!row.arroverId
      // 若开启审批流,已提交，或者撤回、审批中且存在流程id
      return isOpen && (isSubmit || (needStatus && isApprove))
    },
    // 批量删除
    async delOrder (type, rows) {
      const sign = await this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
      if (sign !== 'confirm') return

      let unDel = 0
      const list = type === 'mutil' ? this.currentRows : rows
      list.forEach(item => {
        if (
          item.orderStatus !== 'DRAFT' ||
          item.createdBy !== this.globalNickname
        ) {
          unDel++
        }
      })
      this.$nextTick(() => {
        if (unDel <= 0) {
          const params = list.map(i => i.orderId)
          this.$http({
            url: '/api-sup-ce/po/order/batchDelete',
            method: 'POST',
            data: params
          }).then(res => {
            this.$message.success(this.$t('common.success'))
            this.getQuerydata()
          })
        } else {
          this.$message({
            type: 'warning',
            message: this.$t('purchaseDemand.have') + unDel + this.$t('purchaseDemand.msgNotDelete')
          })
        }
      })
    },
    getQuerydata (obj) {
      const params = {}
      const { dateList, ...rest } = obj || this.queryParam
      if (dateList) {
        params.startTime = dateList[0]
        params.endTime = dateList[1]
      }
      let fields = 'ORDER_ID,ORDER_NUMBER,BUDGET_MANAGEMENT_NUM,CEEA_PURCHASE_ORDER_DATE,ORDER_TYPE,ORDER_STATUS,CEEA_ORG_NAME,ORGANIZATION_NAME,VENDOR_NAME,REFUSE_REASON,SOURCE_SYSTEM,CREATED_FULL_NAME,CREATION_DATE,CEEA_IF_SUPPLIER_CONFIRM,VENDOR_CODE,CEEA_EMP_USERNAME,CEEA_SAVE_BY,CREATED_BY'
      this.queryParam = { ...rest, ...params, fields }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    handleCurrentChange (val) {
      this.currentRows = val
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
    confirmDelivery (row, showType = '') {
      // 编辑tab
      const tab = {
        component: purchaseOrderDetail,
        params: {
          flag: 'edit',
          row,
          ctrlHeight: true,
          showType: row.orderStatus === 'REFUSED' ? 'readOnly' : ''
        },
        title: row.orderNumber,
        name: 'purchaseOrderDetail' + row.orderNumber
      }
      this.$emit('tab-add', tab)
    },
    addOrder (showType = '') {
      // 编辑tab
      const tab = {
        component: purchaseOrderDetail,
        params: {
          flag: 'add',
          row: {},
          ctrlHeight: true,
          showType
        },
        title: this.$t('orderMod.buyerOrderSynergy.newOrder'),
        name: 'purchaseOrderDetail'
      }
      this.$emit('tab-add', tab)
    },
    // 审批
    approvalOne (row) {
      const tab = {
        component: purchaseOrderDetail,
        params: {
          flag: 'approvalOnly',
          row: row,
          ctrlHeight: true,
          showType: 'readOnly',
          tabName: 'purchaseOrderDetail' + row.orderNumber,
          activeWorkflowTab: true
        },
        title: row.orderNumber,
        name: 'purchaseOrderDetail' + row.orderNumber
      }
      this.$emit('tab-add', tab)
    },
    afterQuery (data) {
      this.$refs[this.gridId].setTableData(async tableData => {
        tableData.forEach(item => this.$set(item, 'integrationMode', this.integrationMode))

        if (this.notSearchTodoMode.includes(this.integrationMode)) {
          return
        }

        await this.listQueryTodo()

        const maps = []
        this.queryTodoList.forEach(item => maps.push(item.businessId))
        tableData.forEach(row => {
          let tempId = String(row.orderId)
          if (maps.includes(tempId)) {
            this.$set(row, 'workflowAuditStatus', 'WAIT')
            this.$set(row, 'arroverId', tempId)
          }
        })
      })
    },
    async listQueryTodo () {
      let res = await this.$api.base.flowAPI.queryTodo({ businessType: 'ORDER' })
      this.queryTodoList = res.data
    },
    async getFlowIntegrationMode () {
      let res = await this.$api.base.flowAPI.getFlowIntegrationMode({ businessType: 'ORDER' })
      if (res.data) {
        this.integrationMode = res.data
      }
    }
  }
}
</script>
<style scoped lang="scss">
</style>
