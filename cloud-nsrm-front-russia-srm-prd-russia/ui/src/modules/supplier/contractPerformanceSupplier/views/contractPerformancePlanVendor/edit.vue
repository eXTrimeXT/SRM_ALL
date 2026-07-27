<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <el-collapse v-model="colValue">
        <el-collapse-item title="基础信息" name="1">
          <el-form ref="form" :model="form">
            <srm-row>
              <srm-col>
                <el-form-item
                  :label="$t('合同序号')"
                  prop="contractNo"
                  :rules="[{ required: true, message: $t('contract_mod.required') }]"
                >
                  <QuickSearch
                    :show-input="form.contractNo"
                    show-key="contractNo"
                    :scope-data="form"
                    name="queryPerformContract"
                    :disabled="disabledFlag"
                    @close-quicksearch="writeBackContract"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  label="里程碑模板编号"
                  prop="processNum"
                  :rules="[{ required: true, message: $t('contract_mod.required') }]"
                >
                  <QuickSearch
                    :show-input="form.processNum"
                    show-key="processNum"
                    :scope-data="form"
                    name="scc_cont_per_templ_head"
                    :disabled="disabledFlag"
                    @close-quicksearch="processNumClose"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item label="里程碑模板名称" prop="templateName">
                  <el-input v-model="form.templateName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('common.vendorName')" prop="vendorName">
                  <el-input v-model="form.vendorName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('contract_mod.contractType')" prop="contractClass">
                  <dict-select v-model="form.contractClass" disabled code="ELEM_CONTRACT_TYPE" />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('bid_mod.businessEntity')" prop="buName">
                  <el-input v-model="form.buName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item label="合同履约单号" prop="perPlanNo">
                  <el-input v-model="form.perPlanNo" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('状态')" prop="status">
                  <dict-select v-model="form.status" disabled code="CONTRACT_PLAN_STATUS" />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('合同总金额（含税）')" prop="includeTaxAmount">
                  <el-input v-model="form.includeTaxAmount" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('币种')" prop="currencyName">
                  <el-input v-model="form.currencyName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('common.creator')" prop="createdFullName">
                  <el-input v-model="form.createdFullName" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item :label="$t('common.creationTime')" prop="creationDate">
                  <el-date-picker v-model="form.creationDate" disabled />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>
        <el-collapse-item title="合同明细" name="2">
          <IOrderDetail :data="perPlanDetailList" />
        </el-collapse-item>
        <el-collapse-item title="里程碑" name="3">
          <IPerformPlan
            ref="perform"
            :data="perPlanMilestoneList"
            :dict-class="dictClass"
            :mode="urlParams.flag"
            :disabled="disabledFlag"
            @handover="handover"
          />
        </el-collapse-item>
        <el-collapse-item title="付款计划" name="4">
          <el-button type="primary" :disabled="disabledFlag" @click="payAdd">
            新增
          </el-button>
          <IPayformPlan
            ref="payform"
            class="mt-10"
            :data="perPayPlanList"
            :dict-class="dictClass"
            :disabled="disabledFlag"
            :mode="mode"
            :milestoneTypeList="milestoneTypeList"
            @setAmount="setPayAmount"
            @delete="deletePay"
          />
        </el-collapse-item>
      </el-collapse>
    </el-main>
    <CToolbar>
      <div slot="right">
        <el-button @click="back">
          取消
        </el-button>
      </div>
    </CToolbar>
    <IFileList :id="fileRow.perPlanMilestoneId" :show.sync="fileShow" />
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import validate from 'lib@/mixins/validate'
import IPerformPlan from '../components/i-perform-plan.vue'
import IPayformPlan from '../components/i-payform-plan.vue'
import IOrderDetail from '../components/i-order-detail.vue'
import { createDictClass } from '@/library/utils/dict/dict-utils'
import CToolbar from 'lib@/components/c-toolbar'
import IFileList from 'modb@/contractPerformance/views/components/i-file-list.vue'
export default {
  name: 'ContractPerformancePlanDetail',
  components: {
    QuickSearch,
    OrganizationSelector,
    IPerformPlan,
    IPayformPlan,
    IOrderDetail,
    CToolbar,
    IFileList
  },
  mixins: [tabTodoMixin, validate],
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
    milestoneTypeList () {
      let list = []
      for (let item of this.perPlanMilestoneList) {
        for (let innerItem of this.MILESTONE_SCHEDULE) {
          if (item.milestoneType === innerItem.value) {
            list.push({
              value: item.milestoneType,
              label: innerItem.label,
              id: item.performTemplLineId
            })
          }
        }
      }
      return list
    }
  },
  created () {
    console.log('dictClass', this.dictClass)
    let { flag, row } = this.urlParams
    if (row.perPlanId) {
      this.perPlanId = row.perPlanId
      this.getFormDetail(row.perPlanId)
    }
  },
  methods: {
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
        performTemplLineId: null
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
            name: 'contractPerformanceCheckVendor',
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
    async writeBackContract (data) {
      this.form.contractNo = data.contractNo
      if (!data || !data.contractClass) return
      const res = await this.$api.cmPerform.vendor.plan.performPlan.getPerPlanByContractNo(data.contractNo)
      const { perPlanDetailList, perPlanMilestoneList, perPayPlanList, ...rest } = res.data
      let { processNum, templateName, status, ...restCopy } = rest
      Object.assign(this.form, restCopy)
      this.perPlanDetailList = perPlanDetailList
    },
    async processNumClose (node) {
      console.log('node:::', node)
      let attrs = ['processNum', 'templateName', 'perTemplHeadId']
      for (let key of attrs) {
        this.form[key] = node ? node[key] : null
      }
      this.form.performTemplHeadId = this.form.perTemplHeadId
      if (!node || !node.perTemplHeadId) return
      const res = await this.$api.cmPerform.vendor.plan.performPlan.getPerOrderPlan(node.perTemplHeadId)
      this.perPlanMilestoneList = res.data
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
      console.log('params:::', params)
      return params
    },
    async saveBill (type) {
      console.log('type:::', type)
      let params = this.initParams()
      let { saveOrUpdate } = this.$api.cmPerform.vendor.plan.performPlan
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
        let perFlag = true; let payFlag = true // 校验履约计划和开票计划
        if (!perPlanMilestoneList.length) perFlag = false
        let validAttrPer = ['nodePersonName', 'planStartDate', 'planEndDate']
        for (let item of perPlanMilestoneList) {
          for (let key of validAttrPer) {
            if (!item[key]) {
              perFlag = false
              break
            }
          }
          if (new Date(item.planStartDate).getTime() > new Date(item.planEndDate).getTime()) {
            perFlag = false
            break
          }
        }
        if (!perFlag) {
          this.__jump_error__('perform')
          return
        }
        if (!perPayPlanList.length) payFlag = false
        let validAttrPay = ['milestoneType', 'nodePersonName', 'paymentStage', 'payExplain', 'payMethod', 'paymentRatio', 'palnPaymentDate']
        let totalPercent = 0
        for (let item of perPayPlanList) {
          for (let key of validAttrPay) {
            if (!item[key]) {
              payFlag = false
              break
            }
          }
          totalPercent += item.paymentRatio
        }
        if (!payFlag) {
          this.__jump_error__('payform')
          return
        }
        if (totalPercent.toString() !== '100') {
          this.__jump_error__('payform', 'component', '计划开票比例之后必须是100')
          return
        }
        saveMethods(params).then(async (res) => {
          if (res.code == '0') {
            this.$message.success(res.message)
          }
          this.curAction = 'approval'
          if (this.perPlanId) await this.workflowActive()
          await this.getFormDetail(res.data)
          await this.handlerAfter(type)
        })
      }
    },
    async getFormDetail (id) {
      const res = await this.$api.cmPerform.vendor.plan.performPlan.getPerOrderById(id)
      const { perPlanDetailList, perPlanMilestoneList, perPayPlanList, ...rest } = res.data
      Object.assign(this.form, rest)
      this.perPlanId = this.form.perPlanId
      this.perPlanDetailList = perPlanDetailList
      this.perPlanMilestoneList = perPlanMilestoneList
      this.perPayPlanList = perPayPlanList
      this.perPayPlanList.forEach(item => (item.payExplain = Number(item.payExplain)))
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
