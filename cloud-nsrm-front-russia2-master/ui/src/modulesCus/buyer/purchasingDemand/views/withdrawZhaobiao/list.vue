<template>
  <el-container class="flex-container the_contractTemplateList_wrapper" direction="vertical">
    <el-main>
      <FormWrapper
        ref="formRef"
        :form-array="preArr"
        @getFormData="getQuerydata"
      />

      <MainHeader :l-span="22" :r-span="2">
        <template slot="left">
          <!-- 导出 -->
          <ExportExcel
            type="default"
            pageUrl="/api-sup-ce/api-ql/PrSouRequirementCancelForBuyer/query"
            :tableHeader="tableHeader"
            :dictCodes="dictCodes"
            :generateMeiQLExportRequest="generateMeiQLExportRequest"
            exportMode="front"
            exportType="meiqlApi"
          />
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        bigData
        :table-header="tableHeader"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :adept-mei-ql="true"
        :source="purchaseApplicationApi.getCancelInfoQuery"
        @getFooter="getFooter"
        @getFooterSize="getFooterSize"
        @afterQuery="afterQuery"
      />
      <!-- 选择需求类型 -->
      <srm-dialog
      :title="$t('cusEntry.supplement20250121.selectType')"
      :visible.sync="dialogFormVisible"
      size="middle"
    >
      <el-form
          ref="form"
          :model="form"
        >
          <srm-row :gutter="32">
            <srm-col :span="32">
              <el-form-item
                prop="demandType"
                :label="$t('purchaseDemand.selectRequireType')"
              >
                <DictSelect
                  v-model="form.demandType"
                  code="DEMAND_TYPE"
                  @change="(value, dictItem) => demandTypeChange(value, dictItem)"
                />
              </el-form-item>
            </srm-col>
            <srm-col :span="32">
              <!-- 是否招标 -->
              <el-form-item
                prop="extBidFlag"
                :label="$t('cusEntry.vendorMod.ifBid')"
              >
                <DictSelect
                  v-model="form.extBidFlag"
                  code="YES_OR_NO"
                />
              </el-form-item>
            </srm-col>
          </srm-row>
      </el-form>
      <template
        #footer
        class="dialog-footer"
      >
        <el-button type="primary" @click="confirmSave">
          {{ $t('common.confirm') }}
        </el-button>
        <el-button
          @click="cancel"
        >
          {{ $t('common.cancel') }}
        </el-button>
      </template>
      </srm-dialog>

    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import TableView from 'lib@/components/Table/TableView'
import MainHeader from 'lib@/components/Table/MainHeader'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import purchaseApplicationDetail from './edit'
import { parseTime } from '@/utils'
import ExportExcel from 'lib@/components/export-excel'
import { purchaseApplicationApi } from 'modc@/buyer/purchasingDemand/api'
import OrganizationSelector from 'lib@/components/organization-selector'
import { transformMQL } from '@/library/utils/util'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'

