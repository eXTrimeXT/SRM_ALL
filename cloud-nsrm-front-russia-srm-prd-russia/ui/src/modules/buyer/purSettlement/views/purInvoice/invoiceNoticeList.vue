<template>
  <el-container
    class="flex-container the_quotationPrices_wrapper"
    direction="vertical"
  >
    <el-main>
      <FormWrapper
        :form-array="preArr"
        form-label-width="120px"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!-- 新增 -->
          <AuthorityButton type="primary" @click="editTab('add')">
            {{ $t("purSettlementMod.newStatement") }}
          </AuthorityButton>
          <!-- 导出 -->
          <ExportExcel
            type="default"
            :table-header="tableHeader"
            export-mode="front"
            :dict-codes="dictCodes"
            :page-url="queryUrl"
            :filter-params="queryParam"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :checkbox="true"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :comActive="$attrs['changeTab']"
        :url="queryUrl"
        @afterQuery="afterQuery"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import invoiceNoticeDetail from './invoiceNoticeDetail'
import { parseTime } from '@/utils'
import { downloadFileLinkByPost } from 'lib@/utils/file'
import ExportExcel from 'lib@/components/export-excel'

export default {
  name: 'InvoiceNoticeList',
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
        invoiceNoticeStatus: 'INVOICE_NOTICE_STATUS'
      },
      globalNickname: null,
      name: '',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      tableName: 'invoiceNoticeList',
      gridId: 'invoiceNoticeList',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      preArr: [
        {
          prop: 'invoiceNoticeNumber',
          label: () => this.$t('purSettlementMod.statementNumber') // 对账单号
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
          prop: 'vendorId',
          label: () => this.$t('bidMod.provider'), // 供应商
          type: 'quicksearch',
          showKey: 'companyName',
          propKey: 'companyId',
          name: 'scc_sup_company_info_all'
        },
        {
          prop: 'invoiceNoticeStatus',
          label: () => this.$t('purSettlementMod.paymentPlanStatus'), // 单据状态
          type: 'dict',
          code: 'INVOICE_NOTICE_STATUS'
        },
        {
          prop: 'receiveDate',
          label: () => this.$t('purSettlementMod.approvalCompleTime'), // 审批完成时间
          type: 'daterange'
        },
        {
          prop: 'receiveOrderNo',
          label: () => this.$t('accountMod.inboundReturnOrderNo') // 入库/退货单号
        },
        {
          prop: 'orderNumber',
          label: () => this.$t('purSettlementMod.orderNumber') // 采购订单号
        }
      ],
      queryParam: {},
      curRole: this.$store.getters.userType, // VENDOR BUYER curRole==='VENDOR'
      rolePermissions: '', // 操作角色 Buyer 采购员\ AccountSpecialist 财务专员
      userInfo: this.$store.getters.userInfo,
      queryUrl: ''
    }
  },
  watch: {
    $route: {
      // 当前功能已经打开的时候监听是否是从其他功能跳转过来的
      deep: true,
      immediate: true,
      handler () {
        if (
          this.$route.params.from === 'workCount' &&
          this.$route.params.funName === 'purInvoice'
        ) {
          // 供应商 工作台跳转
          this.queryParam.invoiceNoticeStatus =
            this.$route.params.invoiceNoticeStatus
          this.preFormObj = Object.assign(
            {},
            { invoiceNoticeStatus: this.$route.params.invoiceNoticeStatus }
          )
        } else if (
          this.$route.params.from === 'fromFun' &&
          this.$route.params.funName === 'purInvoice'
        ) {
          // 采购商 工作台跳转
          const invoiceNoticeId = Number(this.$route.params.formId)
          const formNo = this.$route.params.formNo // 流程标题
          const row = {
            ...this.$route.params,
            invoiceNoticeId,
            invoiceNoticeNumber: formNo // tab 标题显示
          }
          this.editTab('view', row)
        }
      }
    }
  },
  created () {
    this.globalNickname = this.$store.getters.userInfo
      ? this.$store.getters.userInfo.username
      : null
    this.queryUrl = '/api-sup-ce/ps/invoice/invoiceNotice/listPageByParm'
    const _this = this
    this.tableHeader = [
      { prop: 'orgName', label: _this.$t('quota.org'), width: 120 }, // 业务实体
      {
        prop: 'organizationName',
        label: _this.$t('purchaseDemand.invOrg'),
        width: 120
      }, // 库存组织
      {
        prop: 'invoiceNoticeNumber',
        label: _this.$t('purSettlementMod.statementNumber'), // 对账单号
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
      {
        prop: 'invoiceNoticeStatus',
        label: _this.$t('purSettlementMod.paymentPlanStatus'), // 单据状态
        width: 100,
        dataType: 'dict',
        code: 'INVOICE_NOTICE_STATUS'
      },
      { prop: 'vendorCode', label: _this.$t('common.vendorCode'), width: 120 }, // 供应商编码
      {
        prop: 'vendorName',
        label: _this.$t('bidMod.provider'), // 供应商
        minWidth: 150
      },
      {
        prop: 'ceeaReceiveStartDate',
        label: () => this.$t('purSettlementMod.statementStartTime'), // 对账期间从
        width: 150,
        formattor: (scope, row) => {
          return row.ceeaReceiveStartDate
        }
      },
      {
        prop: 'ceeaReceiveEndDate',
        label: () => this.$t('purSettlementMod.statementEndTime'), // 对账期间至
        width: 150,
        formattor: (scope, row) => {
          return row.ceeaReceiveEndDate
        }
      },
      {
        prop: 'ceeaTaxTotalAmount',
        label: _this.$t('contractMod.totalAmountTax'), // 含税总金额
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
      {
        prop: 'createdUserName',
        label: _this.$t('common.creator'), // 创建人
        width: 100
      },
      {
        prop: 'creationDate',
        label: _this.$t('purSettlementMod.creationDate'), // 创建日期
        width: 100,
        formattor: (val) => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      {
        prop: 'approvedDate',
        label: _this.$t('purSettlementMod.approvalCompleTime'), // 审批完成时间
        width: 120,
        formattor: (val) => (val ? parseTime(val, '{y}-{m}-{d}') : '')
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
            show: (row) => ['DRAFT'].includes(row.invoiceNoticeStatus)
          },
          {
            callback: row => this.delRowData(row),
            formattor: _ => this.$t('common.delete'), // 删除
            show: (row) =>
              ['DRAFT'].includes(row.invoiceNoticeStatus) &&
              row.createdBy === this.globalNickname
          },
          {
            callback: row => this.approvalOne(row),
            formattor: _ => this.$t('common.approve'), // 审批
            show: row => this.isShowApprove(row)
          },
          {
            callback: row => this.supplierConfirm(row),
            formattor: _ => this.$t('purSettlementMod.Approved'), // 审批通过  终审
            show: (row) =>
              ['FIRST_REVIEW_APPROVED', 'SUBMITTED'].includes(row.invoiceNoticeStatus) &&
              this.srmFlowMode.includes(row.integrationMode) &&
              !row.workflowAuditStatus
          },
          {
            callback: row => this.viewTab(row),
            formattor: _ => this.$t('common.edit'), // 查看审批 -- 改为编辑
            // [供方已提交]
            show: (row) => ['VENDOR_SUBMITTED', 'REJECTED', 'WITHDRAW'].includes(row.invoiceNoticeStatus)
          },
          {
            callback: row => this.buyerAbandon(row),
            formattor: _ => this.$t('common.cancelled'), // 作废
            show: (row) =>
              row.invoiceNoticeStatus === 'REFUSED' &&
              row.createdBy === this.globalNickname
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })
    this.getFlowIntegrationMode()
  },
  methods: {
    // 作废
    async buyerAbandon (row) {
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
        url: '/api-sup-ce/ps/invoice/invoiceNotice/abandon',
        method: 'POST',
        data: { invoiceNoticeId: row.invoiceNoticeId },
        loading: true
      }).then((res) => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    // 判断是否显示审批按钮；区分开启关闭审批流能否指定审批人
    isShowApprove (row) {
      // 审批流tab接入情况下
      const isOpen = this.flowWithTabMode.includes(row.integrationMode)
      const isSubmit = row.invoiceNoticeStatus === 'SUBMITTED'
      const needStatus = row.invoiceNoticeStatus === 'UNDER_APPROVAL'
      const isFirstApprove = row.invoiceNoticeStatus === 'FIRST_REVIEW_APPROVED'
      const isApprove = !!row.arroverId
      // 若开启审批流,已提交，或者撤回、审批中且是指定审批人
      if (isOpen) {
        // 注意：只有审批中状态才有流程id
        return isSubmit || isFirstApprove || (needStatus && isApprove)
      }
    },
    isApprove (userId) {
      return String(userId) === String(this.userInfo.userId)
    },
    // 导出
    exportList () {
      const params = Object.assign(
        {},
        {
          ...this.queryParam,
          pageNum: this.getFooterNum,
          pageSize: 15
        }
      )
      // (待) 等接口
      downloadFileLinkByPost('/api-sup-ce/', null, params).catch(
        () => {
          // 下载失败
          this.$message.error(this.$t('components.eio.downloadFail'))
        }
      )
    },
    getQuerydata (obj) {
      const params = {}
      const { dateList, receiveDate, ...rest } = obj || this.queryParam
      if (dateList) {
        params.startCreationDate = dateList[0]
        params.endCreationDate = dateList[1]
      }
      if (receiveDate) {
        params.startApprovedDate = receiveDate[0]
        params.endApprovedDate = receiveDate[1]
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
    // 采购方撤回
    buyerWithdraw (row) {
      this.$http({
        url: '/api-sup-ce/ps/invoice/invoiceNotice/withdraw',
        method: 'GET',
        params: { invoiceNoticeId: row.invoiceNoticeId },
        loading: true
      }).then((res) => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    // 终审
    supplierConfirm (row) {
      this.$http({
        url: '/api-sup-ce/ps/invoice/invoiceNotice/finalApproval',
        method: 'GET',
        params: { invoiceNoticeId: row.invoiceNoticeId },
        loading: true
      }).then((res) => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    // 审批 -- 跳审批流
    approvalOne (row) {
      this.$emit('tab-add', {
        component: invoiceNoticeDetail,
        params: {
          flag: 'approvalOnly',
          invoiceNoticeId: row.invoiceNoticeId,
          tabName: 'invoiceNoticeDetail' + row.invoiceNoticeNumber,
          activeWorkflowTab: true
        },
        title: row.invoiceNoticeNumber,
        name: 'invoiceNoticeDetail' + row.invoiceNoticeNumber
      })
    },
    // 供应商驳回
    abandonHandel (row) {
      this.$http({
        url: '/api-sup-ce/ps/invoice/invoiceNotice/reject',
        method: 'GET',
        params: {
          invoiceNoticeId: row.invoiceNoticeId,
          rejectReason: this.rejectReason
        },
        loading: true
      }).then((res) => {
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

      const invoiceNoticeId = row.invoiceNoticeId
      this.$http({
        url: '/api-sup-ce/ps/invoice/invoiceNotice/deleteByInvoiceNoticeId',
        method: 'GET',
        params: { invoiceNoticeId },
        loading: true
      }).then((res) => {
        this.$message({
          message: res.message,
          type: 'success'
        })
        this.getQuerydata()
      })
    },
    // 查看审批
    viewTab (row) {
      // 修改
      const tab = {
        component: invoiceNoticeDetail,
        params: {
          flag: 'viewApproval',
          invoiceNoticeId: row.invoiceNoticeId,
          tabName: 'invoiceNoticeDetail' + row.invoiceNoticeNumber
        },
        title: row.invoiceNoticeNumber,
        name: 'invoiceNoticeDetail' + row.invoiceNoticeNumber
      }
      this.$emit('tab-add', tab)
    },
    editTab (type, row) {
      let tab = {}
      if (type === 'add') {
        // 新增
        tab = {
          component: invoiceNoticeDetail,
          params: {
            flag: 'add',
            tabName: 'invoiceNoticeDetail'
          },
          title: this.$t('purSettlementMod.newStatement'),
          name: 'invoiceNoticeDetail'
        }
      } else {
        // 修改
        tab = {
          component: invoiceNoticeDetail,
          params: {
            flag: type,
            showType: 'approveNumber',
            invoiceNoticeId: row.invoiceNoticeId,
            tabName: 'invoiceNoticeDetail' + row.invoiceNoticeNumber
          },
          title: row.invoiceNoticeNumber,
          name: 'invoiceNoticeDetail' + row.invoiceNoticeNumber
        }
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
          let tempId = String(row.invoiceNoticeId)
          if (maps.includes(tempId)) {
            this.$set(row, 'workflowAuditStatus', 'WAIT')
            this.$set(row, 'arroverId', tempId)
          }
        })
      })
    },
    async listQueryTodo () {
      const res = await this.$api.base.flowAPI.queryTodo({ businessType: 'invoiceNotice' })
      this.queryTodoList = res.data
    },
    async getFlowIntegrationMode () {
      const res = await this.$api.base.flowAPI.getFlowIntegrationMode({ businessType: 'invoiceNotice' })
      if (res.data) {
        this.integrationMode = res.data
      }
    }
  }
}
</script>
<style scoped lang="scss">
  .topComment {
    margin-top: 15px;
    float: right;
  }
</style>
