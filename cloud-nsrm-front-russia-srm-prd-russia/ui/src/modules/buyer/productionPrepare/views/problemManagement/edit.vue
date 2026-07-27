<template>
  <el-container class="flex-container the-problemDetail-detail" direction="vertical">
    <el-main>
      <div class="form-container2">
        <el-form
          ref="problemsForm"
          :model="problemsForm"
          label-width="80px"
          label-position="top"
          :rules="rules"
        >
          <el-collapse v-model="activeDims" class="tab-form-style">
            <!-- 基本信息 -->
            <el-collapse-item ref="baseInfo" :title="$t('vendorMod.basicInformation')" name="1">
              <srm-row>
                <!-- 问题单号 -->
                <srm-col>
                  <el-form-item
                    :label="$t('problemManagement.problemExchangeCode')"
                    :label-width="formLabelWidth"
                  >
                    <el-input v-model="problemsForm.problemExchangeCode" disabled />
                  </el-form-item>
                </srm-col>

                <!-- 车型编码 -->
                <srm-col>
                  <el-form-item
                    :label="$t('problemManagement.motorcycleTypeCode')"
                    :label-width="formLabelWidth"
                    prop="motorcycleTypeCode"
                  >
                    <el-input
                      v-model="problemsForm.motorcycleTypeCode"
                      :disabled="isReadOnly || status !== 'DRAFT'"
                    />
                  </el-form-item>
                </srm-col>

                <!-- 车型名称 -->
                <srm-col>
                  <el-form-item
                    :label="$t('problemManagement.motorcycleTypeName')"
                    :label-width="formLabelWidth"
                    prop="motorcycleTypeName"
                  >
                    <el-input
                      v-model="problemsForm.motorcycleTypeName"
                      :disabled="isReadOnly || status !== 'DRAFT'"
                    />
                  </el-form-item>
                </srm-col>

                <!-- 物料编码 -->
                <srm-col>
                  <el-form-item
                    :label="$t('problemManagement.itemCode')"
                    :label-width="formLabelWidth"
                    prop="itemCode"
                  >
                    <QuickSearch
                      :disabled="isReadOnly || status !== 'DRAFT'"
                      :show-input="problemsForm.itemCode"
                      show-key="materialCode"
                      :scope-data="problemsForm"
                      name="scc_base_material_item"
                      @close-quicksearch="getItemObj"
                    />
                  </el-form-item>
                </srm-col>

                <!-- 物料名称 -->
                <srm-col>
                  <el-form-item
                    :label="$t('problemManagement.itemName')"
                    :label-width="formLabelWidth"
                    prop="itemName"
                  >
                    <el-input v-model="problemsForm.itemName" disabled />
                  </el-form-item>
                </srm-col>

                <!-- 供应商编码 -->
                <srm-col>
                  <el-form-item
                    :label="this.$t('problemManagement.vendorCode')"
                    :label-width="formLabelWidth"
                    prop="vendorCode"
                  >
                    <QuickSearch
                      :disabled="isReadOnly || status !== 'DRAFT'"
                      :show-input="problemsForm.vendorCode"
                      show-key="companyName"
                      :scope-data="problemsForm"
                      name="scc_sup_company_info_new"
                      @close-quicksearch="getVendorObj"
                    />
                  </el-form-item>
                </srm-col>

                <!-- 供应商名称 -->
                <srm-col>
                  <el-form-item
                    :label="$t('common.vendorName')"
                    :label-width="formLabelWidth"
                    prop="vendorName"
                  >
                    <el-input v-model="problemsForm.vendorName" disabled />
                  </el-form-item>
                </srm-col>

                <!-- 发生阶段 -->
                <srm-col>
                  <el-form-item
                    :label="$t('problemManagement.problemExchangeStage')"
                    :label-width="formLabelWidth"
                    prop="problemExchangeStage"
                  >
                    <dict-select
                      v-model="problemsForm.problemExchangeStage"
                      :disabled="isReadOnly || status !== 'DRAFT'"
                      code="PROBLEM_EXCHANGE_STAGE"
                    />
                  </el-form-item>
                </srm-col>

                <!-- 风险等级 -->
                <srm-col>
                  <el-form-item
                    :label="$t('problemManagement.problemExchangeRiskLevel')"
                    :label-width="formLabelWidth"
                    prop="problemExchangeRiskLevel"
                  >
                    <dict-select
                      v-model="problemsForm.problemExchangeRiskLevel"
                      :disabled="isReadOnly || status !== 'DRAFT'"
                      code="PROBLEM_EXCHANG_ERISK_LEVEL"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>

            <!-- 问题描述及附件 -->
            <el-collapse-item :title="$t('problemManagement.problemDescItem')" name="2">
              <srm-row>
                <srm-col :initCol="1">
                  <!-- 问题描述 -->
                  <el-form-item
                    :label="$t('problemManagement.problemDesc')"
                    :label-width="formLabelWidth"
                    prop="problemExchangeInfo"
                  >
                    <el-input
                      v-model="problemsForm.problemExchangeInfo"
                      type="textarea"
                      :rows="2"
                      :disabled="isReadOnly || status !== 'DRAFT'"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
              <FileDynamic
                ref="infoSceneAttachment"
                v-model="problemsForm.infoFiles"
                scene-module-code="SCENE_PROBLEM_EXCHANGE_INFO_ATTACHMENT"
                :business-id="problemExchangeInfoFileId"
                :editable="!isReadOnly && status === 'DRAFT'"
              />
            </el-collapse-item>

            <!-- 原因描述及对策 -->
            <el-collapse-item :title="$t('problemManagement.problemReasonItem')" name="3">
              <srm-row>
                <srm-col :initCol="1">
                  <!-- 原因描述 -->
                  <el-form-item
                    :label="$t('problemManagement.problemReasonItem')"
                    :label-width="formLabelWidth"
                    prop="problemExchangeReason"
                    :rules="['PJ_REJECT', 'PJ_TO_BE_SUBMITTED'].includes(status) ? rules.problemExchangeReason : [{ required:false}]"
                  >
                    <el-input
                      v-model="problemsForm.problemExchangeReason"
                      type="textarea"
                      :rows="2"
                      :disabled="isReadOnly || !(['PJ_REJECT', 'PJ_TO_BE_SUBMITTED'].includes(status))"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
              <FileDynamic
                ref="reasonSceneAttachment"
                v-model="problemsForm.reasonFiles"
                scene-module-code="SCENE_PROBLEM_EXCHANGE_REASON_ATTACHMENT"
                :business-id="problemExchangeReasonFileId"
                :editable="!isReadOnly && ['PJ_REJECT', 'PJ_TO_BE_SUBMITTED'].includes(status)"
              />
            </el-collapse-item>

            <!-- 处理结果 -->
            <el-collapse-item :title="$t('problemManagement.problemResultItem')" name="4">
              <srm-row>
                <srm-col :initCol="1">
                  <!-- 处理结果 -->
                  <el-form-item
                    :label="$t('problemManagement.problemResultItem')"
                    :label-width="formLabelWidth"
                    prop="problemExchangeResult"
                    :rules="status === 'TO_BE_ACCEPTED' ? rules.problemExchangeResult : [{ required:false}]"
                  >
                    <el-input
                      v-model="problemsForm.problemExchangeResult"
                      type="textarea"
                      :rows="2"
                      :disabled="isReadOnly || status !== 'TO_BE_ACCEPTED'"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
              <FileDynamic
                ref="resultSceneAttachment"
                v-model="problemsForm.resultFiles"
                scene-module-code="SCENE_PROBLEM_EXCHANGE_RESULT_ATTACHMENT"
                :business-id="problemExchangeResultFileId"
                :editable="!isReadOnly && status === 'TO_BE_ACCEPTED'"
              />
            </el-collapse-item>
          </el-collapse>
        </el-form>
      </div>
      <CToolbar>
        <template slot="right">
          <!-- 关闭 -->
          <el-button @click="backOne">
            {{ $t("common.close") }}
          </el-button>
          <template v-if="!isReadOnly">
            <!-- 暂存 -->
            <el-button
              v-if="status === 'DRAFT' && userType === 'BUYER'"
              type="primary"
              @click="submitOne(0)"
            >
              {{ $t("common.staging") }}
            </el-button>
            <!-- 提交 -->
            <el-button
              v-if="status === 'DRAFT' && userType === 'BUYER'"
              type="primary"
              @click="submitOne(1)"
            >
              {{ $t('problemManagement.submit') }}
            </el-button>
            <!-- 整改方案提交 -->
            <el-button
              v-if="['PJ_REJECT', 'PJ_TO_BE_SUBMITTED'].includes(status) && userType === 'VENDOR'"
              type="primary"
              @click="submitOne(2)"
            >
              {{ $t('problemManagement.projectSubmit') }}
            </el-button>
            <!-- 整改方案通过 -->
            <el-button
              v-if="status === 'PJ_TO_BE_CONFIRMED' && userType === 'BUYER'"
              type="primary"
              @click="submitOne(3)"
            >
              {{ $t('problemManagement.projectPass') }}
            </el-button>
            <!-- 整改方案驳回 -->
            <el-button
              v-if="status === 'PJ_TO_BE_CONFIRMED' && userType === 'BUYER'"
              type="primary"
              @click="submitOne(4)"
            >
              {{ $t('problemManagement.projectReject') }}
            </el-button>
            <!--问题关闭申请  -->
            <el-button
              v-if="['RECTIFICATION_REJECTION','UNDER_RECTIFICATION'].includes(status) && userType === 'VENDOR'"
              type="primary"
              @click="submitOne(5)"
            >
              {{ $t('problemManagement.problemCloseApply') }}
            </el-button>
            <!-- 问题关闭 -->
            <el-button
              v-if="status === 'TO_BE_ACCEPTED' && userType === 'BUYER'"
              type="primary"
              @click="submitOne(6)"
            >
              {{ $t('problemManagement.problemClose') }}
            </el-button>
            <!-- 整改驳回 -->
            <el-button
              v-if="status === 'TO_BE_ACCEPTED' && userType === 'BUYER'"
              type="primary"
              @click="submitOne(7)"
            >
              {{ $t('problemManagement.reject') }}
            </el-button>
          </template>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import MainHeader from 'lib@/components/Table/MainHeader'
