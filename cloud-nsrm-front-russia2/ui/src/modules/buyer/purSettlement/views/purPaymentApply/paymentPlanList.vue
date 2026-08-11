<template>
  <el-container class="flex-container the_contractTemplateList_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        :form-array="preArr"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />
      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!-- 新增 -->
          <AuthorityButton
            code="pm:purPaymentApply:add"
            type="primary"
            @click="addNewBill"
          >
            {{ $t('common.add') }}
          </AuthorityButton>
          <!-- 自定义导出 -->
          <ExportExcel
            type="default"
            page-url="/api-sup-ce/payment/paymentApply/listPage"
            :filter-params="queryParam"
            :table-header="tableHeader"
            :dict-codes="dictCodes"
            timeout="1000000"
            export-mode="front"
          />
          <!-- 作废 -->
          <el-button @click="cancelledList">
            {{ $t('common.cancelled') }}
          </el-button>
          <!-- 付款 -->
          <el-button @click="payAmountList">
            {{ $t('purSettlementMod.payment') }}
          </el-button>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :checkbox="true"
        :table-data="tableData"
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-sup-ce/payment/paymentApply/listPage"
        :check-change="handleChange"
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
import paymentPlanDetail from './paymentPlanDetail'
import { parseTime } from '@/utils'
import OrganizationSelector from 'lib@/components/organization-selector'
import CPagination from 'lib@/components/c-pagination'
import QuickSearch from 'lib@/components/QuickSearch'
import ExportExcel from 'lib@/components/export-excel'

