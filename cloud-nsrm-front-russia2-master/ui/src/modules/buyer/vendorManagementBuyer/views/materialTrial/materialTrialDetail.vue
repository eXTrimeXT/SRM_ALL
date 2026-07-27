<template>
  <el-container
    class="flex-container the-materialTrialDetail-detail"
    direction="vertical"
  >
    <el-main>
      <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :funParams="workflowParamsInfo"
        :buttonConfigInfo="buttonConfigInfo"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="(type) => submittedHandle(type)"
        @submit-direct="(type) => submittedHandle(type)"
        @confirm="(type, comment) => submittedHandle(type, comment)"
        @close-tab="back"
      >
        <div class="stepDiv">
          <VendorAccessSteps
            access-type="material"
            :approve-status="orderStatus"
          />
        </div>
        <el-collapse v-model="activeDims" class="tab-form-style">
          <el-collapse-item
            :title="$t('vendorMod.mtTrialOrderInfo')"
            name="1"
          >
            <el-form
              ref="formInfo"
              :model="form"
              :rules="formRules"
              class="form-fill-style"
              :disabled="
                curRole === 'VENDOR' ||
                  curOpt === 'view' ||
                  (orderStatus !== 'DRAFT' && orderStatus !== '')
              "
            >
              <srm-row>
                <srm-col>
                  <el-form-item
                    :label="$t('vendorMod.mtTrialNum')"
                    prop="trialNumber"
                  >
                    <el-input
                      v-model="form.trialNumber"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('common.vendorName')"
                    prop="vendorName"
                  >
                    <QuickSearch
                      :disabled="
                        curRole === 'VENDOR' ||
                          curOpt === 'view' ||
                          (orderStatus !== 'DRAFT' && orderStatus !== '')
                      "
                      :show-input="form.vendorName"
                      show-key="companyName"
                      :scope-data="form"
                      name="scc_sup_company_info2"
                      @close-quicksearch="getCompanyObj"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item :label="$t('material.types')" prop="entryType">
                    <DictSelect
                      v-model="form.entryType"
                      code="MATERRIAL_ENTRY_TYPE"
                      @change="entryTypeChange"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col v-if="form.entryType === 'ENTRY_TYPE'">
                  <el-form-item :label="$t('sourcingBuyer.reviewFormNumber')" prop="reviewFormNumber">
                    <QuickSearch
                      :disabled="
                        curRole === 'VENDOR' ||
                          curOpt === 'view' ||
                          (orderStatus !== 'DRAFT' && orderStatus !== '')
                      "
                      :show-input="form.reviewFormNumber"
                      :pre-query-data="queryReviewFormParame"
                      show-key="reviewFormNumber"
                      :scope-data="form"
                      name="scc_sup_auth_review_form"
                      @close-quicksearch="getReviewFormObj"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col v-if="form.entryType === 'OTHER_TYPE'">
                  <el-form-item
                    :label="$t('vendorMod.sampleNum')"
                    prop="sampleNumber"
                  >
                    <QuickSearch
                      :disabled="
                        curRole === 'VENDOR' ||
                          curOpt === 'view' ||
                          (orderStatus !== 'DRAFT' && orderStatus !== '')
                      "
                      :show-input="form.sampleNumber"
                      :pre-query-data="querySimpleFormParame"
                      show-key="sampleNumber"
                      :scope-data="form"
                      name="scc_sup_auth_qua_sample"
                      @close-quicksearch="getSimpleFormObj"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('vendorMod.mtRequireTime')"
                    prop="trialRequireTime"
                  >
                    <el-date-picker
                      v-model="form.trialRequireTime"
                      type="date"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                      :placeholder="$t('vendorMod.datePicker')"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('vendorMod.trialStartTime')"
                    prop="trialStartDate"
                  >
                    <el-date-picker
                      v-model="form.trialStartDate"
                      type="date"
                      :picker-options="pickerOptionsStart"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                      :placeholder="$t('vendorMod.datePicker')"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('vendorMod.trialEndTime')"
                    prop="trialEndDate"
                  >
                    <el-date-picker
                      v-model="form.trialEndDate"
                      type="date"
                      :picker-options="pickerOptionsEnd"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                      :placeholder="$t('vendorMod.datePicker')"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('vendorMod.tryBatch')"
                    prop="tryBatch"
                  >
                    <DictSelect
                      v-model="form.tryBatch"
                      code="TRIAL_TIMES"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    :label="$t('vendorMod.orderStatus')"
                    prop="approveStatus"
                  >
                    <DictSelect
                      v-model="form.approveStatus"
                      code="SAMPLE_STATUS"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="2">
                  <el-form-item
                    :label="$t('vendorMod.trialRemark')"
                    prop="trialInstruction"
                  >
                    <el-input
                      v-model="form.trialInstruction"
                      type="textarea"
                      :rows="2"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
          </el-collapse-item>
          <el-collapse-item
            :title="$t('vendorMod.trialInfo')"
            name="2"
          >
            <el-form
              ref="orgCateJournalsRef"
              :model="orgCateJournalsRefForm"
              class="form-fill-style"
              :disabled="curRole === 'VENDOR' || curOpt === 'view'"
            >
              <QuickSearch
                :disabled="curRole === 'VENDOR' || curOpt === 'view'"
                name="scc_base_material_item_display"
                :btnTitle="$t('common.add')"
                showButton
                multiSelect
                style="margin-bottom:5px"
                @close-quicksearch="getMaterialList"
              />
              <el-table
                :data="orgCateJournalsRefForm.tableData"
                stripe
                border
                style="width: 100%"
                max-height="350px"
                tooltip-effect="dark"
              >
                <!-- 组织 -->
                <el-table-column
                  prop="orgId"
                  :label="$t('vendorMod.organization')"
                  min-width="120"
                >
                  <template slot-scope="scope">
                    <OrganizationSelector
                      v-if="scope.row.orgId == ''"
                      ref="organizationSelector"
                      v-model="scope.row.orgId"
                      :disabled="curOpt === 'view'"
                      nodeType="OU"
                      :scope="scope.row"
                      @select="sampleOrgHandle"
                    />
                    <span v-else>{{ scope.row.orgName }}</span>
                  </template>
                </el-table-column>
                <!-- 品类 -->
                <el-table-column
                  prop="categoryName"
                  :label="$t('common.category')"
                  min-width="120"
                >
                  <template slot-scope="scope">
                    <!-- <CCategorySelect
                        v-model="scope.row.categoryName"
                        :scope="scope.row"
                        showKey="categoryName"
                        @select="comfirmCategory"
                      /> -->
                    {{ scope.row.categoryName }}
                  </template>
                </el-table-column>
                <!-- 物料编码 -->
                <el-table-column
                  prop="materialCode"
                  :label="$t('common.materialCode')"
                  min-width="120"
                >
                  <template slot-scope="scope">
                    <QuickSearch
                      v-if="!scope.row.materialId"
                      :disabled="curOpt === 'view'"
                      :show-input="scope.row.materialCode"
                      :pre-query-data="{
                        't.category_id': scope.row.categoryId
                      }"
                      show-key="materialCode"
                      :scope-data="scope.row"
                      name="scc_base_material_item_display"
                      @close-quicksearch="getItemObj"
                    />
                    <span v-else>{{ scope.row.materialCode }}</span>
                  </template>
                </el-table-column>
                <!-- 物料名称 -->
                <el-table-column
                  prop="materialName"
                  :label="$t('common.materialName')"
                  min-width="120"
                />
                <!-- 试用数量 -->
                <el-table-column
                  prop="quantity"
                  :label="$t('vendorMod.trialQty')"
                  min-width="120"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-if="scope.row.otherTrialQty"
                      v-model="scope.row.otherTrialQty"
                      type="number"
                    />
                    <el-input
                      v-else
                      v-model="scope.row.quantity"
                      type="number"
                    />
                  </template>
                </el-table-column>
                <!-- 试用结果 -->
                <el-table-column
                  v-if="
                    (curRole === 'BUYER' && orderStatus === 'CONFIRMED') ||
                      orderStatus === 'SUBMITTED' ||
                      orderStatus === 'APPROVED'
                  "
                  prop="result"
                  :label="$t('vendorMod.trialResult')"
                  min-width="100"
                >
                  <template #header>
                    <i style="color: red">*</i>
                    {{ $t("vendorMod.trialResult") }}
                  </template>
                  <template slot-scope="scope">
                    <el-form-item
                      :prop="'tableData.' + scope.$index + '.result'"
                      :rules="[{ required: true }]"
                    >
                      <DictSelect
                        v-model="scope.row.result"
                        code="SAMPLE_TEST_RESULT"
                      />
                    </el-form-item>
                  </template>
                </el-table-column>
                <el-table-column
                  v-if="
                    (curRole === 'BUYER' && orderStatus === 'CONFIRMED') ||
                      orderStatus === 'SUBMITTED' ||
                      orderStatus === 'APPROVED'
                  "
                  prop="resultRemark"
                  :label="$t('vendorMod.sampleTestResult')"
                  min-width="120"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.resultRemark"
                      :disabled="orderStatus !== 'CONFIRMED'"
                    />
                  </template>
                </el-table-column>
                <!-- 操作列 -->
                <el-table-column
                  v-if="orderStatus === 'DRAFT' || curOpt === 'add'"
                  :label="$t('common.operation')"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      @click="deleteRow(scope)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-form>
          </el-collapse-item>
          <el-collapse-item
            v-if="
              false ||
                (curRole === 'VENDOR' && orderStatus === 'PUBLISHED') ||
                orderStatus === 'CONFIRMED' ||
                orderStatus === 'SUBMITTED' ||
                orderStatus === 'APPROVED'
            "
            :title="$t('vendorMod.vendorConfirm')"
            name="3"
          >
            <el-form
              ref="vendorForm"
              :model="vendorConfirmInfo"
              :rules="vendorConfirmInfoRules"
              class="form-fill-style"
              :disabled="orderStatus !== 'PUBLISHED' || (curOpt === 'view' && curRole !== 'VENDOR')"
            >
              <srm-row>
                <srm-col>
                  <el-form-item
                    prop="expressType"
                    :label="$t('vendorMod.expressType')"
                  >
                    <DictSelect
                      v-model="vendorConfirmInfo.expressType"
                      code="SEND_TYPE"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    prop="expressNumber"
                    :label="$t('vendorMod.expressNum')"
                  >
                    <el-input v-model="vendorConfirmInfo.expressNumber" />
                  </el-form-item>
                </srm-col>
                <srm-col>
                  <el-form-item
                    prop="estimatedDeliveryTime"
                    :label="$t('vendorMod.estimatedDeliveryTime')"
                  >
                    <el-date-picker
                      v-model="vendorConfirmInfo.estimatedDeliveryTime"
                      type="date"
                      :format="$formatDatePicker"
                      value-format="yyyy-MM-dd"
                      :placeholder="$t('common.pleaseSelect')"
                    />
                  </el-form-item>
                </srm-col>
                <srm-col :initCol="2">
                  <el-form-item
                    prop="vendorConfirmRemark"
                    :label="$t('vendorMod.vendorConfirmRemark')"
                  >
                    <el-input
                      v-model="vendorConfirmInfo.vendorConfirmRemark"
                      type="textarea"
                      :rows="2"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
          </el-collapse-item>

          <el-collapse-item
            :title="$t('vendorMod.attachment')"
            name="4"
          >
            <vendorAccessAttachment
              ref="sceneAttachment"
              v-model="fileRecords"
              sence-code="MATERIAL"
              :business-id="materialTrialId"
              :att-opt="curOpt"
              :up-file-info="fileInfo"
            />
          </el-collapse-item>
        </el-collapse>
        <template slot="buttonOne">
          <!-- 暂存| 发布|确认|拒绝|评价保存|提交|批准|驳回-->
          <el-button
            v-if="curRole === 'BUYER' && (orderStatus === 'DRAFT' || orderStatus === '')"
            @click="stagingHandle"
          >
            {{ $t('common.staging') }}
          </el-button>
          <el-button
            v-if="curRole === 'BUYER' && (orderStatus === 'DRAFT' || orderStatus === '')"
            type="primary"
            @click="publishHandle"
          >
            {{ $t('common.publish') }}
          </el-button>
          <el-button
            v-if="curRole === 'VENDOR' && orderStatus === 'PUBLISHED'"
            type="primary"
            @click="confirmedHandle"
          >
            {{ $t('common.affirm') }}
          </el-button>
          <el-button
            v-if="curRole === 'VENDOR' && orderStatus === 'PUBLISHED'"
            @click="refusedHandle"
          >
            {{ $t('common.refused') }}
          </el-button>
          <el-button
            v-if="curRole === 'BUYER' && orderStatus === 'CONFIRMED'"
            @click="saveTestResultsHandle"
          >
            {{ $t('vendorMod.saveEvaluate') }}
          </el-button>
        </template>
      </CWorkflowMulti>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import vendorAccessAttachment from 'modb@/vendorManagementBuyer/components/vendorAccessAttachment'
