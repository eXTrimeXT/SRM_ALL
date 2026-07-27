<template>
  <el-container
    class="the-sampleConfirmedDetail-detail"
    direction="vertical"
  >
    <el-main>
      <div class="stepDiv">
        <VendorAccessSteps
          access-type="sample"
          :approve-status="orderStatus"
        />
      </div>
      <el-collapse
        v-model="activeDims"
        class="tab-form-style"
      >
        <el-collapse-item
          :title="$t('vendorMod.sampleOrderInfo')"
          name="1"
        >
          <el-form
            ref="sampleForm"
            :model="form"
            :rules="formRules"
            class="form-fill-style"
            :disabled="true"
          >
            <srm-row>
              <srm-col>
                <el-form-item
                  prop="sampleNumber"
                  :label="$t('vendorMod.sampleNum')"
                >
                  <el-input
                    v-model="form.sampleNumber"
                    disabled
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  prop="vendorName"
                  :label="$t('common.vendorName')"
                >
                  <QuickSearch
                    :show-input="form.vendorName"
                    show-key="companyName"
                    :scope-data="form"
                    name="scc_sup_company_info_display"
                    @close-quicksearch="getCompanyObj"
                  />
                </el-form-item>
              </srm-col>
              <!--  资质审查单号-->
              <srm-col>
                <el-form-item
                  prop="reviewFormNumber"
                  :label="$t('vendorMod.quaNum')"
                >
                  <QuickSearch
                    :show-input="form.reviewFormNumber"
                    :pre-query-data="queryReviewFormParame"
                    show-key="reviewFormNumber"
                    :scope-data="form"
                    name="scc_sup_auth_review_form"
                    @close-quicksearch="getReviewFormObj"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  prop="receiver"
                  :label="$t('vendorMod.sampleReceiver')"
                >
                  <el-input v-model="form.receiver" />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  prop="receiverPhone"
                  :label="$t('vendorMod.receiverPhone')"
                >
                  <el-input
                    v-model="form.receiverPhone"
                    type="text"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  prop="requireSendTime"
                  :label="$t('vendorMod.sendTime')"
                >
                  <el-date-picker
                    v-model="form.requireSendTime"
                    type="date"
                    value-format="yyyy-MM-dd"
                    :placeholder="$t('common.pleaseSelect')"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  prop="isMaterialTrial"
                  :label="$t('vendorMod.isTrial')"
                >
                  <DictSelect
                    v-model="form.isMaterialTrial"
                    code="YES_OR_NO"
                    :disabled="isTrial"
                  />
                </el-form-item>
              </srm-col>
              <srm-col>
                <el-form-item
                  prop="approveStatus"
                  :label="$t('vendorMod.orderStatus')"
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
                  prop="buyerConfirmRemark"
                  :label="$t('vendorMod.vendorConfirmRemark')"
                >
                  <el-input
                    v-model="form.buyerConfirmRemark"
                    type="textarea"
                    :rows="2"
                  />
                </el-form-item>
              </srm-col>
            </srm-row>
          </el-form>
        </el-collapse-item>
        <!-- 样品信息 -->
        <el-collapse-item
          :title="$t('vendorMod.sampleInfo')"
          name="2"
        >
          <el-form
            ref="refsampleInfo"
            class="form-fill-style"
            :disabled="true"
          >
            <QuickSearch
              v-if="orderStatus !== 'CONFIRMED'"
              name="scc_base_material_item_display"
              :btnTitle="$t('common.add')"
              showButton
              multiSelect
              style="margin-bottom:5px"
              @close-quicksearch="getMaterialList"
            />
            <el-table
              :data="orgCateJournals"
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
                  <span v-if="scope.row.vendorId">
                    {{ scope.row.orgName }}
                  </span>
                  <OrganizationSelector
                    v-else
                    ref="organizationSelector"
                    v-model="scope.row.orgId"
                    :parent-id="-1"
                    node-type="OU"
                    :placeholder="$t('common.pleaseSelect')"
                    :disabled="orderStatus === 'CONFIRMED' || curOpt === 'view'"
                    @select="selectHandler($event, scope.row)"
                  />
                </template>
              </el-table-column>
              <!-- 品类 -->
              <el-table-column
                prop="categoryName"
                :label="$t('common.category')"
                min-width="120"
              >
                <template slot-scope="scope">
                  <span v-if="scope.row.vendorId">
                    {{ scope.row.categoryName }}
                  </span>
                  <CCategorySelect
                    v-else
                    v-model="scope.row.categoryName"
                    :disabled="orderStatus === 'CONFIRMED' || curOpt === 'view'"
                    :scope="scope.row"
                    showKey="categoryName"
                    :placeholder="$t('vendorMod.msgCategoryNormalizer')"
                    @select="comfirmSelect"
                  />
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
                    :show-input="scope.row.materialCode"
                    :pre-query-data="{
                      't.category_id': scope.row.categoryId
                    }"
                    show-key="materialCode"
                    :scope-data="scope.row"
                    :disabled="orderStatus === 'CONFIRMED'"
                    name="scc_base_material_item_display"
                    @close-quicksearch="getItemObj"
                  />
                </template>
              </el-table-column>
              <!-- 物料名称 -->
              <el-table-column
                prop="materialName"
                :label="$t('common.materialName')"
                min-width="120"
              >
                <template slot-scope="scope">
                  <el-input
                    v-if="!scope.row.materialId"
                    v-model="scope.row.materialName"
                  />
                  <span v-else>{{ scope.row.materialName }}</span>
                </template>
              </el-table-column>
              <!-- 样品需求数量 -->
              <el-table-column
                prop="quantity"
                :label="$t('vendorMod.sampleQty')"
                min-width="120"
              >
                <template slot-scope="scope">
                  <el-input
                    v-model="scope.row.quantity"
                    :disabled="orderStatus === 'CONFIRMED'"
                    type="number"
                  />
                </template>
              </el-table-column>

              <el-table-column
                v-if="
                  orderStatus === 'SUBMITTED' ||
                    orderStatus === 'APPROVED'
                "
                prop="result"
                min-width="100"
              >
                <template slot="header">
                  <span>
                    <em class="required">*</em>
                  </span>
                  <!-- 测试结果 -->
                  <span>{{ $t('vendorMod.testResult') }}</span>
                </template>
                <template slot-scope="scope">
                  <DictSelect
                    v-model="scope.row.result"
                    code="SAMPLE_TEST_RESULT"
                  />
                </template>
              </el-table-column>
              <el-table-column
                v-if="
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
              <!-- 操作 -->
              <el-table-column
                v-if="orderStatus !== 'CONFIRMED'"
                fixed="right"
                :label="$t('common.operation')"
                width="100"
              >
                <template slot-scope="scope">
                  <el-button type="text" @click="deleteClick(scope)">
                    <!-- 删除 -->
                    {{ $t('common.delete') }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-form>
        </el-collapse-item>
        <!-- 样品确认单 -->
        <el-collapse-item
          v-if="
            orderStatus === 'PUBLISHED' ||
              orderStatus === 'CONFIRMED' ||
              orderStatus === 'SUBMITTED' ||
              orderStatus === 'APPROVED'
          "
          :title="$t('vendorMod.sampleOrderInfo')"
          name="3"
        >
          <el-form
            ref="vendorForm"
            :model="vendorConfirmInfo"
            :rules="vendorConfirmInfoRules"
            class="form-fill-style"
            :disabled="orderStatus !== 'PUBLISHED' || curOpt === 'view'"
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
            sence-code="SAMPLE"
            :business-id="curOrderId"
            :att-opt="curOpt"
            :up-file-info="fileInfo"
            :validate="true"
          />
        </el-collapse-item>
      </el-collapse>
      <CToolbar>
        <template v-if="curOpt != 'view'" slot="right">
          <el-button @click="goBack">
            {{ $t('common.cancel') }}
          </el-button>
          <el-button
            v-if="orderStatus === 'PUBLISHED'"
            type="primary"
            @click="confirmedHandle"
          >
            {{ $t('common.affirm') }}
          </el-button>
          <el-button
            v-if="orderStatus === 'PUBLISHED'"
            @click="refusedHandle"
          >
            {{ $t('common.refused') }}
          </el-button>
        </template>
      </CToolbar>
    </el-main>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import vendorAccessAttachment from 'modb@/vendorManagementBuyer/components/vendorAccessAttachment'
import CCategorySelect from 'lib@/components/c-category-select'
import VendorAccessSteps from 'modb@/vendorManagementBuyer/components/VendorAccessSteps'
import OrganizationSelector from 'lib@/components/organization-selector'
import { sampleConfirmed } from 'mods@/vendorManagementSupplier/api/index'

export default {
  name: 'SampleConfirmedDetail',
  components: {
    vendorAccessAttachment,
    CCategorySelect,
    CToolbar,
    QuickSearch,
    OrganizationSelector,
    VendorAccessSteps
  },
  mixins: [tabTodoWatch, tabTodoMixin],
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
      activeStatus: 0,
      curOpt: 'add',
      isTrial: false,
      sampleId: null,
      orderStatus: 'DRAFT',
      orgCateJournals: [], // 样品数据信息
      form: {
        // 样品单据信息
        vendorId: null,
        vendorCode: '',
        vendorName: '',
        reviewFormId: null, // 资质审查单号
        sampleNumber: '', // 样品单号
        receiver: '', // 样品接收人
        receiverPhone: '', // 样品接收电话
        requireSendTime: '', // 要求送样时间
        isMaterialTrial: '', // 是否试用
        approveStatus: '', // 状态
        buyerConfirmRemark: '' // 确认说明
      },
      formRules: {
        vendorName: [{ required: true, message: this.$t('vendorMod.msgVendorId') }], // 请输入供应商名称
        receiver: [{ required: true, message: this.$t('dataConfMod.msgReceiver') }], // 请输入样品接收人
        receiverPhone: [{ required: true, message: this.$t('vendorMod.mgsReceiverPhone') },
                        { pattern: /^((0\d{2,3}-\d{7,8})|(1[358479]\d{9}))$/, message: this.$t('vendorMod.ruleReceiverPhone'), trigger: 'blur' }], // 请输入合法手机号/电话号
        requireSendTime: [{ required: true, message: this.$t('vendorMod.mgsrequireSendTime') }]// 请输入要求送样时间
      },
      vendorConfirmInfo: {
        // 供应商回复信息
        expressType: '', // 送样方式
        expressNumber: '', // 物流单号
        estimatedDeliveryTime: '', // 预计送达时间
        vendorConfirmRemark: '' // 确认说明
      },
      vendorConfirmInfoRules: {
        expressType: [{ required: true, message: this.$t('common.pleaseInput') }], // '请输入
        estimatedDeliveryTime: [{ required: true, message: this.$t('common.pleaseInput') }] // '请输入
      },
      fileRecords: [],
      approveStatus: [], // 审批状态
      testResult: [], // 测试结果
      sendType: [], // 送样方式
      yesOrNo: [],
      attachTableData: [], // 附件
      isReviewFormId: false, // 是否选择了资质审查单号
      orgCatOldData: [],
      queryParame: {}, // 物料查询入参
      vendorList: [],
      reviewOrderList: [],
      fileUploadIds: [],
      queryReviewFormParame: {}, // 查询资质审查单据入参

      resultFlow: false,
      resultFlowParams: {}, // 结果审批流程参数
      openWorkFlow: false, // 审批流程相关参数
      menuId: '', // 菜单ID 审批流程相关参数
      sampleInfoTable: [
        {
          attrs: {
            minWidth: '120',
            label: () => this.$t('vendorMod.organization'), // 组织
            prop: 'orgName' // 有问题
          },
          slot: 'orgName'
        },
        {
          attrs: {
            minWidth: '120',
            label: () => this.$t('common.category'), // 品类
            prop: 'categoryName'
          },
          slot: 'categoryName'
        },
        {
          attrs: {
            minWidth: '120',
            label: () => this.$t('common.materialCode'), // 物料编码
            prop: 'materialCode'
          },
          slot: 'materialCode'
        },
        {
          attrs: {
            minWidth: '120',
            label: () => this.$t('common.materialName'), // 物料名称
            prop: 'materialName'
          },
          slot: 'materialName'
        },
        {
          attrs: {
            minWidth: '120',
            label: () => this.$t('vendorMod.sampleQty'), // 样品需求数量
            prop: 'quantity'
          },
          slot: 'quantity'
        },
        {
          attrs: {
            minWidth: '100',
            label: () => this.$t('vendorMod.testResult'), // 测试结果
            prop: 'result'
          },
          slot: 'result',
          rules: { required: true, message: this.$t('common.pleaseInput') }, // 请输入
          hidden: () => {
            return !(
              this.orderStatus === 'SUBMITTED' ||
              this.orderStatus === 'APPROVED'
            )
          }
        },
        {
          attrs: {
            minWidth: '120',
            label: this.$t('vendorMod.sampleTestResult'),
            prop: 'resultRemark'
          },
          slot: 'resultRemark',
          hidden: () => {
            return !(
              this.orderStatus === 'SUBMITTED' ||
              this.orderStatus === 'APPROVED'
            )
          }
        }
      ]
    }
  },
  computed: {
    viewUpdateButton () { // 用来控制保存、提交按钮是否可见。如果自定义按钮则无需添加
      return this.orderStatus === 'CONFIRMED'
    },
    workflowBusinessId () { // 用来指定工作流的业务ID
      return this.$attrs.params.sampleId ? this.$attrs.params.sampleId : null
    },
    // 展示工作流tab页
    workflowTabDisabled () {
      let bol = true
      if (this.orderStatus == 'SUBMITTED') {
        bol = false
      }
      if (this.orderStatus == 'APPROVED') {
        bol = false
      }
      return bol
    },
    curStatus: function () {
      if (this.orderStatus === 'DRAFT' || this.orderStatus === '') {
        // 拟定
        return 0
      } else if (this.orderStatus === 'PUBLISHED') {
        // 采购商发布
        return 1
      } else if (this.orderStatus === 'CONFIRMED') {
        // 供应商 确认
        return 2
      } else if (this.orderStatus === 'SUBMITTED') {
        // 采购上提交
        return 3
      } else if (this.orderStatus === 'APPROVED') {
        // 已审批
        return 4
      } else {
        return 0
      }
    }
  },
  watch: {},
  created () {
    this.fatchVendorData() // 查供应商
    this.curOpt = this.$attrs.params.flag
    if (
      this.$attrs.params.flag === 'edit' ||
      this.$attrs.params.flag === 'view' ||
      this.$attrs.params.flag === 'doApproval'
    ) {
      this.sampleId = this.$attrs.params.sampleId
      this.fatchOldData() // 查询旧数据
    }
  },
  methods: {
    // 供应商拒绝
    refusedHandle () {
      this.dataHandle('refused')
    },
    comfirmSelect (node, scope) {
      scope.categoryId = node ? node.categoryId : null
      scope.categoryName = node ? node.categoryName : ''
      scope.categoryCode = node ? node.categoryCode : ''
    },
    selectHandler (node, row) {
      row.orgId = node ? node.organizationId : null
      row.orgCode = node ? node.organizationCode : null
      row.orgName = node ? node.organizationName : null
    },
    back () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('SampleConfirmedList.getQuerydata')
    },
    deleteClick (spase) {
      this.orgCateJournals.splice(spase.$index, 1)
    },
     getMaterialList (data) {
      if (data.length > 0) {
        let materialIdList = []
        for (let item of this.orgCateJournals) {
          item.materialId && materialIdList.push(item.materialId)
        }
        data.forEach(item => {
          if (item.materialId && !materialIdList.includes(item.materialId)) {
            this.orgCateJournals.unshift({
              orgId: '',
              orgCode: '',
              orgName: '',
              categoryName: '',
              categoryCode: '',
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
    // 查询供应商
    fatchVendorData () {
      let parame = {
        pageNum: 1,
        pageSize: 200
      }
      sampleConfirmed.getVendorDataList(parame).then((res) => {
        if (res.data && res.data.list) {
          this.vendorList = res.data.list
        }
      })
    },
    // 选择供应商回调
    getCompanyObj (val, data) {
      this.form.vendorId = val ? val.companyId : ''
      this.form.vendorCode = val ? val.companyCode : ''
      this.form.vendorName = val ? val.companyName : ''
      this.queryReviewFormParame = {
        // 资质审查单入参
        't.vendor_id': this.form.vendorId,
        't.approve_status': 'APPROVED',
        't.CEEA_IF_VENDOR_AUTH': 'Y'
      }
    },
    // 查询是否物料试用
    async fatchIsMtTry (reviewFormId) {
      const res = await sampleConfirmed.getEntryConfigRecord({
        reviewFormId
      })
      if (res) {
        let trialProcess = res.data.ifMaterial
        if (trialProcess === 'Y') {
          this.form.isMaterialTrial = 'Y'
          this.isTrial = true
        } else if (trialProcess === 'N') {
          this.form.isMaterialTrial = 'N'
          this.isTrial = true
        } else if (trialProcess === 'C') {
          this.form.isMaterialTrial = 'N'
          this.isTrial = true
        } else {
          this.form.isMaterialTrial = ''
          this.isTrial = false
        }
      }
    },
    // 选择资质审查单据回调 (快速查询回调)
    getReviewFormObj (val, data) {
      let quaReviewType = val ? val.quaReviewType : ''
      this.form.quaReviewType = val ? quaReviewType : ''
      this.form.reviewFormNumber = val ? val.reviewFormNumber : ''
      this.form.reviewFormId = val ? val.reviewFormId : ''
      // 获取组织品类关系
      if (val) {
        this.getOrgCatByReviewId(val.reviewFormId)
        this.fatchIsMtTry(val.reviewFormId) // 判断是否需要物料试用
        let query = { reviewFormId: val.reviewFormId, type: 'SAMPLE' }
        this.fatchQuaFileConfig(query)
        this.fatchCatOrgDatas({ reviewFormId: val.reviewFormId }) // 查询组织品类物料
      }
    },
    fatchQuaFileConfig (query) {
      sampleConfirmed.getTemplateFilesByReviewFormId(query).then((res) => {
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
    // 根据资质审查单查询组织品类物料信息
    fatchCatOrgDatas (query) {
      sampleConfirmed.listOrgCateJournalByReviewId(query).then((res) => {
        this.orgCateJournals = res.data || []
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
      sampleConfirmed.getOrgCatByReviewId({ reviewFormId }).then((res) => {
        if (res && res.data) {
          let formateArr = []
          let oldData = res.data
          if (oldData.length > 0) {
            oldData.forEach((elm) => {
              if (elm.orgInfo && elm.orgCategory) {
                formateArr.push({
                  purchaseOrgId: elm.orgInfo && elm.orgInfo.orgId ? elm.orgInfo.orgId : '',
                  purchaseOrgCode: elm.orgInfo && elm.orgInfo.orgCode ? elm.orgInfo.orgCode : '',
                  purchaseOrgName: elm.orgInfo && elm.orgInfo.orgName ? elm.orgInfo.orgName : '',
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
                    elm.categoryFullName && elm.orgCategory.categoryFullName
                      ? elm.orgCategory.categoryFullName
                      : ''
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
      let sampleId = this.sampleId
      if (sampleId) {
        sampleConfirmed.getQuaSampleData({ sampleId }).then((res) => {
          if (res && res.data) {
            let sampleData = res.data.quaSample // 表单信息
            this.fileRecords = res.data.fileRecords || []
            this.orgCateJournals = res.data.orgCateJournals || []

            this.form.sampleId = sampleData.sampleId
            this.form.vendorId = sampleData.vendorId
            this.form.vendorName = sampleData.vendorName
            this.form.vendorCode = sampleData.vendorCode
            this.form.reviewFormId = sampleData.reviewFormId // 资质审查单号
            this.form.reviewFormNumber = sampleData.reviewFormNumber // 资质审查单号
            this.form.sampleNumber = sampleData.sampleNumber // 样品单号
            this.form.receiver = sampleData.receiver // 样品接收人
            this.form.receiverPhone = sampleData.receiverPhone // 样品接收电话
            this.form.requireSendTime = sampleData.requireSendTime // ? this.$dayjs(sampleData.requireSendTime).valueOf() : ""; // 要求送样时间
            this.form.isMaterialTrial = sampleData.isMaterialTrial // 是否试用
            this.form.approveStatus = sampleData.approveStatus // 状态
            this.form.buyerConfirmRemark = sampleData.buyerConfirmRemark // 确认说明
            this.orderStatus = sampleData.approveStatus
            console.log(this.orderStatus)

            this.vendorConfirmInfo.expressType = sampleData.expressType // 送样方式
            this.vendorConfirmInfo.expressNumber = sampleData.expressNumber // 物流单号
            this.vendorConfirmInfo.estimatedDeliveryTime = sampleData.estimatedDeliveryTime
            // ? this.$dayjs(sampleData.estimatedDeliveryTime).valueOf()
            // : ""; // 预计送达时间
            this.vendorConfirmInfo.vendorConfirmRemark = sampleData.vendorConfirmRemark // 确认说明
            // 关联旧数据查询
            if (this.form.vendorId) {
              this.queryReviewFormParame = {
                // 资质审查单入参
                't.vendor_id': this.form.vendorId,
                't.approve_status': 'APPROVED',
                't.if_site_form': 'Y'
              }
            }
            if (this.form.reviewFormId) {
              // 有资质审查单 关联查询 组织和品类数据
              this.isReviewFormId = true
              this.getOrgCatByReviewId(this.form.reviewFormId)
            }
            // 流程ID
            // let cbpmInstaceId = sampleData.cbpmInstaceId
            // if (cbpmInstaceId) {

            // }
          }
        })
      }
    },
    // 暂存
    stagingHandle () {
      this.dataHandle('stage')
    },
    // 发布
    publishHandle () {
      this.$refs.sampleForm.validate((valid) => {
        if (valid) {
          // 判断附件是否上传
          if (!this.$refs.sceneAttachment.validRequired()) {
            return false
          }
          if (this.orgCateJournals.length === 0) {
            this.__jump_error__('refsampleInfo', null, this.$t('vendorMod.sampleEmptyWarning'))
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
          this.__focus_error__()
          return false
        }
      })
    },
    // 保存测试结果
    saveTestResultsHandle () {
      this.dataHandle('submittedSave')
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
      if (type === 'stage') {
        // 暂存
        url = '/api-sup/qua/quaSample/saveTemporary'
        submitData.quaSample = { ...this.form }
        submitData.orgCateJournals = this.orgCateJournals
        submitData.fileRecords = this.fileRecords
      } else if (type === 'publish') {
        // 采购商填完单据后发布
        url = '/api-sup/qua/quaSample/publish'
        submitData.quaSample = { ...this.form }
        submitData.menuId = this.menuId // 提交流程必要参数
        submitData.orgCateJournals = this.orgCateJournals
        submitData.fileRecords = this.fileRecords
      } else if (type === 'confirmed') {
        // 供应商确认
        url = '/api-sup/qua/quaSample/confirmed'
        submitData = {
          ...this.form,
          ...this.vendorConfirmInfo
        }
      } else if (type === 'submittedSave') {
        // 保存测试结果
        url = '/api-sup/qua/quaSample/submittedSave'
        submitData.quaSample = { ...this.form }
        submitData.menuId = this.menuId // 提交流程必要参数
        submitData.orgCateJournals = this.orgCateJournals
      } else if (type === 'submitted') {
        // 采购商维护测试结果后提交审批
        // url = "/api-sup/qua/quaSample/submitted";
        url = '/api-sup/qua/quaSample/submitWithFlow'
        submitData.quaSample = { ...this.form }
        submitData.menuId = this.menuId // 提交流程必要参数
        submitData.orgCateJournals = this.orgCateJournals
      } else if (type === 'approved') {
        // 审批
        url = '/api-sup/qua/quaSample/approved'
        submitData = {
          ...this.form,
          ...this.vendorConfirmInfo
        }
      } else if (type === 'rejected') {
        // 驳回
        url = '/api-sup/qua/quaSample/rejected'
        submitData = {
          ...this.form,
          ...this.vendorConfirmInfo
        }
      }  else if (type === 'refused') {
        // 供应商确认
        url = '/api-sup/qua/quaSample/refused'
        submitData = {
          ...this.form,
          ...this.vendorConfirmInfo
        }
      }
      this.$refs.sampleForm.validate((valid, object) => {
        if (!valid) {
          this.__jump_error__(
            'sampleForm',
            null,
            this.$t('vendorMod.pleasefinishRequired')
          )
        } else {
          submitData.vendorId = parseInt(submitData.vendorId)
          submitData.reviewFormId = parseInt(submitData.reviewFormId)
          submitData.quantity = parseInt(submitData.quantity)
          sampleConfirmed.saveOrUpdatuOrderByStatus(url, submitData).then(async res => {
            if (res) {
              // 提交
              this.$message({
                message: res.message,
                type: 'success'
              })
              if (type === 'stage') {
                // 暂存查询旧数据
                this.sampleId = res.data
                this.curOpt = 'edit'
                this.fileRefresh = true
                this.fatchOldData()
              } else if (type === 'submitted') {
                // 评价提交
                let resData = res.data
                // 走工作流 [[
                await this.fatchOldData(resData)
                await this.handlerAfter('SUBMIT')
              } else {
                // this.sampleId = res.data.businessId;
                // this.fatchOldData();
                this.$emit('tab-remove', this.$attrs.params.tabName)
                this.__setTabTodo('SampleConfirmedList.getQuerydata')
              }
            }
          })
        }
      })
    },
    goBack () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('SampleConfirmedList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.the-sampleConfirmedDetail-detail {
  .el-table .el-date-editor {
    width: 135px;
  }
  .el-collapse-item__content > .el-button {
    margin-bottom: 5px;
  }
  .addBtn {
    margin-bottom: 8px;
  }
}
::v-deep .base-table {
  min-height: 150px;
}
::v-deep .table-wrapper {
  padding-left: 0px;
}
.required {
  color: #ff4949;
  padding-right: 2px;
}
</style>
