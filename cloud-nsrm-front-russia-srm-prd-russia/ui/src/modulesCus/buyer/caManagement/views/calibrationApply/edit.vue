<template>
  <el-container class="flex-container" direction="vertical">
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
        business-type="SOU_CA"
        :approvalStatus="form.status"
        :status-map="statusMap"
        :projectName="form.souName"
        :readonly="$attrs.params.flag === 'view'"
        :operation-pre-options="operationPreOptions"
        @approval-handler-callback="approvalHandlerCallback"
        @get-flow-node-list="getFlowNodeList"
      >
        <el-form ref="form" :model="form" :rules="formRules">
          <el-collapse v-model="colValue">
            <el-collapse-item title="业务信息" name="1">
              <BusinessInfo
                ref="businessInfo"
                :form.sync="form"
                :readonly="disabledFlag"
                :flag="urlParams.flag"
                :approval-flag="approvalFlag"
                v-on="$listeners"
                @tabAdd="tabAdd"
              />
            </el-collapse-item>
            <el-collapse-item title="基础信息" name="2">
              <BaseInfo
                ref="baseInfo"
                :form.sync="form"
                :readonly="disabledFlag"
                :approval-flag="approvalFlag"
                :flag="urlParams.flag"
                @tab-add="openNewTab"
              />
            </el-collapse-item>
            <el-collapse-item title="供应商情况" name="3">
              <Participate
                ref="participate"
                :form.sync="form"
                :readonly="disabledFlag"
                :caTenderTimes="form.caTenderTimes"
                :isShow="form.ifWrite == 'N'"
                :ca-negotiate-extend-table.sync="caNegotiateExtendTable"
                :ca-negotiate-extend-table-header.sync="caNegotiateExtendTableHeader"
                @tabAdd="tabAdd"
              />
            </el-collapse-item>
            <!-- <el-collapse-item v-if="form.ifWrite =='N'" title="谈判情况" name="4">
              <Negotiations
                ref="negotiations"
                :form.sync="form"
                :readonly="disabledFlag"
                :table.sync="caNegotiateExtendTable"
                :tableHeader="caNegotiateExtendTableHeader"
              />
            </el-collapse-item> -->
            <el-collapse-item title="招标历史价格" name="5">
              <BidHistoryPrice
                ref="bidHistoryPrice"
                :form.sync="form"
                :readonly="disabledFlag"
                :history-price-list="form.historyPriceList"
                :approval-flag="approvalFlag"
                v-on="$listeners"
              />
            </el-collapse-item>
            <el-collapse-item title="供应商报价对比详情" name="6">
              <QuoteCompare
                ref="quoteCompare"
                :form.sync="form"
                :readonly="disabledFlag"
                :winOptions="winOptions"
                :input-flag="inputFlag"
                :flag="urlParams.flag"
                @viewQuote="viewQuote"
                @viewQuoteBy="viewQuoteBy"
              />
            </el-collapse-item>
            <el-collapse-item title="附件信息" name="7">
              <FileDynamic
                ref="sceneAttachment"
                v-model="form.sceneFiles"
                scene-module-code="SCENE_SOU_CA_ATTACHMENT"
                :business-id="caId"
                :editable="!disabledFlag"
              />
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </ApprovalProcess>
      <!-- </CWorkflowMulti> -->
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import WorkflowCommon from '@/library/mixins/workflow-common'
import BaseInfo from './components/baseInfo'
import BusinessInfo from './components/businessInfo'
import QuoteCompare from './components/quoteCompare'
import BidHistoryPrice from './components/bidHistoryPrice'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import PriceComparison from 'modcb@/biddingBuyer/views/biddingManagement/biddingDetail/businessManagement/priceComparison'
import HistoryCooperationInfo from "modcb@/biddingBuyer/views/biddingManagement/biddingDetail/businessManagement/historyCooperationInfo";
import { transformMQL } from 'lib@/utils/util'
import caHttp from './api'
import { bidBuyerHttp } from 'modcb@/biddingBuyer/api'
import Participate from './components/participate'
import Negotiations from './components/negotiations'
import inspectApplyDetail from '@/modulesCus/buyer/inspectManagement/views/inspectManage/applyDetail'
import ApprovalProcess from 'modc@/components/approval-process'

