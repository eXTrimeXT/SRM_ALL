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
                    :rules="[{ required: true, message: $t('contract_mod.required') }]"
                  >
                    <quick-search
                      :show-input="form.contractNo"
                      show-key="contractNo"
                      :scope-data="form"
                      :selectClearable="false"
                      name="queryPerformContract"
                      :disabled="disabledFlag"
                      @close-quicksearch="writeBackContract"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('contract_mod.processNum')"
                    prop="processNum"
                    :rules="[{ required: true, message: $t('contract_mod.required') }]"
                  >
                    <quick-search
                      :show-input="form.processNum"
                      show-key="processNum"
                      :scope-data="form"
                      name="scc_cont_per_templ_head"
                      :disabled="disabledFlag || !form.contractNo"
                      :pre-query-data="{'t.CONTRACT_TYPE':form.contractClass}"
                      @close-quicksearch="processNumClose"
                    />
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
                    :label="$t('contract_mod.perOrderNo')"
                    prop="perPlanNo"
                  >
                    <el-input
                      v-model="form.perPlanNo"
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
                      code="CONTRACT_PLAN_STATUS"
                    />
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
          <el-collapse-item :title="$t('other.key14')" name="2">
            <i-order-detail :data="perPlanDetailList" />
          </el-collapse-item>
          <el-collapse-item :title="$t('contractMod.milestone')" name="3">
            <i-perform-plan
              ref="perform"
              :data="perPlanMilestoneList"
              :dict-class="dictClass"
              :mode="urlParams.flag"
              :disabled="disabledFlag"
              @handover="handover"
            />
          </el-collapse-item>
          <el-collapse-item :title="$t('route.purPaymentPlan')" name="4">
            <el-button type="primary" :disabled="disabledFlag" @click="payAdd">
              {{ $t('bidMod.affairsIncreased') }}
            </el-button>
            <i-payform-plan
              ref="payform"
              class="mt-10"
              :data="perPayPlanList"
              :dict-class="dictClass"
              :disabled="disabledFlag"
              :mode="mode"
              :milestoneTypeList="milestoneTypeList"
              @payment="payment"
              @advanceApply="advanceApply"
              @paymentApply="paymentApply"
              @setAmount="setPayAmount"
              @delete="deletePay"
            />
          </el-collapse-item>
        </el-collapse>
      </CWorkflowMulti>
    </el-main>
    <i-file-list :id="fileRow.perPlanMilestoneId" :show.sync="fileShow" />
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import validate from 'lib@/mixins/validate'
import WorkflowCommon from '@/library/mixins/workflow-common'
import IPerformPlan from '../components/i-perform-plan.vue'
import IPayformPlan from '../components/i-payform-plan.vue'
import IOrderDetail from '../components/i-order-detail.vue'
import { createDictClass } from '@/library/utils/dict/dict-utils'
import IFileList from '../components/i-file-list.vue'
import advancePaymentDetail from 'modb@/purSettlement/views/advancePayment/advancePaymentDetail'
import paymentPlanDetail from 'modb@/purSettlement/views/purPaymentApply/paymentPlanDetail'
export default {
  name: 'ContractPerformancePlanDetail',
  components: {
    QuickSearch,
    OrganizationSelector,
    IPerformPlan,
    IPayformPlan,
    IOrderDetail,
    IFileList
  },
  mixins: [tabTodoMixin, validate, WorkflowCommon],
  data () {
    return {
      dictClass: createDictClass({
        MILESTONE_SCHEDULE: [], // 里程碑名称
        ELEM_CONTRACT_TYPE: [], // 合同类型
        CONTRACT_PLAN_STATUS: [], // 履约状态
        MILESTONE_STATE: [] // 里程碑列表状态
      }),
      colValue: ['1', '2', '3', '4'],
      curRole: this.$store.getters.userType,
      curAction: '', // 判断审批流页签是否可选 approval no-approval
      inputFormat: { type: 'float', digits: 2, negative: false, zero: false },
      form: {
        contractNo: null,
        vendorName: null,
        contractClass: null,
        buName: null,
        status: 'DRAFT',
        perPlanNo: null,
        createdFullName: null,
        includeTaxAmount: null,
        currencyName: null,
        creationDate: null,
        processNum: null,
        templateName: null,
        perTemplHeadId: null,
        performTemplHeadId: null
      },
      perPlanDetailList: [], // 合同明细
      perPlanMilestoneList: [], // 合同履约计划
      perPayPlanList: [], // 合同履约开票计划
      mode: '',
      perPlanId: '',
      fileShow: false,
      fileRow: {}
    }
  },
  computed: {
    urlParams () {
      return this.$attrs.params || {}
    },
    disabledFlag () {
      return !!['view', 'approval', 'manage'].includes(this.urlParams.flag)
    },
    MILESTONE_SCHEDULE () {
      return this.dictClass.getDict('MILESTONE_SCHEDULE')
    },
    viewUpdateButton () { // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
      return ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(this.form.status)
    },
    disabledUpdateButton () {
      return ['APPROVING'].includes(this.form.status)
    },
    workflowBusinessId () { // 用来指定工作流的业务ID 单据ID 注意每一个单的ID不一样
      return this.perPlanId || null
    },
    workflowTabDisabled () {
      if (this.curAction) return this.curAction !== 'approval'
      return this.form.status === 'DRAFT'
    },
    milestoneTypeList () {
      let list = []
      for (let item of this.perPlanMilestoneList) {
        for (let innerItem of this.MILESTONE_SCHEDULE) {
          if (item.milestoneType === innerItem.value) {
            list.push({
              value: item.milestoneType,
              label: innerItem.label,
              id: item.performTemplLineId,
              perPlanMilestoneId: item.perPlanMilestoneId // 里程碑id
            })
          }
        }
      }
      return list
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
    let { flag, row } = this.urlParams
    if (row.perPlanId) {
      this.perPlanId = row.perPlanId
      this.getFormDetail(row.perPlanId)
    }
    this.getButtonConfig()
  },
  methods: {
    paymentApply (id) {
      this.$emit('tab-add', {
        component: paymentPlanDetail,
        params: {
          flag: 'readOnly',
          showType: 'approveNumber',
          paymentApplyId: id,
          tabName: 'paymentPlanDetail' + id
        },
        title: id,
        name: 'paymentPlanDetail' + id
      })
    },
    advanceApply (id) {
      this.$emit('tab-add', {
        component: advancePaymentDetail,
        params: {
          flag: 'readOnly',
          row: {
            advanceApplyId: id
          },
          showType: 'approveNumber',
          tabName: 'advancePaymentDetail' + id
        },
        title: id,
        name: 'advancePaymentDetail' + id
      })
    },
    setPayAmount (row) {
      if (!this.form.includeTaxAmount) return
      row.stagePaymentAmount = Math.round((row.paymentRatio / 100) * this.form.includeTaxAmount * 100) / 100
    },
    deletePay (scope) {
      this.perPayPlanList.splice(scope.$index, 1)
    },
    payAdd () {
      this.perPayPlanList.push({
        milestoneType: '',
        nodePersonName: '',
        nodePersonId: '',
        nodePersonBy: '',
        paymentStage: '',
        payExplain: '',
        payMethod: '',
        paymentRatio: '',
        stagePaymentAmount: '',
        palnPaymentDate: null,
        planInvoiceCompleteDate: null,
        paymentNode: '',
        paymentApplyNo: '',
        performTemplLineId: null,
        perPlanMilestoneId: null
      })
    },
    handover (row, type) { // type 1.deliver 交付 2.file 附件
      if (type === 'deliver') {
        this.$confirm('确认要生成合同验收单么？', {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        }).then(() => {
          this.$router.push({
            name: 'contractPerformanceCheck',
            params: {
              from: 'contractPerformancePlan',
              row
            }
          })
        }).catch(() => {})
      }
      if (type === 'file') {
        this.fileShow = true
        this.fileRow = row
      }
    },
    payment (row) {
      let con = null
      if (row.paymentStage == 'ADVANCE_CHARGE') {
        con = 'advancePayment'
      } else {
        con = 'purPaymentApply'
      }
      this.$router.push({
        name: con,
        params: {
          from: 'contractPerformancePlan',
          row: row,
          form: this.form
        }
      })
    },
    async writeBackContract (data) {
      if (!data || !data.contractNo) return
      this.form = {
        contractNo: null,
        vendorName: null,
        contractClass: null,
        buName: null,
        status: 'DRAFT',
        perPlanNo: null,
        createdFullName: null,
        includeTaxAmount: null,
        currencyName: null,
        creationDate: null,
        processNum: null,
        templateName: null,
        perTemplHeadId: null,
        performTemplHeadId: null
      }
      this.form.contractNo = data.contractNo
      const res = await this.$api.cmPerform.buyer.main.performPlan.getPerPlanByContractNo(data.contractNo)
      const { perPlanDetailList, perPlanMilestoneList, perPayPlanList, ...rest } = res.data
      let { processNum, templateName, status, ...restCopy } = rest
      Object.assign(this.form, restCopy)
      this.perPlanDetailList = perPlanDetailList
    },
    async processNumClose (node) {
      let attrs = ['processNum', 'templateName', 'perTemplHeadId']
      for (let key of attrs) {
        this.form[key] = node ? node[key] : null
      }
      this.form.performTemplHeadId = this.form.perTemplHeadId
      if (!node || !node.perTemplHeadId) return
      const res = await this.$api.cmPerform.buyer.main.performPlan.getPerOrderPlan(node.perTemplHeadId)
      this.perPlanMilestoneList = res.data
      this.perPayPlanList = []
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
      this.__setTabTodo('ContractPerformancePlanList.getQuerydata')
    },
    initParams () { // 参数
      let params = {}
      for (let key in this.form) {
        params[key] = this.form[key]
      }
      params.perPlanDetailList = this.perPlanDetailList // 合同明细
      params.perPlanMilestoneList = this.perPlanMilestoneList// 合同履约计划
      params.perPayPlanList = this.perPayPlanList// 合同履约开票计划
      return params
    },
    async saveBill (type) {
      let params = this.initParams()
      let { saveOrUpdate } = this.$api.cmPerform.buyer.main.performPlan
      let saveMethods = saveOrUpdate
      if (type === 'SAVE') {
        saveMethods(params).then((res) => {
          if (res.code == '0') {
            this.$message.success(res.message)
          }
          this.getFormDetail(res.data)
        })
      } else if (type === 'SUBMIT') {
        let validForm
        this.$refs.form.validate(valid => (validForm = valid))
        if (!validForm) {
          this.__focus_error__()
          return
        }
        let { perPlanMilestoneList, perPayPlanList } = params
        if (!perPlanMilestoneList.length) {
          this.jumpErrorPer('请填写里程碑相关信息')
          return
        }
        for (let item of perPlanMilestoneList) {
          if (!item.nodePersonName) {
            this.jumpErrorPer('里程碑---节点负责人必填')
            return
          }
          if (!item.planStartDate) {
            this.jumpErrorPer('里程碑---计划开始时间必填')
            return
          }
          if (!item.planEndDate) {
            this.jumpErrorPer('里程碑---计划结束时间必填')
            return
          }
          if (new Date(item.planStartDate).getTime() > new Date(item.planEndDate).getTime()) {
            this.jumpErrorPer('里程碑---计划完成时间必须晚于里程碑计划开始时间')
            return
          }
        }
        if (!perPayPlanList.length) {
          this.jumpErrorPay('请填写付款计划相关信息')
          return
        }
        let totalPercent = 0; let milestoneTypeArr = []
        for (let item of perPayPlanList) {
          if (!item.milestoneType) {
            this.jumpErrorPay('付款计划---里程碑名称必填')
            return
          }
          if (!item.nodePersonName) {
            this.jumpErrorPay('付款计划---节点负责人必填')
            return
          }
          if (!item.paymentStage) {
            this.jumpErrorPay('付款计划---付款阶段必填')
            return
          }
          if (!item.payExplain) {
            this.jumpErrorPay('付款计划---付款条件必填')
            return
          }
          if (!item.payMethod) {
            this.jumpErrorPay('付款计划---付款方式必填')
            return
          }
          if (!item.paymentRatio) {
            this.jumpErrorPay('付款计划---付款比例必填')
            return
          }
          if (!item.palnPaymentDate) {
            this.jumpErrorPay('付款计划---计划开始时间必填')
            return
          }
          if (!item.planInvoiceCompleteDate) {
            this.jumpErrorPay('付款计划---计划结束时间必填')
            return
          }
          if (new Date(item.palnPaymentDate).getTime() > new Date(item.planInvoiceCompleteDate).getTime()) {
            this.jumpErrorPay('付款计划---计划结束时间必须晚于计划开始时间')
            return
          }
          totalPercent += item.paymentRatio
          milestoneTypeArr.push(item.milestoneType)
        }
        let typeArr = milestoneTypeArr.reduce((pre, cur) => {
          if (cur in pre) {
            pre[cur]++
          } else {
            pre[cur] = 1
          }
          return pre
        }, {})
        for (let key in typeArr) {
          if (typeArr[key] > 1) {
            this.jumpErrorPay('付款计划---里程碑重复，请修改')
            return
          }
        }
        if (totalPercent.toString() !== '100') {
          this.jumpErrorPay('付款比例相加应该等于100')
          return
        }
        saveMethods(params).then(async (res) => {
          this.curAction = 'approval'
          if (res.code == '0') {
            this.$message.success(res.message)
          }
          await this.getFormDetail(res.data)
          await this.handlerAfter(type)
        })
      }
    },
    async getFormDetail (id) {
      const res = await this.$api.cmPerform.buyer.main.performPlan.getPerOrderById(id)
      const { perPlanDetailList, perPlanMilestoneList, perPayPlanList, ...rest } = res.data
      Object.assign(this.form, rest)
      this.perPlanId = this.form.perPlanId
      this.perPlanDetailList = perPlanDetailList
      this.perPlanMilestoneList = perPlanMilestoneList
      this.perPayPlanList = perPayPlanList
      this.perPayPlanList.forEach(item => (item.payExplain = Number(item.payExplain)))
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'PERFORM_PLAN'
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
    },
    jumpErrorPer (message) {
      this.__jump_error__('perform', 'component', message)
    },
    jumpErrorPay (message) {
      this.__jump_error__('payform', 'component', message)
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
