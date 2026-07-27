<template>
  <el-container class="flex-container the-vendorEffectDetail-detail" direction="vertical">
    <el-main>
          <el-collapse v-model="activeDims" class="tab-form-style">
            <!-- 考核单据信息 -->
            <el-collapse-item
              ref="assessmentFormBill"
              :title="$t('perfMod.assessmentFormBill')"
              name="1"
            >
              <el-form
                ref="assessmentForm"
                :model="assessmentForm"
                :rules="assessmentFormRules"
                class="form-fill-style"
                :disabled="
                  curOpt === 'view' ||
                    assessmentForm.status === 'VENDOR_FEEDBACK' ||
                    assessmentForm.status === 'ASSESSED' ||
                    assessmentForm.status === 'OBSOLETE'||
                    userType === 'VENDOR'
                "
              >
                <srm-row>
                  <!-- 考核单号 -->
                  <srm-col >
                    <el-form-item :label="$t('perfMod.assessmentNo')">
                      <el-input v-model="assessmentForm.assessmentNo" disabled />
                    </el-form-item>
                  </srm-col>
                  <!-- 考核类型 -->
                  <srm-col >
                    <el-form-item :label="$t('perfMod.inspectionType')" prop="ceeaAssessmentType">
                      <DictSelect
                        v-model="assessmentForm.ceeaAssessmentType"
                        code="ASSESSMENT_BILL_TYPE"
                      />
                    </el-form-item>
                  </srm-col>
                  <!-- 考核时间 -->
                  <srm-col >
                    <el-form-item :label="$t('perfMod.assessmentDate')" prop="assessmentDate">
                      <el-date-picker
                        v-model="assessmentForm.assessmentDate"
                        format="yyyy-MM-dd"
                        value-format="yyyy-MM-dd"
                        :placeholder="$t('perfMod.assessmentDate')"
                      />
                    </el-form-item>
                  </srm-col>
                  <!-- 来源单据类型 -->
                  <srm-col >
                    <el-form-item :label="$t('qualitySynergy.sourceDocumentType')">
                      <DictSelect
                        v-model="assessmentForm.sourceType"
                        code="PER_CHECK_FORM_SOURCE"
                      />
                    </el-form-item>
                  </srm-col>
                  <!-- 来源单号 -->
                  <srm-col >
                    <el-form-item :label="$t('orderMod.buyerOrderSynergy.externalNum')">
                      <el-input v-model="assessmentForm.sourceNumber" disabled />
                    </el-form-item>
                  </srm-col>
                  <!-- 指标维度 -->
                  <srm-col >
                    <el-form-item :label="$t('perfMod.indicatorDimension')">
                      <DictSelect
                        v-model="assessmentForm.indicatorDimension"
                        code="INDICATORS_DIM"
                        @change="indicatorDimensionChange"
                      />
                    </el-form-item>
                  </srm-col>
                  <!-- 指标名称 -->
                  <srm-col >
                    <el-form-item :label="$t('perfMod.indicatorName')">
                      <el-select
                        v-model="assessmentForm.indicatorName"
                        @change="indicatorNameChange"
                      >
                        <el-option
                          v-for="item in indicatorNameList"
                          :key="item.indicatorHeadId"
                          :label="item.indicatorName"
                          :value="item.indicatorName"
                        />
                      </el-select>
                    </el-form-item>
                  </srm-col>
                  <!-- 评价结果 -->
                  <srm-col >
                    <el-form-item :label="$t('vendorMod.indicatorLineDes')">

                      <el-select
                        v-model="assessmentForm.ceeaIndicatorLineDes"
                        @change="evaluationResultsChange"
                      >
                        <el-option
                          v-for="item in indicatorLineList"
                          :key="item.templateIndsLineId"
                          :label="item.indicatorLineDes"
                          :value="item.templateIndsLineId"
                        />
                      </el-select>
                    </el-form-item>
                  </srm-col>
                  <!-- 建议考核金额 -->
                  <srm-col >
                    <el-form-item :label="$t('perfMod.assessmentPenalty')">
                      <el-input v-model="assessmentForm.assessmentPenalty" disabled />
                    </el-form-item>
                  </srm-col>
                  <!-- 考核税率 -->
                  <srm-col >
                    <el-form-item :label="$t('perfMod.taxCode')" prop="taxCode">
                      <el-select v-model="assessmentForm.taxCode">
                        <el-option
                          v-for="item in taxList"
                          :key="item.value"
                          :label="item.label"
                          :value="item.key"
                        />
                      </el-select>
                    </el-form-item>
                  </srm-col>
                  <!-- 实际考核金额(含税)  -->
                  <srm-col >
                    <el-form-item
                      :label="$t('perfMod.actualAssessmentAmountY')"
                      prop="actualAssessmentAmountY"
                    >
                      <el-input
                        v-model="assessmentForm.actualAssessmentAmountY"
                        v-input-format="{ type: 'float' }"
                        :placeholder="$t('perfMod.taxAmount')"
                        @change="handleChangeAmount"
                      />
                    </el-form-item>
                  </srm-col>
                  <!-- 实际考核金额(不含税) -->
                  <srm-col >
                    <el-form-item :label="$t('perfMod.actualAssessmentAmountN')" prop="actualAssessmentAmountN">
                      <el-input v-model="assessmentForm.actualAssessmentAmountN" disabled />
                    </el-form-item>
                  </srm-col>
                  <!-- 业务实体 -->
                  <srm-col >
                    <el-form-item :label="$t('perfMod.businessEntity')" prop="organizationId">
                      <OrganizationSelector
                        ref="organizationSelector"
                        v-model="assessmentForm.organizationId"
                        :parent-id="-1"
                        node-type="OU"
                        :placeholder="$t('common.pleaseSelect')"
                        :disabled="
                          curOpt === 'view' ||
                            assessmentForm.status === 'IN_FEEDBACK' ||
                            assessmentForm.status === 'ASSESSED' ||
                            assessmentForm.status === 'OBSOLETE'
                        "
                        :scope="assessmentForm"
                        @select="selectHandler"
                      />
                    </el-form-item>
                  </srm-col>
                  <!-- 供应商名称 -->
                  <srm-col >
                    <el-form-item :label="$t('perfMod.vendorName')" prop="vendorCode">
                      <QuickSearch
                        :show-input="assessmentForm.vendorName"
                        show-key="companyName"
                        :scope-data="assessmentForm"
                        name="scc_sup_company_info"
                        :disabled="[0,1].includes(+assessmentForm.sourceType) && $attrs.params.flag == 'edit'"
                        @close-quicksearch="getVendorObj"
                      />
                    </el-form-item>
                  </srm-col>
                  <!-- 币种 -->
                  <srm-col >
                    <el-form-item :label="$t('perfMod.currencyCode')" prop="currencyCode">
                      <DictSelect v-model="assessmentForm.currencyCode" code="currency" />
                    </el-form-item>
                  </srm-col>
                  <!-- 采购分类 -->
                  <srm-col >
                    <el-form-item :label="$t('perfMod.categoryName')">
                      <CCategorySelect
                        v-model="assessmentForm.categoryName"
                        :scope="assessmentForm"
                        :disabled="curOpt === 'view'"
                        show-key="categoryName"
                        :placeholder="$t('vendorMod.msgCategoryNormalizer')"
                        @select="comfirmCategory"
                      />
                    </el-form-item>
                  </srm-col>
                  <!-- 物料编码 -->
                  <srm-col >
                    <el-form-item :label="$t('perfMod.materialCode')">
                      <QuickSearch
                        :show-input="assessmentForm.materialCode"
                        show-key="materialCode"
                        :scope-data="assessmentForm"
                        name="scc_base_material_item_display"
                        :disabled="[0,1].includes(+assessmentForm.sourceType) && $attrs.params.flag == 'edit'"
                        @close-quicksearch="getItemObj"
                      />
                    </el-form-item>
                  </srm-col>
                  <!-- 物料名称 -->
                  <srm-col >
                    <el-form-item :label="$t('perfMod.materialName')">
                      <el-input v-model="assessmentForm.materialName" disabled />
                    </el-form-item>
                  </srm-col>
                  <!-- 考核责任人 -->
                  <srm-col >
                    <el-form-item :label="$t('perfMod.respFullName')" prop="respFullName">
                      <QuickSearch
                        :show-input="assessmentForm.respFullName"
                        show-key="username"
                        :scope-data="assessmentForm"
                        name="scc_rbac_user_display"
                        @close-quicksearch="getUserObj"
                      />
                    </el-form-item>
                  </srm-col>
                  <!-- 考核附件 -->
                  <srm-col >
                    <el-form-item :label="$t('perfMod.assessmentFiles')">
                      <div>
                        <SrmCommonFile
                          :extra-data="fileInfo"
                          :default-file="{
                            fileId: assessmentForm.fileUploadId,
                            fileName: assessmentForm.fileSourceName
                          }"
                          :readonly="curOpt == 'view' || !(['','DRAFT','WITHDRAWN'].includes(assessmentForm.status))"
                          @on-change="({file}) => handleUploadSuccess(file)"
                        />
                      </div>
                    </el-form-item>
                  </srm-col>
                  <!-- 建议降级至 -->
                  <srm-col >
                    <el-form-item :label="$t('perfMod.eecomDowngrade')">
                      <el-select v-model="assessmentForm.ceeaCategoryStatus">
                        <el-option
                          v-for="item in catStatusList"
                          :key="item.id"
                          :label="item.label"
                          :value="item.value"
                        />
                      </el-select>
                    </el-form-item>
                  </srm-col>
                  <!-- 处理状态 -->
                  <srm-col >
                    <el-form-item :label="$t('perfMod.ProcessStatus')">
                      <DictSelect
                        v-model="assessmentForm.status"
                        code="VENDOR_ASSES_STATUS"
                        disabled
                      />
                    </el-form-item>
                  </srm-col>
                  <!-- 标题 -->
                  <srm-col >
                    <el-form-item :label="$t('perfMod.headline')">
                      <el-input v-model="assessmentForm.ceeaAssessmentTitle" />
                    </el-form-item>
                  </srm-col>
                  <!-- 是否结算关联 -->
                  <srm-col >
                    <el-form-item :label="$t('perfMod.association')">
                      <el-select v-model="assessmentForm.ceeaAssociatedStates">
                        <el-option
                          v-for="item in yesNoList"
                          :key="item.value"
                          :label="item.label"
                          :value="item.value"
                        />
                      </el-select>
                    </el-form-item>
                  </srm-col>
                  <!-- BPM起草人意见 -->
                  <srm-col :initCol="1">
                    <el-form-item :label="$t('perfMod.drafterComments')">
                      <el-input
                        v-model="assessmentForm.ceeaDrafterOpinion"
                        type="textarea"
                        :rows="3"
                      />
                    </el-form-item>
                  </srm-col>
                  <!-- 考核说明 -->
                  <srm-col :initCol="1">
                    <el-form-item :label="$t('perfMod.explanation')" prop="explanation">
                      <el-input v-model="assessmentForm.explanation" type="textarea" :rows="3" />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-form>
            </el-collapse-item>
            <!-- 供应商反馈信息 -->
            <el-collapse-item
              v-if="
                (curRole === 'VENDOR' && assessmentForm.status === 'IN_FEEDBACK') ||
                  (curRole === 'BUYER' &&
                  assessmentForm.status === 'VENDOR_FEEDBACK' &&
                  assessmentForm.vIsFeedback === 'Y') ||
                  assessmentForm.status === 'ASSESSED'
              "
              ref="vendorFeedbackFormInfo"
              :title="$t('perfMod.vendorFeedbackFormInfo')"
              name="2"
            >
              <el-form
                ref="vendorForm"
                :model="vendorFeedbackForm"
                class="form-fill-style"
                :disabled="!(curOpt !== 'view' && curRole === 'VENDOR' && assessmentForm.status === 'IN_FEEDBACK' && assessmentForm.vIsFeedback === 'N')
                "
              >
                <srm-row>
                  <!-- 反馈时间 -->
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('perfMod.vFeedbackTime')">
                      <el-date-picker
                        v-model="vendorFeedbackForm.vFeedbackTime"
                        format="yyyy-MM-dd"
                        value-format="yyyy-MM-dd HH:mm:ss"
                        :placeholder="$t('perfMod.vFeedbackTime')"
                      />
                    </el-form-item>
                  </srm-col>
                  <!-- 反馈附件 -->
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('perfMod.vFeedbackFileUploadId')">
                      <div>
                        <SrmCommonFile
                          :extra-data="fileInfo"
                          :default-file="{
                            fileId: vendorFeedbackForm.vFeedbackFileUploadId,
                            fileName: vendorFeedbackForm.vFeedbackFileSourceName
                          }"
                          :validate-options="{
                            accept: acceptFileType
                          }"
                          :readonly="!(curRole === 'VENDOR' &&
                            assessmentForm.status === 'IN_FEEDBACK' &&
                            assessmentForm.vIsFeedback === 'N')"
                          @on-change="({file}) => vendorHandleUploadSuccess(file)"
                        />
                      </div>
                    </el-form-item>
                  </srm-col>
                  <!-- 反馈说明 -->
                  <srm-col :initCol="2">
                    <el-form-item
                      :label="$t('perfMod.vFeedbackExplanation')"
                      prop="vFeedbackExplanation"
                    >
                      <el-input
                        v-model="vendorFeedbackForm.vFeedbackExplanation"
                        type="textarea"
                        :rows="2"
                      />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-form>
            </el-collapse-item>
            <el-collapse-item
              v-if="
                (curRole === 'BUYER' && assessmentForm.status === 'VENDOR_FEEDBACK') ||
                  assessmentForm.status === 'ASSESSED'
              "
              ref="buyersProcessForm"
              :title="$t('perfMod.buyersProcessForm')"
              name="3"
            >
              <el-form
                ref="buyForm"
                :model="buyersProcessForm"
                class="form-fill-style"
                :disabled="curOpt === 'view'"
              >
                <srm-row>
                  <!-- 处理时间 -->
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('perfMod.mFeedbackTime')" prop="mFeedbackTime">
                      <el-date-picker
                        v-model="buyersProcessForm.mFeedbackTime"
                        format="yyyy-MM-dd"
                        value-format="yyyy-MM-dd HH:mm:ss"
                        :placeholder="$t('perfMod.mFeedbackTime')"
                        disabled
                      />
                    </el-form-item>
                  </srm-col>
                  <!-- 处理状态 -->
                  <srm-col :initCol="4">
                    <el-form-item :label="$t('perfMod.ProcessStatus')" prop="status">
                      <el-select v-model="buyersProcessForm.status" @change="statusChange">
                        <el-option
                          v-for="item in statusList"
                          :key="item.value"
                          :label="item.label"
                          :value="item.value"
                        />
                      </el-select>
                    </el-form-item>
                  </srm-col>
                  <!-- 处理说明 -->
                  <srm-col :initCol="2">
                    <el-form-item
                      :label="$t('perfMod.mFeedbackExplanation')"
                      prop="mFeedbackExplanation"
                    >
                      <el-input
                        v-model="buyersProcessForm.mFeedbackExplanation"
                        type="textarea"
                        :rows="2"
                      />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-form>
            </el-collapse-item>
          </el-collapse>

          <CToolbar>
            <template slot="right">
              <el-button @click="toBack">
                {{ $t('common.backTo') }}
              </el-button>
              <el-button
                v-if="
                  curRole === 'BUYER' &&
                    (assessmentForm.status === 'DRAFT' || assessmentForm.status == '')
                "
                type="primary"
                @click="saveHandle"
              >
                {{ $t('common.staging') }}
              </el-button>
              <el-button
                v-if="
                  curRole === 'BUYER' &&
                    (assessmentForm.status === 'DRAFT' ||
                    assessmentForm.status == 'REJECTED' ||
                    assessmentForm.status == 'WITHDRAWN')
                "
                type="primary"
                @click="submitHandle"
              >
                {{ $t('common.submit') }}
              </el-button>
              <!--              <el-button-->
              <!--                v-if="assessmentForm.status === 'SUBMITTED' && curRole === 'BUYER'"-->
              <!--                type="primary"-->
              <!--                :disabled="assessmentForm.approveStatus === 'APPROVED'"-->
              <!--                @click="approval"-->
              <!--              >-->
              <!--                {{ $t('vendorMod.doApproval') }}-->
              <!--              </el-button>-->

              <!--已提交状态的审批-->
              <el-button
                v-if="assessmentForm.status === 'SUBMITTED' && curRole === 'BUYER'"
                type="primary"
                :disabled="assessmentForm.approveStatus === 'APPROVED'"
                @click="approvalSB"
              >
                {{ $t('vendorMod.doApproval') }}
              </el-button>
              <el-button
                v-if="
                  curRole === 'BUYER' &&
                    assessmentForm.approveStatus === 'APPROVED' &&
                    (assessmentForm.status === 'DRAFT' || assessmentForm.status == '')
                "
                type="primary"
                @click="notifySupplier"
              >
                {{ $t('perfMod.notifySupplier') }}
              </el-button>
              <el-button
                v-if="
                  curRole === 'BUYER' &&
                    assessmentForm.status === 'VENDOR_FEEDBACK' &&
                    assessmentForm.vIsFeedback === 'Y'
                "
                type="primary"
                @click="submitProcessRes"
              >
                {{ $t('perfMod.submitProcessRes') }}
              </el-button>
              <el-button
                v-if="
                  curOpt !== 'view' &&
                    curRole === 'VENDOR' &&
                    assessmentForm.status === 'IN_FEEDBACK' &&
                    assessmentForm.vIsFeedback === 'N'
                "
                type="primary"
                @click="vendorFeedback"
              >
                {{ $t('perfMod.vendorFeedback') }}
              </el-button>
            </template>
          </CToolbar>
      <!-- 人员选择 -->
      <CPeopleSelector
        ref="peopleSelector"
        :visible.sync="peopleDialog"
        :multi-select="false"
        @on-confirm="getPeople"
      />
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import CCategorySelect from 'lib@/components/c-category-select'
import CPeopleSelector from '@/library/components/c-people-selector'
import CCurrencyInput from '@/library/components/c-currency-input'
import OrganizationSelector from 'lib@/components/organization-selector'
import { getAllPurTax } from '@/api/common'
import { adaptDictData } from '@/utils'
import _omit from 'lodash/omit'
import _pick from 'lodash/pick'
import Big from 'big.js'
import { performanceManagement } from 'modb@/performanceManagement/api/index'

