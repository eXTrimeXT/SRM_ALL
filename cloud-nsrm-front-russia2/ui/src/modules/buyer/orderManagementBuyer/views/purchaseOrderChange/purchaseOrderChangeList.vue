<template>
  <el-container class="flex-container the_purchaseOrderChangeList" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="preArr"
        form-label-width="120px"
        @getFormData="getQueryData"
      />
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        :auto-query="false"
        :comActive="$attrs['changeTab']"
        url="/api-sup-ce/po/orderchange/listPage"
        :open-custom-table="true"
        @afterQuery="afterQuery"
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

export default {
  name: 'PurchaseOrderChangeList',
  components: {
    TableView,
    MainHeader,
    FormWrapper
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      globalNickname: '',
      currentRows: [],
      pageSize: 15,
      gridId: 'list',
      tableData: [],
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
          prop: 'organizationId',
          parentId: 'orgId',
          label: () => this.$t('purchaseDemand.invOrg'),
          type: 'INVorganizationSelector' // 库存组织
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
          width: 120,
          btnStyle: 'text',
          fixed: 'right',
          showType: 'buttons',
          buttons: [
            {
              callback: row => this.editOne(row),
              code: 'pm:purchaseOrderChange:editOrder',
              show: row => ['DRAFT', 'REJECT', 'WITHDRAW'].includes(row.orderChangeStatus),
              formattor: () => this.$t('common.edit')
            },
            {
              callback: row => this.deleteOne(row),
              code: 'pm:purchaseOrderChange:delete',
              show: row =>
                row.orderChangeStatus === 'DRAFT' && row.createdBy === this.globalNickname,
              formattor: () => this.$t('common.delete')
            },
            {
              callback: row => this.approvalOne(row),
              code: 'pm:purchaseOrderChange:approveOrder',
              show: row => this.isShowApprove(row),
              formattor: () => this.$t('common.approve') // 审批 打开审批流情况下
            },
            {
              callback: row => this.supplierConfirm(row),
              code: 'pm:purchaseOrderChange:passOrder',
              formattor: () => this.$t('bidMod.approvalPass'),
              show: row =>
                ['SUBMITTED'].includes(row.orderChangeStatus) &&
                ['None', 'Push'].includes(row.integrationMode) &&
                row.createdBy === this.globalNickname
            },
            {
              callback: function (row) {
                this.destoryHandle(row)
              }.bind(this),
              code: 'pm:purchaseOrderChange:abandon',
              show: row =>
                ['REJECT', 'WITHDRAW', 'REFUSED'].includes(row.orderChangeStatus) &&
                row.createdBy === this.globalNickname,
              formattor: () => this.$t('common.cancelled') // 作废
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
            orderChangeNumber: formNo // tab 标题显示
          }
          this.readChangeOrderOne(row)
        }

        // 采购订单点击订单变更按钮跳转至采购订单变更详情页面
        if (this.$route.params.from === 'buyerPurchaseOrder') {
          const { row, data } = this.$route.params
          this.editOne(row, data)
        }
      }
    }
  },
  created () {
    this.globalNickname = this.$store.getters.userInfo
      ? this.$store.getters.userInfo.username
      : null

    this.$nextTick(() => {
      this.getQueryData()
    })

    this.getFlowIntegrationMode()
  },
  methods: {
    // 作废
    async destoryHandle (row) {
      const confirmSelectValue = await this.$confirm(
        this.$t('common.confirmAbandon'), // 确认作废这条数据
        this.$t('common.tips'),
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        },
      )

      if (confirmSelectValue !== 'confirm') return

      this.$http({
        url: '/api-sup-ce/po/orderchange/abandonOrderChange',
        method: 'GET',
        params: { id: row.orderChangeId },
        loading: true
      }).then(_ => {
        this.$message({
          type: 'success',
          message: this.$t('common.success')
        })
        this.getQuerydata()
      })
    },
    // 审批通过 - 不开启审批流
    async supplierConfirm (row) {
      const confirmSign = await this.$confirm(
        this.$t('orderMod.supplierConfirm'),
        this.$t('common.tips'),
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        }
      )
      if (confirmSign !== 'confirm') return

      this.$http({
        url: '/api-sup-ce/po/orderchange/approvedOrderChange',
        method: 'GET',
        params: { id: row.orderChangeId },
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQueryData()
      })
    },
    // 判断是否显示审批按钮；区分开启关闭审批流能否指定审批人
    isShowApprove (row) {
      // tab审批流模式开启下
      const isOpen = this.flowWithTabMode.includes(row.integrationMode)
      const isSubmit = ['SUBMITTED'].includes(row.orderChangeStatus)
      const needStatus = ['UNDER_APPROVAL'].includes(row.orderChangeStatus)
      const isApprove = !!row.arroverId
      // 若开启审批流,已提交，或者撤回、审批中且是指定审批人
      return isOpen && (isSubmit || (needStatus && isApprove))
    },
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
    deleteOne (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-sup-ce/po/orderchange/delete',
            method: 'GET',
            params: { id: row.orderChangeId },
            loading: true
          })
            .then(data => {
              this.$message({
                type: 'success',
                message: this.$t('common.successDelete')
              })
              this.getQueryData()
            })
            .catch(err => {
              console.log(err)
            })
        })
        .catch(() => {})
    },
    // 跳转采购订单变更页面 - 只读
    readChangeOrderOne (row) {
      const tab = {
        component: purchaseOrderChangeDetail,
        params: {
          flag: 'approveNumber',
          row,
          showType: 'readOnly'
        },
        title: row.orderChangeNumber,
        name: 'purchaseOrderChangeDetail' + row.orderChangeNumber
      }
      this.$emit('tab-add', tab)
    },
    // 跳转采购订单页面 - 只读
    readOrderOne (row) {
      this.$router.push({
        name: 'buyerPurchaseOrder',
        params: { from: 'purchaseOrderChangeList', row }
      })
    },
    // 审批
    approvalOne (row) {
      this.$emit('tab-add', {
        component: purchaseOrderChangeDetail,
        params: {
          flag: 'approvalOnly',
          row: row,
          showType: 'readOnly',
          tabName: 'purchaseOrderChangeDetail' + row.orderChangeNumber,
          activeWorkflowTab: true
        },
        title: row.orderChangeNumber,
        name: 'purchaseOrderChangeDetail' + row.orderChangeNumber
      })
    },
    editOne (row, data) {
      // 修改模式
      const tab = {
        component: purchaseOrderChangeDetail,
        params: {
          flag: 'edit',
          row,
          data
        },
        title: row.orderChangeNumber,
        name: 'purchaseOrderChangeDetail' + row.orderChangeNumber
      }
      this.$emit('tab-add', tab)
    },
    afterQuery () {
      this.$refs[this.gridId].setTableData(async tableData => {
        tableData.forEach(item => this.$set(item, 'integrationMode', this.integrationMode))

        if (this.notSearchTodoMode.includes(this.integrationMode)) {
          return
        }

        await this.listQueryTodo()

        const maps = []
        this.queryTodoList.forEach(item => maps.push(item.businessId))
        tableData.forEach(row => {
          let tempId = String(row.orderChangeId)
          if (maps.includes(tempId)) {
            this.$set(row, 'workflowAuditStatus', 'WAIT')
            this.$set(row, 'arroverId', tempId)
          }
        })
      })
    },
    async listQueryTodo () {
      const res = await this.$api.base.flowAPI.queryTodo({ businessType: 'ORDERCHANGE' })
      this.queryTodoList = res.data
    },
    async getFlowIntegrationMode () {
      const res = await this.$api.base.flowAPI.getFlowIntegrationMode({ businessType: 'ORDERCHANGE' })
      if (res.data) {
        this.integrationMode = res.data
      }
    }
  }
}
</script>
<style scoped lang="scss">
</style>