import CToolbar from 'lib@/components/c-toolbar'
import CCategorySelect from 'lib@/components/c-category-select'
import OriginContactInfo from 'lib@/composition/origin/contactInfo'
import OriginInviteSuppliers from 'lib@/composition/origin/inviteSuppliers'
import _pick from 'lodash/pick'
import OrganizationSelector from 'lib@/components/organization-selector'
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import MImport from 'lib@/components/import'
import FileDynamic from '@/library/components/c-file-management/file-dynamic'
import { cannotLessCurrentTime } from '@/library/mixins/datePickerOptions'
import { problemManagement } from 'modb@/productionPrepare/api'

export default {
  name: 'ProblemDetail',
  components: {
    MainHeader,
    CToolbar,
    CCategorySelect,
    MImport,
    QuickSearch,
    OrganizationSelector,
    OriginInviteSuppliers,
    OriginContactInfo,
    FileDynamic
  },
  mixins: [tabTodoMixin, cannotLessCurrentTime],
  data () {
    return {
      activeDims: ['1', '2', '3', '4'],
      problemsForm: {
        // 基本信息
        problemExchangeCode: '',
        motorcycleTypeCode: '',
        motorcycleTypeName: '',
        itemCode: '',
        itemName: '',
        itemId: '',
        vendorCode: '',
        vendorName: '',
        vendorId: '',
        problemExchangeStage: '',
        problemExchangeRiskLevel: '',

        // 问题描述及附件
        problemExchangeInfo: '',
        infoFiles: [],

        // 原因描述及对策
        problemExchangeReason: '',
        reasonFiles: [],

        // 问题描述及附件
        problemExchangeResult: '',
        resultFiles: []
      },
      rules: {
        motorcycleTypeCode: [{ required: true, message: '请输入车型编码' }], // 车型编码
        motorcycleTypeName: [{ required: true, message: '请输入车型名称' }], // 车型名称
        itemCode: [{ required: true, message: '请选择物料编码' }], // 物料编码
        itemName: [{ required: true, message: '请选择物料名称' }], // 物料名称
        vendorCode: [{ required: true, message: '请选择供应商编码' }], // 供应商编码
        vendorName: [{ required: true, message: '请选择供应商名称' }], // 供应商名称
        problemExchangeStage: [{ required: true, message: '请选择发生阶段' }], // 发生阶段
        problemExchangeRiskLevel: [{ required: true, message: '请选择风险等级' }], // 风险等级
        problemExchangeInfo: [{ required: true, message: '请输入问题描述' }], // 问题描述
        problemExchangeReason: [{ required: true, message: '请输入原因描述及对策' }], // 原因描述及对策
        problemExchangeResult: [{ required: true, message: '请输入处理结果' }] // 处理结果
      },
      formLabelWidth: '120px',
      status: 'DRAFT',
      problemExchangeInfoFileId: null,
      problemExchangeReasonFileId: null,
      problemExchangeResultFileId: null
    }
  },
  computed: {
    userType () {
      return this.$store.getters.userType
    },
    isReadOnly () {
      return this.$attrs.params.flag === 'view'
    }
  },
  created () {
    const { flag, row } = this.$attrs.params
    if (flag === 'add') {
      this.$nextTick(() => {
        this.$refs.infoSceneAttachment.loadFileInfo()
        this.$refs.reasonSceneAttachment.loadFileInfo()
        this.$refs.resultSceneAttachment.loadFileInfo()
      })
    } else {
      this.getFormDetail(row.problemExchangeId)
    }
  },
  methods: {
    // 选择物料编码
    getItemObj (value) {
      this.problemsForm.itemCode = value.materialCode || ''
      this.problemsForm.itemName = value.materialName || ''
      this.problemsForm.itemId = value.materialId || ''
    },
    // 选择供应商
    getVendorObj (val, scope) {
      this.problemsForm.vendorId = val ? val.companyId : ''
      this.problemsForm.vendorName = val ? val.companyName : ''
      this.problemsForm.vendorCode = val ? val.companyCode : ''
    },

    async getFormDetail (problemExchangeId, flag = false) {
      let res = await problemManagement.info({ problemExchangeId })
      if (res.data) {
        if (flag) {
          this.problemsForm.problemExchangeCode = res.data.problemExchangeCode
        } else {
          this.problemsForm = res.data
          this.status = this.problemsForm.problemExchangeStatus || 'DRAFT'
          this.problemExchangeInfoFileId = this.problemsForm.problemExchangeInfoFileId
          this.problemExchangeReasonFileId = this.problemsForm.problemExchangeReasonFileId
          this.problemExchangeResultFileId = this.problemsForm.problemExchangeResultFileId
          this.$refs.infoSceneAttachment.loadFileInfo()
          this.$refs.reasonSceneAttachment.loadFileInfo()
          this.$refs.resultSceneAttachment.loadFileInfo()
        }
      }
    },
    backOne () {
      if (this.$attrs.params.flag === 'add') {
        this.$emit('tab-remove', 'problemDetail')
      } else {
        this.$emit('tab-remove', this.$attrs.params.tabName)
      }
      this.__setTabTodo('problemList.getQuerydata')
    },
    async submitOne (i) {
      if ([1, 2, 6, 7].includes(i)) {
        const flag = await this.$refs.problemsForm.validate()
        if (!flag) {
          return this.__jump_error__('baseInfo', null, null)
        }
      }
      let url = [
        '/api-ppap/problem/exchange/manuscript', // 暂存
        '/api-ppap/problem/exchange/submit', // 提交
        '/api-ppap/problem/exchange/vendor/pj/submit', // 整改方案提交
        `/api-ppap/problem/exchange/buyer/pj/pass?problemExchangeId=${this.problemsForm.problemExchangeId}`, // 方案通过
        `/api-ppap/problem/exchange/buyer/pj/reject?problemExchangeId=${this.problemsForm.problemExchangeId}`, // 方案驳回
        `/api-ppap/problem/exchange/vendor/close/request?problemExchangeId=${this.problemsForm.problemExchangeId}`, // 问题关闭申请
        '/api-ppap/problem/exchange/buyer/rectification/close', // 问题关闭
        '/api-ppap/problem/exchange/buyer/rectification/reject' // 整改驳回
      ]
      let data = this.problemsForm
      if (i === 3 || i === 4 || i === 5) {
        data = {}
      }
      let res = await problemManagement.modify(url[i], data)
      if (res.data) {
        this.$message.success(this.$t('common.success'))
        if (!this.problemsForm.problemExchangeCode && i === 0) {
          this.problemsForm.problemExchangeId = res.data
          this.__setTabTodo('problemList.getQuerydata')
          this.getFormDetail(res.data, true)
        } else {
          this.backOne()
        }
      }
    }
  }
}
</script>
<style lang="scss" scoped></style>
