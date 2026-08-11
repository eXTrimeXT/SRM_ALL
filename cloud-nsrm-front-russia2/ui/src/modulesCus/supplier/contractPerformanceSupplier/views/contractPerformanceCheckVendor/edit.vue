<template>
  <el-container class="flex-container" direction="vertical">
    <el-main>
      <el-collapse v-model="colValue">
        <!-- <el-collapse-item title="基础信息" name="1"> -->
        <el-collapse-item :title="this.$t('common.baseInfo')" name="1">
          <el-form
            ref="form"
            :model="form"
          >
            <srm-row>
              <srm-col>
                <el-form-item
                  :label="$t('bidMod.compactIndex')"
                  prop="contractNo"
                >
                  <el-input v-model="form.contractNo" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- <el-form-item
                  label="里程碑模板编号"
                  prop="processNum"
                > -->
                <el-form-item
                  :label="$t('contract_mod.processNum')"
                  prop="processNum"
                >
                  <el-input v-model="form.processNum" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- <el-form-item
                  label="里程碑模板名称"
                  prop="templateName"
                > -->
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
                <!-- <el-form-item
                  label="合同验收单号"
                  prop="perAcceptanceNo"
                > -->
                <el-form-item
                  :label="$t('bid_mod.perAcceptanceNo')"
                  prop="perAcceptanceNo"
                >
                  <el-input
                    v-model="form.perAcceptanceNo"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- <el-form-item
                  :label="$t('状态')"
                  prop="status"
                > -->
                <el-form-item
                  :label="$t('components.stratProcess.headers.docStatusValue')"
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
                <!-- <el-form-item prop="perPlanNo" label="合同履约计划单号"> -->
                <el-form-item prop="perPlanNo" :label="$t('bid_mod.perPlanNo')">
                  <el-input v-model="form.perPlanNo" disabled />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- <el-form-item
                  :label="$t('合同总金额（含税）')"
                  prop="includeTaxAmount"
                > -->
                <el-form-item
                  :label="$t('purSettlementMod.includeTaxAmount')"
                  prop="includeTaxAmount"
                >
                  <el-input
                    v-model="form.includeTaxAmount"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <!-- <el-form-item
                  :label="$t('币种')"
                  prop="currencyName"
                > -->
                <el-form-item
                  :label="$t('vendorMod.currencyCode')"
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
                    :format="$formatDatePickerTime"
                    disabled
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>
        <!-- <el-collapse-item ref="perPlanMilestone" title="里程碑" name="2"> -->
        <el-collapse-item ref="perPlanMilestone" :title="$t('bid_mod.milestoneType')" name="2">
          <el-table border stripe :data="perPlanMilestoneList">
            <!-- <el-table-column prop="serialNumber" label="节点" width="50" /> -->
            <el-table-column prop="serialNumber" :label="$t('components.processTable.headers.fdNodeName')" width="50" />
            <!-- <el-table-column prop="milestoneType" label="里程碑名称" min-width="130" show-overflow-tooltip> -->
            <el-table-column prop="milestoneType" :label="$t('contract_mod.processNodeName')" min-width="130" show-overflow-tooltip>
              <template slot-scope="scope">
                {{ dictClass.getDictLabel('MILESTONE_SCHEDULE',scope.row.milestoneType) }}
              </template>
            </el-table-column>
            <!--             <el-table-column prop="nodePersonName" label="节点负责人" min-width="120" show-overflow-tooltip />
            <el-table-column prop="planStartDate" label="计划开始时间" :formatter="(row, column, cellValue) => $parseTime(cellValue)" min-width="130" show-overflow-tooltip />
            <el-table-column prop="planEndDate" label="计划结束时间" :formatter="(row, column, cellValue) => $parseTime(cellValue)" min-width="130" show-overflow-tooltip />
            <el-table-column prop="nodePlanNum" label="节点交付数量" min-width="130" show-overflow-tooltip> -->
            <el-table-column prop="nodePersonName" :label="$t('common.nodeLeader')" min-width="120" show-overflow-tooltip />
            <el-table-column prop="planStartDate" :label="$t('vendorMod.planStartDate')" :formatter="(row, column, cellValue) => $parseTime(cellValue)" min-width="130" show-overflow-tooltip />
            <el-table-column prop="planEndDate" :label="$t('perfMod.planEndDate')" :formatter="(row, column, cellValue) => $parseTime(cellValue)" min-width="130" show-overflow-tooltip />
            <el-table-column prop="nodePlanNum" :label="$t('perfMod.numberOfNodesDelivered')" min-width="130" show-overflow-tooltip>
              <template slot-scope="scope">
                <el-input v-model="scope.row.nodePlanNum" :disabled="disabledFlag || form.dataCreationType === 'BUYER'" />
              </template>
            </el-table-column>
            <el-table-column prop="practicallyEndDate" min-width="150">
              <template slot="header">
                <i class="toRequired">*</i>
                <!-- 实际结束时间 -->
                $t('perfMod.actualEndTime')
              </template>
              <template slot-scope="scope">
                <el-date-picker v-model="scope.row.practicallyEndDate" :format="$formatDatePicker" :disabled="disabledFlag || form.dataCreationType === 'BUYER'" :placeholder="$t('vendorMod.relegation.optionDate')" value-format="yyyy-MM-dd" />
              </template>
            </el-table-column>
            <!-- <el-table-column prop="remarks" label="特殊备注" min-width="150" /> -->
            <el-table-column prop="remarks" :label="$t('perfMod.specialRemarks')" min-width="150" />
            <el-table-column prop="fileId" :label="$t('dataConfMod.attachTemplate')" min-width="150" show-overflow-tooltip>
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
        <el-collapse-item title="相关附件" name="3">
          <el-button type="primary" :disabled="disabledFlag" @click="fileAdd">
            <!-- 新增 -->
            {{ $t("common.add") }}
          </el-button>
          <el-table
            class="mt-10"
            :data="fileData"
            border
            stripe
          >
            <!-- <el-table-column
              type="index"
              width="60"
              label="序号"
            /> -->
            <el-table-column
              type="index"
              width="60"
              :label="$t('components.common.sort')"
            />
            <!-- <el-table-column
              label="附件上传"
            > -->
            <el-table-column
              :label="$t('components.upload.fileUpload')"
            >
              <template slot-scope="{row,$index}">
                <SrmCommonFile
                  :extra-data="fileInfo"
                  :default-file="{
                    fileId: row.fileId,
                    fileName: row.fileName
                  }"
                  :readonly="disabledFlag && row.uploadType === 'VENDOR'"
                  @on-change="({file}) => fileSuccess(file,row,$index)"
                />
              </template>
            </el-table-column>
            <!-- <el-table-column
              prop="createdUserName"
              label="上传人"
            /> -->
            <el-table-column
              prop="createdUserName"
              :label="$t('components.fileupload.uploadUserName')"
            />
            <!-- <el-table-column
              prop="creationDate"
              label="上传时间"
              :formatter="(row, column, cellValue) => $parseTime(cellValue)"
            /> -->
            <el-table-column
              prop="creationDate"
              :label="$t('components.fileupload.uploadDate')"
              :formatter="(row, column, cellValue) => $parseTime(cellValue)"
            />
            <!-- <el-table-column
              label="操作"
              width="100"
            > -->
            <el-table-column
              :label="$t('components.headers.operation')"
              width="100"
            >
              <template slot-scope="scope">
                <el-button type="text" :disabled="disabledFlag || scope.row.uploadType !== 'VENDOR'" @click="deleteFile(scope)">
                  <!-- 删除 -->
                  {{ $t("components.common.delete") }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
      </el-collapse>
    </el-main>
    <CToolbar>
      <div slot="right">
        <el-button @click="back">
          <!-- 取消 -->
          {{ $t("components.common.cancel") }}
        </el-button>
        <el-button v-if="!disabledFlag" type="primary" @click="saveBill('SAVE')">
          <!-- 暂存 -->
          {{ $t("common.staging") }}
        </el-button>
        <el-button v-if="!disabledFlag" type="primary" @click="saveBill('SUBMIT')">
          <!-- 提交 -->
          {{ $t("common.submit") }}
        </el-button>
      </div>
    </CToolbar>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
import validate from 'lib@/mixins/validate'
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
  mixins: [tabTodoMixin, validate],
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
        status: null,
        perPlanNo: null,
        includeTaxAmount: null,
        currencyName: null,
        createdFullName: null,
        creationDate: null,
        deliveryExplain: null,
        dataCreationType: 'VENDOR' // 供应商填写 VENDOR 采购商填写 BUYER
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
    }
  },
  created () {
    console.log('dictClass', this.dictClass)
    let { row } = this.urlParams
    if (row.perAcceptanceId) {
      this.perAcceptanceId = row.perAcceptanceId
      this.getFormDetail(row.perAcceptanceId)
    } else if (row.perPlanMilestoneId) {
      this.getFormDetailByPlanId(row.perPlanMilestoneId)
    }
  },
  methods: {
    fileAdd () {
      this.fileData.push({
        fileId: '',
        fileName: '',
        uploadType: 'VENDOR' // BUYER 采购商 VENDOR 供应商
      })
    },
    deleteFile (scope) {
      let { $index } = scope
      this.fileData.splice($index, 1)
    },
    fileSuccess (file, row, index) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileId = fileId.toString()
      row.fileName = fileName
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
      let { addOrUpdate, vendorSubmit } = this.$api.cmPerform.vendor.check.performAcceptance
      let saveMethods = type === 'SAVE' ? addOrUpdate : vendorSubmit
      if (type === 'SAVE') {
        saveMethods(params).then((res) => {
          this.getFormDetail(res.data)
        })
      } else if (type === 'SUBMIT') {
        for (let item of this.perPlanMilestoneList) {
          if (!item.practicallyEndDate) {
            // this.__jump_error__('perPlanMilestone', 'component', '请填写里程碑---实际结束时间')
            this.__jump_error__('perPlanMilestone', 'component', this.$t('cusEntry.supplement20250205.milestoneActualEndTime'))
            return
          }
        }
        saveMethods(params).then(async (res) => {
          this.back()
        })
      }
    },
    async getFormDetail (id) {
      const res = await this.$api.cmPerform.vendor.check.performAcceptance.getByPerAcceptanceId(id)
      const { perPlanMilestoneList, perAcceptanceConfList, perAcceptanceAttList, ...rest } = res.data
      Object.assign(this.form, rest)
      this.perAcceptanceId = this.form.perAcceptanceId
      this.perPlanMilestoneList = perPlanMilestoneList
      this.perAcceptanceConfList = perAcceptanceConfList
      this.fileData = perAcceptanceAttList
    },
    async getFormDetailByPlanId (id) {
      const res = await this.$api.cmPerform.vendor.check.performAcceptance.getByPerPlanMilestoneId(id)
      const { perPlanMilestoneList, perAcceptanceConfList, perAcceptanceAttList, ...rest } = res.data
      Object.assign(this.form, rest)
      this.perPlanMilestoneList = perPlanMilestoneList
      this.perAcceptanceConfList = perAcceptanceConfList
      this.fileData = perAcceptanceAttList
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