import OrganizationSelector from 'lib@/components/organization-selector'
import QuickSearch from 'lib@/components/QuickSearch'
import CCategorySelect from 'lib@/components/c-category-select'
import VendorAccessSteps from 'modb@/vendorManagementBuyer/components/VendorAccessSteps'
import WorkflowCommon from '@/library/mixins/workflow-common'
import { saveOrUpdateOrderByUrl, accessCommonApi, materialTrialApi, quaApi } from 'modb@/vendorManagementBuyer/api/vendorManagement'

export default {
  name: 'MaterialTrialDetail',
  components: {
    CToolbar,
    CCategorySelect,
    QuickSearch,
    vendorAccessAttachment,
    OrganizationSelector,
    VendorAccessSteps
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      sampleActiveInfo: 'tab1',
      fileInfo: {
        uploadType: 'DEF',
        sourceType: 'WEB_APP',
        fileModular: 'sup',
        fileFunction: 'sampleConfirmed',
        fileType: 'images'
      },
      fileRefresh: false,
      activeDims: ['1', '2', '3', '4'],
      curRole: this.$store.getters.userType, // vendor buyer
      curOpt: 'add',
      activeStatus: 0,
      isTrial: false,
      materialTrialId: null,
      orderStatus: 'DRAFT',
      orgCateJournals: [],
      fileRecords: [],
      form: {
        vendorId: null,
        vendorName: '',
        vendorCode: '',
        sampleId: null,
        sampleNumber: '', // 样品单号
        trialRequireTime: '', // 物料需要时间
        trialStartDate: '', // 试用开始时间
        trialEndDate: '', // 试用结束时间
        tryBatch: '', // 批次
        trialInstruction: '', // 试用说明
        entryType: '', // 物料试用类型
        reviewFormNumber: '', // 资质审查单号
        reviewFormId: '' // 资质审查单id
      },
      formRules: {
        vendorName: [{ required: true, message: this.$t('vendorMod.msgVendorId') }], // '请输入供应商名称
        trialRequireTime: [{ required: true, message: this.$t('vendorMod.mgsMtRequireTime') }], // '请输入物料需要时间
        trialStartDate: [{ required: true, message: this.$t('vendorMod.mgsTrialStartTime') }], // '请输入试用开始时间
        trialEndDate: [{ required: true, message: this.$t('vendorMod.mgsTrialEndTime') }], // '请输入试用结束时间
        // sampleNumber: [{ required: true, message: this.$t('common.pleaseInput') }]
        // '请输入物料试用类型'
        entryType: [{ required: true, message: i18nExpression('cusEntry.supplement20250211.inputMaterialTrialType') }],
        // '请输入资质审查单号'
        reviewFormNumber: [{ required: true, message: i18nExpression('cusEntry.supplement20250211.inputQualificationReviewNumber') }]
      },
      vendorConfirmInfo: {
        // 供应商回复信息
        expressType: '', // 送样方式
        expressNumber: '', // 物流单号
        estimatedDeliveryTime: '', // 预计送达时间
        vendorConfirmRemark: '' // 确认说明
      },
      vendorConfirmInfoRules: {
        expressType: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }], // '请输入
        estimatedDeliveryTime: [{ required: true, message: this.$t('vendorMod.pleaseEnter') }] // '请输入
      },
      orgCateJournalsRefForm: {
        tableData: []
      },
      approveStatus: [], // 审批状态
      testResult: [], // 测试结果
      sendType: [], // 送样方式
      trialTimes: [], // 试用批次
      yesOrNo: [],
      attachTableData: [], // 附件
      isSampleFormId: false, // 是否选择了资质审查单号
      orgCatOldData: [],
      queryParame: {},
      vendorList: [],
      sampleOrderList: [],
      menuId: '',
      resultFlow: false,
      resultFlowParams: {}, // 结果审批流程参数
      openWorkFlow: false, // 审批流程相关参数
      fileUploadIds: [],
      querySimpleFormParame: {}, // 查询样品确认单据入参
      pickerOptionsStart: {
        disabledDate: time => {
          const endDateVal = new Date(this.form.trialEndDate).getTime()
          if (endDateVal) {
            return time.getTime() > endDateVal - 0
          }
        }
      },
      pickerOptionsEnd: {
        disabledDate: time => {
          const beginDateVal = new Date(this.form.trialStartDate).getTime()
          console.log(beginDateVal)
          if (beginDateVal) {
            return time.getTime() < beginDateVal - 0
          }
        }
      }
    }
  },
  computed: {
    // 资质审查单入参
    queryReviewFormParame () {
      return {
        't.vendor_id': this.form.vendorId,
        't.approve_status': 'APPROVED',
        't.CEEA_IF_VENDOR_AUTH': 'Y'
      }
    },
    viewUpdateButton () { // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
      return this.curRole === 'BUYER' && this.orderStatus === 'CONFIRMED'
    },
    workflowBusinessId () { // 用来指定工作流的业务ID
      return this.$attrs.params.materialTrialId ? this.$attrs.params.materialTrialId : null
    },
    // 展示工作流tab页
    workflowTabDisabled () {
      return !['SUBMITTED', 'APPROVED', 'WITHDRAW', 'REJECTED'].includes(this.orderStatus)
    },
    curStatus: function () {
      if (this.orderStatus === 'DRAFT' || this.orderStatus === '') {
        return 0
      } else if (this.orderStatus === 'PUBLISHED') {
        return 1
      } else if (this.orderStatus === 'CONFIRMED') {
        return 2
      } else if (this.orderStatus === 'SUBMITTED') {
        return 3
      } else if (this.orderStatus === 'APPROVED') {
        // 已审批
        return 4
      } else {
        return 0
      }
    }
  },
  watch: {
    // 监听保存提交 按钮变更状态，如果自定义按钮则无需添加
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = false
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    }
  },
  created () {
    this.buttonConfigInfo.save.view = false
    this.buttonConfigInfo.submit.view = this.viewUpdateButton
    this.buttonConfigInfo.cancel.view = false
    this.buttonConfigInfo.close.view = false

    this.curOpt = this.$attrs.params.flag
    console.log(this.curOpt)
    if (
      this.$attrs.params.flag === 'edit' ||
      this.$attrs.params.flag === 'view' ||
      this.$attrs.params.flag === 'doApproval'
    ) {
      this.materialTrialId = this.$attrs.params.materialTrialId
      this.fatchOldData() // 查询旧数据
    }
  },
  methods: {
    // 物料试用类型切换
    entryTypeChange (val) {
      this.form.reviewFormNumber = null
      this.form.reviewFormId = null
      this.form.sampleId = null
      this.form.sampleNumber = null
      this.orgCateJournalsRefForm.tableData = []
    },
    // 选择资质审查单据回调 (快速查询回调)
    getReviewFormObj (val) {
      if (val) {
        this.form.reviewFormNumber = val.reviewFormNumber
        this.form.reviewFormId = val.reviewFormId
        if (!this.form.reviewFormId) return
        this.$http({
          url: '/api-sup/review/reviewForm/getReviewFormDTO',
          method: 'GET',
          params: { reviewFormId: this.form.reviewFormId },
          loading: true
        }).then(res => {
          let { cateJournals, orgJournals } = res.data
          let arr = []
          for (let item of orgJournals) {
            for (let innerItem of cateJournals) {
              arr.push({
                orgId: item.orgId,
                orgCode: item.orgCode,
                orgName: item.orgName,
                categoryId: innerItem.categoryId,
                categoryCode: innerItem.categoryCode,
                categoryName: innerItem.categoryName,
                materialId: null,
                materialCode: null,
                materialName: null,
                quantity: null,
                result: null,
                resultRemark: null
              })
            }
          }
          this.orgCateJournalsRefForm.tableData = arr
        })
      } else {
        this.form.reviewFormNumber = ''
        this.form.reviewFormId = null
      }
    },
    back () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('MaterialTrialList.getQuerydata')
    },
    async getWorkflowBusinessVariables () { // 定义流程变量，如果没有可以不添加
      return {
        formNo: this.form.trialNumber
      }
    },
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'MATERIALTRIAL'
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    deleteRow (scope) {
      this.orgCateJournalsRefForm.tableData.splice(scope.$index, 1)
    },
    // 新增物料信息
    // addorgCateJournals () {
    //   this.orgCateJournalsRefForm.tableData.push({
    //     orgId: '',
    //     orgCode: '',
    //     orgName: '',
    //     categoryName: '',
    //     categoryCode: '',
    //     materialCode: '',
    //     materialName: '',
    //     quantity: '',
    //     result: '',
    //     resultRemark: ''
    //   })
    // },
     getMaterialList (data) {
      if (data.length > 0) {
        let materialIdList = []
        for (let item of this.orgCateJournalsRefForm.tableData) {
          item.materialId && materialIdList.push(item.materialId)
        }
        data.forEach(item => {
          console.log(item)
          if (item.materialId && !materialIdList.includes(item.materialId)) {
            this.orgCateJournalsRefForm.tableData.unshift({
              orgId: '',
              orgCode: '',
              orgName: '',
              categoryName: item.categoryName,
              categoryCode: item.categoryCode,
              materialId: item.materialId,
              materialName: item.materialName,
              materialCode: item.materialCode,
              quantity: '',
              result: '',
              resultRemark: ''
            })
          }
        })
      }
    },
    // 选择组织
    sampleOrgHandle (e, id, scope) {
      scope.orgId = e ? e.organizationId : null
      scope.orgCode = e ? e.organizationCode : ''
      scope.orgName = e ? e.organizationName : ''
    },
    // 选择供应商回调
    getCompanyObj (val, data) {
      this.form.vendorId = val ? val.companyId : ''
      this.form.vendorCode = val ? val.companyCode : ''
      this.form.vendorName = val ? val.companyName : ''
      this.form.entryType = null
      this.form.reviewFormNumber = null
      this.form.reviewFormId = null
      this.form.sampleId = null
      this.form.sampleNumber = null
      this.orgCateJournalsRefForm.tableData = []

      this.querySimpleFormParame = {
        // 查询样品确认单入参设置
        't.vendor_id': this.form.vendorId,
        // 't.approve_status': 'APPROVED',
        't.is_material_trial': 'Y'
      }
    },
    // 查询样品确认单回调
    getSimpleFormObj (val, data) {
      this.form.sampleId = val.sampleId || null
      this.form.sampleNumber = val.sampleNumber || ''
      if (val) {
        this.isSampleFormId = true
        let sampleId = val.sampleId
        if (this.form.entryType !== 'ENTRY_TYPE') {
          this.fatchQuaFileConfig({ sampleId })
          this.fatchSampleCatData(sampleId)
        }
      } else {
        // Object.keys(formObj).map(key => (formObj[key] = ""));
        this.isSampleFormId = false
        this.fileRecords = []
      }
    },
    fatchSampleCatData (sampleId) {
      if (sampleId) {
        accessCommonApi.getQuaSampleData({ sampleId }).then((res) => {
          if (res && res.data) {
            res.data.orgCateJournals.map(
              (i) => ((i.orgCateJournalId = null), (i.result = null), (i.resultRemark = null))
            )
            this.orgCateJournalsRefForm.tableData = res.data.orgCateJournals || []
          }
        })
      }
    },
    // 查询附件信息
    fatchQuaFileConfig (query) {
      quaApi.getTemplateFilesBySampleId(query).then((res) => {
        if (res.data && res.data.length > 0) {
          this.fileRecords = res.data.map((i) => ({
            ...i,
            fileId: '',
            fileName: ''
          }))
        } else {
          this.fileRecords = []
        }
      })
    },
    // 确认选择品类
    comfirmCategory (node, scope) {
      scope.categoryId = node ? node.categoryId : null
      scope.categoryName = node ? node.categoryName : ''
      scope.categoryCode = node ? node.categoryCode : ''
      scope.categoryFullName = node ? node.categoryFullName : ''
      scope.materialCode = ''
      scope.materialName = ''
      scope.materialId = null
      this.queryParame = { 't.category_id': node.categoryId }
    },
    // 确认选择物料
    getItemObj (val, data) {
      data.materialId = val ? val.materialId : null
      data.materialCode = val ? val.materialCode : ''
      data.materialName = val ? val.materialName : ''
    },
    // 通过资质审查单据ID获取组织品类信息
    getOrgCatByReviewId (reviewFormId) {
      accessCommonApi.getOrgCatByReviewId({ reviewFormId }).then((res) => {
        if (res && res.data) {
          let formateArr = []
          let oldData = res.data
          if (oldData.length > 0) {
            oldData.forEach((elm) => {
              if (elm.orgInfo && elm.orgCategory) {
                formateArr.push({
                  orgId: elm.orgInfo && elm.orgInfo.orgId ? elm.orgInfo.orgId : '',
                  orgCode: elm.orgInfo && elm.orgInfo.orgCode ? elm.orgInfo.orgCode : '',
                  orgName: elm.orgInfo && elm.orgInfo.orgName ? elm.orgInfo.orgName : '',
                  parentOrgId:
                    elm.orgInfo && elm.orgInfo.parentOrgId ? elm.orgInfo.parentOrgId : '',
                  parentOrgCode:
                    elm.orgInfo && elm.orgInfo.parentOrgCode ? elm.orgInfo.parentOrgCode : '',
                  parentOrgName:
                    elm.orgInfo && elm.orgInfo.parentOrgName ? elm.orgInfo.parentOrgName : '',
                  categoryId:
                    elm.orgCategory && elm.orgCategory.categoryId ? elm.orgCategory.categoryId : '',
                  categoryName:
                    elm.orgCategory && elm.orgCategory.categoryName
                      ? elm.orgCategory.categoryName
                      : '',
                  categoryCode:
                    elm.orgCategory && elm.orgCategory.categoryCode
                      ? elm.orgCategory.categoryCode
                      : '',
                  categoryFullName:
                    elm.orgCategory && elm.orgCategory.categoryFullName
                      ? elm.orgCategory.categoryFullName
                      : '',
                  level: elm.orgCategory && elm.orgCategory.level ? elm.orgCategory.level : ''
                })
              }
            })
          }
          this.orgCatOldData = formateArr
        }
      })
    },
    // 查询旧数据
    fatchOldData () {
      let materialTrialId = this.materialTrialId
      if (materialTrialId) {
        materialTrialApi.getMaterialTrialById({ materialTrialId }).then((res) => {
          if (res && res.data) {
            let sampleData = res.data.materialTrial // 表单信息
            this.orderStatus = sampleData.approveStatus
            this.fileRecords = res.data.fileRecords || []
            this.orgCateJournalsRefForm.tableData = res.data.orgCateJournals || []
            console.log(this.orderStatus)
            // 单据
            this.form.materialTrialId = sampleData.materialTrialId
            this.form.trialNumber = sampleData.trialNumber
            this.form.vendorId = sampleData.vendorId
            this.form.vendorName = sampleData.vendorName
            this.form.vendorCode = sampleData.vendorCode
            this.form.sampleId = sampleData.sampleId // 样品确认单号
            this.form.sampleNumber = sampleData.sampleNumber // 样品单号
            this.form.receiver = sampleData.receiver // 样品接收人
            this.form.receiverPhone = sampleData.receiverPhone // 样品接收电话
            this.form.approveStatus = sampleData.approveStatus // 状态
            this.form.tryBatch = sampleData.tryBatch // 试用批次
            this.form.trialInstruction = sampleData.trialInstruction // 试用说明
            this.form.entryType = sampleData.entryType // 物料试用类型
            this.form.reviewFormNumber = sampleData.reviewFormNumber // 资质审查单号
            this.form.reviewFormId = sampleData.reviewFormId // 资质审查单号

            this.form.trialRequireTime = sampleData.trialRequireTime
            // ? this.$dayjs(sampleData.trialRequireTime).valueOf()
            // : ""; // 物料需要时间
            this.form.trialStartDate = sampleData.trialStartDate
            // ? this.$dayjs(sampleData.trialStartDate).valueOf()
            // : ""; // 试用开始时间
            this.form.trialEndDate = sampleData.trialEndDate
            // ? this.$dayjs(sampleData.trialEndDate).valueOf()
            // : ""; // 试用结束时间
            this.form.requireSendTime = sampleData.requireSendTime
            // ? this.$dayjs(sampleData.requireSendTime).valueOf()
            // : ""; // 要求送样时间
            // 供应商确认
            this.vendorConfirmInfo.expressType = sampleData.expressType // 送样方式
            this.vendorConfirmInfo.expressNumber = sampleData.expressNumber // 物流单号
            this.vendorConfirmInfo.estimatedDeliveryTime = sampleData.estimatedDeliveryTime
            // ? this.$dayjs(sampleData.estimatedDeliveryTime).valueOf()
            // : ""; // 预计送达时间
            this.vendorConfirmInfo.vendorConfirmRemark = sampleData.vendorConfirmRemark // 确认说明

            // 关联旧数据查询
            if (this.form.vendorId) {
              this.querySimpleFormParame = {
                // 查询样品确认单入参设置
                't.vendor_id': this.form.vendorId,
                // 't.approve_status': 'APPROVED',
                't.is_material_trial': 'Y'
              }
            }
            if (this.form.sampleId) {
              // 有样品查单 关联查询 组织和品类数据
              this.isSampleFormId = true
              this.getOrgCatByReviewId(this.form.sampleId)
            } else {
              this.isSampleFormId = false
            }
            if (this.orderStatus === 'SUBMITTED' && this.curRole === 'BUYER') {
              // 切换到工作流tab页
              var workflowMode = this.workflowParamsInfo.integrationMode === 'Product' || this.workflowParamsInfo.integrationMode === 'Iframe' || this.workflowParamsInfo.integrationMode === 'Self'
              if (workflowMode) {
                this.activeTabName = 'workflowTab'
              }
            }
          }
        })
      }
    },
    // 暂存
    stagingHandle () {
      this.$refs.formInfo.validate((valid) => {
        if (valid) {
          // 判断附件是否上传
          if (!this.$refs.sceneAttachment.validRequired()) {
            return false
          }
          this.dataHandle('stage')
        } else {
          this.__focus_error__()
          return false
        }
      })
    },
    // 发布
    publishHandle () {
      this.$refs.formInfo.validate((valid) => {
        if (valid) {
          // 判断附件是否上传
          if (!this.$refs.sceneAttachment.validRequired()) {
            return false
          }
          this.dataHandle('publish')
        } else {
          this.__focus_error__()
          return false
        }
      })
    },
    // 供应商确认
    confirmedHandle () {
      this.$refs.vendorForm.validate((valid) => {
        if (valid) {
          this.dataHandle('confirmed')
        } else {
          this.$message({
            message: this.$t('vendorMod.enterRequired'), // '请输入必填项',
            type: 'error'
          })
          return false
        }
      })
    },
    // 供应商拒绝
    refusedHandle () {
      this.dataHandle('refused')
    },
    // 保存测试结果
    saveTestResultsHandle () {
      this.$refs.orgCateJournalsRef.validate(val => {
        if (!val) {
          this.$message({
            message: this.$t('vendorMod.enterRequired'), // '请输入必填项',
            type: 'error'
          })
          return false
        }
        this.dataHandle('submittedSave')
      })
    },
    // 采购商维护测试结果后提交审批
    submittedHandle () {
      this.dataHandle('submitted')
    },
    // 审批通过
    approvedHandle () {
      this.dataHandle('approved')
    },
    // 驳回
    rejectedHandle () {
      this.dataHandle('rejected')
    },
    // 数据处理
    dataHandle (type) {
      let submitData = {}
      let url = ''
      let bolB = true
      console.log(type)
      if (type === 'stage') {
        // 暂存
        url = '/api-sup/materialTrial/saveTemporary'
        submitData.materialTrial = { ...this.form }
        submitData.orgCateJournals = this.orgCateJournalsRefForm.tableData
        submitData.fileRecords = this.fileRecords
      } else if (type === 'publish') {
        // 采购商填完单据后发布
        url = '/api-sup/materialTrial/publish'
        submitData.materialTrial = { ...this.form }
        submitData.orgCateJournals = this.orgCateJournalsRefForm.tableData
        submitData.fileRecords = this.fileRecords
        submitData.menuId = this.menuId
      } else if (type === 'confirmed') {
        // 供应商确认
        url = '/api-sup/materialTrial/confirmed'
        submitData = {
          ...this.form,
          ...this.vendorConfirmInfo
        }
      } else if (type === 'refused') {
        // 供应商拒绝
        url = '/api-sup/materialTrial/refused'
        submitData = {
          ...this.form,
          ...this.vendorConfirmInfo
        }
      } else if (type === 'submittedSave') {
        // 保存测试结果
        url = '/api-sup/materialTrial/submittedSave'
        submitData.materialTrial = { ...this.form }
        submitData.orgCateJournals = this.orgCateJournalsRefForm.tableData
        submitData.menuId = this.menuId
        if (submitData.orgCateJournals.length > 0) {
          submitData.orgCateJournals.forEach(e => {
            if (!e.result) {
              this.__jump_error__(
                'orgCateJournalsRef',
                null,
                this.$t('vendorMod.pleaseEnterTrialResults') // 请输入试用结果
              )
              bolB = false
            }
          })
        }
      } else if (type === 'submitted') {
        // 采购商维护测试结果后提交审批
        url = '/api-sup/materialTrial/submitWithFlow'
        submitData.materialTrial = { ...this.form }
        submitData.orgCateJournals = this.orgCateJournalsRefForm.tableData
        submitData.menuId = this.menuId
        if (submitData.orgCateJournals.length > 0) {
          submitData.orgCateJournals.forEach(e => {
            if (!e.result) {
              this.__jump_error__(
                'orgCateJournalsRef',
                null,
                this.$t('vendorMod.pleaseEnterTrialResults') // 请输入试用结果
              )
              bolB = false
            }
          })
        }
      } else if (type === 'approved') {
        // 审批
        url = 'api-sup/materialTrial/approved'
        submitData = {
          ...this.form,
          ...this.vendorConfirmInfo
        }
      } else if (type === 'rejected') {
        // 驳回
        url = '/api-sup/materialTrial/rejected'
        submitData = {
          ...this.form,
          ...this.vendorConfirmInfo
        }
      }
      if (!bolB) {
        return false
      }
      submitData.vendorId = parseInt(submitData.vendorId)
      submitData.reviewFormId = parseInt(submitData.reviewFormId)
      submitData.trialQty = parseInt(submitData.trialQty)
      if (!submitData.materialTrial.sourceData) submitData.materialTrial.sourceData = '手工创建' // 来源系统没有值就默认是手工创建

      saveOrUpdateOrderByUrl(url, submitData).then(async res => {
        if (res) {
          // 提交
          this.$message({
            message: res.message,
            type: 'success'
          })
          if (type === 'stage') {
            // 暂存 查询旧数据
            this.$emit('tab-remove', this.$attrs.params.tabName)
            this.__setTabTodo('MaterialTrialList.getQuerydata')
          } else if (type === 'submitted') {
            // 评价提交
            let resData = res.data
            // 走工作流 [[
            await this.fatchOldData(resData)
            await this.handlerAfter('SUBMIT')
          } else {
            this.$emit('tab-remove', this.$attrs.params.tabName)
            this.__setTabTodo('MaterialTrialList.getQuerydata')
          }
        }
      })
    }
  }
}
</script>
<style scoped lang="scss">
.the-materialTrialDetail-detail {
  .sub_header {
    padding: 4px 11px;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
  .el-collapse-item__content > .el-button {
    margin-bottom: 5px;
  }
  .addBtn {
    margin-bottom: 5px;
  }
}
</style>
