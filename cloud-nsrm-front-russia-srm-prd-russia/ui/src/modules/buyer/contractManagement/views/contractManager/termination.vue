<template>
  <el-container
    direction="vertical"
    class="flex-container contractInformation"
  >
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
        <div class="stepDiv">
          <el-steps
            :active="curStatus"
            :align-center="true"
            finish-status="success"
          >
            <el-step :title="$t('contractMod.terminationRelease')" />
            <el-step v-if="mergeForm.needVendorConfirm == 'Y'" :title="$t('contractMod.terminationDetermine')" />
            <el-step :title="$t('contractMod.terminationApproval')" />
            <el-step :title="$t('contractMod.termination')" />
          </el-steps>
        </div>
        <el-collapse
          v-model="activeDims"
          class="tab-form-style"
        >
          <!--合同信息-->
          <el-collapse-item
            v-if="IS_BUYER()"
            :title="$t('logisticsMod.contractInfo')"
            name="1"
          >
            <el-form
              ref="formHeader"
              :model="mergeForm"
              :disabled="IS_READ_ONLY || !IS_BUYER()"
            >
              <srm-row>
                <!--合同名称-->
                <srm-col
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('contractMod.contractName')"
                  >
                    <el-input v-model="mergeForm.contractName" disabled />
                  </el-form-item>
                </srm-col>
                <!--合同编号-->
                <srm-col
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('contractMod.contractNo')"
                  >
                    <el-input v-model="mergeForm.contractNo" disabled />
                  </el-form-item>
                </srm-col>
                <!--业务实体-->
                <srm-col
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('contractMod.fullPathId')"
                  >
                    <organization-selector
                      ref="m_ou"
                      v-model="mergeForm.buId"
                      :parent-id="-1"
                      :jump-login="jumpLogin"
                      :placeholder="$t('common.pleaseSelect')"
                      :disabled="true"
                      node-type="OU"
                      @select="buHandler"
                    />
                  </el-form-item>
                </srm-col>
                <!--供应商名称-->
                <srm-col
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('contractMod.vendorName')"
                  >
                    <quick-search
                      :show-input="mergeForm.vendorName"
                      show-key="companyName"
                      :scope-data="mergeForm"
                      name="scc_sup_company_info_new"
                      :disabled="true"
                      @close-quicksearch="getVendorObj"
                    />
                  </el-form-item>
                </srm-col>
                <!--合同有效期从-->
                <srm-col
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('contractMod.contractValidFrom')"
                  >
                    <el-date-picker
                      v-model="mergeForm.effectiveDateFrom"
                      type="date"
                      :placeholder="$t('common.pleaseSelectDate')"
                      format="yyyy-MM-dd"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <!--合同有效期至-->
                <srm-col
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('contractMod.contractValidTo')"
                  >
                    <el-date-picker
                      v-model="mergeForm.effectiveDateTo"
                      type="date"
                      :placeholder="$t('common.pleaseSelectDate')"
                      format="yyyy-MM-dd"
                      disabled
                    />
                  </el-form-item>
                </srm-col>
                <!--合同类型-->
                <srm-col
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('contractMod.mgsContractType')"
                  >
                    <dict-select
                      v-model="mergeForm.contractClass"
                      code="ELEM_CONTRACT_TYPE"
                      :disabled="true"
                      @change="elemContractTypeHandler"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
          </el-collapse-item>
          <!--终止信息-->
          <el-collapse-item
            v-if="IS_BUYER()"
            :title="$t('contractMod.terminationInformation')"
            name="2"
          >
            <el-form
              ref="formHeader2"
              :model="mergeForm"
              :rules="rules"
              :disabled="IS_READ_ONLY || !IS_BUYER()"
            >
              <srm-row>
                <!--是否需要供应商确认-->
                <srm-col
                  :initCol="4"
                >
                  <el-form-item
                    :label="$t('contractMod.supplierConfirmation')"
                    prop="needVendorConfirm"
                  >
                    <el-radio-group
                      v-model="mergeForm.needVendorConfirm"
                      style="width: 100%"
                    >
                      <el-radio label="Y">
                        {{ $t('common.yes') }}
                      </el-radio>
                      <el-radio label="N">
                        {{ $t('common.no') }}
                      </el-radio>
                    </el-radio-group>
                  </el-form-item>
                </srm-col>
                <!--终止原因-->
                <srm-col
                  :initCol="1"
                >
                  <el-form-item
                    :label="$t('contractMod.reasonTermination')"
                    prop="contractTerminationReason"
                  >
                    <el-input
                      v-model="mergeForm.contractTerminationReason"
                      type="textarea"
                      :rows="2"
                      maxlength="300"
                      show-word-limit
                      :placeholder="$t('common.pleaseTypeContents')"
                    />
                  </el-form-item>
                </srm-col>
                <!--起草人意见-->
                <srm-col
                  :initCol="1"
                >
                  <el-form-item
                    :label="$t('vendorMod.loggerComment')"
                  >
                    <el-input
                      v-model="mergeForm.drafterOpinion"
                      type="textarea"
                      :rows="2"
                      maxlength="300"
                      show-word-limit
                      :placeholder="$t('common.pleaseTypeContents')"
                    />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-form>
          </el-collapse-item>
          <!-- 合同附件信息 -->
          <el-collapse-item
            ref="file-area"
            :title="$t('contractMod.fileInfo')"
            name="7"
          >
            <el-button
              v-if="!IS_READ_ONLY && IS_BUYER()"
              style="margin-bottom: 10px"
              type="primary"
              class="detail-pbtn"
              @click="addUploadOne"
            >
              {{ $t("common.add") }}
            </el-button>
            <el-table
              :data="fileuploads"
              style="width: 100%"
              border
              max-height="250px"
            >
              <el-table-column
                align="center"
                type="index"
                :label="$t('common.sort')"
                width="50"
              />
              <!--协议类型-->
              <el-table-column
                align="center"
                prop="fileType"
                :label="$t('dataConfMod.attachmentType')"
              >
                <template slot-scope="scope">
                  {{ $getDictLabel('CONTRACT_AGREEMENT_ATTACHMENT', scope.row.fileType) }}
                </template>
              </el-table-column>
              <!-- 附件名称 -->
              <el-table-column
                align="center"
                prop="fileSourceName"
                :label="$t('bidMod.fileName')"
                :render-header="_addStarToColumn"
              >
                <template slot-scope="scope">
                  <SrmCommonFile
                    :extra-data="fileInfo"
                    :default-file="{
                      fileId: scope.row.fileuploadId,
                      fileName: scope.row.fileSourceName
                    }"
                    :readonly="false"
                    @on-change="({file}) => outerHandleUploadSuccess(file,scope.row)"
                  />
                </template>
              </el-table-column>
              <!-- 上传人 -->
              <el-table-column
                align="center"
                prop="createdUserName"
                :label="$t('purchaseDemand.attachmentCreatedBy')"
              />
              <!-- 上传时间 -->
              <el-table-column
                align="center"
                prop="creationDate"
                :label="$t('purchaseDemand.attachmentCreatedDate')"
              />
              <!-- 操作 -->
              <el-table-column
                v-if="IS_BUYER()"
                :label="$t('common.operation')"
                width="60"
              >
                <template slot-scope="scope">
                  <el-button
                    v-if="!scope.row.sourceId && scope.row.del !== 'N' && mergeForm.contractStatus !== 'ARCHIVED' && !IS_READ_ONLY && scope.row.fileType != 'SEAL_AGREEMENT'"
                    :disabled="illegal == 'view'"
                    type="text"
                    @click="handleDelClick(scope.$index, scope.row)"
                  >
                    {{ $t("common.delete") }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
        </el-collapse>
        <!-- 底部按钮定义 -->
        <template slot="buttonOne">
          <!--发布供应商暂存-->
          <el-button
            v-if="mergeForm.needVendorConfirm == 'Y' && IS_BUYER() &&
              (['DRAFT', 'SUPPLIER_REJECTED', 'WITHDRAW', 'REJECTED'].includes(mergeForm.contractStatus) ||
              ($attrs.params ? $attrs.params.flag === 'termination' : null))"
            type="primary"
            @click="submit('savePublish')"
          >
            {{ $t("common.staging") }}
          </el-button>
          <!--发布供应商-->
          <el-button
            v-if="mergeForm.needVendorConfirm == 'Y' && IS_BUYER() &&
              (['DRAFT', 'SUPPLIER_REJECTED', 'WITHDRAW', 'REJECTED'].includes(mergeForm.contractStatus) ||
              ($attrs.params ? $attrs.params.flag === 'termination' : null))"
            type="primary"
            @click="submit('publish')"
          >
            {{ $t("contractMod.releaseSupplier") }}
          </el-button>
          <!--供应商确认按钮-->
          <el-button
            v-if="!this.IS_BUYER() && this.mergeForm.contractStatus === 'SUPPLIER_CONFIRMING'"
            type="primary"
            @click="submit('SUPPLIER_CONFIRMING')"
          >
            {{ $t("orderMod.buyerOrderSynergy.confirm") }}
          </el-button>
          <!--供应商驳回按钮-->
          <el-button
            v-if="!this.IS_BUYER() && this.mergeForm.contractStatus === 'SUPPLIER_CONFIRMING'"
            type="primary"
            @click="submit('SUPPLIER_REFUSE')"
          >
            {{ $t("components.approvalHead.headers.refuse") }}
          </el-button>
        </template>
      </CWorkflowMulti>
    </el-main>
  </el-container>
</template>
<script>
import BaseForm from 'lib@/components/BaseForm'
import BaseTable from 'lib@/components/BaseTable'
import QuickSearch from 'lib@/components/QuickSearch'

import materialList from './material-list'
import payPlan from './pay-plan'
import partner from './partner'
import cloneDeep from 'lodash/cloneDeep'
import CToolbar from 'lib@/components/c-toolbar'
import { tabTodoMixin } from '@/utils/mixins'
import OrganizationSelector from 'lib@/components/organization-selector'
import WorkflowCommon from '@/library/mixins/workflow-common'
import DictSelect from '@/library/components/c-select/dict-select'

export default {
  name: 'ContractInformation',
  components: {
    CToolbar,
    BaseForm,
    BaseTable,
    DictSelect,
    payPlan,
    materialList,
    partner,
    QuickSearch,
    OrganizationSelector
  },
  directives: {
    // 使用局部注册指令的方式
    resize: {
      // 指令的名称
      bind (el, binding) {
        // el为绑定的元素，binding为绑定给指令的对象
        let width = ''
          let height = ''
        function isReize () {
          const style = document.defaultView.getComputedStyle(el)
          if (width !== style.width || height !== style.height) {
            binding.value() // 关键
          }
          width = style.width
          height = style.height
        }
        el.__vueSetInterval__ = setInterval(isReize, 300)
      },
      unbind (el) {
        clearInterval(el.__vueSetInterval__)
      }
    }
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      waterBol: 'Y', // 是否开启水印功能，Y为开启
      supplementaryAgreement: '',
      materialDataChangeBol: 0,
      pdfUrl: '',
      releaseParamsVisible: false,
      releaseParams: {
        name: '',
        phone: '',
        email: ''
      },
      rules: {
        needVendorConfirm: [{ required: true, message: this.$t('common.pleaseInput') }],
        isInvalidOldContract: [{ required: true, message: this.$t('common.pleaseInput') }],
        contractTerminationReason: [{ required: true, message: this.$t('common.pleaseInput') }]
      },
      watermark_wrapper_style: {
        position: 'absolute',
        top: 0,
        left: 0,
        zIndex: 0,
        width: '100%',
        height: '100%'
      },
      watermark_style: {
        display: 'inline-block',
        width: '650px',
        height: '247px',
        opacity: 0.3,
        margin: '0 auto'
      },
      preview_wrapper_style: {
        // IE浏览器 649px x 978px
        width: '794px',
        // height: '1123px',
        margin: '0 auto',
        position: 'relative'
      },
      contractDataList2: [],
      frameworkAgreementVisible: false,
      sumForm2: {
        vendorId: null,
        vendorCode: null,
        vendorName: null,
        isFrameworkAgreement: 'Y',
        pageSize: 9999,
        pageNum: 1
      },
      jumpLogin: true, // BPM在无登录情况下执行
      materialSelection: [], // 选择的物料
      illegal: '', // 兼容禁用
      curOpt: 'add', // 默认
      payPlanData: [],
      partnerData: [],
      watermark_base64: null,
      lastElemContractType: null,
      materialListData: [],
      IS_READ_ONLY: false,
      sourceList: [],
      queryParams: {},
      originCustomTable: {},
      menuInfo: null,
      selectionVisible: false,
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'vendorBiddingManagement',
        fileType: 'images'
      },
      activeDims: ['1', '2', '3', '4', '5', '6', '7', '8'],
      userType: this.$store.getters.userType,
      diffhtml: null,
      diffVisible: false,
      contenteditable: false,
      editorInstance: null,
      placeholder2html: {},
      modelList: [],
      modelLine: [],
      mergeForm: {
        contractHeadId: null,
        isInvalidOldContract: 'Y',
        contractTerminationReason: '',
        contractStatus: '',
        contractRemark: '',
        drafterOpinion: '',
        modelEnable: 'Y',
        needVendorConfirm: 'Y',
        formal: '',
        isFrameworkAgreement: 'Y',
        enable: 'Y',
        ceeaIfVirtual: 'N',
        currencyCode: 'CNY',
        currencyId: '7007437216088064',
        includeTaxAmount: 0,
        currencyName: '人民币',
        contractCode: '', // 合同编码
        partyContacts: '', // 甲方联系人
        partyPhone: '', // 甲方手机号
        partyAddress: '', // 甲方地址
        partyFax: '', // 甲方传真
        secondPartyContacts: '', // 乙方联系人
        secondPartyPhone: '', // 乙方手机号
        secondPartyAddress: '', // 乙方地址
        secondPartyFax: '', // 乙方传真
        thirdPartyContacts: '', // 丙方联系人
        thirdPartyPhone: '', // 丙方手机号
        thirdPartyAddress: '', // 丙方地址
        thirdPartyFax: '', // 丙方传真
        totalItems: '0', // 物料合计
        totalMaterialAmount: '0', // 物料金额合计(大写)
        partyTaxpayer: '', // 甲方-纳税人识别号
        partyBank: '', // 甲方-开户行
        partyBankAccount: '', // 甲方-银行账号
        partyTax: '', // 甲方-税号
        secondPartyTaxpayer: '', // 乙方-纳税人识别号
        secondPartyBank: '', // 乙方-开户行
        secondPartyBankAccount: '', // 乙方-银行账号
        secondPartyTax: '', // 乙方-税号
        thirdPartyTaxpayer: '', // 丙方-纳税人识别号
        thirdPartyBank: '', // 丙方-开户行
        thirdPartyBankAccount: '', // 丙方-银行账号
        thirdPartyTax: '' // 丙方-税号
      },
      fileuploads: [],
      childContext: null,
      visible: true,
      vendorIdList: [],
      currencyList: [],
      currencyList2: [],
      originMaterialTable: [],
      originPayPlanTable: [],
      bankRowIndex: null,
      taxList: [],
      currenRows: [],
      modelHeadIdList: [],
      columns: [
        {
          attrs: {
            width: '50',
            align: 'center',
            type: 'selection'
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            type: 'index',
            label: context => context.$t('contractMod.order')
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'materialCode',
            label: context => context.$t('contractMod.materialCode')
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'materialName',
            label: context => context.$t('contractMod.materialName')
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'categoryName',
            label: context => context.$t('contractMod.categoryName')
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'taxedPrice',
            label: context => context.$t('contractMod.taxedPrice')
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'contractQuantity',
            label: context => context.$t('contractMod.contractQuantity')
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'unitName',
            label: context => context.$t('contractMod.unitName')
          }
        },
        {
          attrs: {
            minWidth: '120',
            align: 'center',
            prop: 'buName',
            label: context => context.$t('contractMod.buId')
          }
        }
      ]
    }
  },
  computed: {
    workflowBusinessId () {
      // 用来指定工作流的业务ID
      return this.mergeForm.contractHeadId ? this.mergeForm.contractHeadId : null
    },
    workflowTabDisabled () {
      return !['SUPPLIER_CONFIRMED', 'REJECTED', 'WITHDRAW', 'APPROVAL', 'UNDER_REVIEW', 'UN_ARCHIVED', 'SIGNATUREING', 'ARCHIVED', 'TERMINATED', 'ABANDONED'].includes(this.mergeForm.contractStatus) || this.mergeForm.contractStatus === 'DRAFT'
    },
    viewUpdateButtonsubmit () {
      return (
        this.mergeForm.needVendorConfirm != 'Y' && this.IS_BUYER() &&
        (['DRAFT', 'WITHDRAW', 'REJECTED'].includes(this.mergeForm.contractStatus) ||
          (this.$attrs.params ? this.$attrs.params.flag === 'termination' : null))
      )
    },
    viewUpdateButtonSave () {
      return (
        this.mergeForm.needVendorConfirm != 'Y' && (['DRAFT', 'WITHDRAW', 'REJECTED'].includes(this.mergeForm.contractStatus) ||
          (this.$attrs.params ? this.$attrs.params.flag === 'termination' : null))
      )
    },
    disabledUpdateButton () {
      return this.SUBMIT_STATUS()
    },
    isFromPriceApproval () {
      return this.mergeForm.sourceType === 'PRICE_APPROVAL'
    },
    materialEditableRows () {
      return this.materialListData.filter(i => {
        if (i.handleMark === undefined || i.handleMark === 0) {
          return true
        }
        return false
      })
    },
    viewUpdateButton () {
      return (
        this.curRole === 'BUYER' &&
        !this.isReadOnly &&
        this.requirementHead.auditStatus !== 'APPROVED'
      )
    },
    curStatus: function () {
      if (this.mergeForm.contractStatus === 'DRAFT' || this.mergeForm.contractStatus === '' || this.mergeForm.contractStatus == 'ARCHIVED') {
        return 0
      } else if (this.mergeForm.contractStatus === 'SUPPLIER_CONFIRMED') {
        return 1
      } else if (this.mergeForm.contractStatus === 'UNDER_REVIEW') {
        return 2
      } else if (this.mergeForm.contractStatus === 'APPROVAL') {
        return 3
      } else {
        return 0
      }
    }
  },
  watch: {
    // 监听保存提交 按钮变更状态，如果自定义按钮则无需添加
    viewUpdateButtonsubmit () {
      this.buttonConfigInfo.submit.view = this.viewUpdateButtonsubmit
    },
    viewUpdateButtonSave () {
      this.buttonConfigInfo.save.view = this.viewUpdateButtonSave
    },
    disabledUpdateButton () {
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
    }
  },
  created () {
    this.buttonConfigInfo.save.name = '暂存'
    this.buttonConfigInfo.save.view = this.viewUpdateButtonSave
    this.buttonConfigInfo.submit.view = this.viewUpdateButtonsubmit
    this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
    this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
    this.buttonConfigInfo.cancel.view = false
    this.buttonConfigInfo.close.view = false
    this.illegal = this.$attrs.params.illegal
    this.jumpLogin = this.$attrs.params.jumpLogin
    if (this.illegal == 'view') {
      this.userType = 'BUYER'
    }
  },
  mounted () {
    this.curOpt = this.$attrs.params.flag
    this.initData()
  },
  methods: {
    // 指定工作流的业务类型，在定义工作流时指定
    async getWorkflowBusinessType () {
      return 'CONTRACT'
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    back () {
      this.cancel()
    },
    buHandler (node, value) {
      const { organizationCode, organizationName, fullPathId } = node
      this.mergeForm.buCode = organizationCode
      this.$set(this.mergeForm, 'buName', organizationName)
      this.mergeForm.buFullPathId = fullPathId
    },
    elemContractTypeHandler (value, force = true) {
      if (this.mergeForm.modelHeadId && force) {
        this.$confirm(this.$t('contractMod.clearModelMsg'), {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        })
          .then(() => {
            this.queryModelHeadList(value)
            this.mergeForm.modelHeadId = null
            this.$refs.markedContent.innerHTML = ''
          })
          .catch(() => {
            console.log('点击取消')
            this.mergeForm.contractClass = this.lastElemContractType
          })
      } else {
        this.lastElemContractType = value
        this.queryModelHeadList(value)
      }
    },
    queryModelHeadList (value) {
      this.$api.cm.buyer.main.modelListByType(value).then(res => {
        this.modelHeadIdList = res.data.map(i => ({
          id: i.modelCode,
          label: i.modelName,
          value: i.modelHeadId,
          type: i.modelType
        }))
      })
    },
    SUBMIT_STATUS () {
      return false
    },
    addUploadOne () {
      this.fileuploads.push({
        fileuploadId: null,
        fileSourceName: '',
        fileType: 'OTHER_AGREEMENT'
      })
    },
    outerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.fileSourceName = fileName
    },
    handleDelClick (index, row) {
      this.fileuploads.splice(index, 1)
    },
    IS_BUYER () {
      return this.userType === 'BUYER'
    },
    initData () {
      this.IS_READ_ONLY = this.$attrs.params.isReadOnly
      if (this.$attrs.params.flag !== 'add') {
        const { contractHeadId } = this.$attrs.params.row
        const contractType = 'TERMINATION'
        this.mergeForm.contractHeadId = contractHeadId
        this.setContractInfo(contractHeadId, contractType)
      }
    },
    async setContractInfo (
      contractHeadId,
      contractType,
      sourceId = '',
      isInit = true
    ) {
      const { data } = await this.$api.cm.buyer.main.contract.getInfoById(
        contractHeadId,
        sourceId
      )

      const {
        contractHead,
        modelLines,
        annexes,
        payPlans,
        contractMaterials,
        contractPartners
      } = data
      this.modelLine = modelLines
      this.fileuploads = annexes
      this.payPlanData = payPlans.map(i => ({
        ...i,
        payExplain: Number(i.payExplain)
      }))
      this.partnerData = contractPartners
      if (contractType === 'MIAN_CONTRACT_ADD') {
        // 新增
        this.mergeForm = Object.assign(this.mergeForm, contractHead)
      } else {
        const {
          contractType,
          contractCode,
          contractHeadId,
          ...rest
        } = contractHead
        if (this.$attrs.params.flag === 'add') {
          if (isInit) {
            this.mergeForm = Object.assign(this.mergeForm, { ...rest })
            this.$set(
              this.mergeForm,
              'contractOldCode', // 原合同编号更改byEasion
              contractCode
            )
            this.$set(
              this.mergeForm,
              'ceeaContractOldId', // 原合同id
              contractHeadId
            )
            this.$set(
              this.mergeForm,
              'contractType',
              this.$attrs.params.contractType
            )
            this.$set(this.mergeForm, 'contractChangeCode', '')
            this.$set(this.mergeForm, 'contractAgreementCode', '')
          } else {
            this.mergeForm = Object.assign(this.mergeForm, contractHead)
            this.$set(this.mergeForm, 'contractChangeCode', '')
            this.$set(this.mergeForm, 'contractAgreementCode', '')
          }
        } else {
          this.mergeForm = Object.assign(this.mergeForm, contractHead)
          this.$set(this.mergeForm, 'contractChangeCode', '')
          this.$set(this.mergeForm, 'contractAgreementCode', '')
        }
      }
      this.materialListData = contractMaterials.map(item => {
        const i = { ...item }
        if (i.tradingLocations) {
          let tradingLocations = i.tradingLocations
          try {
            tradingLocations = JSON.parse(tradingLocations)
          } catch (e) {
            console.log('送货地址转换出错')
          }
          i.tradingLocations = tradingLocations
        }
        return i
      })
      this.elemContractTypeHandler(this.mergeForm.contractClass, false)
      if (this.childContext) {
        this.childContext.elemKeys = modelLines.reduce((obj, i) => {
          const { modelKey, modelValue } = i
          let value = modelValue
          try {
            if (isNaN(modelValue)) {
              value = JSON.parse(modelValue)
            }
          } catch (e) {

          }
          obj[modelKey] = value
          return obj
        }, {})
      }

      if (this.$attrs.params.flag == 'termination') {
        this.fileuploads = []
      }

      // 当合同终止的时候，判断是否有解除协议如果没有的话需要新加一条
      let fileBol = true
      this.fileuploads.forEach((e) => {
        if (e.fileType === 'TERMINATION_AGREEMENT') {
          fileBol = false
        }
      })
      if (fileBol) {
        this.fileuploads.push({ fileType: 'TERMINATION_AGREEMENT' })
      }
    },
    preview () {
      if (this.childContext) {
        this.childContext.editable = false
      }
      this.contenteditable = false
    },
    async saveBill (type) {
      if (type === 'SUBMIT') {
        this.preview()
        try {
          await this.$refs.formHeader2.validate()
        } catch (e) {
          this.__focus_error__(this.$t('contractMod.msgContractManage[14]'))
          return
        }
        this.$nextTick(() => this.submit('approval'))
      } else if (type === 'SAVE') {
        this.preview()
        this.$nextTick(() => this.submit())
      }
    },
    async submit (type = 'submit') {
      let cloneFrom
      if (this.childContext) {
        cloneFrom = cloneDeep(this.childContext.elemKeys)
      }
      // 校验文件是否上传
      let isNull = this.fileuploads.some(i => !i.fileuploadId)
      if (!this.fileuploads.length) isNull = true
      if (isNull) {
        return this.__jump_error__(
          'file-area',
          null,
          this.$t('contractMod.msgContractManage[11]')
        )
      }
      const modelLines = []
      const form = this.mergeForm
      if (cloneFrom) {
        for (const [key, value] of Object.entries(cloneFrom)) {
          const modelLineId = (
            this.modelLine.find(i => key === i.modelKey) || {}
          ).modelLineId
          if (Array.isArray(value)) {
            modelLines.push({
              modelLineId: modelLineId || null,
              modelKey: key,
              modelValue: JSON.stringify(value)
            })
          } else {
            modelLines.push({
              modelLineId: modelLineId || null,
              modelKey: key,
              modelValue: value
            })
          }
        }
      }
      const { effectiveDateTo, effectiveDateFrom, ...rest } = form

      if (this.IS_BUYER()) {
        const data = {
          modelLines,
          annexes: this.fileuploads,
          payPlans: this.payPlanData,
          contractMaterials: this.materialEditableRows,
          contractPartners: this.partnerData,
          contractHead: {
            ...rest
          }
        }
        if (effectiveDateTo) {
          data.contractHead.effectiveDateTo = this.$dayjs(
            effectiveDateTo
          ).format('YYYY-MM-DD')
        }
        if (effectiveDateFrom) {
          data.contractHead.effectiveDateFrom = this.$dayjs(
            effectiveDateFrom
          ).format('YYYY-MM-DD')
        }
          const { mainContractNo } = this.$attrs.params
          data.contractHead.mainContractNo = mainContractNo
          if (this.$attrs.params.flag !== 'edit') {
            data.contractHead.contractOldCode = this.mergeForm.contractNo
            data.contractHead.ceeaContractOldId = this.mergeForm.contractHeadId
            data.contractHead.contractHeadId = ''
            data.contractHead.contractType = 'TERMINATION'
          }
        if (type === 'approval') {
          // 提交审批之后就不能修改
          // 合同变更、补充协议 直接提交审批把原来主合同号赋值
          this.$api.cm.buyer.main.contract.approval(data).then(async res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            let contractHeadId = res.data
            await this.setContractInfo(contractHeadId, type, '', false)
            this.$set(this.mergeForm, 'contractHeadId', res.data)
            await this.handlerAfter('SUBMIT')
          })
        } else if (type === 'savePublish') {
          // 发布到供应商暂存
          // 发布供应商暂存
          this.$api.cm.buyer.main.contract.savePublish(data).then(res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            this.cancel()
          })
        } else if (type === 'publish') {
          // 发布到供应商
          // 发布供应商提交
          this.$api.cm.buyer.main.contract.publish(data).then(res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            this.cancel()
          })
        } else {
          // 提交审批之后就不能修改
          if (['MIAN_CONTRACT_ALTER', 'SUPPLEMENTAL_AGREEMENT', 'TERMINATION'].includes(this.$attrs.params.contractType)) {
            const { mainContractNo } = this.$attrs.params
            data.contractHead.mainContractNo = mainContractNo
            if (this.$attrs.params.flag !== 'edit') {
              data.contractHead.contractOldCode = this.mergeForm.contractNo
              data.contractHead.ceeaContractOldId = this.mergeForm.contractHeadId
              data.contractHead.contractHeadId = ''
            }
          }
          this.$api.cm.buyer.main.contract.savePublish(data).then(res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            this.cancel()
          })
        }
      } else {
        const data = {
          ...rest
        }
        try {
          if (effectiveDateTo) {
            data.contractHead.effectiveDateTo = this.$dayjs(
              effectiveDateTo
            ).format('YYYY-MM-DD')
          }
          if (effectiveDateFrom) {
            data.contractHead.effectiveDateFrom = this.$dayjs(
              effectiveDateFrom
            ).format('YYYY-MM-DD')
          }
        } catch (e) {}

        // 供应商请求接口
        if (type === 'SUPPLIER_CONFIRMING') {
          this.$api.cm.buyer.main.contract.vendorConfirm2(data.contractHeadId).then(res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            this.cancel()
          })
        } else if (type === 'SUPPLIER_REFUSE') {
          this.$api.cm.buyer.main.contract.reject(data.contractHeadId).then(res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            this.cancel()
          })
        } else {
          this.$api.cm.buyer.main.contract.vendorConfirm(data).then(res => {
            this.$message({
              type: 'success',
              message: res.message
            })
            this.initData()
          })
        }
      }
    },
    cancel () {
      const { row, flag } = this.$attrs.params
      if (flag == 'add') {
        this.$emit('tab-remove', 'termination')
      } else {
        this.$emit('tab-remove', 'termination' + row.contractName)
      }
      this.__setTabTodo('contractList.getQuerydata')
    },
    getVendorObj (val) {
      this.mergeForm.vendorId = val ? val.companyId : ''
      this.mergeForm.vendorName = val ? val.companyName : ''
      this.mergeForm.vendorCode = val ? val.companyCode : ''
      this.mergeForm.erpVendorCode = val ? val.erpVendorCode : ''
      this.mergeForm.erpVendorId = val ? val.erpVendorId : ''
      if (val.companyName) {
        const flag = this.partnerData.some(i => i.partnerType === '乙方')
        if (!flag) {
          this.partnerData.push({
            partnerType: '乙方',
            partnerName: val.companyName
          })
        }
      }
    }
  }
}
</script>
<style scoped>
.preview_wrapper {
  padding: 15px;
}
.conetnt .ins {
  background-color: #cfc;
  text-decoration: none;
}
.conetnt .del {
  color: #999;
  background-color: #fec8c8;
}
.paper {
  position: relative;
  padding: 0 40px 100px 40px;
  background: #fff;
  border: 1px solid #eee;
  float: left;
  margin: 10px;
  box-shadow: 0 0 12px 0 rgba(0, 0, 0, 0.06), 0 0 0 1px rgba(0, 0, 0, 0.04);
}
.paper::after,
.paper::before {
  content: "";
  position: absolute;
  bottom: 6px;
  width: 100px;
  height: 1px;
  z-index: -1;
  box-shadow: 0 2px 12px 5px rgba(0, 0, 0, 0.3);
}
.paper::after {
  left: 4px;
  transform: rotate(-6deg);
}
.paper::before {
  right: 4px;
  transform: rotate(6deg);
}
.base-form {
  padding: 15px 30px 0;
}
</style>