export default {
  name: 'AssessmentDetail',
  components: {
    CCategorySelect,
    QuickSearch,
    CToolbar,
    CPeopleSelector,
    CCurrencyInput,
    OrganizationSelector
  },
  mixins: [tabTodoWatch, tabTodoMixin],
  data () {
    return {
      sampleActiveInfo: 'tab1',
      activeDims: ['1', '2', '3'],
      assessmentForm: {
        assessmentNo: '', // 考核单号
        ceeaAssessmentType: '', // 考核类型
        assessmentDate: '', // 考核时间
        sourceType: '', // 来源单据类型
        sourceNumber: '', // 来源单号
        indicatorDimension: '', // 指标维度
        indicatorHeadId: '', // 指标ID
        indicatorName: '', // 指标名称
        indicatorLineDes: '', // 评价结果
        assessmentPenalty: '', // 建议考核金额
        taxCode: '', // 税率编码
        taxName: '', // 税率名称
        taxKay: '',
        actualAssessmentAmountY: '', // 实际考核金额(含税)
        actualAssessmentAmountN: '', // 实际考核金额(不含税)
        fullPathId: null, // 组织全路径
        organizationId: null, // 组织ID
        organizationCode: '', // 组织Code
        organizationName: '', // 组织名称
        vendorCode: '', // 供应商code
        vendorName: '', // 供应商名称
        currencyCode: '', // 币种
        categoryName: '', // 品类名称
        categoryCode: '', // 品类编码
        categoryId: null,
        materialCode: '', // 物料编码
        materialName: '', // 物料名称
        materialId: null, // 物料ID
        respUserName: '', // 责任人账号
        respFullName: '', // 责任人名字
        fileUploadId: null, // 考核附件文件id
        fileSourceName: '', // 考核附件文件名
        ceeaCategoryStatus: '', // 建议降级至
        status: 'DRAFT', // 考核状态
        ceeaAssessmentTitle: '', // 标题
        ceeaAssociatedStates: '', // 标题
        ceeaDrafterOpinion: '', // BPM起草人意见
        explanation: '', // 考核说明
        approveStatus: '', // 审核状态
        ceeaVendorId: null
      },
      assessmentFormRules: {
        assessmentDate: [{ required: true, message: this.$t('perfMod.selectExamination') }],
        indicatorLineDes: [{ required: true, message: this.$t('perfMod.selectEvaluationResult') }],
        currencyCode: [{ required: true, message: this.$t('perfMod.selectCurrency') }],
        taxCode: [{ required: true, message: this.$t('perfMod.selectAssessmenRate') }],
        actualAssessmentAmountY: [{ required: true, message: this.$t('perfMod.actualAssessment') }],
        actualAssessmentAmountN: [{ required: true, message: '请重新输入实际考核金额(含税)' }],
        organizationId: [{ required: true, message: this.$t('perfMod.selectBusinessEntity') }],
        vendorCode: [{ required: true, message: this.$t('perfMod.selectSupplier') }],
        explanation: [{ required: true, message: this.$t('perfMod.enterInstructions') }],
        respFullName: [{ required: true, message: this.$t('perfMod.personResponsible') }],
        ceeaAssessmentType: [{ required: true, message: this.$t('perfMod.msgInspectionType') }]
      },
      userType: '',
      fileInfo: {
        fileModular: 'perf',
        fileFunction: 'assessmentDetail',
        fileType: 'images'
      },
      acceptFileType: ['jpeg', 'png', 'xls', 'xlsx', 'docx', 'pdf'],
      curRole: this.$store.getters.userType, // VENDOR BUYER curRole==='VENDOR'
      taxList: [],
      resultFlow: false,
      resultFlowParams: {}, // 结果审批流程参数
      openWorkFlow: false, // 审批流程相关参数
      indicatorNameList: [
        { value: 'gfgf', label: this.$t('perfMod.indexOne') },
        { value: 'bnbnb', label: this.$t('perfMod.indexTwo') }
      ],
      yesNoList: [
        { value: 'Y', label: this.$t('common.yes') },
        { value: 'N', label: this.$t('common.no') }
      ],
      // 指标列表
      indicatorLineList: [], // 指标行下拉列表
      catStatusList: [
        { value: 'YELLOW', label: this.$t('perfMod.yellowCard') },
        { value: 'RED', label: this.$t('perfMod.redCard') },
        { value: 'BLACK', label: this.$t('perfMod.blackCard') }
      ],
      // 反馈
      vendorFeedbackForm: {
        vendorAssesId: null, // 核单ID
        vFeedbackTime: '', // 供应商反馈时间
        vFeedbackFileUploadId: null, // 供应商反馈附件文件id
        vFeedbackFileSourceName: '', // 供应商反馈附件文件名
        vFeedbackExplanation: '' // 供应商反馈说明
      },
      // 采购商处理
      buyersProcessForm: {
        vendorAssesId: null, // 核单ID
        mFeedbackTime: new Date(), // 处理供应商反馈时间
        mFeedbackExplanation: '', // 说明
        status: '' // 状态
      },
      curOpt: 'add',
      peopleDialog: false,
      curOrderId: null, // 单据Id
      statusList: [
        { value: 'WITHDRAWN', label: this.$t('common.recall') },
        { value: 'OBSOLETE', label: this.$t('common.abandon') },
        { value: 'ASSESSED', label: this.$t('perfMod.inspection') }
      ]
    }
  },
  created () {
    // OA嵌套页面多语言配置
    const funName = this.$attrs.params.funName
    const locale = this.$route.query.locale
    this.userType = this.$store.getters.user.userInfo.userType
    if (funName) {
      if (locale) {
        if (locale === 'zh_CN') {
          this.handleSetLanguage(locale)
        } else if (locale === 'en_US') {
          this.handleSetLanguage(locale)
        } else if (locale === 'ja_JP') {
          this.handleSetLanguage(locale)
        }
      } else {
        this.handleSetLanguage('zh_CN')
      }
    }
    this.fatchDictData() // 字典
    this.curOpt = this.$attrs.params.flag
    if (this.$attrs.params.flag == 'adds') {
      console.log(this.$attrs.params.row)
      this.assessmentForm.vendorName = this.$attrs.params.row.companyName
      this.assessmentForm.vendorCode = this.$attrs.params.row.companyCode
      this.assessmentForm.ceeaVendorId = this.$attrs.params.row.companyId
      this.assessmentForm.organizationId = this.$attrs.params.row.organizationId
      this.assessmentForm.overallScoreId = this.$attrs.params.row.overallScoreId
    }
    if (this.$attrs.params.flag !== 'add' && this.$attrs.params.flag !== 'adds') {
      this.curOrderId = this.$attrs.params.orderId // 单据Id
      this.getOrderFormDetail()
    }
  },
  methods: {
    handleSetLanguage (lang) {
      this.$i18n.locale = lang
    },
    // 获取数据字典
    fatchDictData () {
      // 获取所有税率
      getAllPurTax().then(res => {
        this.taxList = adaptDictData(res.data, 'tax')
        console.log(this.taxList)
      })
    },
    // 查询单据详情
    getOrderFormDetail (vendorAssesId = this.curOrderId) {
      performanceManagement.vendorAssesQueryById({ vendorAssesId }).then(res => {
        let formInfo = res.data
        this.assessmentForm = _omit(formInfo, [
          'creationDate',
          'lastUpdateDate',
          'createdBy',
          'createdByIp',
          'vFeedbackTime',
          'vFeedbackFileUploadId',
          'vFeedbackFileSourceName',
          'vFeedbackExplanation',
          'mFeedbackTime',
          'mFeedbackExplanation'
        ])
        this.vendorFeedbackForm = _pick(formInfo, [
          'vendorAssesId',
          'vFeedbackTime',
          'vFeedbackFileUploadId',
          'vFeedbackFileSourceName',
          'vFeedbackExplanation'
        ])
        this.buyersProcessForm = _pick(formInfo, [
          'vendorAssesId',
          'mFeedbackTime',
          'status',
          'mFeedbackExplanation'
        ])
        if (!this.buyersProcessForm.mFeedbackTime) {
          this.buyersProcessForm.mFeedbackTime = new Date()
        }
        if (res.data.status === 'VENDOR_FEEDBACK') {
          // 采购商未处理 表单状态设置空
          this.buyersProcessForm.status = ''
        }
        // 流程ID
        let cbpmInstaceId = res.data.cbpmInstaceId
        if (cbpmInstaceId && this.curRole === 'BUYER') {
          this.openWorkFlow = true
          this.resultFlow = true
          this.resultFlowParams = {
            fdId: cbpmInstaceId
          }
          this.$nextTick(() => {
            this.sampleActiveInfo = 'tab2' // 切换到流程卡
          })
        }
        // 查询旧数据
        let params = {}
        params.indicatorType = 'ASSESSMENT'
        params.indicatorDimension = formInfo.indicatorDimension
        this.getPefTempIndicatorByDim(params) // 查询考核指标下拉
        let indicatorHeaderId = formInfo.indicatorHeadId
        this.findPerfTemplateLineAndIndsLine({ indicatorHeaderId }) // 查询
      })
    },

    // 指标维度切换
    indicatorDimensionChange (val) {
      // 查询维度下的指标列表
      if (val) {
        let params = {}
        params.indicatorType = 'ASSESSMENT'
        params.indicatorDimension = val
        this.getPefTempIndicatorByDim(params) // 查询考核指标下拉
      }
      this.assessmentForm.indicatorHeadId = null
      this.assessmentForm.indicatorName = ''
      this.assessmentForm.indicatorLineDes = ''
      this.assessmentForm.actualAssessmentAmountY = null
      this.assessmentForm.actualAssessmentAmountN = null
    },
    // 根据指标类型和指标维度查询指标
    getPefTempIndicatorByDim (params) {
      performanceManagement.getPefTempLineByDim(params).then(res => {
        this.indicatorNameList = res.data
      })
    },
    // 指标名称切换
    indicatorNameChange (val) {
      if (val) {
        const row = this.indicatorNameList.find(item => {
          return item.indicatorName === val
        })
        if (row) {
          this.assessmentForm.indicatorHeadId = row.indicatorHeadId
          let indicatorHeaderId = row.indicatorHeadId
          this.findPerfTemplateLineAndIndsLine({ indicatorHeaderId }) // 查询
        }
      }
      this.assessmentForm.indicatorLineDes = ''
      this.assessmentForm.actualAssessmentAmountY = null
      this.assessmentForm.actualAssessmentAmountN = null
    },
    // 根据绩效模型指标ID或指标库行ID获取绩效指标详情信息
    findPerfTemplateLineAndIndsLine (params) {
      performanceManagement.findPerfTemplateLineAndIndsLine(params).then(res => {
        this.indicatorLineList = res.data.perfTemplateIndsLineList
        console.log(this.indicatorLineList)
      })
    },
    evaluationResultsChange (id) {
      const row = this.indicatorLineList.find((datas)=>{return datas.templateIndsLineId==id})
      this.assessmentForm.assessmentPenalty = row.assessmentPenalty
    },
    // 报价输入金额改变
    handleChangeAmount (data) {
      // 计算未税单价
      this.computeAssessmentAmountWithTax()
    },
    computeAssessmentAmountWithTax () {
      // 计算未税单价
      const { actualAssessmentAmountY, taxCode } = this.assessmentForm
      let detailData = this.assessmentForm
      console.log(taxCode)
      if (taxCode) {
        // 含税价 = 不含税价 * （1+税率） times ||  不含税价 = 含税价 / （1+税率）div
        if (actualAssessmentAmountY) {
          let rateValue = taxCode.toString().replace(/%/g, '')
          let rateValuePercent = new Big(rateValue || 0).div(100)
          let rateTotal = new Big(rateValuePercent || 0).plus(1)
          let rateTotalValue = rateTotal.valueOf()
          detailData.actualAssessmentAmountN = new Big(actualAssessmentAmountY)
            .div(rateTotalValue)
            .toFixed(2)
            .valueOf() // 未税单价
        }
      } else {
        this.$message({
          message: this.$t('perfMod.selectAssessmenRate'), // 请选择考核税率
          type: 'error'
        })
      }
    },
    selectHandler (e, value, scope) {
      scope.organizationId = e ? e.organizationId : null
      scope.organizationCode = e ? e.organizationCode : ''
      scope.organizationName = e ? e.organizationName : null
    },
    // 选择供应商回调
    getVendorObj (val, scope) {
      scope.ceeaVendorId = val ? val.companyId : null
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    // 确认选择品类
    comfirmCategory (node, scope) {
      scope.categoryId = node ? node.categoryId : null
      scope.categoryName = node ? node.categoryName : ''
      scope.categoryCode = node ? node.categoryCode : ''
      scope.categoryFullName = node ? node.categoryFullName : ''
    },
    // 确认选择物料
    getItemObj (val, scope) {
      scope.materialId = val ? val.materialId : null
      scope.materialCode = val ? val.materialCode : ''
      scope.materialName = val ? val.materialName : ''
      scope.categoryId = val ? val.categoryId : null
      scope.categoryName = val ? val.categoryName : ''
      scope.categoryCode = val ? val.categoryCode : ''
      scope.categoryFullName = val ? val.categoryFullName : ''
    },
    getUserObj (val, scope) {
      scope.respFullName = val ? val.nickname : ''
      scope.respUserName = val ? val.username : ''
    },
    // 考核附件上传成功
    handleUploadSuccess (file) {
      const { fileId = '', fileName = '' } = file || {}
      this.assessmentForm.fileUploadId = fileId.toString()
      this.assessmentForm.fileSourceName = fileName
    },
    // 控制类型切换
    statusChange (val) {
      if (val) {
        let row = this.statusList.find(item => {
          return val === item.value
        })
      }
    },
    // 获取选择器
    getPeople (data) {
      let user = data[0]
      this.assessmentForm.respFullName = user.nickname || user.username
      this.assessmentForm.respUserName = user.username
    },
    // 已提交状态的审批
    approvalSB () {
      let taxCode = this.assessmentForm.taxCode
      this.assessmentForm.status = 'REVIEWED'
      this.assessmentForm.mFeedbackExplanation = this.buyersProcessForm.mFeedbackExplanation
      const submitData = {
        ...this.assessmentForm,
        taxCode
      }
      // 提交审批
      performanceManagement.vendorAssesModify(submitData).then(res => {
        if (res.code == '0') {
          this.$message.success(res.message)
          this.$emit('tab-remove', this.$attrs.params.tabName)
          this.__setTabTodo('assessmentList.getQuerydata')
        } else {
          this.$message.error(res.message)
        }
      })
    },
    // 通知供应商
    notifySupplier () {
      let taxCode = this.assessmentForm.taxCode
      const taxKey = (this.taxList.find(j => j.value === taxCode) || {}).key || ''
      let submitData = { ...this.assessmentForm, taxKey }
      performanceManagement.notifySupplier([submitData]).then(res => {
        this.$message({
          message: res.message,
          type: 'success'
        })
        this.$emit('tab-remove', this.$attrs.params.tabName)
        this.__setTabTodo('assessmentList.getQuerydata')
      })
    },
    // 提交供应商反馈
    vendorFeedback () {
      let submitData = this.vendorFeedbackForm
      performanceManagement.vendorFeedback(submitData).then(res => {
        this.$message({
          message: res.message,
          type: 'success'
        })
        this.$emit('tab-remove', this.$attrs.params.tabName)
        this.__setTabTodo('assessmentList.getQuerydata')
      })
    },
    // 采购商提交处理结果
    submitProcessRes () {
      let submitData = this.buyersProcessForm
      performanceManagement.buyersProcess(submitData).then(res => {
        this.$message({
          message: res.message,
          type: 'success'
        })
        this.$emit('tab-remove', this.$attrs.params.tabName)
        this.__setTabTodo('assessmentList.getQuerydata')
      })
    },
    // 保存
    saveHandle () {
      this.$refs.assessmentForm.validate(valid => {
        if (!valid) {
          return this.__jump_error__('assessmentFormBill', null, this.$t('vendorMod.enterRequired'))
        } else {
          this.dataHandle()
        }
      })
    },
    // submit
    submitHandle () {
      this.$refs.assessmentForm.validate(valid => {
        if (!valid) {
          return this.__jump_error__('assessmentFormBill', null, this.$t('perfMod.enterRequired'))
        } else {
          let taxCode = this.assessmentForm.taxCode
          const taxKey = (this.taxList.find(j => j.value === taxCode) || {}).key || ''
          let submitData = { ...this.assessmentForm, taxKey }
          this.$http({
            url: '/api-pef/vendorAsses/submitBatch',
            method: 'POST',
            data: submitData,
            loading: true
          }).then(res => {
            this.$message({
              message: res.message,
              type: 'success'
            })
            this.toBack()
          })
        }
      })
    },

    // 数据保存更新
    dataHandle () {
      let taxCode = this.assessmentForm.taxCode
      const taxKey = (this.taxList.find(j => j.value === taxCode) || {}).key || ''
      let submitData = { ...this.assessmentForm, taxKey }
      if (this.curOpt === 'add' || this.curOpt === 'adds') {
        // 新增
        performanceManagement.vendorAssesAdd(submitData).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.curOrderId = res.data.vendorAssesId
          this.toBack()
          // this.getOrderFormDetail() // 查询单据数据
        })
      } else {
        performanceManagement.vendorAssesModify(submitData).then(res => {
          this.$message({
            message: res.message,
            type: 'success'
          })
          this.toBack()
          // this.getOrderFormDetail() // 查询单据数据
        })
      }
    },
    // 返回
    toBack () {
      if (this.curOpt === 'adds') {
        this.$router.push('/performanceManagement/performanceAssessment')
      } else {
        this.$emit('tab-remove', this.$attrs.params.tabName)
        this.__setTabTodo('AssessmentList.getQuerydata')
      }
    }
  }
}
</script>
<style scoped lang="scss">
.the-vendorEffectDetail-detail {
  .el-table .el-date-editor {
    width: 135px;
  }
  .el-collapse-item__content > .el-button {
    margin-bottom: 5px;
  }
}
</style>
<style>
.orgCatPage .c-pagination {
  margin: 10px 5px;
}
.orgCatPage .c-pagination .el-input__inner {
  height: 24px !important;
}
</style>
