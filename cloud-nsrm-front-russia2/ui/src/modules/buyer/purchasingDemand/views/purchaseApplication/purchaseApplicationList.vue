<template>
  <el-container class="flex-container the_contractTemplateList_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        ref="formRef"
        :form-array="preArr"
        @getFormData="getQuerydata"
        @synchronous-value="syncFilterParams"
      />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <AuthorityButton
            code="pr:purchaseApplicationList:add"
            type="primary"
            @click="addOne"
          >
            {{ $t("common.add") }}
          </AuthorityButton>
        </template>
      </MainHeader>

      <TableView
        :ref="gridId"
        bigData
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        url="/api-sup-ce/pr/requirementHead/listPage"
        @getFooter="getFooter"
        @getFooterSize="getFooterSize"
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
import purchaseApplicationDetail from './purchaseApplicationDetail'
import { parseTime } from '@/utils'
import { downloadFileLinkByPost } from 'lib@/utils/file'
import OrganizationSelector from 'lib@/components/organization-selector'

export default {
  name: 'PurchaseApplicationList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    OrganizationSelector
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      integrationMode: '',
      pageSize: 15,
      gridId: 'list',
      currentRow: null,
      tableHeader: [],
      tableData: [],
      getFooterNum: null,
      getFooterSizeNum: null,
      globalNickname: null,
      queryTodoList: [],
      preArr: [
        // 申请编号
        {
          prop: 'requirementHeadNum',
          label: () => this.$t('purchaseDemand.requirementHeadNum')
        },
        {
          prop: 'ceeaPurchaseType',
          label: () => this.$t('purchaseDemand.purchaseType'),
          type: 'dict',
          code: 'PURCHASE_TYPE' // 采购类型
        },
        {
          prop: 'auditStatus',
          label: () => this.$t('purchaseDemand.applyStatus'), // 单据状态
          type: 'dict',
          code: 'APPROVAL_STATUS'
        },
        {
          prop: 'orgId',
          label: () => this.$t('dataConfMod.orgId'), // 业务实体
          type: 'OUorganizationSelector'
        },
        {
          prop: 'organizationId',
          label: () => this.$t('dataConfMod.organizationId'), // 库存组织
          type: 'INVorganizationSelector',
          parentId: 'orgId'
        },
        // 采购项目
        {
          prop: 'purchaseProject',
          label: () => this.$t('purchaseDemand.purchaseItem')
        },
        // 申请日期
        {
          prop: 'dateList',
          label: () => this.$t('purchaseDemand.applyDate'),
          type: 'daterange'
        },
        // 申请部门
        {
          prop: 'ceeaDepartmentId',
          label: () => this.$t('purchaseDemand.ceeaDepartment'),
          type: 'quicksearch',
          showKey: 'descr',
          propKey: 'deptid',
          name: 'ceea_base_dept'
        },
        // 申请人
        {
          prop: 'createdFullName',
          label: () => this.$t('purchaseDemand.applicant'),
          type: 'quicksearch',
          showKey: 'nickname',
          name: 'scc_rbac_user_display'
        },
        // 物料大类
        {
          prop: 'categoryId',
          label: () => this.$t('purchaseDemand.materialCate'),
          type: 'quicksearch',
          showKey: 'categoryName',
          propKey: 'categoryId',
          name: 'scc_base_purchase_category3'
        },
        // 需求类型
        {
          prop: 'demandType',
          label: () => this.$t('purchaseDemand.demandType'),
          type: 'dict',
          code: 'DEMAND_TYPE'
        },
        {
          prop: 'budgetManagementId',
          label: this.$t('purchaseDemand.budgetNumber'), // 预算编号
          type: 'quicksearch',
          showKey: 'budgetManagementNumber',
          propKey: 'budgetManagementId',
          name: 'scc_pb_budget_management_effective'
        }
      ],
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
          this.$route.params.funName === 'purchaseApplication'
        ) {
          const requirementHeadId = Number(this.$route.params.formId)
          // 流程标题
          const formNo = this.$route.params.formNo
          const row = {
            ...this.$route.params,
            requirementHeadId,
            // tab 标题显示
            requirementHeadNum: formNo
          }
          this.readOne(row)
        }
      }
    }
  },
  created () {
    this.globalNickname = this.$store.getters.userInfo
      ? this.$store.getters.userInfo.username
      : null
    this.tableHeader = [
      // 申请编号
      {
        prop: 'requirementHeadNum',
        label: () => this.$t('purchaseDemand.requirementHeadNum'),
        width: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.readOne(row),
        formattor: val => val || '--'
      },
      {
        prop: 'budgetManagementNum',
        label: this.$t('purchaseDemand.budgetNumber'), // 预算编号
        width: 150
      },
      // 需求类型
      {
        prop: 'demandType',
        label: () => this.$t('purchaseDemand.demandType'),
        width: 120,
        formattor: val => this.$getDictLabel('DEMAND_TYPE', val)
      },
      // 采购类型
      {
        prop: 'ceeaPurchaseType',
        label: () => this.$t('purchaseDemand.purchaseType'),
        width: 120,
        formattor: val => this.$getDictLabel('PURCHASE_TYPE', val)
      },
      // 单据状态
      {
        prop: 'auditStatus',
        label: () => this.$t('purchaseDemand.applyStatus'),
        width: 100,
        formattor: val => this.$getDictLabel('APPROVAL_STATUS', val)
      },
      // 申请日期
      {
        prop: 'applyDate',
        label: () => this.$t('purchaseDemand.applyDate'),
        width: 100,
        formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      // 业务实体
      {
        prop: 'orgName',
        label: () => this.$t('purchaseDemand.businessEntity'),
        width: 120
      },
      // 库存组织
      {
        prop: 'organizationName',
        label: () => this.$t('purchaseDemand.invOrg'),
        width: 100
      },
      // 采购项目
      {
        prop: 'purchaseProject',
        label: () => this.$t('purchaseDemand.purchaseItem'),
        width: 100
      },
      // 申请部门
      {
        prop: 'ceeaDepartmentName',
        label: () => this.$t('purchaseDemand.ceeaDepartment'),
        width: 100
      },
      // 申请人
      {
        prop: 'createdFullName',
        label: () => this.$t('purchaseDemand.applicant'),
        width: 100
      },
      // 物料大类
      {
        prop: 'categoryName',
        label: () => this.$t('purchaseDemand.materialCate'),
        width: 120
      },
      // 备注
      {
        prop: 'comments',
        label: () => this.$t('common.remark')
        // width: 130
      },
      {
        prop: 'operation',
        label: this.$t('common.operation'),
        width: 150,
        showType: 'buttons',
        fixed: 'right',
        btnStyle: 'text',
        buttons: [
          // 编辑
          {
            callback: row => this.editOne(row),
            formattor: () => this.$t('common.edit'),
            code: 'pr:purchaseApplicationList:edit',
            show: row =>
              ['DRAFT', 'WITHDRAW', 'REJECTED'].includes(row.auditStatus) &&
              (row.createdBy === this.globalNickname || row.sourceSystem === 'MRP')
          },
          // 审批
          {
            callback: row => this.approvalOne(row),
            formattor: () => this.$t('common.approve'),
            show: row =>
            // None为本地，Push为第三方审批回调
            !['None', 'Push'].includes(row.integrationMode) &&
            (row.auditStatus === 'SUBMITTED' ||
              (['WITHDRAW', 'REJECTED', 'APPROVING'].includes(row.auditStatus) &&
                !!row.arroverId))
          },
          // 审批通过
          {
            callback: row => this.approvalOneItem(row),
            formattor: () => this.$t('purchaseDemand.approved'),
            show: row =>
              ['SUBMITTED', 'APPROVING'].includes(row.auditStatus) &&
              ['None', 'Push'].includes(row.integrationMode) &&
              !row.workflowAuditStatus
          },
          // 删除
          {
            callback: row => this.deleteOne(row),
            formattor: () => this.$t('common.delete'),
            show: row =>
              ['DRAFT'].includes(row.auditStatus) && (row.createdBy === this.globalNickname || row.sourceSystem === 'MRP')
          },
          // 废弃
          {
            callback: row => this.abandonOne(row),
            formattor: () => this.$t('common.abandon'),
            code: 'pr:purchaseApplicationList:abandon',
            show: row =>
              ['WITHDRAW', 'REJECTED', 'REFUSED', 'UNDER_APPROVAL'].includes(row.auditStatus) &&
              (row.createdBy === this.globalNickname || row.sourceSystem === 'MRP')
          },
          // 预算释放 条件（已审批或者已废弃、非生产性需求、剩余可用金额大于0）
          {
            callback: row => this.budgetRelease(row),
            formattor: () => this.$t('purchaseDemand.budgetRelease'),
            show: row =>
              ['APPROVED', 'ABANDONED'].includes(row.auditStatus) &&
              row.demandType === 'NONPRODUCTIVE_DEMAND' &&
              row.unusedBudget > 0 &&
              (row.createdBy === this.globalNickname || row.sourceSystem === 'MRP')
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })

    this.getFlowIntegrationMode()
  },
  activated () {
    if (
      this.$route.params.from === 'demandPoolManagement' &&
      this.$route.params.funName === 'purchaseApplication'
    ) {
      const row = this.$route.params.fdSubject
      this.$emit('tab-add', {
        component: purchaseApplicationDetail,
        params: {
          flag: 'readOnly',
          row: row,
          showType: 'readOnly',
          tabName: 'purchaseApplicationDetail' + row.requirementHeadNum
        },
        title: row.requirementHeadNum,
        name: 'purchaseApplicationDetail' + row.requirementHeadNum
      })
    }
  },
  methods: {
    // 预算释放
    async budgetRelease (row) {
      const confirmSelectValue = await this.$confirm(
        this.$t('purchaseDemand.sureReleaseBugget'), // 确认释放预算吗
        this.$t('common.tips'),
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        },
      )
      if (confirmSelectValue !== 'confirm') return
      this.$http({
        url: '/api-sup-ce/pr/requirementManage/releaseBudget',
        method: 'GET',
        params: { requirementHeadId: row.requirementHeadId },
        loading: true
      }).then(_ => {
        // 操作成功
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    async abandonOne (row) {
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
        url: '/api-sup-ce/pr/requirementHead/abandon',
        method: 'GET',
        params: { requirementHeadId: row.requirementHeadId },
        loading: true
      })
        .then(data => {
          this.$message.success(this.$t('common.success')) // 操作成功
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    getQuerydata (obj) {
      const { dateList, ...rest } = obj || this.queryParam
      const params = {}
      if (dateList) {
        params.startApplyDate = dateList[0]
        params.endApplyDate = dateList[1]
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
    getFooter (data) {
      this.getFooterNum = data.value
    },
    getFooterSize (data) {
      this.getFooterSizeNum = data.value
    },
    addOne () {
      this.$emit('tab-add', {
        component: purchaseApplicationDetail,
        params: {
          flag: 'add',
          tabName: 'purchaseApplicationDetail'
        },
        // 创建采购申请单
        title: this.$t('purchaseDemand.addPurApplication'),
        name: 'purchaseApplicationDetail'
      })
    },
    async editOne (row) {
      await this.setTabRemove(row)
      this.$emit('tab-add', {
        component: purchaseApplicationDetail,
        params: {
          flag: 'edit',
          row: row,
          tabName: 'purchaseApplicationDetail' + row.requirementHeadNum
        },
        title: row.requirementHeadNum,
        name: 'purchaseApplicationDetail' + row.requirementHeadNum
      })
    },
    setTabRemove (row) {
      if (!['None', 'Push'].includes(row.integrationMode)) {
        this.$emit('tab-remove', 'purchaseApplicationDetail' + row.requirementHeadNum)
      }
    },
    async approvalOne (row) {
      await this.setTabRemove(row)
      this.$emit('tab-add', {
        component: purchaseApplicationDetail,
        params: {
          flag: 'approvalOnly',
          row: row,
          showType: 'readOnly',
          tabName: 'purchaseApplicationDetail' + row.requirementHeadNum,
          activeWorkflowTab: true
        },
        title: row.requirementHeadNum,
        name: 'purchaseApplicationDetail' + row.requirementHeadNum
      })
    },
    readOne (row) {
      this.$emit('tab-add', {
        component: purchaseApplicationDetail,
        params: {
          flag: 'approveNumber',
          row: row,
          showType: 'readOnly',
          tabName: 'purchaseApplicationDetail' + row.requirementHeadNum,
          activeWorkflowTab: this.integrationMode !== 'None' && this.integrationMode !== 'Push'
        },
        title: row.requirementHeadNum,
        name: 'purchaseApplicationDetail' + row.requirementHeadNum
      })
    },
    approvalOneItem (row) {
      this.$http({
        url: '/api-sup-ce/pr/requirementHead/approval',
        method: 'GET',
        params: { requirementHeadId: row.requirementHeadId },
        loading: true
      })
        .then(data => {
          // 操作成功
          this.$message.success(this.$t('common.success'))
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    deleteOne (row) {
      // 当前操将永久删除此数据，确认删除此数据
      this.$confirm(this.$t('common.confirmDelete'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      })
        .then(() => {
          this.$http({
            url: '/api-sup-ce/pr/requirementHead/deleteByHeadId',
            method: 'GET',
            params: { requirementHeadId: row.requirementHeadId },
            loading: true
          })
            .then(data => {
              // 删除成功
              this.$message.success(this.$t('common.successDelete'))
              this.getQuerydata()
            })
            .catch(err => {
              console.log(err)
            })
        })
        .catch(() => { })
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
          let tempId = String(row.requirementHeadId)
          if (maps.includes(tempId)) {
            this.$set(row, 'workflowAuditStatus', 'WAIT')
            this.$set(row, 'arroverId', tempId)
          }
        })
      })
    },
    async listQueryTodo () {
      const res = await this.$api.base.flowAPI.queryTodo({ businessType: 'REQUIREMENT' })
      this.queryTodoList = res.data
    },
    async getFlowIntegrationMode () {
      const res = await this.$api.base.flowAPI.getFlowIntegrationMode({ businessType: 'REQUIREMENT' })
      if (res.data) {
        this.integrationMode = res.data
      }
    }
  }
}
</script>

<style lang="scss" scoped>
:deep(.el-table--border) {
  .el-table__cell {
    &:last-child {
      border-left: 1px solid #dfe6ec;
    }
  }
}
</style>
