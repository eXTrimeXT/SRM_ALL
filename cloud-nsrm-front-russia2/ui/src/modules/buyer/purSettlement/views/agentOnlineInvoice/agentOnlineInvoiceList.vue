<template>
  <el-container class="flex-container the_onlineInvoice_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="preArr"
        form-label-width="120px"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!-- 创建开票单 -->
          <AuthorityButton type="primary" @click="editTab('add')">
            {{ $t('purSettlementMod.createBillingSlip') }}
          </AuthorityButton>
          <ExportExcel
            type="default"
            :table-header="tableHeader"
            export-mode="front"
            :dict-codes="dictCodes"
            page-url="/api-sup-ce/ps/invoice/onlineInvoice/listPageByParm"
            :filter-params="queryParam"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :comActive="$attrs['changeTab']"
        url="/api-sup-ce/ps/invoice/onlineInvoice/listPageByParm"
        @afterQuery="afterQuery"
      />
    </el-main>
  </el-container>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import CPagination from 'lib@/components/c-pagination'
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import agentOnlineInvoiceDetail from './agentOnlineInvoiceDetail'
import { parseTime } from '@/utils'
import ExportExcel from 'lib@/components/export-excel'

export default {
  name: 'AgentOnlineInvoiceList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    CPagination,
    OrganizationSelector,
    QuickSearch,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      dictCodes: {
        invoiceStatus: 'INVOICE_STATUS',
        businessType: 'BUSINESS_TYPE',
        payMethod: 'PAYMENT_MODE'
      },
      globalNickname: null,
      name: '',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      tableName: 'agentOnlineInvoiceList',
      gridId: 'agentOnlineInvoiceList',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      preArr: [
        {
          prop: 'onlineInvoiceNum',
          label: () => this.$t('purSettlementMod.billingNumber') // 开票单号
        },
        {
          prop: 'orgId',
          label: () => this.$t('quota.org'), // 业务实体
          type: 'OUorganizationSelector'
        },
        {
          prop: 'organizationId',
          parentId: 'orgId',
          label: () => this.$t('purchaseDemand.invOrg'), // 库存组织
          type: 'INVorganizationSelector'
        },
        {
          prop: 'dateList',
          label: () => this.$t('bidMod.dateCreated'), // 创建日期
          type: 'daterange'
        },
        {
          prop: 'dateComplates',
          label: () => this.$t('purSettlementMod.approvalCompleTime'),
          type: 'daterange'
        },
        {
          prop: 'invoiceNoticeNumber',
          label: () => this.$t('purSettlementMod.statementNumber') // 对账单号
        },
        {
          prop: 'vendorId',
          label: () => this.$t('bidMod.provider'), // 供应商
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyId',
          name: 'scc_sup_company_info_all'
        },
        {
          prop: 'invoiceStatus',
          label: () => this.$t('purSettlementMod.paymentPlanStatus'), // 单据状态
          type: 'dict',
          code: 'INVOICE_STATUS'
        },
        {
          prop: 'payMethod',
          label: () => this.$t('paymentType.paymentWay'), // 付款方式
          type: 'dict',
          code: 'PAYMENT_MODE'
        },
        {
          prop: 'invoiceCode',
          label: () => this.$t('accountMod.invoiceCode') // 发票代码
        }
      ],
      queryParam: {},
      // curRole: this.$store.getters.userType, // VENDOR BUYER curRole==='VENDOR'
      curRole: '',
      rolePermissions: '', // 操作角色 Buyer 采购员\ AccountSpecialist 财务专员
      // userInfo: this.$store.getters.userInfo,
      userInfo: {},
      selectionItem: [],
      displayMaterialItem: [],
      parentOrgTableDataPage: {
        total: 0,
        pageNum: 1,
        pageSize: 20
      },
      dialogFormVisible: false,
      parentOrgQueryForm: {
        pageNum: 1,
        pageSize: 10
      },
      formLabelWidth: '120px'
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
          this.$route.params.funName === 'agentOnlineInvoice'
        ) {
          let onlineInvoiceId = Number(this.$route.params.formId)
          let formNo = this.$route.params.formNo // 流程标题
          let row = {
            ...this.$route.params,
            onlineInvoiceId,
            onlineInvoiceNum: formNo // tab 标题显示
          }
          this.editTab('view', row)
        }
      }
    }
  },
  created () {
    let _this = this
    this.globalNickname = this.$store.getters.userInfo
      ? this.$store.getters.userInfo.username
      : null
    this.tableHeader = [
      { prop: 'orgName', label: _this.$t('quota.org'), width: 120 }, // 业务实体
      { prop: 'organizationName', label: _this.$t('purchaseDemand.invOrg'), width: 120 }, // 库存组织
      {
        prop: 'onlineInvoiceNum',
        label: this.$t('purSettlementMod.billingNumber'), // 开票单号
        width: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.currentRow = row
          this.editTab('view', row)
        }.bind(this),
        formattor (val) {
          return val || '--'
        }
      },
      // 单据状态
      {
        prop: 'invoiceStatus',
        label: _this.$t('purSettlementMod.paymentPlanStatus'),
        width: 100,
        dataType: 'dict',
        code: 'INVOICE_STATUS'
      },
      // 供应商编码
      { prop: 'vendorCode', label: _this.$t('common.vendorCode'), width: 120 },
      // 供应商名称
      { prop: 'vendorName', label: _this.$t('common.vendorName'), width: 150 },
      // 开票含税金额
      {
        prop: 'taxTotalAmount',
        label: _this.$t('purSettlementMod.taxTotalAmount'),
        width: 120
      },
      // 付款方式
      {
        prop: 'payMethod',
        label: _this.$t('paymentType.paymentWay'),
        dataType: 'dict',
        code: 'PAYMENT_MODE',
        width: 120
      },
      {
        prop: 'currencyName',
        label: _this.$t('vendorMod.currencyCode'), // 币种
        width: 120
      },
      {
        prop: 'taxRate',
        label: _this.$t('bid_mod.taxRate'), // 税率
        width: 120
      },
      // 创建人
      {
        prop: 'createdUserName', // createdBy
        label: _this.$t('common.creator'),
        width: 100
      },
      // 创建时间
      {
        prop: 'creationDate',
        label: _this.$t('common.creationTime'),
        width: 100,
        formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      // 审批完成日期
      {
        prop: 'approvedDate',
        label: _this.$t('purSettlementMod.approvedDate'),
        width: 130,
        formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      {
        prop: 'operation',
        label: _this.$t('common.operation'),
        width: 180,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: row => this.editTab('edit', row),
            formattor: _ => this.$t('common.edit'), // 编辑
            show: row => ['DRAFT'].includes(row.invoiceStatus)
          },
          {
            callback: row => this.buyerAbandon(row),
            formattor: _ => this.$t('common.cancelled'), // 作废
            show: row => ['REJECTED', 'WITHDRAW'].includes(row.invoiceStatus) &&
              row.createdBy === this.globalNickname
          },
          {
            callback: row => this.delRowData(row),
            formattor: _ => this.$t('common.delete'), // 删除
            show: row => ['DRAFT'].includes(row.invoiceStatus) &&
              row.createdBy === this.globalNickname
          },
          {
            callback: row => this.approvalOne(row),
            formattor: _ => this.$t('common.approve'), // 审批
            show: row => this.isShowApprove(row)
          },
          {
            callback: row => this.supplierConfirm(row),
            formattor: _ => this.$t('bidMod.approvalPass'), // 审批通过 终审
            show: row =>
              ['FIRST_REVIEW_APPROVED', 'SUBMITTED'].includes(row.invoiceStatus) &&
              this.notSearchTodoMode.includes(row.integrationMode) &&
              !row.workflowAuditStatus
          },
          {
            callback: row => this.viewTab(row),
            formattor: _ => this.$t('common.edit'), // 查看审批 -- 改为编辑
            // [供方已提交]
            show: row => ['VENDOR_SUBMITTED', 'REJECTED', 'WITHDRAW'].includes(row.invoiceStatus)
          }
        ]
      }
    ]

    this.$nextTick(() => {
      this.getQuerydata()
    })
  },
  methods: {
    // 判断是否显示审批按钮；区分开启关闭审批流能否指定审批人
    isShowApprove (row) {
      // tab审批流模式开启下
      const isOpen = this.flowWithTabMode.includes(row.integrationMode)
      const isSubmit = row.invoiceStatus === 'SUBMITTED'
      const needStatus = row.invoiceStatus === 'UNDER_APPROVAL'
      const isFirstApprove = row.invoiceStatus === 'FIRST_REVIEW_APPROVED'
      const isApprove = !!row.arroverId
      // 若开启审批流,已提交，或者撤回、审批中且是指定审批人
      if (isOpen) {
        // 注意：只有审批中状态才有流程id
        return isSubmit || isFirstApprove || (needStatus && isApprove)
      }
    },
    // 审批通过
    supplierConfirm (row) {
      this.$http({
        url: '/api-sup-ce/ps/invoice/onlineInvoice/finalApproval',
        method: 'GET',
        params: { onlineInvoiceId: row.onlineInvoiceId },
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    // 审批 -- 跳审批流
    approvalOne (row) {
      this.$emit('tab-add', {
        component: agentOnlineInvoiceDetail,
        params: {
          flag: 'approvalOnly',
          onlineInvoiceId: row.onlineInvoiceId,
          tabName: 'agentOnlineInvoiceDetail' + row.onlineInvoiceNum,
          activeWorkflowTab: true
        },
        title: row.onlineInvoiceNum,
        name: 'agentOnlineInvoiceDetail' + row.onlineInvoiceNum
      })
    },
    // 驳回
    abandonHandel (row) {
      this.$http({
        url: '/api-sup-ce/ps/invoice/onlineInvoice/reject',
        method: 'GET',
        params: { onlineInvoiceId: row.onlineInvoiceId, rejectReason: this.rejectReason },
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    getQuerydata (obj) {
      const params = {}
      const { dateList, dateComplates, ...rest } = obj || this.queryParam
      if (dateList) {
        params.startCreationDate = dateList[0]
        params.endCreationDate = dateList[1]
      }
      if (dateComplates) {
        params.startApprovedDate = dateComplates[0]
        params.endApprovedDate = dateComplates[1]
      }
      this.queryParam = { ...rest, ...params }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
      })
    },
    syncFilterParams (values) {
      this.queryParam = values
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    // 采购商审核
    buyerApproval (row) {
      this.$http({
        url: '/api-sup-ce/invoice/invoiceNotice/confirm',
        method: 'GET',
        params: { onlineInvoiceId: row.onlineInvoiceId },
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    // 删除
    async delRowData (row) {
      const sign = await this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
      if (sign !== 'confirm') return

      this.$http({
        url: '/api-sup-ce/ps/invoice/onlineInvoice/deleteByOnlineInvoiceId',
        method: 'GET',
        params: { onlineInvoiceId: row.onlineInvoiceId },
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    // 查看审批
    viewTab (row) {
      // 修改
      const tab = {
        component: agentOnlineInvoiceDetail,
        params: {
          flag: 'viewApproval',
          onlineInvoiceId: row.onlineInvoiceId,
          tabName: 'agentOnlineInvoiceDetail' + row.onlineInvoiceNum
        },
        title: row.onlineInvoiceNum,
        name: 'agentOnlineInvoiceDetail' + row.onlineInvoiceNum
      }
      this.$emit('tab-add', tab)
    },
    editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: agentOnlineInvoiceDetail,
          params: {
            flag: 'add',
            tabName: 'agentOnlineInvoiceDetail'
          },
          title: this.$t('purSettlementMod.newOnlineInvoice'),
          name: 'agentOnlineInvoiceDetail'
        }
      } else {
        // 修改
        tab = {
          component: agentOnlineInvoiceDetail,
          params: {
            flag: type,
            showType: 'approveNumber',
            onlineInvoiceId: row.onlineInvoiceId,
            tabName: 'agentOnlineInvoiceDetail' + row.onlineInvoiceNum
          },
          title: row.onlineInvoiceNum,
          name: 'agentOnlineInvoiceDetail' + row.onlineInvoiceNum
        }
      }
      this.$emit('tab-add', tab)
    },
    handleSelectionChange2 (selection) {
      this.selectionItem = selection
    },
    setRowAmount (row) {
      if (row.notInvoiceQuantity > 0) {
        if (row.invoiceQuantity > row.notInvoiceQuantity) {
          return this.$message.warning(this.$t('purSettlementMod.invoiceMsg[0]'))
        }
      } else {
        if (row.invoiceQuantity < row.notInvoiceQuantity || row.invoiceQuantity > 0) {
          return this.$message.warning(this.$t('purSettlementMod.invoiceMsg[0]'))
        }
      }
      row.noTaxAmount = Number(
        Number(row.invoiceQuantity) * Number(row.unitPriceExcludingTax || 0),
      ).toFixed(2)
    },
    // 【作废】
    async buyerAbandon (row) {
      const isConfirm = await this.$confirm(
        this.$t('purSettlementMod.isDiscarded'),
        this.$t('common.tips'),
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        },
      )
      if (isConfirm !== 'confirm') {
        return
      }
      this.$http({
        url: '/api-sup-ce/ps/invoice/onlineInvoice/abandon',
        method: 'GET',
        params: { onlineInvoiceId: row.onlineInvoiceId },
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    getcontractObj (val, scope) {
      scope.contractNo = val.contractCode
    },
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    async afterQuery () {
      await this.getFlowIntegrationMode()

      this.$refs[this.gridId].setTableData(async tableData => {
        tableData.forEach(item => this.$set(item, 'integrationMode', this.integrationMode))

        if (this.notSearchTodoMode.includes(this.integrationMode)) {
          return
        }

        await this.listQueryTodo()

        const maps = []
        this.queryTodoList.forEach(item => maps.push(item.businessId))
        tableData.forEach(row => {
          let tempId = String(row.onlineInvoiceId)
          if (maps.includes(tempId)) {
            this.$set(row, 'workflowAuditStatus', 'WAIT')
            this.$set(row, 'arroverId', tempId)
          }
        })
      })
    },
    async listQueryTodo () {
      const res = await this.$api.base.flowAPI.queryTodo({ businessType: 'onlineInvoice' })
      this.queryTodoList = res.data
    },
    async getFlowIntegrationMode () {
      const res = await this.$api.base.flowAPI.getFlowIntegrationMode({ businessType: 'onlineInvoice' })
      if (res.data) {
        this.integrationMode = res.data
      }
    }
  }
}
</script>
<style scoped lang="scss">
.the_onlineInvoice_wrapper {
  .the_filter_form {
    .el-form-item {
      margin-bottom: 5px;
    }
  }
  .topComment {
    margin-top: 15px;
    float: right;
  }
}
</style>
