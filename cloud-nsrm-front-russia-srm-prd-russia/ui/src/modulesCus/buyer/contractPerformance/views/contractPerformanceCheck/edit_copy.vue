<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <CWorkflowMulti
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
      >
        <el-collapse v-model="colValue">
          <el-collapse-item :title="$t('supRisk.baseInfo')" name="1">
            <el-form
              ref="form"
              :model="form"
            >
              <srm-row :gutter="32">
                <srm-col>
                  <el-form-item
                    :label="$t('合同序号')"
                    prop="contractNo"
                  >
                    <el-input v-model="form.contractNo" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('contract_mod.processNum')"
                    prop="processNum"
                  >
                    <el-input v-model="form.processNum" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('contract_mod.templateName')"
                    prop="templateName"
                  >
                    <el-input
                      v-model="form.templateName"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('common.vendorName')"
                    prop="vendorName"
                  >
                    <el-input
                      v-model="form.vendorName"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('contract_mod.contractType')"
                    prop="contractClass"
                  >
                    <dict-select
                      v-model="form.contractClass"
                      disabled
                      code="ELEM_CONTRACT_TYPE"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('bid_mod.businessEntity')"
                    prop="buName"
                  >
                    <el-input
                      v-model="form.buName"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    label="合同验收单号"
                    prop="perAcceptanceNo"
                  >
                    <el-input
                      v-model="form.perAcceptanceNo"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('状态')"
                    prop="status"
                  >
                    <dict-select
                      v-model="form.status"
                      disabled
                      code="CONTRACT_CHECK_STATUS"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item prop="perPlanNo" label="合同履约计划单号">
                    <el-input v-model="form.perPlanNo" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('合同总金额（含税）')"
                    prop="includeTaxAmount"
                  >
                    <el-input
                      v-model="form.includeTaxAmount"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('币种')"
                    prop="currencyName"
                  >
                    <el-input
                      v-model="form.currencyName"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('common.creator')"
                    prop="createdFullName"
                  >
                    <el-input
                      v-model="form.createdFullName"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('common.creationTime')"
                    prop="creationDate"
                  >
                    <el-date-picker
                      v-model="form.creationDate"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
          </el-collapse-item>
          <el-collapse-item ref="perPlanMilestone" :title="$t('contractMod.milestone')" name="2">
            <el-table border stripe :data="perPlanMilestoneList">
              <el-table-column prop="serialNumber" :label="$t('components.processTable.headers.fdNodeName')" width="50" />
              <el-table-column prop="milestoneType" :label="$t('contract_mod.processNodeName')" min-width="130" show-overflow-tooltip>
                <template slot-scope="scope">
                  {{ dictClass.getDictLabel('MILESTONE_SCHEDULE',scope.row.milestoneType) }}
                </template>
              </el-table-column>
              <el-table-column prop="nodePersonName" label="节点负责人" min-width="120" show-overflow-tooltip />
              <el-table-column prop="planStartDate" :label="$t('perfMod.planStartDate')" min-width="130" show-overflow-tooltip />
              <el-table-column prop="planEndDate" label="计划结束时间" min-width="130" show-overflow-tooltip />
              <el-table-column prop="nodePlanNum" label="节点交付数量" min-width="130" show-overflow-tooltip>
                <template slot-scope="scope">
                  <el-input v-model="scope.row.nodePlanNum" :disabled="disabledFlag || ['SUPPLIER_SUBMITTED','FIRST_PASS'].includes(form.status) || form.dataCreationType === 'VENDOR'" />
                </template>
              </el-table-column>
              <el-table-column prop="practicallyEndDate" min-width="150">
                <template slot="header">
                  <i class="toRequired">*</i>
                  实际结束时间
                </template>
                <template slot-scope="scope">
                  <el-date-picker v-model="scope.row.practicallyEndDate" :disabled="disabledFlag || ['SUPPLIER_SUBMITTED','FIRST_PASS'].includes(form.status) || form.dataCreationType === 'VENDOR'" :placeholder="$t('purchaseDemand.datePicker')" value-format="yyyy-MM-dd" />
                </template>
              </el-table-column>
              <el-table-column prop="remarks" label="特殊备注" min-width="150" />
              <el-table-column prop="fileId" :label="$t('dataConfMod.attachmentTemplate')" min-width="150" show-overflow-tooltip>
                <template slot-scope="scope">
                  <SrmCommonFile
                    :default-file="{
                      fileId: scope.row.fileId,
                      fileName: scope.row.fileName
                    }"
                    :readonly="true"
                  />
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
          <el-collapse-item ref="perAcceptance" title="履约过程评价" name="3">
            <i-field-view
              ref="perAcceptanceConf"
              :data="perAcceptanceConfList"
              :disabled="disabledFlag"
            />
          </el-collapse-item>
          <el-collapse-item ref="delivery" name="4">
            <template slot="title">
              <i class="toRequired">*</i>
              交付说明
            </template>
            <el-input
              v-model="form.deliveryExplain"
              type="textarea"
              :disabled="disabledFlag"
              :rows="3"
              :maxlength="300"
              show-word-limit
            />
          </el-collapse-item>
          <el-collapse-item :title="$t('accountMod.relevantAttachment')" name="5">
            <el-button type="primary" :disabled="disabledFlag" @click="fileAdd">
              {{ $t('bidMod.affairsIncreased') }}
            </el-button>
            <el-table
              class="mt-10"
              :data="fileData"
              border
              stripe
            >
              <el-table-column
                type="index"
                width="60"
                :label="$t('contractMod.order')"
              />
              <el-table-column
                :label="$t('contractMod.addUploadFile')"
              >
                <template slot-scope="scope">
                  <SrmCommonFile
                    :extra-data="fileInfo"
                    :default-file="{
                      fileId: scope.row.fileId,
                      fileName: scope.row.fileName
                    }"
                    :readonly="disabledFlag && scope.row.uploadType === 'BUYER'"
                    @on-change="({file}) => fileSuccess(file,scope.row)"
                  />
                </template>
              </el-table-column>
              <el-table-column
                prop="createdUserName"
                :label="$t('quota.uploadBy')"
              />
              <el-table-column
                prop="creationDate"
                :label="$t('components.fileupload.uploadDate')"
              />
              <el-table-column
                :label="$t('formula.handle')"
                width="100"
              >
                <template slot-scope="scope">
                  <el-button type="text" :disabled="disabledFlag || scope.row.uploadType !== 'BUYER'" @click="deleteFile(scope)">
                    {{ $t('components.common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
        </el-collapse>
      </CWorkflowMulti>
    </el-main>
    <c-toolbar v-if="['SUPPLIER_SUBMITTED'].includes(form.status) && !disabledFlag">
      <div slot="right">
        <el-button @click="back">
          {{ $t('bidMod.cancel') }}
        </el-button>
        <el-button type="primary" @click="handleControl('PASS')">
          {{ $t('common.toApprove') }}
        </el-button>
        <el-button type="primary" @click="handleControl('REJECT')">
          {{ $t('orderMod.buyerOrderSynergy.sureRefuse') }}
        </el-button>
      </div>
    </c-toolbar>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import validate from 'lib@/mixins/validate'
import WorkflowCommon from '@/library/mixins/workflow-common'
import IPayformPlan from '../components/i-payform-plan.vue'
import IOrderDetail from '../components/i-order-detail.vue'
import IFieldView from '../components/i-field-view.vue'
import { createDictClass } from '@/library/utils/dict/dict-utils'
import CToolbar from 'lib@/components/c-toolbar'
export default {
  name: 'ContractPerformanceCheckDetail',
  components: {
    QuickSearch,
    OrganizationSelector,
    CToolbar,
    IPayformPlan,
    IOrderDetail,
    IFieldView
  },
  mixins: [tabTodoMixin, validate, WorkflowCommon],
  data () {
    return {
      dictClass: createDictClass({
        MILESTONE_SCHEDULE: [] // 里程碑名称
      }),
      colValue: ['1', '2', '3', '4', '5'],
      curRole: this.$store.getters.userType,
      curAction: '', // 判断审批流页签是否可选 approval no-approval
      inputFormat: { type: 'float', digits: 2, negative: false, zero: false },
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'contractPerformanceCheck',
        fileType: 'images'
      },
      form: {
        perAcceptanceId: null,
        contractNo: null,
        processNum: null,
        templateName: null,
        vendorName: null,
        contractClass: null,
        buName: null,
        perAcceptanceNo: null,
        status: 'DRAFT',
        perPlanNo: null,
        includeTaxAmount: null,
        currencyName: null,
        createdFullName: null,
        creationDate: null,
        deliveryExplain: null,
        dataCreationType: 'BUYER' // 供应商填写 VENDOR 采购商填写 BUYER
      },
      perPlanMilestoneList: [], // 合同里程碑
      perAcceptanceConfList: [], // 履约过程评价
      fileData: [],
      mode: '',
      perAcceptanceId: ''
    }
  },
  computed: {
    urlParams () {
      return this.$attrs.params || {}
    },
    disabledFlag () {
      return !!['view', 'approval'].includes(this.urlParams.flag)
    },
    viewUpdateButton () { // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
      return (!this.disabledFlag && !['APPROVED', 'SUPPLIER_SUBMITTED'].includes(this.form.status))
    },
    disabledUpdateButton () {
      return ['APPROVING'].includes(this.form.status)
    },
    workflowBusinessId () { // 用来指定工作流的业务ID 单据ID 注意每一个单的ID不一样
      return this.perAcceptanceId ? this.perAcceptanceId : null
    },
    workflowTabDisabled () {
      if (this.curAction) return this.curAction !== 'approval'
      return ['DRAFT', 'SUPPLIER_SUBMITTED', 'FIRST_PASS'].includes(this.form.status)
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
    }
  },
  created () {
    console.log('dictClass', this.dictClass)
    let { flag, row } = this.urlParams
    console.log('row:::', row)
    if (row.perAcceptanceId) {
      this.perAcceptanceId = row.perAcceptanceId
      this.getFormDetail(row.perAcceptanceId)
    } else if (row.perPlanMilestoneId) {
      this.getFormDetailByPlanId(row.perPlanMilestoneId)
    }
    this.getButtonConfig()
  },
  methods: {
    fileAdd () {
      this.fileData.push({
        fileId: '',
        fileName: '',
        uploadType: 'BUYER' // BUYER 采购商 VENDOR 供应商
      })
    },
    deleteFile (scope) {
      let { row, $index } = scope
      this.fileData.splice($index, 1)
    },
    fileSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileId = fileId.toString()
      row.fileName = fileName
    },
    getButtonConfig () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.cancel.view = !this.disabledFlag
      this.buttonConfigInfo.close.view = this.disabledFlag
    },
    back () {
      let { tabName } = this.$attrs.params
      this.$emit('tab-remove', tabName)
      this.__setTabTodo('ContractPerformanceCheckList.getQuerydata')
    },
    initParams () { // 参数
      let params = {}
      for (let key in this.form) {
        params[key] = this.form[key]
      }
      params.perPlanMilestoneList = this.perPlanMilestoneList
      params.perAcceptanceConfList = this.perAcceptanceConfList // 履约过程评价
      params.perAcceptanceAttList = this.fileData// 相关附件
      console.log('params:::', params)
      return params
    },
    async saveBill (type) {
      console.log('type:::', type)
      let params = this.initParams()
      let { addOrUpdate } = this.$api.cmPerform.buyer.main.performAcceptance
      let saveMethods = addOrUpdate
      if (type === 'SAVE') {
        saveMethods(params).then((res) => {
          this.getFormDetail(res.data)
        })
      } else if (type === 'SUBMIT') {
        for (let item of this.perPlanMilestoneList) {
          if (!item.practicallyEndDate) {
            this.__jump_error__('perPlanMilestone', 'component', '请填写里程碑---实际结束时间')
            return
          }
        }
        let flag = await this.$refs.perAcceptanceConf.validate()
        if (!flag) {
          this.__jump_error__('perAcceptance', 'component', '请填写履约过程评价必填内容')
          return
        }
        if (!this.form.deliveryExplain) {
          this.__jump_error__('delivery', 'component', '请填写交付说明')
          return
        }
        saveMethods(params).then(async (res) => {
          this.curAction = 'approval'
          await this.getFormDetail(res.data)
          await this.handlerAfter(type)
        })
      }
    },
    async handleControl (type) {
      let params = this.initParams()
      let { approvePass, rejected } = this.$api.cmPerform.buyer.main.performAcceptance
      let saveMethods = type === 'PASS' ? approvePass : rejected
      saveMethods(params).then(res => {
        this.$message.success(res.message)
        this.back()
      })
    },
    async getFormDetail (id) {
      const res = await this.$api.cmPerform.buyer.main.performAcceptance.getByPerAcceptanceId(id)
      const { perPlanMilestoneList, perAcceptanceConfList, perAcceptanceAttList, ...rest } = res.data
      Object.assign(this.form, rest)
      this.perAcceptanceId = this.form.perAcceptanceId
      this.perPlanMilestoneList = perPlanMilestoneList
      this.perAcceptanceConfList = perAcceptanceConfList
      this.fileData = perAcceptanceAttList
    },
    async getFormDetailByPlanId (id) {
      const res = await this.$api.cmPerform.buyer.main.performAcceptance.getByPerPlanMilestoneId(id)
      const { perPlanMilestoneList, perAcceptanceConfList, perAcceptanceAttList, ...rest } = res.data
      Object.assign(this.form, rest)
      this.perPlanMilestoneList = perPlanMilestoneList
      this.perAcceptanceConfList = perAcceptanceConfList
      this.fileData = perAcceptanceAttList
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'PERFORM_ACCEPTANCE'
    },
    // 获取ref值
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    // 定义流程额外变量，如果没有就不用添加这个函数
    async getWorkflowBusinessVariables () {
      return {
        // Amount: this.requirementHead.ceeaTotalBudget
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.mt-10 {
  margin-top: 10px;
  margin-bottom: 10px;
}
</style>