export default {
  name: 'PaymentPlanList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    paymentPlanDetail,
    OrganizationSelector,
    CPagination,
    QuickSearch,
    ExportExcel
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      paymentSelect: [],
      dictCodes: {
        status: 'PAYMENT_APPLY_STATUS',
        paymentStatus: 'PAYMENT_PAID_STATUS'
      },
      currentPage: null,
      name: 'contractTemplateTable',
      tableName: 'paymentPlanList',
      Currentpage: null,
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      selectList: [],
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      isModify: false,
      preArr: [
        { prop: 'paymentApplyNumber', label: () => this.$t('contractMod.paymentApplyNumber') }, // 付款申请单号
        {
          prop: 'orgId',
          label: () => this.$t('oneStopShopping.businessEntity'), // 业务实体
          type: 'OUorganizationSelector'
        },
        {
          prop: 'organizationId',
          parentId: 'orgId',
          label: () => this.$t('purchaseDemand.invOrg'), // 库存组织
          type: 'INVorganizationSelector'
        },
        {
          prop: 'createdBy',
          label: () => this.$t('common.creator') // 创建人
        },
        {
          prop: 'vendorCode',
          label: () => this.$t('common.vendor'), // 供应商
          type: 'quicksearch',
          showKey: 'companyCode',
          name: 'scc_sup_company_info_all'
        },
        {
          prop: 'dateList',
          label: () => this.$t('quota.createdDate'), // 创建日期
          type: 'daterange'
        },
        {
          prop: 'status',
          label: () => this.$t('purSettlementMod.paymentPlanStatus'), // 单据状态
          type: 'dict',
          code: 'PAYMENT_APPLY_STATUS'
        },
        {
          prop: 'paymentStatus',
          label: () => this.$t('contractMod.payStatus'), // 付款状态
          type: 'dict',
          code: 'PAYMENT_PAID_STATUS'
        }
      ],
      formLabelWidth: '120px',
      queryParam: {},
      selectionItem: [],
      globalNickname: null
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
          this.$route.params.funName === 'purPaymentApply'
        ) {
          let paymentApplyId = Number(this.$route.params.formId)
          let formNo = this.$route.params.formNo // 流程标题
          let row = {
            ...this.$route.params,
            paymentApplyId,
            paymentApplyNumber: formNo // tab 标题显示
          }
          this.readOne(row)
        } else if (this.$route.params.from === 'contractPerformancePlan') {
          this.playPlan(this.$route.params.row, this.$route.params.form)
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
      {
        prop: 'orgName',
        label: this.$t('bid_mod.businessEntity'), // 业务实体
        width: 150
      },
      {
        prop: 'organizationName',
        label: this.$t('bid_mod.inv'), // 库存组织,
        width: 150
      },
      {
        prop: 'paymentApplyNumber',
        label: this.$t('contractMod.paymentApplyNumber'), // 付款申请单号
        width: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: function (row) {
          this.readOne(row)
        }.bind(this),
        formattor (val) {
          return val || '--'
        }
      },
      {
        prop: 'status',
        label: this.$t('purSettlementMod.paymentPlanStatus'), // 单据状态
        width: 100,
        dataType: 'dict',
        code: 'PAYMENT_APPLY_STATUS'
      },
      {
        prop: 'paymentStatus',
        label: this.$t('contractMod.payStatus'), // 付款状态
        width: 100,
        dataType: 'dict',
        code: 'PAYMENT_PAID_STATUS'
      },
      // 供应商编码
      { prop: 'vendorCode', label: this.$t('common.vendorCode'), width: 120 },
      // 供应商名称
      { prop: 'vendorName', label: this.$t('common.vendorName'), minWidth: 150 },
      {
        prop: 'actualInvoiceAmountY',
        label: this.$t('purSettlementMod.actualInvoiceAmountY2'), // 发票含税总金额
        width: 140
      },
      {
        prop: 'includeTaxAmount',
        label: this.$t('purSettlementMod.includeTaxAmount3'), // 付款含税总金额
        width: 140
      },
      // 币种
      { prop: 'currencyName', label: this.$t('quota.currency'), width: 100 },
      {
        prop: 'taxRate',
        label: _this.$t('purchaseDemand.taxRate'), // 税率
        width: 100
      },
      // 汇率
      // { prop: 'exchangeRate', label: this.$t('bid_mod.priceTax'), width: 100 },
      {
        prop: 'createdUserName', // createdBy
        label: () => _this.$t('common.creator'), // 创建人
        width: 100
      },
      {
        prop: 'creationDate',
        label: () => _this.$t('quota.createdDate'), // 创建日期
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'), // 操作
        width: 120,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: function (row) {
              this.editOne(row)
            }.bind(this),
            // 拟定/已驳回/已撤回
            show: row => ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(row.status),
            formattor (val) {
              return _this.$t('common.edit') // 编辑
            }
          },
          {
            callback: function (row) {
              this.delRowData(row)
            }.bind(this),
            show: row => row.status === 'DRAFT' &&
              row.createdBy === this.globalNickname,
            formattor (val) {
              return _this.$t('common.delete') // 删除
            }
          },
          {
            callback: row => this.approvalOne(row),
            formattor: _ => this.$t('common.approve'), // 审批
            show: row => this.isShowApprove(row)
          },
          // SRM 接口审批
          {
            callback: row => this.supplierConfirm(row),
            formattor: () => this.$t('bidMod.approvalPass'),
            show: row =>
              this.srmFlowMode.includes(row.integrationMode) &&
              ['SUBMITTED'].includes(row.status) &&
              row.createdBy === this.globalNickname
          },
          {
            callback: row => this.payAbandon(row),
            show: row =>
              row.status === 'REFUSED' &&
              row.createdBy === this.globalNickname,
            formattor: () => this.$t('common.cancelled') // 废弃
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
    playPlan (row, froms) {
      this.$emit('tab-add', {
        component: paymentPlanDetail,
        params: {
          flag: 'playPlan',
          row,
          head: froms,
          tabName: 'paymentPlanDetail'
        },
        title: this.$t('contractMod.paymentApplyDetail'),
        name: 'paymentPlanDetail'
      })
    },
    // 废弃
    async payAbandon (row) {
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
        url: '/api-sup-ce/payment/paymentApply/abandonPaymentApply',
        method: 'GET',
        params: {
          id: row.paymentApplyId
        },
        loading: true
      }).then(_ => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    // 判断是否显示审批按钮；区分开启关闭审批流能否指定审批人
    isShowApprove (row) {
      // 审批流tab接入情况下
      const isOpen = this.flowWithTabMode.includes(row.integrationMode)
      const isSubmit = ['SUBMITTED'].includes(row.status)
      const needStatus = ['UNDER_APPROVAL'].includes(row.status)
      const isApprove = !!row.arroverId
      // 若开启审批流,已提交，或者撤回、审批中且是指定审批人
      return isOpen && (isSubmit || (needStatus && isApprove))
    },
    // 开票单新增 - select
    handleChange (select) {
      this.paymentSelect = select
    },
    // 驳回
    abandonHandel (row) {
      this.$http({
        url: '/api-sup-ce/payment/paymentApply/rejectPaymentApply',
        method: 'GET',
        params: {
          id: row.paymentApplyId,
          rejectReason: this.rejectReason
        },
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    // 审批通过
    async supplierConfirm (row) {
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
        url: '/api-sup-ce/payment/paymentApply/approvedPaymentApply',
        method: 'GET',
        params: { id: row.paymentApplyId },
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    // 审批 -- 跳审批流
    approvalOne (row) {
      this.$emit('tab-add', {
        component: paymentPlanDetail,
        params: {
          flag: 'approvalOnly',
          paymentApplyId: row.paymentApplyId,
          tabName: 'paymentPlanDetail' + row.paymentApplyNumber,
          activeWorkflowTab: true
        },
        title: row.paymentApplyNumber,
        name: 'paymentPlanDetail' + row.paymentApplyNumber
      })
    },
    // 付款
    async payAmountList () {
      if (this.paymentSelect.length < 1) {
        return this.$message.warning(this.$t('purSettlementMod.selectAtLeastOnePieceOfData')) // 请至少选择一条数据！
      }

      const isConfirm = await this.$confirm(
        this.$t('purSettlementMod.payAmountListConfirm'),
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

      const ids = this.paymentSelect.map(row => row.paymentApplyId)
      this.$http({
        url: '/api-sup-ce/payment/paymentApply/batchPaid',
        method: 'POST',
        data: ids,
        loading: true
      }).then(res => {
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    getQuerydata (obj) {
      const { dateList, ...rest } = obj || this.queryParam
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
    syncFilterParams (values) {
      this.queryParam = values
    },
    readOne (row) {
      this.$emit('tab-add', {
        component: paymentPlanDetail,
        params: {
          flag: 'readOnly',
          showType: 'approveNumber',
          row: row,
          paymentApplyId: row.paymentApplyId,
          tabName: 'paymentPlanDetail' + row.paymentApplyNumber
        },
        title: row.paymentApplyNumber,
        name: 'paymentPlanDetail' + row.paymentApplyNumber
      })
    },
    editOne (row) {
      this.$emit('tab-add', {
        component: paymentPlanDetail,
        params: {
          flag: 'edit',
          row: row,
          paymentApplyId: row.paymentApplyId,
          tabName: 'paymentPlanDetail' + row.paymentApplyNumber
        },
        title: row.paymentApplyNumber,
        name: 'paymentPlanDetail' + row.paymentApplyNumber
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

      const paymentApplyId = row.paymentApplyId
      this.$http({
        url: '/api-sup-ce/payment/paymentApply/delete',
        method: 'GET',
        params: { id: paymentApplyId },
        loading: true
      }).then(res => {
        this.$message({
          message: res.message,
          type: 'success'
        })
        this.getQuerydata()
      })
    },
    // 批量废弃
    cancelledList () {
      if (this.paymentSelect.length < 1) {
        return this.$message.warning(this.$t('purSettlementMod.selectAtLeastOnePieceOfData')) // 请至少选择一条数据！
      }
      const ids = this.paymentSelect.map(item => item.paymentApplyId)
      this.$confirm(this.$t('common.confirmAbandon'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: '/api-sup-ce/payment/paymentApply/batchAbandonPaymentApply',
          method: 'POST',
          data: ids,
          loading: true
        }).then(data => {
          this.$message.success(this.$t('common.success'))
          this.getQuerydata()
        })
      })
    },
    addNewBill () {
      this.$emit('tab-add', {
        component: paymentPlanDetail,
        params: {
          flag: 'add',
          tabName: 'paymentPlanDetail'
        },
        title: this.$t('contractMod.newPaymentApply'),
        name: 'paymentPlanDetail'
      })
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
          let tempId = String(row.paymentApplyId)
          if (maps.includes(tempId)) {
            this.$set(row, 'workflowAuditStatus', 'WAIT')
            this.$set(row, 'arroverId', tempId)
          }
        })
      })
    },
    async listQueryTodo () {
      const res = await this.$api.base.flowAPI.queryTodo({ businessType: 'paymentapply' })
      this.queryTodoList = res.data
    },
    async getFlowIntegrationMode () {
      const res = await this.$api.base.flowAPI.getFlowIntegrationMode({ businessType: 'paymentapply' })
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
