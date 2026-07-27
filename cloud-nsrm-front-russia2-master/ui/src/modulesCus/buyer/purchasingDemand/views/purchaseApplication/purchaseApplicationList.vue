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
            code="poolZhaobiao:add"
            type="primary"
            @click="addOne"
          >
            {{ $t("common.add") }}
          </AuthorityButton>
          <!-- 导出 -->
          <ExportExcel
            type="default"
            pageUrl="/api-sup-ce/api-ql/PurchaseRequirementHead/query"
            :tableHeader="tableHeader"
            :dictCodes="dictCodes"
            :generateMeiQLExportRequest="generateMeiQLExportRequest"
            exportMode="front"
            exportType="meiqlApi"
          />
          <!-- 导入 -->
          <!-- <MImport
            up-load-url="/api-sup-ce/npm/pr/requirement/sou/importExcel"
            :extra-data="extraData"
            type="default"
            :title="$t('cusEntry.supplement20250121.importBidPlan')"
            @downloadTemplate="downloadTemplate"
            @handleSuccess="handleSuccess"
          /> -->
          <!-- 确认领料单 -->
          <AuthorityButton
            code="poolZhaobiao:confirms"
            @click="confirms"
          >
            {{ $t('cusEntry.supplement20250121.confirmMaterial') }}
          </AuthorityButton>
          <!-- 招标计划取消 -->
          <AuthorityButton
            code="poolZhaobiao:quxiao"
            @click="requirementCancellation"
          >
            {{ $t('cusEntry.supplement20250121.biddingPlanCancel') }}
          </AuthorityButton>
          <!-- 导出明细 -->
          <AuthorityButton
            type="primary"
            code="poolZhaobiao:exprotDetail"
            @click="exprotDetail"
          >
            {{ $t('cusEntry.common.exprotDetail') }}
          </AuthorityButton>
        </template>
      </MainHeader>
      <TableView
        :ref="gridId"
        bigData
        :checkbox="true"
        :table-data="tableData"
        :table-header="tableHeader"
        :current-change="handleCurrentChange"
        :page-size="pageSize"
        :pre-query-data="queryParam"
        :open-custom-table="true"
        :comActive="$attrs['changeTab']"
        :adept-mei-ql="true"
        :source="purchaseApplicationApi.query"
        :checkChange="checkChange"
        @getFooter="getFooter"
        @getFooterSize="getFooterSize"
        @afterQuery="afterQuery"
      >
        <template #extInPool="{ scope }">
          {{ ['Y'].includes(scope.row?.extBidFlag) ? null : $getDictLabel('YES_OR_NO', scope.row?.extInPool) }}
        </template>
      </TableView>
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
            <srm-col :initCol="2">
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
            <srm-col :initCol="2">
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
import purchaseApplicationDetail from './purchaseApplicationDetail'
import purchaseApplicationDetail2 from './purchaseApplicationDetailZhaobiao'
import { parseTime } from '@/utils'
import ExportExcel from 'lib@/components/export-excel'
import { purchaseApplicationApi, planPool } from 'modc@/buyer/purchasingDemand/api'
import OrganizationSelector from 'lib@/components/organization-selector'
import { transformMQL } from '@/library/utils/util'
import MImport from 'lib@/components/import'
import { downloadFileLink, downloadFileLinkByPost } from 'lib@/utils/file'
import withdrawZhaobiao from '@/modulesCus/buyer/purchasingDemand/views/withdrawZhaobiao/edit'
export default {
  name: 'PurchaseApplicationList',
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
        requireFrom: 'PR_SOU_REQUIREMENT_FROM',
        extIsSyncTask: 'YES_OR_NO',
        extInPool: 'YES_OR_NO',
        demandType: 'DEMAND_TYPE',
        ceeaPurchaseType: 'PURCHASE_TYPE',
        auditStatus: 'APPROVAL_STATUS'
      },
      checkChangeData: [],
      extraData: {
        fileModular: 'sup',
        fileFunction: 'purchaseCatalog',
        fileType: 'excel'
      },
      purchaseApplicationApi: purchaseApplicationApi,
      pageSize: 15,
      gridId: 'list',
      currentRow: null,
      tableHeader: [],
      tableData: [],
      getFooterNum: null,
      getFooterSizeNum: null,
      globalNickname: null,
      preArr: [
        // 申请日期
        {
          prop: 'dateList',
          label: () => this.$t('purchaseDemand.applyDate'),
          type: 'daterange'
        },
        // 申请编号
        {
          prop: 'requirementHeadNum',
          label: () => this.$t('purchaseDemand.requirementHeadNum')
        },
        // 申请人
        {
          prop: 'createdFullName',
          label: () => this.$t('purchaseDemand.applicant')
          // type: 'quicksearch',
          // showKey: 'nickname',
          // name: 'scc_rbac_user_display'
        },
        // 申请部门
        {
          prop: 'ceeaDepartmentName',
          label: () => this.$t('purchaseDemand.ceeaDepartment')
          // type: 'quicksearch',
          // showKey: 'descr',
          // propKey: 'deptid',
          // name: 'ceea_base_dept'
        },
        // 单据状态
        {
          prop: 'auditStatus',
          label: () => this.$t('purchaseDemand.applyStatus'),
          type: 'dict',
          code: 'APPROVAL_STATUS'
        },
        // 需求类型
        {
          prop: 'demandType',
          label: () => this.$t('purchaseDemand.demandType'),
          type: 'dict',
          code: 'DEMAND_TYPE'
        },
        // 是否确认领单
        {
          prop: 'extInPool',
          label: () => this.$t('cusEntry.supplement20250121.extInPool'),
          type: 'dict',
          code: 'YES_OR_NO'
        },
        // 板块
        {
          prop: 'orgBuName',
          label: () => this.$t('cusEntry.bidSuperviseReport.extOrgBuName')
        },
        // 公司
        {
          prop: 'orgName',
          label: () => this.$t('cusEntry.orderMod.companyName')
        },
        // 需求来源
        {
          prop: 'requireFrom',
          label: this.$t('cusEntry.supplement20250121.sourceFrom'),
          type: 'dict',
          code: 'PR_SOU_REQUIREMENT_FROM'
        },
        {
          prop: 'projectName',
          label: () => this.$t('bidMod.bidingName')
        },
        {
          prop: 'categoryName',
          label: () => this.$t('common.category'),
          type: 'catSelect',
          showKey: 'categoryName'
        },
        {
          prop: 'materialCode',
          label: () => this.$t('common.materialCode'),
          type: 'quicksearch',
          propKey: 'materialCode',
          showKey: 'materialCode',
          name: 'scc_base_material_item_contract'
        },
        {
          prop: 'ceeaPrType',
          label: () => this.$t('purchaseDemand.applicationType'),
          type: 'dict',
          code: 'application_form_type'
        },
        // 审批结束时间
        {
          prop: 'extApproveTimeList',
          label: () => this.$t('cusEntry.supplement20250121.extApproveTime'),
          type: 'daterange'
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
        label: this.$t('purchaseDemand.requirementHeadNum'),
        width: 120,
        showType: 'button',
        btnStyle: 'text',
        callback: row => this.readOne(row),
        formattor: val => val || '--'
      },
      // 需求类型
      {
        prop: 'demandType',
        label: this.$t('purchaseDemand.demandType'),
        width: 120,
        formattor: val => this.$getDictLabel('DEMAND_TYPE', val)
      },
      // 单据状态
      {
        prop: 'auditStatus',
        label: this.$t('purchaseDemand.applyStatus'),
        width: 100,
        formattor: val => this.$getDictLabel('APPROVAL_STATUS', val)
      },
      // 是否确认领单
      {
        prop: 'extInPool',
        label: this.$t('cusEntry.supplement20250121.extInPool'),
        width: 120,
        showType: 'slot',
        slot: 'extInPool'
      },
      // 申请人
      {
        prop: 'createdFullName',
        label: this.$t('purchaseDemand.applicant'),
        width: 100
      },
      // 申请部门
      {
        prop: 'ceeaDepartmentName',
        label: this.$t('purchaseDemand.ceeaDepartment'),
        width: 100
      },
      // 申请日期
      {
        prop: 'applyDate',
        label: this.$t('purchaseDemand.applyDate'),
        width: 100,
        formattor: val => (val ? parseTime(val, '{y}-{m}-{d}') : '')
      },
      // 审批结束时间
      {
        prop: 'extApproveTime',
        label: this.$t('cusEntry.supplement20250121.extApproveTime'),
        width: 120,
        dataType: 'dateTime'
      },
      // 板块
      {
        prop: 'orgBuName',
        label: this.$t('cusEntry.bidSuperviseReport.extOrgBuName'),
        formattor: (val, row) => row.souReqHead ? row.souReqHead.orgBuName : row.extOrgBuName,
        width: 120
      },
      // 公司
      {
        prop: 'orgName',
        label: this.$t('cusEntry.orderMod.companyName'),
        width: 120
      },
      // 需求来源
      {
        prop: 'requireFrom',
        label: this.$t('cusEntry.supplement20250121.sourceFrom'),
        formattor: (val, row) => row.souReqHead ? this.$getDictLabel('PR_SOU_REQUIREMENT_FROM', row.souReqHead.requireFrom) : '',
        width: 120
      },
      {
        prop: 'projectName',
        label: this.$t('bidMod.bidingName'),
        formattor: (val, row) => row.souReqHead ? row.souReqHead.projectName : '',
        width: 120
      },
      {
        prop: 'categoryName',
        label: this.$t('common.category'),
        width: 120
      },
      // 行项目总数
      {
        prop: 'reqLineListNum',
        label: this.$t('cusEntry.supplement20250314.reqLineListNum'),
        width: 120
      },
      // 是否同步中国
      {
        prop: 'extIsSyncTask',
        label: this.$t('cusEntry.supplement20250314.extIsSyncTask'),
        formattor: val => this.$getDictLabel('YES_OR_NO', val),
        width: 120
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
              ['DRAFT', 'WITHDRAW', 'REJECTED'].includes(row.auditStatus) &&
              (row.createdBy === this.globalNickname || row.sourceSystem === 'MRP')
          },
          // 删除
          {
            callback: row => this.deleteOne(row),
            formattor: () => this.$t('common.delete'),
            show: row =>
              ['DRAFT'].includes(row.auditStatus) && (row.createdBy === this.globalNickname || row.sourceSystem === 'MRP')
          },
          // 审批
          {
            callback: row => this.approvalOne(row),
            show: row => ['APPROVING'].includes(row.auditStatus) && (row.createdBy === this.globalNickname || row.isApprover == 'Y'),
            formattor: () => this.$t('common.approve')
          },
          {
            callback: row => this.handleDealAbnormal(row),
            formattor: () => this.$t('cusEntry.supplement20250121.dealConfirm'), // 【异常处理确认】 仅针对JD物流
            show: row => row.extIsAbnormal === 'Y'
              
          }
        ]
      }
    ]
    this.$nextTick(() => {
      this.getQuerydata()
    })

    // this.getFlowIntegrationMode()
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
    exprotDetail () {
      if (this.checkChangeData.length === 0) {
        this.$message.warning(this.$t('cusEntry.tipMessage.selectExportData'))
        return false
      }
      downloadFileLinkByPost(
        '/api-sup-ce/purchaseRequirement/exportRequirementLine',
        this.$t('cusEntry.inq.priceOrderDetail'),
        { requirementHeadIdList: this.checkChangeData.map(item => item.requirementHeadId) }
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    async requirementCancellation () {
      const checkChangeData = this.checkChangeData
      if (checkChangeData.length > 1) {
        // 需求取消只能选择一条单据取消，不支持多条同时取消
        this.$message.warning(this.$t('cusEntry.supplement20250121.promptTips21'))
        return false
      } else if (checkChangeData.length <= 0) {
        // 请选择一条单据进行取消
        this.$message.warning(this.$t('cusEntry.supplement20250121.promptTips22'))
        return false
      }
      if (this.checkChangeData[0]?.auditStatus != 'APPROVED') {
        // 请选择已审批状态的单据进行取消
        this.$message.warning(this.$t('cusEntry.supplement20250121.promptTips23'))
        return false
      }
      if (this.checkChangeData[0]?.extBidFlag != 'Y') {
        // 只能选择招标单据进行取消
        this.$message.warning(this.$t('cusEntry.supplement20250121.promptTips24'))
        return false
      }
      const res = await this.$http({
        url: '/api-sup-ce/ext/requiremnetCancle/queryReuiremnetAsWithContract',
        method: 'GET',
        params: { requirementHeadNum: this.checkChangeData[0].requirementHeadNum },
        loading: true
      })
      if (res && res.data && res.data.length) {
        // 当前需求已存在合同，请进行合同废弃或终止后进行需求取消。
        this.$message.warning(this.$t('cusEntry.supplement20250121.promptTips25'))
        return false
      }
      // 需求取消后，该项目所有招标流程将终止（不可撤回），终止后需重新发起采购申请和重新组织招标。确定继续？
      let cancleTips = this.$t('cusEntry.supplement20250121.promptTips26')
      const confirmResult = await this.$confirm(cancleTips, {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })
      if (confirmResult !== 'confirm') {
        return
      }

      const saveData = transformMQL.save('PrSouRequirementPoolForBuyer', [this.checkChangeData[0]], 'checkCancelCondition')
      planPool.checkCancelCondition(saveData).then((datas) => {
        this.$emit('tab-add', {
          component: withdrawZhaobiao,
          params: {
            flag: 'add',
            row: this.checkChangeData[0],
            tabName: 'withdrawZhaobiao',
            extSource: 'REQUIREMENT'
          },
          // 新增取消单
          title: this.$t('cusEntry.supplement20250121.addCancel'),
          name: 'withdrawZhaobiao'
        })
      })
    },
    withdraw (row) {
      let bussinessType = 'MQL_PR_SOU_REQUIREMENT_INIT'
      if (!['Y'].includes(row.extBidFlag)) { // 非招标
        bussinessType = 'REQUIREMENT'
      }
      // 请输入撤回原因
      this.$prompt(this.$t('cusEntry.supplement20250121.promptTips27'), this.$t('common.recall'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel')
      }).then(({ value }) => {
        const allparam = {
          'bussinessType': bussinessType, // 模板编码
          'dataId': row.requirementHeadId, // 业务单据号
          'commentmsg': value
        }
        this.$http({
          url: '/api-pj/external/bpm/rollBackAll',
          method: 'POST',
          data: allparam,
          loading: true
        }).then(res => {
          this.$message.success(this.$t('common.successWithdraw'))
        })
      })
    },
    confirmSave () { // 新增确认
      this.dialogFormVisible = false
      if (this.form.extBidFlag == 'N') { // 非招标
        this.$emit('tab-add', {
          component: purchaseApplicationDetail,
          params: {
            flag: 'add',
            tabName: 'purchaseApplicationDetail',
            demandType: this.form.demandType
          },
          // 创建采购申请单
          title: this.$t('purchaseDemand.addPurApplication'),
          name: 'purchaseApplicationDetail'
        })
      } else {
        this.$emit('tab-add', {
          component: purchaseApplicationDetail2,
          params: {
            flag: 'add',
            tabName: 'purchaseApplicationDetail',
            demandType: this.form.demandType
          },
          // 创建招标计划
          title: this.$t('cusEntry.supplement20250121.addBidPlan'),
          name: 'purchaseApplicationDetail'
        })
      }
    },
    demandTypeChange (value) {
      if (value == 'Material_category') {
        this.form.extBidFlag = 'N'
      } else {
        this.form.extBidFlag = 'Y'
      }
    },
    cancel () {
      this.dialogFormVisible = false
    },
    checkChange (records) {
      this.checkChangeData = records
    },
    confirms () {
      if (this.checkChangeData.length <= 0) {
        this.$message.error(this.$t('components.common.pleaseSelectOne'))
        return false
      }
      let attr = []
      let bol = false
      let zhaobiaoBol = false
      this.checkChangeData.forEach(datas => {
        if (datas.extInPool == 'Y' || datas.auditStatus != 'APPROVED') {
          bol = true
        }
        if (datas.extBidFlag == 'Y') {
          zhaobiaoBol = true
        }
        attr.push({ requirementHeadId: datas.requirementHeadId })
      })
      if (zhaobiaoBol) {
        // 所勾选行存在招标的采购申请，不允许领单
        this.$message.error(this.$t('cusEntry.supplement20250121.promptTips28'))
        return false
      }
      if (bol) {
        // 所勾选行存在已领单的采购申请，请检查
        this.$message.error(this.$t('cusEntry.supplement20250121.promptTips29'))
        return false
      }
      const searchData = transformMQL.save('PurchaseRequirementHead', attr, 'pushPool')
      purchaseApplicationApi.pushPool(searchData).then(datas => {
        this.$message.success(this.$t('common.successConfirm'))
        this.getQuerydata()
      })
    },
    handleSuccess () {
      this.getQuerydata()
    },
    downloadTemplate () {
      downloadFileLink('/api-sup-ce/npm/pr/requirement/sou/downloadExcel', this.$t('mould.importTemplate')).catch(
        () => {
          this.$message.error(this.$t('components.eio.downloadFail'))
        }
      )
    },
    generateMeiQLExportRequest () {
      let queryParam = JSON.parse(JSON.stringify(this.queryParam))
      console.log(queryParam, 'queryParam')
      queryParam.type = 'PurchaseRequirementHead'
      queryParam.action = 'query'
      const all = {
        body: queryParam
      }
      return all
    },
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
    getQuerydata (obj, isQuery = true) {
      const { dateList, extApproveTimeList, extInPool, ...rest } = obj || this.queryParam
      const params = {}
      if (dateList) {
        params.applyDate = [dateList[0], dateList[1]]
      }
      if (extApproveTimeList) {
        params.extApproveTime = [extApproveTimeList[0], extApproveTimeList[1]]
      }
      if (extInPool) {
        params.extInPool = extInPool
        params.extBidFlag = 'N'
      }
      let condition = {}
      if (rest?.orgBuName || rest?.requireFrom || rest?.projectName || rest?.categoryName) {
        condition = {
          $strictQuery: true,
          filter: {
            orgBuName: {
              'contains': rest?.orgBuName
            },
            requireFrom: {
              'contains': rest?.requireFrom
            },
            projectName: {
              'contains': rest?.projectName
            },
            categoryName: {
              'contains': rest?.categoryName
            }
          }
        }
      }
      let conditionChild = {}
      if (rest?.materialCode) {
        conditionChild = {
          $strictQuery: true,
          filter: {
            materialCode: {
              'contains': rest?.materialCode
            }
          }
        }
      }
      this.queryParam = transformMQL.listPageData({
        type: 'PurchaseRequirementHead',
        action: 'query',
        sort: 'requirementHeadNum',
        params: { ...rest, ...params },
        filterOperator: {
          extApproveTime: 'between',
          applyDate: 'between'
        },
        query: {
          '*': {},
          'souReqHead': {
            '*': {},
            $condition: condition
          },
          'reqLineList': {
            '*': {},
            $condition: conditionChild
          }
        }
      })
      if (!isQuery) return
      this.$nextTick(() => {
        this.$refs[this.gridId].query()
        this.$refs[this.gridId].doLayout()
        this.checkChangeData = []
      })
    },
    syncFilterParams (values) {
      this.getQuerydata(values, false)
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
      this.dialogFormVisible = true
    },
    async editOne (row) {
      await this.setTabRemove(row)
      if (!['Y'].includes(row.extBidFlag)) { // 非招标
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
      } else { // 招标
        this.$emit('tab-add', {
          component: purchaseApplicationDetail2,
          params: {
            flag: 'edit',
            row: row,
            tabName: 'purchaseApplicationDetail' + row.requirementHeadNum
          },
          title: row.requirementHeadNum,
          name: 'purchaseApplicationDetail' + row.requirementHeadNum
        })
      }
    },
    setTabRemove (row) {
      if (!['None', 'Push'].includes(row.integrationMode)) {
        this.$emit('tab-remove', 'purchaseApplicationDetail' + row.requirementHeadNum)
      }
    },
    async approvalOne (row) {
      await this.setTabRemove(row)
      if (['Y', 'default'].includes(row.extBidFlag)) { // 招标
        this.$emit('tab-add', {
          component: purchaseApplicationDetail2,
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
      } else {
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
      }
    },
    readOne (row) {
      if (['Y', 'default'].includes(row.extBidFlag)) { // 招标
        this.$emit('tab-add', {
          component: purchaseApplicationDetail2,
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
      } else {
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
      }
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
          const saveData = transformMQL.save('PrRequirementForBuyer', [{ requirementHeadId: row.requirementHeadId }], 'removeRequirement')
          purchaseApplicationApi.delete(saveData).then((datas) => {
            this.$message({
              message: this.$t('common.successSave'),
              type: 'success'
            })
            this.getQuerydata()
          })
        })
        .catch(() => { })
    },
    // 异常处理确认
    async handleDealAbnormal(row){
      let transformParams = transformMQL.save('PurchaseRequirementHead',[{ requirementHeadId: row.requirementHeadId }],'confirmAbnormal')
      let response = await purchaseApplicationApi.confirmAbnormal(transformParams)
      if(response){
        this.$message.success(this.$t('common.success'))
        this.getQuerydata()
      }
    },
    afterQuery () {
      this.$refs[this.gridId].setTableData(async tableData => {
        const res = await this.$api.base.flowAPI.queryTodo()
        let queryTodoList = res.data || []
        tableData.forEach(tableItem => {
          this.$set(tableItem, 'reqLineListNum', tableItem.extBidFlag === 'N' ? tableItem.reqLineList.length : null)
          let obj = queryTodoList.find(todoItem => tableItem.requirementHeadId + '' === todoItem.businessId + '')
          if (obj) {
            this.$set(tableItem, 'isApprover', 'Y')
          } else {
            this.$set(tableItem, 'isApprover', 'N')
          }
        })
      })
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