export default {
  name: 'WithdrawZhaobiaoList',
  components: {
    TableView,
    MainHeader,
    FormWrapper,
    OrganizationSelector,
    ExportExcel,
    MImport
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  provide () {
    return { context: this }
  },
  data () {
    return {
      integrationMode: '',
      dialogFormVisible: false,
      form: {
        extBidFlag: null,
        demandType: null
      },
      dictCodes: {
        demandType: 'DEMAND_TYPE',
        ceeaPurchaseType: 'PURCHASE_TYPE',
        cancelStatus: 'APPROVAL_STATUS'
      },
      extraData: {
        fileModular: 'sup',
        fileFunction: 'purchaseCatalog',
        fileType: 'excel'
      },
      purchaseApplicationApi: purchaseApplicationApi,
      pageSize: 15,
      gridId: 'list',
      tableHeader: [],
      getFooterNum: null,
      getFooterSizeNum: null,
      globalNickname: null,
      queryTodoList: [],
      preArr: [
        // 申请日期
        {
          prop: 'applyDate',
          label: () => this.$t('purchaseDemand.applyDate'),
          type: 'daterange'
        },
        // 需求取消单据号
        {
          prop: 'requirementCancelNo',
          label: this.$t('cusEntry.supplement20250121.requirementCancelNo')
        },
        {
          prop: 'cancelStatus',
          label: () => this.$t('purchaseDemand.applyStatus'),
          type: 'dict',
          code: 'APPROVAL_STATUS'
        },
        // 申请人
        {
          prop: 'applyByNickname',
          label: () => this.$t('purchaseDemand.applicant')
        },
        // 申请部门
        {
          prop: 'departmentName',
          label: () => this.$t('purchaseDemand.ceeaDepartment')
        },
        {
          prop: 'projectName',
          label: this.$t('bidMod.bidingName')
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
      // 需求取消单据号
      {
        prop: 'requirementCancelNo',
        label: this.$t('cusEntry.supplement20250121.requirementCancelNo'),
        minWidth: 150,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.readOne(row),
        formattor: val => val || '--'
      },
      // 单据状态
      {
        prop: 'cancelStatus',
        label: () => this.$t('purchaseDemand.applyStatus'),
        minWidth: 100,
        formattor: val => this.$getDictLabel('APPROVAL_STATUS', val)
      },
      // 申请人
      {
        prop: 'createdFullName',
        label: () => this.$t('purchaseDemand.applicant'),
        minWidth: 100
      },
      // 申请部门
      {
        prop: 'departmentName',
        label: () => this.$t('purchaseDemand.ceeaDepartment'),
        minWidth: 100
      },
      // 申请日期
      {
        prop: 'applyDate',
        label: () => this.$t('purchaseDemand.applyDate'),
        minWidth: 100,
        formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      // 审批结束时间
      {
        prop: 'extApproveTime',
        label: this.$t('cusEntry.supplement20250121.extApproveTime'),
        minWidth: 120,
        dataType: 'dateTime'
      },
      {
        prop: 'requirementHeadNum',
        label: this.$t('purchaseDemand.requirementHeadNum'),
        minWidth: 150
      },
      {
        prop: 'projectName',
        label: this.$t('purchaseDemand.projectName'),
        minWidth: 120
      },
      // 所属板块
      {
        prop: 'orgBuName',
        label: this.$t('cusEntry.supplement20250121.orgBuName'),
        minWidth: 120
      },
      // 申请公司
      {
        prop: 'orgName',
        label: this.$t('cusEntry.supplement20250121.orgName'),
        minWidth: 120
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
            show: row =>
              ['DRAFT', 'WITHDRAW', 'REJECTED'].includes(row.cancelStatus) &&
              (row.createdBy === this.globalNickname || row.sourceSystem === 'MRP')
          },
          // 审批
          {
            callback: row => this.approvalOne(row),
            formattor: () => this.$t('common.approve'),
            show: row => ['WITHDRAW', 'REJECTED', 'APPROVING', 'SUBMITTED'].includes(row.cancelStatus)
          },
          {
            callback: row => this.readOne(row),
            formattor: () => this.$t('common.view'),
            show: row => row.cancelStatus === 'APPROVED'
          },
          // 审批通过
          // {
          //   callback: row => this.approvalOneItem(row),
          //   formattor: () => this.$t('purchaseDemand.approved'),
          //   show: row =>
          //     ['SUBMITTED', 'APPROVING'].includes(row.cancelStatus) &&
          //     ['None', 'Push'].includes(row.integrationMode) &&
          //     !row.workflowAuditStatus
          // },
          // 删除
          {
            callback: row => this.deleteOne(row),
            formattor: () => this.$t('common.delete'),
            show: row =>
              ['DRAFT'].includes(row.cancelStatus) && (row.createdBy === this.globalNickname || row.sourceSystem === 'MRP')
          },
          // 废弃
          {
            callback: row => this.readOne(row),
            formattor: () => this.$t('common.abandon'),
            show: row =>
              ['WITHDRAW', 'REJECTED', 'REFUSED', 'UNDER_APPROVAL'].includes(row.cancelStatus) &&
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
    // 新增确认
    confirmSave(){
      this.dialogFormVisible = false
        this.$emit('tab-add', {
          component: purchaseApplicationDetail,
          params: {
            flag: 'add',
            tabName: 'purchaseApplicationDetail',
            demandType: this.form.demandType
          },
          title: this.$t('purchaseDemand.addPurApplication'),
          name: 'purchaseApplicationDetail'
        })
    },
    demandTypeChange(value){
      if (value == 'Material_category') {
        this.form.extBidFlag = 'N'
      } else {
        this.form.extBidFlag = 'Y'
      }
    },
    cancel(){
      this.dialogFormVisible = false
    },
    generateMeiQLExportRequest () {
      let queryParam = JSON.parse(JSON.stringify(this.queryParam))
      console.log(queryParam, 'queryParam')
      queryParam.type = 'PrSouRequirementCancelForBuyer'
      queryParam.action = 'query'
      const all = {
        body: queryParam
      }
      return all
    },
    // 预算释放
    async budgetRelease (row) {
      const confirmSelectValue = await this.$confirm(
        this.$t('purchaseDemand.sureReleaseBugget'),
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
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      })
    },
    async abandonOne (row) {
      const confirmSelectValue = await this.$confirm(
        this.$t('common.confirmAbandon'),
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
          this.$message.success(this.$t('common.success'))
          this.getQuerydata()
        })
        .catch(err => {
          console.log(err)
        })
    },
    getQuerydata (obj) {
      const { applyDate = [], requirementCancelNo, cancelStatus, applyByNickname, departmentName, projectName } = obj || this.queryParam
      let params = {}
      if (applyDate.length > 0) {
        params.applyDate = { between: applyDate }
      }
      if (requirementCancelNo) {
        params.requirementCancelNo = { contains: requirementCancelNo }
      }
      if (cancelStatus) {
        params.cancelStatus = { eq: cancelStatus }
      }
      if (applyByNickname) {
        params.applyByNickname = { contains: applyByNickname }
      }
      if (departmentName) {
        params.departmentName = { contains: departmentName }
      }

      // 关联明细表查询
      let filter = {}
      if (projectName) {
        filter['$condition'] = {
          '$strictQuery': true,
          filter: {
            projectName: { contains: projectName }
          }
        }
      }

      this.queryParam = {
        type: 'PrSouRequirementCancelForBuyer',
        action: 'query',
        payload: {
          filter: params,
          page: {
            pageNum: 1,
            pageSize: 15,
            sort: 'lastUpdateDate desc'
          }
        },
        query: {
          '*': {},
          'cancelLineList': {
            '*': {},
            ...filter
          }
        },
        lang: 'zh-cn',
        tree: true
      }
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
        this.$refs[this.gridId].doLayout()
      })
    },
    getFooter (data) {
      this.getFooterNum = data.value
    },
    getFooterSize (data) {
      this.getFooterSizeNum = data.value
    },
    addOne () {
      this.dialogFormVisible = true
    },
    async editOne (row) {
      await this.setTabRemove(row)
        this.$emit('tab-add', {
          component: purchaseApplicationDetail,
          params: {
            flag: 'edit',
            row: row,
            tabName: 'purchaseApplicationDetail' + row.requirementCancelNo
          },
          title: row.requirementCancelNo,
          name: 'purchaseApplicationDetail' + row.requirementCancelNo
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
      console.log(row)
        this.$emit('tab-add', {
          component: purchaseApplicationDetail,
          params: {
            flag: 'approveNumber',
            row: row,
            showType: 'readOnly',
            tabName: 'purchaseApplicationDetail' + row.requirementCancelNo,
            activeWorkflowTab: this.integrationMode !== 'None' && this.integrationMode !== 'Push'
          },
          title: row.requirementCancelNo,
          name: 'purchaseApplicationDetail' + row.requirementCancelNo
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
          const saveData = transformMQL.save('PrSouRequirementCancelForBuyer', [{ requirementCancelId: row.requirementCancelId }], 'removeReqCancel')
          purchaseApplicationApi.removeReqCancel(saveData).then((datas) => {
            this.$message({
              message: this.$t('common.successSave'),
              type: 'success'
            })
            this.getQuerydata()
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
