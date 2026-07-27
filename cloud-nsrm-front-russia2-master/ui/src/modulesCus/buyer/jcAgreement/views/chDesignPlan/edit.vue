<template>
  <el-container class="flex-container">
    <el-main>
      <!-- <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :fun-params="workflowParamsInfo"
        :button-config-info="buttonConfigInfo"
        :button-custom="buttonCustom"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="type => saveBill(type)"
        @submit-direct="type => saveBill(type)"
        @confirm="(type, comment) => saveBill(type, comment)"
        @close-tab="back"
      > -->
      <ApprovalProcess
        :business-id="workflowBusinessId"
        business-type="designPlan"
        :approval-status="form.status"
        :status-map="statusMap"
        :readonly="$attrs.params.flag === 'view'"
        :show-button-config="showButtonConfig"
        :operation-pre-options="operationPreOptions"
        @approval-handler-callback="approvalHandlerCallback"
      >
        <el-form ref="form" :model="form" :rules="formRules">
          <el-tabs
            ref="navTab"
            v-model="tabVal"
            type="border-card"
            :before-leave="beforeTabLeave"
          >
            <!-- 基础信息 -->
            <el-tab-pane
              v-if="!approvalFlag"
              name="tab1"
              :label="$t('common.baseInfo')"
            >
              <BaseInfo
                ref="baseInfo"
                :form.sync="form"
                :readonly="disabledFlag"
                :areaList="areaList"
              />
            </el-tab-pane>
            <!-- 需求信息 -->
            <el-tab-pane
              name="tab2"
              :label="$t('sourcingBuyer.requirementInfo')"
            >
              <RequireInfo
                v-if="!approvalFlag"
                ref="requireInfo"
                :form.sync="form"
                :readonly="disabledFlag"
                @pull-order="pullOrder"
              />
              <el-tabs
                ref="requireTab"
                v-model="requireTab"
                type="border-card"
              >
                <!-- 项目策略方案 -->
                <el-tab-pane
                  name="requireTab1"
                  :label="$t('cusEntry.supplement20250121.projectProposal')"
                >
                  <ProjectPlan
                    ref="projectPlan"
                    :form.sync="form"
                    :approval-flag="approvalFlag"
                    :readonly="disabledFlag"
                  />
                </el-tab-pane>
                <!-- 需求 -->
                <el-tab-pane
                  v-if="!approvalFlag"
                  name="requireTab2"
                  :label="$t('dataConfMod.demand')"
                >
                  <RequireTab
                    ref="requireTab"
                    :form.sync="form"
                    :readonly="disabledFlag"
                    @file-success="fileSuccess"
                    @merge-data="mergeData"
                  />
                </el-tab-pane>
                <!-- 上年数据1 -->
                <el-tab-pane
                  v-if="!approvalFlag"
                  name="requireTab3"
                  :label="$t('cusEntry.supplement20250121.lastYearData1')"
                >
                  <LastYear
                    ref="lastYear"
                    :value="lastYearList"
                    :designId="designId"
                  />
                </el-tab-pane>
                <!-- 上上年数据2 -->
                <el-tab-pane
                  v-if="!approvalFlag"
                  name="requireTab4"
                  :label="$t('cusEntry.supplement20250121.beforeLastYear')"
                >
                  <BeforeLastYear
                    ref="beforeLastYear"
                    :value="beforeLastYearList"
                    :designId="designId"
                  />
                </el-tab-pane>
              </el-tabs>
            </el-tab-pane>
          </el-tabs>
        </el-form>
        <div v-if="tabVal === 'tab1' && !disabledFlag" slot="custom">
          <el-button type="primary" style="margin-left:8px;" @click="saveTempBill('save')">
            {{ $t('common.staging') }}
          </el-button>
          <el-button type="primary" @click="saveTempBill('next')">
            {{ $t('common.nextOne') }}
          </el-button>
        </div>
      </ApprovalProcess>
      <!-- </cworkflowmulti> -->
    </el-main>
  </el-container>
</template>
<script>
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
import WorkflowCommon from '@/library/mixins/workflow-common'
import BaseInfo from './components/baseInfo'
import RequireInfo from './components/requireInfo'
import ProjectPlan from './components/projectPlan'
import RequireTab from './components/requireTab'
import LastYear from './components/lastYear'
import BeforeLastYear from './components/beforeLastYear'
import { designPlanHttp } from 'modcb@/jcAgreement/api'
import { cloneDeep } from 'lodash'
import { mapGetters } from 'vuex'
import { getDictItem } from '@/api/common'
import { adaptDictData } from '@/utils'
import ApprovalProcess from 'modc@/components/approval-process'