export default {
  name: 'CalibrationApplyDetail',
  components: {
    BaseInfo,
    BusinessInfo,
    QuoteCompare,
    BidHistoryPrice,
    FileDynamic,
    PriceComparison,
    HistoryCooperationInfo,
    Participate,
    Negotiations,
    ApprovalProcess,
    inspectApplyDetail
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      colValue: ['1', '2', '3', '4', '5', '6', '7'],
      form: {
        caId: null,
        caNo: null,
        status: 'DRAFT',
        extOrgBuId: null,
        extOrgBuCode: null,
        extOrgBuName: null,
        extOrgOuId: null,
        extOrgOuCode: null,
        extOrgOuName: null,
        demandDepartmentName: null,
        demandUserName: null,
        createdFullName: null,
        creationDate: null,
        lastUpdateDate: null,
        demandUserNickname: null,
        extProjectNo: null,
        souName: null,
        warrantyPeriod: null,
        extBudget: null,
        timeLimit: null,
        releaseTime: null,
        bidClosingTime: null,
        bidEvaluationEndTime: null,
        priceOpeningTime: null,
        projectOverviewAndBidScope: null,
        paymentRequirements: null,
        remark: null,
        applicantNo: null,
        ifWrite: null, // 为N是不可填写  Y是可以填写
        contractOperatorUserId: null,
        contractOperatorUsername: null,
        contractOperatorNickname: null,
        approvalUserId: null,
        approvalUserName: null,
        approvalNickname: null,
        caRound: null,
        discardDescription: null,
        caOrders: [],
        caSuppliers: [],
        caSelectionResults: [],
        historyPriceList: [],
        sceneFiles: []
      },
      formRules: {
        extOrgOuName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        contractOperatorNickname: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        approvalNickname: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        caRound: [{ required: true, message: this.$t('cusEntry.tipMessage.caRoundMsg'), trigger: 'blur' }],
        souName: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        extBudget: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        projectOverviewAndBidScope: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        paymentRequirements: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        vendorFlairAdjure: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        vendorBizAdjure: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        budgetPriceDiff: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        previousPurchase: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }],
        manufacturerAnalysis: [{ required: true, message: this.$t('common.requiredField'), trigger: 'blur' }]
      },
      caId: null,
      sceneFiles: [],
      winOptions: [],
      caNegotiateExtendTable: [],
      caNegotiateExtendTableHeader: [],
      flowNodeList2: [],
      timeFlag: false, // 更新实际上报时间标识
      inputFlag: false, // 审批流页面字段可编辑标识
      operationPreOptions: {
        pass: this.prePassHandler,
        nextStep: this.preNextStepHandler
      },
      statusMap: {
        DRAFT: 'DRAFT', // 拟定
        SUBMITTED: 'APPROVING', // 已提交
        APPROVED: 'APPROVED', // 审批通过
        REJECTED: 'REJECTED', // 已驳回
        WITHDRAW: 'WITHDRAW', // 已撤回
        ABANDONED: 'ABANDON' // 已废弃
      }
    }
  },
  computed: {
    urlParams () {
      return this.$attrs.params || {}
    },
    disabledFlag () {
      return !!['view', 'approval', 'manage'].includes(this.urlParams.flag)
    },
    approvalFlag () { // 审批流页面字段展示标识
      return this.urlParams.approvalFlag || false
    },
    username () {
      return this.$store.getters.userInfo.username || ''
    },
    viewUpdateButton () { // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
      return ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(this.form.status) && !['view'].includes(this.urlParams.flag)
    },
    viewWithDrawButton () { // 用于控制撤回按钮是否显示
      return ['APPROVING'].includes(this.form.status) && this.workflowParamsInfo.integrationMode === 'Push'
    },
    disabledUpdateButton () {
      return ['APPROVING'].includes(this.form.status)
    },
    workflowBusinessId () { // 用来指定工作流的业务ID 单据ID 注意每一个单的ID不一样
      return this.caId || null
    },
    workflowTabDisabled () { // 用来控制审批流tab页是否禁用
      // 拟定 驳回 撤回 可编辑 单据
      return !this.caId ||
              (['DRAFT'].includes(this.form.status) && this.urlParams.flag !== 'approval')
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
  created () {
    this.caId = this.urlParams.row.caId
    if (this.caId) {
      this.getFormDetail()
    } else {
      this.loadFileInfo()
    }
    this.getButtonConfig()
  },
  methods: {
    // 参数处理
    dealParams () {
      let copyForm = JSON.parse(JSON.stringify(this.form))
      const { caSelectionResults } = copyForm
      caSelectionResults.forEach(item => {
        if (item.winRange) {
          item.winRange = item.winRange.join(';')
        }
      })
      copyForm.caNegotiateExtend.data = []
      copyForm.caNegotiateExtend.title.forEach(item => {
        this.caNegotiateExtendTable.forEach((child, i) => {
          !copyForm.caNegotiateExtend.data[i] && (copyForm.caNegotiateExtend.data[i] = [])
          copyForm.caNegotiateExtend.data[i].push(child[item.vendorCode])
        })
      })
      return { caSelectionResults, copyForm }
    },
    // 通过前置处理
    async prePassHandler () {
      const { caSelectionResults, copyForm } = this.dealParams()
      if (this.form.ifWrite === 'N' && this.inputFlag && this.urlParams.flag === 'approval') { // 审批填写校验
        for (let item of caSelectionResults) {
          if (!item.isWin) {
            this.$message.warning('供应商选定结果-是否中标不能为空')
            return false
          }
          if (item.isWin === 'Y' && !item.winRange) {
            this.$message.warning('供应商选定结果-中标范围不能为空')
            return false
          }
          if (!item.winReason) {
            this.$message.warning('供应商选定结果-中/落标原因不能为空')
            return false
          }
        }
        // 供应商选定结果 - 至少需要选中一个中标供应商
        if (caSelectionResults.every(item => item.isWin === 'N')) {
          this.$message.warning('供应商选定结果-至少需要有一家中标供应商')
          return false
        }
        // 校验是否是重点关注供应商
        const checkParams = transformMQL.save('Ca', [copyForm], 'selectWinCheck')
        const { data } = await caHttp.selectWinCheck(checkParams)
        const record = data.records.find(record => record.code == 1)
        let checkResult = true
        if (record) {
          await this.$confirm(record.msg, '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            checkResult = true
          }).catch(() => {
            checkResult = false
          })
        }
        if (!checkResult) {
          return false
        }
      }
      // 调用暂存接口
      let transformParams = transformMQL.save('Ca', [copyForm], 'save')
      const response = await caHttp.save(transformParams)
      this.caId = this.form.caId = response.data[0].caId
      this.businessId = this.caId
      if (!this.businessId) {
        this.$message.warning(this.$t('cusEntry.tipMessage.businessIdIsNotExit'))
        return false
      }
      return true
    },
    // 下一步前置处理
    async preNextStepHandler () {
      const { caSelectionResults, copyForm } = this.dealParams()
      if (this.form.ifWrite === 'Y') { // 不是BMP审批通过后回写，才去校验这些
        for (let item of caSelectionResults) {
          if (!item.isWin) {
            this.$message.warning('供应商选定结果-是否中标不能为空')
            return false
          }
          if (item.isWin === 'Y' && !item.winRange) {
            this.$message.warning('供应商选定结果-中标范围不能为空')
            return false
          }
          if (!item.winReason) {
            this.$message.warning('供应商选定结果-中/落标原因不能为空')
            return false
          }
        }
        // 供应商选定结果 - 至少需要选中一个中标供应商
        if (caSelectionResults.every(item => item.isWin === 'N')) {
          this.$message.warning('供应商选定结果-至少需要有一家中标供应商')
          return false
        }
      }
      // 非询比价才去校验
      if (this.form.extSouProcess !== 'INQUIRY') {
        for (let item of copyForm.caSuppliers) {
          if (!item.comprehensiveEvaluation) {
            this.$message.warning('供应商总体情况-综合评定不能为空')
            return false
          }
        }
      }
      if (!await this.validBill()) {
        this.$message.warning('请填写完必填项')
        return false
      }
      // 调用暂存接口
      let transformParams = transformMQL.save('Ca', [copyForm], 'save')
      const response = await caHttp.save(transformParams)
      this.caId = this.form.caId = response.data[0].caId
      this.businessId = this.caId
      if (!this.businessId) {
        this.$message.warning(this.$t('cusEntry.tipMessage.businessIdIsNotExit'))
        return false
      }
      return true
    },
    checkObjEmpty (obj) {
      for (let key in obj) {
        if (obj[key] === null || obj[key] === undefined || obj[key] === '') {
          return true
        }
      }
      return false
    },
    // 审批流操作回调
    approvalHandlerCallback (type) {
      let isEmpty = false
      if (type === 'save') {
        const historyList = this.form.historyPriceList.filter(item => item.addHistoryFlag === true)
        historyList.forEach(item => {
          if (this.checkObjEmpty(item)) {
            isEmpty = true
          }
        })
      }
      if (!isEmpty) {
        this.form.historyPriceList.forEach(item => {
          if (item.addHistoryFlag === true) {
            item.priceTax = Number(item.priceTax)
            item.priceSumTax = Number(item.priceSumTax)
          }
        })
        let { copyForm } = this.dealParams()
        let transformParams = transformMQL.save('Ca', [copyForm], 'save')
        this.getFlowNodeList2(this.flowNodeList2)
        switch (type) {
        case 'save':
          caHttp.save(transformParams).then(res => {
            this.$message.success(this.$t('common.successSave'))
            this.caId = res.data[0].caId
            this.getFormDetail()
          })
          break
        case 'pass':
          if (this.timeFlag) {
            this.$http({
              url: `/api-sou/ext/buyer/bid/init/saveCaSumbimteReport?caId=${this.caId}`,
              method: 'GET',
              loading: true
            })
          }
          break
        case 'submit':
          this.back()
          break
        default:
          break
        }
      } else {
        this.$message.warning('新增历史价格均为必填项，请填写完整')
      }
    },
    // 获取审批流程节点
    getFlowNodeList (flowNodeList = []) {
      this.flowNodeList2 = flowNodeList
      this.getFlowNodeList2(this.flowNodeList2)
    },
    getFlowNodeList2 (flowNodeList = []) {
      const obj = flowNodeList.find(item => item.taskStatus === '1') // 当前节点 taskStatus = '1'
      if(obj && obj.executorId === this.username){
      if (obj.activityName === '5.一级审批') {
        this.inputFlag = true
      }
      if (obj.activityName === '4.部长') {
        this.timeFlag = true
      }
      }
    },
    tabAdd (val) {
      let tab = {
        component: inspectApplyDetail,
        params: {
          flag: 'view',
          row: val,
          tabName: 'inspectApplyDetail' + val.inspectId
        },
        title: val.inspectNum,
        name: 'inspectApplyDetail' + val.inspectId
      }
      this.$emit('tab-add', tab)
    },
    openNewTab (tab) {
      this.$emit('tab-add', tab)
    },
    loadFileInfo (fileRef = 'sceneAttachment') {
      this.$nextTick(() => {
        this.$refs[fileRef].loadFileInfo()
      })
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
    viewQuote  () {
      // 第三方跳转打开标签页
      if (this.approvalFlag && this.urlParams.flag === 'approval') {
        const str = encodeURI(`from=fromFun&funName=bidPriceComparison&formId=${this.form.projectId}`)
        const encodeStr = btoa(str)
        const pathname = window.location.pathname
        const systemUrl = window.location.origin + pathname.substring(0, pathname.length - 1)
        window.open(`${systemUrl}/#/flowTaskViewBase/${encodeStr}`, '_blank')
      } else {
        let tab = {
          component: PriceComparison,
          params: {
            projectId: this.form.projectId,
            tabName: 'PriceComparison' + this.form.projectId
          },
          title: this.form.extProjectNo + '-' + '比价表',
          name: 'PriceComparison' + this.form.projectId
        }
        this.$emit('tab-add', tab)
      }
    },
    viewQuoteBy () {
      let idList = []
      if (this.form.caSelectionResults && this.form.caSelectionResults.length > 0) {
        idList = this.form.caSelectionResults.filter(item => item.vendorId).map(item => item.vendorId)
      }
      console.log(idList)
      if (this.approvalFlag && this.urlParams.flag === 'approval') {
        const str = encodeURI(`from=fromFun&funName=bidHistoryCooperation&formId=${idList}`)
        const encodeStr = btoa(str)
        const pathname = window.location.pathname
        const systemUrl = window.location.origin + pathname.substring(0, pathname.length - 1)
        window.open(`${systemUrl}/#/flowTaskViewBase/${encodeStr}`, '_blank')
      } else {
        let tab = {
          component: HistoryCooperationInfo,
          params: {
            idList: idList,
            tabName: 'HistoryCooperationInfo'
          },
          title: this.form.extProjectNo + '-' + '供应商历史合作信息',
          name: 'HistoryCooperationInfo' + this.form.projectId
        }
        console.log(tab,123321)
        this.$emit('tab-add', tab)
      }

    },
    back () {
      let { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('CalibrationApplyList.getQueryData')
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'SOU_CA'
    },
    // 获取ref值
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    initParams () { // 参数
      let params = {}
      for (let key in this.form) {
        params[key] = this.form[key]
      }

      return params
    },
    async getFormDetail  () {
      let transformParams = transformMQL.save('Ca', [this.caId], 'read',
        {
          '*': {},
          'caOrders': {
            '*': {}
          },
          'caSuppliers': {
            '*': {}
          },
          'caSelectionResults': {
            '*': {}
          },
          'sceneFiles': {
            '*': {}
          },
          'caTenderTimes': {
            '*': {}
          },
          'caPrices': {
            '*': {}
          }
        }
      )
      const response = await caHttp.read(transformParams)
      if (response.data.length) {
        this.form = response.data[0]
        // 前端预处理谈判对比数据
        let dataList = this.form.caNegotiateExtend?.data || []
        let titleList = this.form.caNegotiateExtend?.title || []
        let caNegotiateExtendTable = []
        this.caNegotiateExtendTable = []
        this.caNegotiateExtendTableHeader = []
        titleList.forEach((item, index) => {
          let child = { value: item.vendorCode, label: item.vendorName }
          this.caNegotiateExtendTableHeader.push(child)
          dataList.forEach((son, i) => {
            !caNegotiateExtendTable[i] && (caNegotiateExtendTable[i] = {})
            caNegotiateExtendTable[i][item.vendorCode] = son[index]
          })
        })
        for (let item of caNegotiateExtendTable) {
          this.caNegotiateExtendTable.push(item)
        }
        const { caSelectionResults } = this.form
        caSelectionResults.forEach(item => {
          if (item.winRange) {
            item.winRange = item.winRange.toString().split(';')
          } else {
            item.winRange = []
          }
        })
        this.loadFileInfo()
        // 获取编排过的中标范围选项
        this.getWinOptions()
      }
    },
    async getWinOptions () {
      let { projectId, applicantNo } = this.form
      if (!projectId) return
      const response = await bidBuyerHttp.init.getRequireInfo(projectId)
      if (response && response.data) {
        let itemList = response.data
        let options = []
        // 中标范围取值逻辑：非合并取招标询价单的名称字段，合并的取包名
        // if (this.form.applicantNo.includes(';')) { // 合并申请单号
        //   let packList = itemList.filter(item => item.extPackageName).map(item => item.extPackageName)
        //   // 包名去重
        //   packList = Array.from(new Set(packList))
        //   options = packList.map(item => ({
        //     value: item.extPackageName,
        //     label: item.extPackageName
        //   }))
        // } else { // 非合并
        options = itemList.map(item => ({
          value: item.itemDesc,
          label: item.itemDesc,
          id: item.souItemId // 报价信息行id
        }))
        // }
        this.winOptions = options
      }
    },
    async validBill () {
      return new Promise(async (resolve) => {
        let validForm
        await this.$refs.form.validate(valid => { validForm = valid })
        // 其它待校验组件
        resolve(validForm)
      })
    },
    async saveBill (type) {
      let copyForm = JSON.parse(JSON.stringify(this.form))
      const { caSelectionResults } = copyForm
      caSelectionResults.forEach(item => {
        if (item.winRange) {
          item.winRange = item.winRange.join(';')
        }
      })
      copyForm.caNegotiateExtend.data = []
      copyForm.caNegotiateExtend.title.forEach(item => {
        this.caNegotiateExtendTable.forEach((child, i) => {
          !copyForm.caNegotiateExtend.data[i] && (copyForm.caNegotiateExtend.data[i] = [])
          copyForm.caNegotiateExtend.data[i].push(child[item.vendorCode])
        })
      })
      if (type === 'SUBMIT') {
        if (this.form.ifWrite === 'Y') { // 不是BMP审批通过后回写，才去校验这些
          for (let item of caSelectionResults) {
            if (!item.isWin) {
              return this.$message.warning('供应商选定结果-是否中标不能为空')
            }
            if (item.isWin === 'Y' && !item.winRange) {
              return this.$message.warning('供应商选定结果-中标范围不能为空')
            }
            if (!item.winReason) {
              return this.$message.warning('供应商选定结果-中/落标原因不能为空')
            }
          }
          // 供应商选定结果 - 至少需要选中一个中标供应商
          if (caSelectionResults.every(item => item.isWin === 'N')) {
            return this.$message.warning('供应商选定结果-至少需要有一家中标供应商')
          }
        }
        // 非询比价才去校验
        if (this.form.extSouProcess !== 'INQUIRY') {
          for (let item of copyForm.caSuppliers) {
            if (!item.comprehensiveEvaluation) {
              return this.$message.warning('供应商总体情况-综合评定不能为空')
            }
          }
        }
        if (!await this.validBill()) {
          return this.$message.warning('请填写完必填项')
        }
      }
      let transformParams = transformMQL.save('Ca', [copyForm], type === 'SAVE' ? 'save' : 'submit')
      const response = type === 'SAVE' ? await caHttp.save(transformParams) : await caHttp.submit(transformParams)
      this.caId = this.form.caId = response.data[0].caId
      await this.getFormDetail()
      this.$message.success(this.$t('common.successSave'))
      if (type === 'SUBMIT') {
        await this.handlerAfter(type)
      }
    }
  }
}
</script>
