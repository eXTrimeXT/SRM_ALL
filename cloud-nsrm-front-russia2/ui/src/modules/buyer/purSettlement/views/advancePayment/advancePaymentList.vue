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
          <AuthorityButton
            code="pm:advancePayment:addOne"
            type="primary"
            @click="addOne"
          >
            {{ $t('purSettlementMod.newAdvanceApply') }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        :table-data="tableData"
        :table-header="tableHeader"
        :check-change="handleCurrentChange"
        :page-size="pageSize"
        :checkbox="true"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-sup-ce/sup/advanceApply/listPage"
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
import advancePaymentDetail from './advancePaymentDetail'
import onlineInvoiceDetail from './onlineInvoiceDetail'
import { parseTime } from '@/utils'

export default {
  name: 'AdvancePaymentList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    advancePaymentDetail,
    onlineInvoiceDetail
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      name: 'contractTemplateTable',
      tableName: 'paymentPlanList',
      reviewFormNumber: '',
      gridData: [],
      pageSize: 15,
      gridId: 'list',
      currentRow: null,
      showFilterBar: 1,
      tableHeader: [],
      tableData: [],
      queryTodoList: [],
      isModify: false,
      preArr: [
        {
          prop: 'advanceApplyNumber',
          label: () => this.$t('purSettlementMod.advancePaymentNum') // 预付款申请单号
        },

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
          prop: 'dateList',
          label: () => this.$t('quota.createdDate'), // 创建日期
          type: 'daterange'
        },
        {
          prop: 'vendorCode',
          label: () => this.$t('orderMod.buyerOrderSynergy.vendorCode'), // 供应商编码
          type: 'quicksearch',
          showKey: 'companyCode',
          name: 'scc_sup_company_info_all'
        },
        {
          prop: 'advanceApplyStatus',
          label: this.$t('purchaseDemand.applyStatus'), // 单据状态
          width: 120,
          type: 'dict',
          code: 'ADVANCE_APPLY_STATUS'
        },
        {
          prop: 'createdBy',
          label: this.$t('purchaseDemand.applicant'), // 申请人
          width: 100,
          type: 'quicksearch',
          propKey: 'username',
          showKey: 'nickname',
          name: 'scc_rbac_user_display'
        }
      ],
      queryParam: {},
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
          this.$route.params.funName === 'advancePayment'
        ) {
          let advanceApplyId = Number(this.$route.params.formId)
          let formNo = this.$route.params.formNo // 流程标题
          let row = {
            ...this.$route.params,
            advanceApplyId,
            advanceApplyNumber: formNo // tab 标题显示
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
        prop: 'advanceApplyNumber',
        label: _this.$t('purSettlementMod.advancePaymentNum'), // 预付款申请单号
        width: 150,
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
        prop: 'advanceApplyStatus',
        label: _this.$t('purSettlementMod.paymentPlanStatus'), // 单据状态
        width: 100,
        dataType: 'dict',
        code: 'ADVANCE_APPLY_STATUS'
      },
      {
        prop: 'vendorCode',
        label: _this.$t('common.vendorCode'), // 供应商编码
        width: 120
      },
      {
        prop: 'vendorName',
        label: _this.$t('common.vendorName'), // 供应商
        minWidth: 150
      },
      {
        prop: 'includeTaxAmount',
        label: _this.$t('purSettlementMod.includeTaxAmount2'), // 申请付款金额
        minWidth: 150
      },
      {
        prop: 'unWrittenOffAmount',
        label: _this.$t('purSettlementMod.unWrittenOffAmount'), // 未核销金额
        minWidth: 150
      },
      {
        prop: 'currencyName',
        label: _this.$t('quota.currency'), // 币种
        width: 100
      },
      {
        prop: 'taxRate',
        label: _this.$t('purchaseDemand.taxRate'), // 税率
        width: 100
      },
      {
        prop: 'createdUserName',
        label: _this.$t('purchaseDemand.applicant'), // 申请人
        width: 100
      },
      {
        prop: 'departmentName',
        label: _this.$t('purchaseDemand.ceeaDepartment'), // 申请部门
        width: 100
      },
      {
        prop: 'creationDate',
        label: _this.$t('quota.createdDate'), // 创建日期
        width: 100,
        formattor (val) {
          return val ? parseTime(val, '{y}-{m}-{d}') : ''
        }
      },
      {
        prop: 'operation',
        label: _this.$t('common.operation'),
        width: 150,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          {
            callback: row => this.editOne(row),
            show: row =>
              ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(row.advanceApplyStatus),
            formattor: _ => this.$t('common.edit')
          },
          {
            callback: row => this.deleteOne(row),
            show: row =>
              row.advanceApplyStatus === 'DRAFT' &&
              row.createdBy === this.globalNickname,
            formattor: _ => this.$t('common.delete')
          },
          {
            callback: row => this.approvalOne(row),
            formattor: _ => this.$t('common.approve'), // 审批
            show: row => this.isShowApprove(row)
          },
          {
            callback: row => this.supplierConfirm(row),
            formattor: _ => this.$t('purchaseDemand.approved'), // 审批通过
            show: row =>
              // SRM接口审批
              this.srmFlowMode.includes(row.integrationMode) &&
              row.advanceApplyStatus === 'SUBMITTED' &&
              row.createdBy === this.globalNickname
          },
          {
            callback: function (row) {
              this.destoryHandle(row)
            }.bind(this),
            show: row =>
              ['REJECTED', 'WITHDRAW','SUBMITTED'].includes(row.advanceApplyStatus) &&
              row.createdBy === this.globalNickname,
            formattor: () => this.$t('common.cancelled') // 作废
          }
        ]
      }
    ]
    this.defaultTableHeader = this.tableHeader
    this.$nextTick(() => {
      this.getQuerydata()
    })

    this.getFlowIntegrationMode()
  },
  methods: {
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
        url: '/api-sup-ce/sup/advanceApply/abandonAdvanceApply',
        method: 'GET',
        params: { id: row.advanceApplyId },
        loading: true
      }).then(data => {
        this.$message({
          type: 'success',
          message: this.$t('common.success')
        })
        this.getQuerydata()
      })
    },
    // 判断是否显示审批按钮；区分开启关闭审批流能否指定审批人
    isShowApprove (row) {
      // tab审批流模式开启下
      const isOpen = this.flowWithTabMode.includes(row.integrationMode)
      const isSubmit = ['SUBMITTED'].includes(row.advanceApplyStatus)
      const needStatus = ['UNDER_APPROVAL'].includes(row.advanceApplyStatus)
      const isApprove = !!row.arroverId
      // 若开启审批流,已提交，或者撤回、审批中且是指定审批人
      return isOpen && (isSubmit || (needStatus && isApprove))
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
        this.$refs[this.gridId].doLayout()
      })
    },
    syncFilterParams (values) {
      this.queryParam = values
    },
    handleCurrentChange (val) {
      this.currentRow = val
    },
    readVirtualOne (row) {
      if (!row.onlineInvoiceNum) return
      this.$emit('tab-add', {
        component: onlineInvoiceDetail,
        params: {
          flag: 'add',
          readVirtualOne: true,
          row: row,
          tabName: 'onlineInvoiceDetail' + row.onlineInvoiceNum
        },
        title: row.onlineInvoiceNum,
        name: 'onlineInvoiceDetail' + row.onlineInvoiceNum
      })
    },
    addOne () {
      this.$emit('tab-add', {
        component: advancePaymentDetail,
        params: {
          flag: 'add'
        },
        title: this.$t('purSettlementMod.prepaidApplyDetail'),
        name: 'advancePaymentDetail'
      })
    },
    playPlan (row, froms) {
      this.$emit('tab-add', {
        component: advancePaymentDetail,
        params: {
          flag: 'playPlan',
          row,
          tabName: 'advancePaymentDetail' + row.advanceApplyNumber,
          head: froms
        },
        title: this.$t('purSettlementMod.prepaidApplyDetail'),
        name: 'advancePaymentDetail' + row.advanceApplyNumber
      })
    },
    readOne (row) {
      this.$emit('tab-add', {
        component: advancePaymentDetail,
        params: {
          flag: 'readOnly',
          row: row,
          showType: 'approveNumber',
          tabName: 'advancePaymentDetail' + row.advanceApplyNumber
        },
        title: row.advanceApplyNumber,
        name: 'advancePaymentDetail' + row.advanceApplyNumber
      })
    },
    editOne (row) {
      this.$emit('tab-add', {
        component: advancePaymentDetail,
        params: {
          flag: 'edit',
          row: row,
          tabName: 'advancePaymentDetail' + row.advanceApplyNumber
        },
        title: row.advanceApplyNumber,
        name: 'advancePaymentDetail' + row.advanceApplyNumber
      })
    },
    deleteOne (row) {
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-sup-ce/sup/advanceApply/delete',
            method: 'GET',
            params: { id: row.advanceApplyId },
            loading: true
          })
            .then(data => {
              this.$message({
                type: 'success',
                message: this.$t('common.successDelete')
              })
              this.getQuerydata()
            })
        })
    },
    // 审批
    approvalOne (row) {
      this.$emit('tab-add', {
        component: advancePaymentDetail,
        params: {
          flag: 'readOnly',
          row: row,
          showType: 'approvalOnly',
          tabName: 'advancePaymentDetail' + row.advanceApplyNumber,
          activeWorkflowTab: true
        },
        title: row.advanceApplyNumber,
        name: 'advancePaymentDetail' + row.advanceApplyNumber
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
        url: '/api-sup-ce/sup/advanceApply/approvedAdvanceApply',
        method: 'GET',
        params: { id: row.advanceApplyId },
        loading: true
      }).then(data => {
        this.$message({
          type: 'success',
          message: this.$t('common.success')
        })
        this.getQuerydata()
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
          let tempId = String(row.advanceApplyId)
          if (maps.includes(tempId)) {
            this.$set(row, 'workflowAuditStatus', 'WAIT')
            this.$set(row, 'arroverId', tempId)
          }
        })
      })
    },
    async listQueryTodo () {
      const res = await this.$api.base.flowAPI.queryTodo({ businessType: 'ADVANCEPAYMENT' })
      this.queryTodoList = res.data
    },
    async getFlowIntegrationMode () {
      const res = await this.$api.base.flowAPI.getFlowIntegrationMode({ businessType: 'ADVANCEPAYMENT' })
      if (res.data) {
        this.integrationMode = res.data
      }
    }
  }
}
</script>
<style scoped lang="scss"></style>