export default {
  name: 'ChDesignPlanDetail',
  components: {
    BaseInfo,
    CToolbar,
    RequireInfo,
    ProjectPlan,
    RequireTab,
    LastYear,
    ApprovalProcess,
    BeforeLastYear
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      tabVal: 'tab1',
      requireTab: 'requireTab1',
      designId: null,
      form: {
        // 基本信息内容
        designId: null,
        projectId: null,
        projectCode: null,
        projectName: null,
        num: null,
        createdFullName: null,
        phone: null,
        depId: null,
        depCode: null,
        depName: null,
        projMoney: null,
        area: [],
        projIntroduce: null,
        pricingIdeas: null,
        status: 'DRAFT',
        // 需求信息-策划方案内容
        programmeId: null,
        unitFlag: null,
        buyFlag: null,
        categoryFlag: null,
        workList: [],
        unitList: [],
        categoryList: [],
        supplyList: [],
        strategyList: [],
        otherList: [],
        settingList: [],
        // 需求信息-需求内容
        itemList: [],
        vendorList: []
      },
      formRules: {
        phone: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        projectName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        projMoney: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        area: [{ required: true, message: this.$t('common.requiredField'), trigger: 'change' }]
      },
      lastYearList: [],
      beforeLastYearList: [],
      areaList: [],
      operationPreOptions: {
        nextStep: this.preNextStepHandler
      },
      statusMap: {
        DRAFT: 'DRAFT', // 拟定
        SUBMITTED: 'APPROVING', // 已提交
        APPROVED: 'APPROVED', // 审批通过
        REJECTED: 'REJECTED', // 已驳回
        WITHDRAW: 'WITHDRAW', // 已撤回
        ABANDONED: 'ABANDONED' // 已废弃
      }
    }
  },
  computed: {
    ...mapGetters(['userInfo']),
    urlParams () {
      return this.$attrs.params || {}
    },
    showButtonConfig () { // 审批流按钮展示
      return this.tabVal === 'tab1' ? { saveAndNextStep: false } : { saveAndNextStep: true }
    },
    approvalFlag () { // 审批流页面字段展示标识
      return this.urlParams.approvalFlag || false
    },
    disabledFlag () {
      return !!['view', 'approval', 'manage'].includes(this.urlParams.flag)
    },
    viewUpdateButton () { // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
      return ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(this.form.status) && this.tabVal === 'tab2' && !this.disabledFlag
    },
    viewWithDrawButton () { // 用于控制撤回按钮是否显示
      return ['APPROVING'].includes(this.form.status) && this.workflowParamsInfo.integrationMode === 'Push'
    },
    disabledUpdateButton () {
      return ['APPROVING'].includes(this.form.status)
    },
    workflowBusinessId () { // 用来指定工作流的业务ID 单据ID 注意每一个单的ID不一样
      return this.designId || null
    },
    workflowTabDisabled () { // 用来控制审批流tab页是否禁用
      // 拟定 驳回 撤回 可编辑 单据
      return !this.form.designId ||
              (['DRAFT'].includes(this.form.status) && this.urlParams.flag !== 'approve')
    }
  },
  watch: {
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    },
    disabledUpdateButton () {
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
    },
    viewWithDrawButton () {
      this.buttonConfigInfo.withdraw.view = this.viewWithDrawButton
    }
  },
  async created () {
    this.designId = this.$attrs.params.row.designId
    this.tabVal = this.$attrs.params.tabVal || 'tab1'

    await this.getAreaList()
    if (this.designId) {
      this.getFormDetail()
      this.getRequireInfo()
      this.getVendorInfo()
      this.getPullOrder(1).then(data => {
        this.lastYearList = data
      })
      this.getPullOrder(2).then(data => {
        this.beforeLastYearList = data
      })
      this.getPullOrder(3).then(data => {
        this.form.itemList = data
      })
    } else {
      this.form.depName = this.userInfo.department
    }
    this.getButtonConfig()
  },
  methods: {
    // 下一步前置处理
    async preNextStepHandler () {
      let params = this.initSaveParams()
      const validForm = await this.validBill()
      if (!validForm) {
        this.__focus_error__()
        return false
      }
      // 调用暂存接口
      // 校验物资信息必填
      if (this.form.itemList.length === 0) {
        this.$message.warning(this.$t('cusEntry.tipMessage.maintainMaterialInformationMsg'))
        return false
      }
      const response = await designPlanHttp.saveOrUpdateDemandProjPlan(params)
      this.designId = response.data.designId
      return true
    },
    // 审批流操作回调
    approvalHandlerCallback (type) {
      switch (type) {
      case 'save':
        this.saveBill('SAVE')
        break
      case 'submit':
        this.back()
        break
      default:
        break
      }
    },
    getButtonConfig () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.cancel.view = !this.disabledFlag
      this.buttonConfigInfo.close.view = this.disabledFlag
      this.buttonConfigInfo.withdraw.view = this.viewWithDrawButton
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'designPlan'
    },
    fileSuccess () {
      this.getPullOrder(3).then(data => {
        this.form.itemList = data
      })
    },
    async getAreaList () {
      await getDictItem('REGION').then(res => {
        this.areaList = adaptDictData(res.data, 'dict')
      })
    },
    // 拉取上年数据:1;上上年数据:2
    async pullOrder (type, queryInfo) {
      let areaCodes = this.form.area || []
      let params = {
        designId: this.designId,
        areaCodes,
        type,
        ...queryInfo
      }
      const response = await designPlanHttp.pullOrder(params)
      if (response) {
        let result = response.data || []
        if (type === 1) {
          this.lastYearList = result
        } else {
          this.beforeLastYearList = result
        }
      }
    },
    // 获取物料数据
    // type:1、上年订单数据。2、上上年订单数据。3、合并的数据
    getPullOrder (type, designId, pageNum, pageSize) {
      return new Promise(async (resolve) => {
        const response = await designPlanHttp.getPullOrder({
          type,
          designId: this.designId,
          pageNum: 1,
          pageSize: 1000000
        })
        if (response && response.data) {
          let list = response.data.list || []
          resolve(list)
        }
      })
    },
    // 获取ref值
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    beforeTabLeave (val) {
      if (val === 'tab2') {
        if (!this.designId) {
          // 请先保存基础信息
          this.$message.warning(this.$t('cusEntry.supplement20250121.baseInfoTips'))
          return false
        }
      }
      return true
    },
    // 合并数据之后调用
    mergeData () {
      this.getVendorInfo()
    },
    // 获取供应商信息
    async getVendorInfo () {
      const response = await designPlanHttp.getReqSupInfoList({
        designId: this.designId
      })
      if (response && response.data) {
        this.form.vendorList = response.data || []
      }
    },
    async validBill () {
      return new Promise(async (resolve) => {
        let validForm
        await this.$refs.form.validate(valid => { validForm = valid })
        resolve(validForm)
      })
    },
    async saveTempBill (type) {
      let result
      // 暂存
      if (type === 'save') {
        result = await this.savaOrUpdateDesignPlan()
        this.designId = result.designId
        this.$message.success(this.$t('common.successSave'))
        await this.getFormDetail()
      }

      // 下一步
      if (type === 'next') {
        const validForm = await this.validBill()
        if (!validForm) {
          this.__focus_error__()
          return
        }
        // 校验物资信息必填
        if (this.form.itemList.length === 0) {
          this.$message.warning(this.$t('cusEntry.tipMessage.maintainMaterialInformationMsg'))
          return false
        }
        result = await this.savaOrUpdateDesignPlan()
        this.designId = result.designId
        this.$message.success(this.$t('common.successSave'))
        await this.getFormDetail()
        this.tabVal = 'tab2'
      }
    },
    initParams () {
      let params = cloneDeep(this.form)
      if (params.area && Array.isArray(params.area)) {
        params.area = params.area.join(',')
      }
      console.log('params', params)
      return params
    },
    async savaOrUpdateDesignPlan  () {
      let params = this.initParams()
      return new Promise(async (resolve) => {
        const response = await designPlanHttp.savaOrUpdateDesignPlan(params)
        if (response) {
          resolve(response.data)
        }
      })
    },
    async getFormDetail () {
      const response = await designPlanHttp.getDesignPlanInfo({
        designId: this.designId
      })
      if (response) {
        Object.keys(response.data).forEach(key => {
          this.form[key] = response.data[key]
        })
        this.designId = this.form.designId
        const { area } = this.form
        if (area) {
          this.form.area = area.split(',')
        } else {
          this.form.area = []
        }
      }
    },
    initSaveParams () {
      let tranForm = cloneDeep(this.form)
      let params = {}
      const { designId, programmeId, unitFlag, buyFlag, categoryFlag, workList, unitList, categoryList, supplyList, strategyList, otherList, settingList } = tranForm
      params = {
        designId,
        demandProgramme: {
          unitFlag,
          buyFlag,
          categoryFlag,
          programmeId
        },
        workList,
        unitList,
        categoryList,
        supplyList,
        strategyList,
        otherList,
        settingList
      }
      console.log('params', params)
      return params
    },
    // 审批流暂存、提交，存储项目策略方案信息
    async saveBill (type) {
      let params = this.initSaveParams()
      if (type === 'SUBMIT') {
        // TODO 校验
        const validForm = await this.validBill()
        if (!validForm) {
          this.__focus_error__()
          return
        }
      }
      const response = await designPlanHttp.saveOrUpdateDemandProjPlan(params)
      if (response.data) {
        this.designId = response.data.designId
        this.$message.success(this.$t('common.successSave'))
        await this.getRequireInfo()
        if (type === 'SUBMIT') {
          await this.handlerAfter(type)
        }
      }
    },
    async getRequireInfo () {
      const response = await designPlanHttp.getDemandProjPlanList({
        designId: this.designId
      })
      if (response && response.data) {
        const { demandProgramme = {}, designId, ...rest } = response.data
        Object.keys(rest).forEach(key => {
          this.form[key] = rest[key]
        })
        if (!demandProgramme) return
        this.form.programmeId = demandProgramme.programmeId
        this.form.unitFlag = demandProgramme.unitFlag
        this.form.buyFlag = demandProgramme.buyFlag
        this.form.categoryFlag = demandProgramme.categoryFlag
      }
    },
    back () {
      let { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('ChDesignPlanList.getQueryData')
    }
  }
}
</script>
<style lang="scss" scoped>

</style>
