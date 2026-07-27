<template>
  <el-container class="flex-container the-purchaseApplicationDetail-detail" direction="vertical">
    <el-main>
      <CWorkflowMulti
        ref="workflowMulti"
        v-model="activeTabName"
        :fun-params="workflowParamsInfo"
        :button-config-info="buttonConfigInfo"
        :button-custom="buttonCustom"
        @tab-click="workflowView"
        @workflow-handler="workflowHandler"
        @click-handler="type => saveOrSubmitBill(type)"
        @submit-direct="type => saveOrSubmitBill(type)"
        @confirm="(type, comment) => saveOrSubmitBill(type, comment)"
        @close-tab="back"
      >
        <div class="form-container2">
          <el-form
            ref="requirementHeadRef"
            :model="requirementHead"
            label-width="80px"
            label-position="top"
            :rules="rules"
          >
            <el-collapse v-model="activeDims" class="tab-form-style">
              <el-collapse-item ref="aptInfo" :title="$t('意向金开票申请')" name="1">
                <srm-row>
                  <srm-col>
                    <el-form-item
                      :label="$t('项目名称')"
                      prop="projectName"
                      :rules="{
                        required: true,
                        message: '请填写',
                      }"
                    >
                      <el-input v-model="requirementHead.projectName" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      prop="invoiceType"
                      :label="$t('开票类型')"
                      :rules="{
                        required: true,
                        message: '请填写',
                      }"
                    >
                      <DictSelect
                        v-model="requirementHead.invoiceType"
                        code="SOU_INVOICE_TYPE"
                        :disabled="readOnly"
                        @change="invoiceTypeChange"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="requirementHead.invoiceType == 'RED_INVOICE'" key="1">
                    <el-form-item
                      :label="$t('原意向金开票单号')"
                      :rules="{
                        required: true,
                        message: '请填写',
                      }"
                    >
                      <el-input v-model="requirementHead.fromDepositInvoiceNo" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      :label="$t('开票公司')"
                      :rules="{
                        required: true,
                        message: '请填写',
                      }"
                    >
                      <el-input v-model="requirementHead.invoiceCompany" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      :label="$t('纳税人识别号')"
                      prop="taxPayer"
                    >
                       <el-input v-model="requirementHead.taxPayer" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      :label="$t('电话')"
                      prop="phone"
                      :rules="{
                        required: true,
                        message: '请填写',
                      }"
                    >
                      <el-input v-model="requirementHead.phone" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="开户银行名称"
                      :rules="{
                        required: true,
                        message: '请填写',
                      }"
                    >
                      <el-input v-model="requirementHead.bankName" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="开户银行账户"
                      :rules="{
                        required: true,
                        message: '请填写',
                      }"
                    >
                      <el-input v-model="requirementHead.bankAccount" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="委托代缴证明/红票说明"
                      :rules="{
                        required: requirementHead.invoiceType === 'RED_INVOICE',
                        message: '请填写',
                      }"
                    >
                      <SrmCommonFile
                        :extra-data="fileInfo"
                        :default-file="{
                            fileId: requirementHead.entrustPayVoucherFileId,
                            fileName: requirementHead.entrustPayVoucherFileName
                          }"
                        :readonly="isReadOnly"
                        @on-change="({file}) => requireProductFileSuccess(file)"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="1">
                    <el-form-item
                      label="地址"
                      :rules="{
                        required: true,
                        message: '请填写',
                      }"
                    >
                      <el-input
                        v-model="requirementHead.address"
                        type="textarea"
                        :autosize="{ minRows: 2, maxRows: 5 }"
                        :disabled="requirementHead.invoiceType == 'RED_INVOICE' || readOnly"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col v-if="requirementHead.invoiceType !== 'RED_INVOICE'" key="2">
                    <el-form-item
                      label="发票接收邮箱"
                      prop="invoiceReceiverEmail"
                      :rules="{
                        required: true,
                        message: '请填写',
                      }"
                    >
                      <el-input
                        v-model="requirementHead.invoiceReceiverEmail"
                        :disabled="readOnly"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="意向金开票单号"
                      prop="orgName"
                    >
                      <el-input
                        v-model="requirementHead.invoiceNo"
                        disabled
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="开票数量"
                    >
                      <el-input
                        v-model="requirementHead.invoiceQuantity"
                        disabled
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="单价"
                    >
                      <el-input
                        v-model="requirementHead.price"
                        disabled
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="单据状态"
                    >
                      <DictSelect
                        v-model="requirementHead.status"
                        code="SOU_INT_DEPOSIT_INVOICE_STATUS"
                        disabled
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item label="供应商名称">
                      <el-input
                        v-model="requirementHead.vendorName"
                        disabled
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item label="创建日期">
                      <el-input
                        v-model="requirementHead.creationDate"
                        disabled
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item label="更新日期">
                      <el-input
                        v-model="requirementHead.lastUpdateDate"
                        disabled
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <el-form-item
                      label="发票"
                      prop="fileId"
                    >
                      <SrmCommonFile
                        :extra-data="fileInfo"
                        :default-file="{
                          fileId: requirementHead.fileId,
                          fileName: requirementHead.fileName
                        }"
                        :readonly="true"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="1" v-if="requirementHead.invoiceType == 'RED_INVOICE'" key="3">
                    <el-form-item
                      label="开具红字发票原因"
                    >
                      <el-input
                        v-model="requirementHead.redInvoiceReason"
                        type="textarea"
                        :autosize="{ minRows: 2, maxRows: 5 }"
                        :disabled="readOnly"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="1" v-if="requirementHead.invoiceType == 'RED_INVOICE'" key="4">
                    <p>开票说明：</p>
                    <p>请按照要求填写开票信息,发票开具后会自动回复至招标平台,请注意查收。注:a.个人(或者公司)代缴的投标意向金，如需开具发票抬头为公司的发票,需出具加盖投标单位公章的委托代付说明文件,否则将无法开具发票;b.发票开具完成后,意向金将不再退还；c.贵司提供发票信息后,大约25个工作日内会收到相应的电子发票，请耐心等待,有问题请电话联系0312-2197731。</p>
                  </srm-col>
                  <srm-col :initCol="1" key="5" v-else>
                    <p>开票说明：</p>
                    <p>请按照要求填写开票信息,发票开具后会自动回复至招标平台,请注意查收。注:a.个人(或者公司)代缴的投标意向金，如需开具发票抬头为公司的发票,需出具加盖投标单位公章的委托代付说明文件,否则将无法开具发票;b.发票开具完成后,意向金将不再退还；c.贵司提供发票信息后,大约25个工作日内会收到相应的电子发票，请耐心等待,有问题请电话联系0312-2197731。</p>
                    <p>注：开票信息：纳税人识别号、电话、开户银行名称等信息的的修改请在【开票信息】菜单维护。</p>
                  </srm-col>
                </srm-row>
              </el-collapse-item>
            </el-collapse>
          </el-form>
        </div>
        <!-- 底部按钮定义 -->
        <template slot="buttonOne" class="buttonOneClass">
          <el-button
            type="primary"
            @click="download"
          >
            下载委托代付凭证模板
          </el-button>
        </template>
      </CWorkflowMulti>
    </el-main>
  </el-container>
</template>
<script>
import _pick from 'lodash/pick'
import OrganizationSelector from 'lib@/components/organization-selector'
import { tabTodoMixin } from '@/utils/mixins'
import QuickSearch from 'lib@/components/QuickSearch'
import { parseTime } from '@/utils'
import CPagination from 'lib@/components/c-pagination'
import MImport from 'lib@/components/import'
import { downloadFileLink } from 'lib@/utils/file'
import WorkflowCommon from '@/library/mixins/workflow-common'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { getToken } from '@/utils/auth'
import axios from 'axios'
import { sysPrefix } from '@/config/ipConfig'
import { purchaseApplicationApi, yixiangJin } from 'modc@/buyer/purchasingDemand/api'
import MaterialSelectDialog from 'modc@/buyer/purchasingDemand/views/purchaseApplication/components/materialSelectDialog'
import CCategorySelect from 'lib@/components/c-category-select'
import { transformMQL } from '@/library/utils/util'
import { getMenuInfo } from '@/utils/menu-auth'

export default {
  name: 'PurchaseApplicationDetail',
  components: {
    MImport,
    QuickSearch,
    CPagination,
    OrganizationSelector,
    MaterialSelectDialog,
    CCategorySelect
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      applyId: null,
      buttonCustom:{
        // preRejectedData: {
        //   name: '下载委托代付凭证模板',
        //   view: true,
        //   disabled: false
        // }
      },
      reqLineListDelete: [],
      reqAttachListDelete: [],
      pageInfo: {
        pageTotal: 0,
        pageIndex: 1,
        pageSize: 15
      },
      detailPag: {
        currentPage: 1,
        pageSize: 15
      },
      copyInit: {
        num: 0,
        bol: true
      }, // 需求数量初始值
      materialParam: {},
      supplierParam: {},
      lastCategoryId: '',
      plTableColumnRowStyle: {
        boxSizing: 'border-box',
        overflow: 'hidden',
        whiteSpace: 'nowrap',
        textOverflow: 'ellipsis',
        wordBreak: 'break-all'
      },
      extraData: {
        fileModular: 'pm',
        fileFunction: 'purchaseApplication',
        fileType: 'excel'
      },
      iModal: {
        title: this.$t('common.excelImport'),
        upLoadUrl: '/api-sup-ce/purchaseRequirement/import'
      },
      pickerOptions: {
        disabledDate (time) {
          const today = new Date()
          today.setHours(0)
          today.setMinutes(0)
          today.setSeconds(0)
          today.setMilliseconds(0)
          return time.getTime() < today.getTime()
        }
      },
      curRole: this.$store.getters.userType,
      fileInfo: {
        fileModular: 'sup',
        fileFunction: 'SOURCE_PUBLISH',
        fileType: 'images'
      },
      queryForm: {
        categoryCode: null,
        categoryId: null,
        categoryName: null,
        materialCode: null,
        materialId: null,
        materialName: null,
        orgId: null,
        organizationId: null,
        organizationName: null
      },
      requirementHead: {
        applyId: null,
        invoiceType: 'INVOICE',
        demandDepartmentCode:null, // 部门
        applyByNickname:null, // 申请人
        extBidFlag: 'N',
        budgetManagementNum: null,
        extOrgBuName: null,
        demandType: null,
        reqLineList: [],
        requirementHeadId: null,
        ceeaPurchaseType: null,
        orgId: null,
        orgCode: null,
        orgName: null,
        createdId: null,
        categoryId: null,
        categoryCode: null,
        categoryName: null,
        organizationId: null,
        organizationCode: null,
        organizationName: null,
        createdFullName: null,
        requirementHeadNum: null,
        auditStatus: 'DRAFT',
        ceeaDepartmentId: null,
        ceeaDepartmentCode: null,
        ceeaDepartmentName: null,
        ceeaPrType: null,
        ceeaProjectName: null,
        ceeaProjectNum: null,
        ceeaProjectUserId: null,
        ceeaProjectUserNickname: null,
        ceeaProjectApprovalNum: null,
        ceeaIfVote: null,
        ceeaVoteProjectName: null,
        ceeaAssetType: null,
        totalBudget: 0,
        usedBudget: 0,
        thisYearBudgetAmount: '0',
        nextYearBudgetAmount: '0',
        ceeaIfHq: null,
        ceeaIfUseLogo: null,
        ceeaUrgencyExplain: null,
        ceeaAppointReason: null,
        comments: null,
        applyDate: parseTime(new Date(), '{y}-{m}-{d}')
      },
      currentPage: 1,
      allreqLineList: [],
      activeDims: ['1', '2', '3'],
      rules: {
        vendorName: [{ required: true, message: this.$t('purchaseDemand.selectVendor') }], // 指定供应商
        receiveAddress: [
          { required: true, message: this.$t('purchaseDemand.selectReceiveAddress') }
        ], // 收货地址（仅收货地点）
        requirementDate: [{ required: true, message: this.$t('purchaseDemand.selectRequireDate') }], // 需求日期
        requirementQuantity: [
          { required: true, message: this.$t('purchaseDemand.selectRequireQuantity') }
        ], // 需求数量
        orgId: [{ required: true, message: this.$t('purchaseDemand.orgIdTips') }], // 请选择业务实体
        organizationId: [
          {
            required: true,
            message: this.$t('purchaseDemand.organizationIdTips')
          }
        ], // 请选择库存组织
        ceeaPurchaseType: [
          {
            required: true,
            message: this.$t('purchaseDemand.purchaseTypeTips')
          }
        ], // 请输入采购类型
        categoryName: [
          {
            required: true,
            message: this.$t('purchaseDemand.inputCategoryName')
          } // 请输入物料大类
        ],
        acceptUserName: [
          {
            required: true,
            message: this.$t('purchaseDemand.acceptUserNameTips')
          }
        ], // 请输入验收人
        ceeaUrgencyExplain: [
          {
            required: true,
            message: this.$t('purchaseDemand.ceeaUrgencyExplainTips')
          }
        ], // 请输入紧急情况说明
        demandType: [
          {
            required: true,
            message: this.$t('purchaseDemand.selectRequireType')
          }
        ], // 请选择需求类型
        ceeaAppointReason: [
          {
            validator: (rule, value, callback) => {
              const { ceeaPurchaseType, ceeaAppointReason } = this.requirementHead
              if (ceeaPurchaseType === 'APPOINT' && !ceeaAppointReason) {
                callback(new Error(this.$t('purchaseDemand.ceeaAppointReasonTips')))
              } else {
                callback()
              }
            }
          }
        ]
      },
      isApprovalOnly: this.$attrs.params.flag === 'approvalOnly',
      dialogVisible: false,
      displayItemTable: [],
      reqAttachList: [],
      multipleSelection: [],
      multipleSelection2: [],
      Viewflag: '',
      messagePage: false,
      tableLoading: false,
      readOnly: false
    }
  },
  computed: {
    hideReSubmit () {
      return this.allreqLineList.find(v => v.applyStatus === 'RETURNING')
    },
    importAbled () {
      return (
        this.requirementHead.auditStatus === 'APPROVED' ||
        !this.requirementHead.categoryId ||
        !this.requirementHead.organizationId ||
        !this.requirementHead.orgId
      )
    },
    viewUpdateButton () {
      return (
        !this.isReadOnly
      )
    },
    disabledUpdateButton () {
      return (
        this.requirementHead.auditStatus === 'SUBMITTED' ||
        this.requirementHead.auditStatus === 'APPROVING'
      )
    },
    workflowBusinessId () {
      return this.requirementHead ? this.requirementHead.requirementHeadId : null
    },
    workflowTabDisabled () {
      return !this.isApprovalOnly
    },
    isReadOnly () {
      return !['add', 'edit'].includes(this.$attrs.params.flag)
    }
  },
  watch: {
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = false
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    },
    disabledUpdateButton () {
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
    }
  },
  created () {
    this.Viewflag = this.$attrs.params?.flag
    const row = this.$attrs.params?.row[0]
    if(row?.souReqApplyList){
      this.$set(this.requirementHead, 'applyId', row.souReqApplyList[0]?.applyId)
    }
    if (this.$attrs.params.flag === 'add') {
      this.getData(row.reqHeadId)
    } else {
      this.getData(row.reqHeadId, row.invoiceId)
    }
    if (this.$attrs.params?.flag == 'view') {
      this.readOnly = true
    }
    this.getButtonConfig()
  },
  methods: {
    invoiceTypeChange () {
      const row = this.$attrs.params?.row[0]
      if (this.$attrs.params.flag === 'add') {
        this.getData(row.reqHeadId)
      } else {
        this.getData(row.reqHeadId, row.invoiceId)
      }
    },
    requireProductFileSuccess(file){
      const { fileId = '', fileName = '' } = file || {}
      this.requirementHead.entrustPayVoucherFileId = fileId.toString()
      this.requirementHead.entrustPayVoucherFileName = fileName
    },
    download(){
      downloadFileLink(
        '/api-sou/intDepositInvoice/importModelDownload',
        '代付凭证模板',
      ).catch(() => {
        this.$message.error(this.$t('purchaseDemand.downloadFail'))
      })
    },
    getData(reqHeadId,invoiceId = null){ // by wl
      let attr = []
      console.log(this.requirementHead.applyId, 'this.applyId')
      const applyIdOld = this.requirementHead.applyId
      if (invoiceId) { // 编辑进来
        attr = [{reqHeadId, invoiceId, invoiceType: this.requirementHead.invoiceType, applyId: this.requirementHead.applyId}]
      } else { // 新增
        attr = [{reqHeadId, invoiceType: this.requirementHead.invoiceType, applyId: this.requirementHead.applyId}]
      }
      const action = invoiceId ? 'read' : 'getInfo'
      const searchData = transformMQL.save('SouIntDepositInvoice', attr, action)
      const getUrl = path => `${path}`
      this.$http({
        url: getUrl(`/api-sou/api-ql/SouIntDepositInvoice/${action}`),
        method: 'POST',
        data: searchData,
        loading: true
      }).then((res) => {
        console.log(res, 'res')
        if (!invoiceId) {
          const { status,...data } = res.data.records[0]
          this.requirementHead = data
          this.requirementHead.applyId = applyIdOld
        } else {
          this.requirementHead = res.data[0]
          this.requirementHead.applyId = applyIdOld
        }
      })
    },
    // 确认选择品类
    comfirmSelect (node, scope) {
      scope.categoryId = node ? node.categoryId : ''
      scope.categoryName = node ? node.categoryFullName : ''
      scope.categoryCode = node ? node.categoryCode : ''
    },
    getButtonConfig () {
      this.buttonConfigInfo.save.view = false
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
      this.buttonConfigInfo.save.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.submit.disabled = this.disabledUpdateButton
      this.buttonConfigInfo.cancel.view = !this.isReadOnly
      this.buttonConfigInfo.close.view = false
    },
    handleTypeChange (val) {
      // 不是非生产性需求清空预算编号
      if (this.requirementHead.demandType !== 'NONPRODUCTIVE_DEMAND') {
        this.requirementHead.budgetManagementNum = null
        this.requirementHead.budgetManagementId = null
        this.requirementHead.totalBudget = null
        this.requirementHead.usedBudget = null
        this.requirementHead.unusedBudget = null
      }
    },
    // 收货地点选择
    changeSiteInfo (row, { element }) {
      this.$set(row, 'receiveContact', element ? element.receiver : '')
      this.$set(row, 'receiveTelephone', element ? element.receiverPhone : '')
      this.$set(row, 'receiveAddress', element ? element.siteName : '')
    },
    // 指定供应商绿牌  参数添加
    beforeOpenSuppier (row) {
      this.supplierParam['c.CATEGORY_ID'] = row.categoryId
    },
    // 物料明细导出
    async exportFile () {
      const Authorization = getToken() ? 'Bearer ' + getToken() : '' // token
      let menuInfo = getMenuInfo()
      const res = await axios({
        method: 'POST',
        url: `${sysPrefix()}/api-sup-ce/pr/requirementLine/excelExport?requirementHeadId=${
          this.$attrs.params.row.requirementHeadId
        }`,
        headers: {
          Authorization: Authorization,
          'X-Fun-Info': menuInfo.secretKey
        },
        responseType: 'blob'
      })
      if (!res) return
      if (res.data.type === 'application/json') {
        throw new Error(this.$t('purchaseDemand.downloadFail'))
      }
      const content = res.data
      const blob = new Blob([content])
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.style.display = 'none'
      link.href = url
      link.rel = 'noopener'
      link.setAttribute(
        'download',
        window.decodeURI(res.headers['content-disposition'].split('=')[1]),
      )
      document.body.appendChild(link)
      link.click()
      document.body.removeChild()
    },
    async getWorkflowBusinessType () {
      return 'REQUIREMENT'
    },
    async getWorkflowBusinessVariables () {
      return {
        Amount: this.requirementHead.totalBudget
      }
    },
    getCWorkflowRefName () {
      return 'workflowMulti'
    },
    beforeUpload () {
      const extraData = this.extraData
      const header = _pick(this.requirementHead, [
        'orgId',
        'orgName',
        'orgCode',
        'organizationCode',
        'organizationId',
        'organizationName',
        'requirementHeadId',
        'requirementHeadNum'
      ])
      this.extraData = { ...header, ...extraData }
    },
    handleSuccess (data) {
      console.log(data)
      // 导入成功就刷新界面
      data?.data.forEach(row => {
        this.setTotalAmount2(row)
        this.requirementHead.reqLineList.unshift(row)
      })
    },
    downloadTemplate () {
      downloadFileLink(
        '/api-sup-ce/purchaseRequirement/downloadTemplate',
        this.$t('purchaseDemand.importMaterialItemModelDownload'),
      ).catch(() => {
        this.$message.error(this.$t('purchaseDemand.downloadFail'))
      })
    },
    async handleCurrentChange (val = 1) {
      this.tableLoading = true
      this.$set(this.detailPag, 'currentPage', val)
      this.$set(this.detailPag, 'pageSize', 15)

      this.$set(this.requirementHead, 'reqLineList', this.allreqLineList.slice(
        (val - 1) * 15,
        val * 15
      ))
      setTimeout(() => {
        this.tableLoading = false
        this.multipleSelection2 = []
      })
      this.$forceUpdate()
    },
    async getFormDetail (requirementHeadId) {
      const searchData = transformMQL.listPageData({
        type:'PurchaseRequirementHead',
        action: 'query',
        params:{ requirementHeadId: requirementHeadId },
        query:{'*':{},'reqLineList': {'*': {}},'reqAttachList': {'*': {}}}
      })
      purchaseApplicationApi.queryData(searchData).then((datas) => {
        console.log(datas, 'datas')
        let data = datas.data.records[0]
        if (data?.reqLineList == null) {
          data['reqLineList'] = []
        }
        this.reqAttachList = data.reqAttachList
        this.requirementHead = data
      })
      // if (data.data) {
      //   this.requirementHead = data.data.requirementHead || {}
      //   this.reqAttachList = data.data.reqAttachList
      //   let list = data.data.reqLineList.map(
      //     ({ ceeaDeliveryPlace, ...rest }) => {
      //       let d = null
      //       try {
      //         if (ceeaDeliveryPlace) {
      //           d = JSON.parse(ceeaDeliveryPlace)
      //         }
      //       } catch (e) {
      //         console.log(e)
      //       }
      //       return {
      //         ...rest,
      //         ceeaDeliveryPlace: d
      //       }
      //     },
      //   )
      //   this.requirementHead.reqLineList = list
      //   this.allreqLineList = list
      //   this.handleCurrentChange(1)
      //   this.lastCategoryId = this.requirementHead.categoryId
      // }
    },
    // 删除
    deleteOneContent (index, row) {
      if (row.requirementLineId) {
        this.reqLineListDelete.push({'$delete': row.requirementLineId})
      }
      this.requirementHead.reqLineList.splice(index, 1)
    },
    addUploadOne () {
      this.reqAttachList.push({
        attachId: null,
        fileuploadId: null,
        attachName: ''
      })
    },
    getVendorObj (val, scope) {
      scope.vendorId = val ? val.companyId : ''
      scope.vendorCode = val ? val.companyCode : ''
      scope.vendorName = val ? val.companyName : ''
    },
    handleItemSelection2 (val) {
      this.multipleSelection2 = val
    },
    outerHandleUploadSuccess (file, row) {
      const { fileId = '', fileName = '', createdBy = '', creationDate = '', createdUserName = '' } = file || {}
      row.fileuploadId = fileId.toString()
      row.attachName = fileName
      row.createdBy = createdBy
      row.createdUserName = createdUserName
      row.creationDate = creationDate
    },
    outerHandleUploadSuccess2 (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.extAttachId = fileId.toString()
      row.extAttachName = fileName
    },
    // 行删除
    handleDelClick (index, row) {
      if (row?.attachId) {
        this.reqAttachListDelete.push({$delete:row?.attachId})
      }
      this.reqAttachList.splice(index, 1)
    },
    // 重置对象条件
    resetForm (form) {
      for (let i in form) {
        form[i] = ''
      }
    },
    async openDialog () {
      this.requirementHead.reqLineList.push({
        materialCode: null,
        materialName: null,
        categoryName: null,
        extProductFlag: 'N',
        extUseDepartmentCode: null,
        extUseDepartmentName: null,
        requirementDepartment: null
      })
    },
    queryContent (queryForm) {
      this.$http({
        url: '/api-base/material/materialItem/listMaterialByPurchaseCategory',
        method: 'POST',
        data: {
          categoryId: queryForm.categoryId || this.requirementHead.categoryId,
          materialCode: queryForm.materialCode,
          materialName: queryForm.materialName,
          organizationId: this.requirementHead.organizationId,
          organizationName: this.requirementHead.organizationName,
          ceeaPurchaseType: this.requirementHead.ceeaPurchaseType,
          pageSize: queryForm.pageSize || 15,
          pageNum: queryForm.pageNum || 1
        },
        laoding: true
      }).then(data => {
        if (data && data.data) {
          this.displayItemTable = data.data.list
          this.pageInfo.pageTotal = data.data.total
        }
      })
    },
    orgInvIdChange (node, value, scope) {
      console.log(node?.organizationName, 'organizationName')
      const { organizationTypeCode = null , organizationName = null } = node
      scope.extUseDepartmentCode = organizationTypeCode
      scope.extUseDepartmentName = organizationName
      scope.requirementDepartment = organizationName
      console.log(this.requirementHead.reqLineList,'reqLineList')
    },
    selectHandler (node, value, scope) {
      console.log(node, 'node')
      this.requirementHead.orgId = node ? node.organizationId : null
      this.requirementHead.orgCode = node ? node.organizationCode : null
      this.requirementHead.orgName = node ? node.organizationName : null
      // 板块
      this.requirementHead.extOrgBuName = node ? node.parentOrganizationName : null
      this.requirementHead.organizationId = node ? node.organizationId : null
      this.requirementHead.organizationCode = node ? node.organizationCode : null
      this.requirementHead.organizationName = node ? node.organizationName : null

      // if (!this.requirementHead.organizationId) return
      // this.requirementHead.organizationId = null
      // this.requirementHead.organizationName = null
      // this.requirementHead.organizationCode = null
    },
    selectHandler2 (node, value, scope) {
      this.requirementHead.organizationId = node ? node.organizationId : null
      this.requirementHead.organizationCode = node ? node.organizationCode : null
      this.requirementHead.organizationName = node ? node.organizationName : null
    },
    addOneContent (multipleSelection) {
      if (multipleSelection.length === 0) {
        return
      }

      const obj = {}
      this.$refs.materialDetailRef.columns.forEach(headers => {
        // 物料明细表所有属性置空初始化
        obj[headers.property] = null
      })

      const dataMap = multipleSelection.map(item => {
        const selectItem = {
          ...item,
          unit: item.unitName,
          unitCode: item.unit,
          totalAmount: (item.notaxPrice || 0) * (item.requirementQuantity || 0),
          ceeaIe: item.ceeaIfDirectory === 'Y' ? 'true' : 'false',
          businessSmall: this.requirementHead.businessSmall,
          // ceeaBusinessSmallCode: this.requirementHead.ceeaBusinessSmallCode
          requirementQuantity: undefined,
          orgId: this.requirementHead.orgId,
          orgCode: this.requirementHead.orgCode,
          orgName: this.requirementHead.orgName,
          organizationId: this.requirementHead.organizationId,
          organizationCode: this.requirementHead.organizationCode,
          organizationName: this.requirementHead.organizationName
        }
        return selectItem
      })
      // 拿到最终的添加数据数组
      const newArr = dataMap.map(row => {
        const o = JSON.parse(JSON.stringify(obj))
        Object.keys(row).forEach(key => {
          this.$set(o, key, row[key])
        })
        return o
      })

      this.allreqLineList.unshift(...newArr)
      this.handleCurrentChange()
      this.dialogVisible = false
    },
    checkMaterialList (categoryId) {
      return new Promise(resolve => {
        if (
          this.allreqLineList.length &&
          this.lastCategoryId &&
          this.lastCategoryId !== categoryId
        ) {
          this.$confirm(
            this.$t('purchaseDemand.checkMaterialListConfirm'),
            this.$t('common.tips'),
            {
              confirmButtonText: this.$t('common.confirm'),
              cancelButtonText: this.$t('common.cancel'),
              type: 'warning'
            },
          )
            .then(() => {
              this.allreqLineList = []
              this.handleCurrentChange()
              this.$message.info(this.$t('purchaseDemand.checkMaterialListTips1'))
              resolve(true)
            })
            .catch(() => {
              this.$message.info(this.$t('purchaseDemand.checkMaterialListTips2'))
              resolve(false)
            })
        } else {
          resolve(true)
        }
      })
    },
    // 预算编号快查关闭回写值
    getBudgetNumObj (val) {
      this.requirementHead.budgetManagementNum = val ? val.budgetManagementNumber : ''
      this.requirementHead.budgetManagementId = val ? val.budgetManagementId : ''
    },
    async getCategoryObj (val, scope) {
      scope.materialCode = val?.materialCode
      scope.materialName = val?.materialName
      scope.categoryCode = val?.categoryCode
      scope.categoryFullName = val?.categoryFullName
      scope.categoryId = val?.categoryId
      scope.categoryName = val?.categoryName
      scope.brand = val?.brand
      scope.extMaterialModel = val?.specification
      scope.unitCode = val?.unit
      scope.unitName = val?.unitName

      // 看是否商品
      if (!this.requirementHead.orgId) {
        this.$message.error('请选择公司')
        return false
      }
      const searchData = transformMQL.listPageData({
        type: 'CatalogOnShelves',
        action: 'query',
        params: {
          materialId: val?.materialId,
          orgId: this.requirementHead.orgId,
          status: 'ON_SHELVES'
        },
        filterOperator: {
          materialId: 'eq',
          orgId: 'eq',
          status: 'eq'
        }
      })
      purchaseApplicationApi.searchCaigou(searchData).then((datas) => {
        console.log(datas,'datas')
        const num = datas.data?.records?.length
        if (num) {
          scope.extProductFlag = 'Y'
        } else {
          scope.extProductFlag = 'N'
        }
      })
    },
    async getCategoryObj1 (val, scope) {
      scope.row.dmandLineRequest = val?.nickname
      scope.row.extUserName = val?.username
      scope.row.extUserCode = val?.userId
    },
    getTotalAmount (n) {
      // 存起来一个初始需求数量
      if (this.copyInit.bol) {
        this.copyInit.num = +n
        this.copyInit.bol = false
      }
    },
    setTotalAmount2 (row, index) {
      const total = Number(row.extPredictPrice) * Number(row.requirementQuantity)
      if (total) {
        this.$set(row, 'extPredictAmount', total)
        // this.$set(this.requirementHead.reqLineList[index], 'extPredictAmount', total)
      }
    },
    async setTotalAmount (row, index) {
      const formFiled = await this.formValidate('requirementHeadRef')
      const fieldKeys = Object.keys(formFiled.obj)
      if (!formFiled.flag && fieldKeys.length > 0 && fieldKeys[0].includes('reqLineList')) {
        this.messagePage = true
      } else {
        this.messagePage = false
      }

      if (row.applyStatus === 'RETURNING' && row.requirementQuantity >= this.copyInit.num) {
        row.requirementQuantity = this.copyInit.num
        return this.$message.warning(this.$t('purchaseDemand.mustBeLessEqual'))
      }
      if (row.requirementQuantity <= 0) {
        return this.$message.warning(this.$t('purchaseDemand.setTotalAmountTips1'))
      }
      row.totalAmount = Number(
        Number(row.notaxPrice || 0) * Number(row.requirementQuantity || 0),
      ).toFixed(2)
      console.log(row.totalAmount, 'row.totalAmount')
      setTimeout(() => {
        const totalAmountArr = this.allreqLineList.map(v => v.totalAmount || 0)
        const totalBudget = totalAmountArr.reduce((p, c) => (Number(p) || 0) + (Number(c) || 0))
        this.$set(this.requirementHead, 'totalBudget', totalBudget)
        this.requirementHead.nextYearBudgetAmount =
          this.requirementHead.totalBudget - this.requirementHead.thisYearBudgetAmount
      }, 100)
    },
    getUserObj (val, scope) {
      scope.createdId = val ? val.userId : ''
      scope.createdFullName = val ? val.nickname : ''
    },
    setBudget (val) {
      if (
        Number(this.requirementHead.thisYearBudgetAmount) > Number(this.requirementHead.totalBudget)
      ) {
        return this.$message.error(this.$t('purchaseDemand.setBudgetTips1'))
      }
      setTimeout(() => {
        this.requirementHead.nextYearBudgetAmount =
          this.requirementHead.totalBudget - this.requirementHead.thisYearBudgetAmount
      }, 100)
    },
    formatterStatus (row, column, cellValue, index) {
      return this.$getDictLabel('APPLICATION_STATUS', cellValue)
    },
    formatterIfDirector (row, column, cellValue, index) {
      return cellValue === 'Y' ? this.$t('common.yes') : this.$t('common.no')
    },
    // 提交校验
    async submitCheck () {
      if (this.allreqLineList.length === 0) {
        return this.__jump_error__(
          'materialDetailRef',
          null,
          this.$t('purchaseDemand.saveBillTips5'),
        )
      }

      if (this.requirementHead.ceeaPurchaseType === 'APPOINT') {
        let confirmSelectValue = await this.$confirm(
          // 此申请为指定采购，确认则提交采购申请单！
          this.$t('purchaseDemand.saveBillConfirm1'),
          this.$t('common.tips'),
          {
            confirmButtonText: this.$t('common.confirm'),
            cancelButtonText: this.$t('common.cancel'),
            type: 'warning'
          },
        )
        if (confirmSelectValue !== 'confirm') return false
      }

      let isConfirm = await this.$confirm(
        this.$t('purchaseDemand.clickConfirmToApproval'), // 点击确认则跳转到审批
        this.$t('common.tips'),
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        },
      )
      return isConfirm === 'confirm'
    },
    async submitEvent (allparam) {
      // const sign = await this.submitCheck()
      // if (!sign) return
      const saveData = transformMQL.save('SouIntDepositInvoice', [allparam], 'submit')
      yixiangJin.save(saveData).then((datas) => {
        this.$message({
          message: '保存成功',
          type: 'success'
        })

        this.back()
      })
    },
    saveBill (allparam) {
      const saveData = transformMQL.save('SouIntDepositInvoice', [allparam], 'save')
      yixiangJin.save(saveData).then(() => {
        this.$message({
          message: '暂存成功',
          type: 'success'
        })
        this.back()
      })
    },
    // form验证返回promise校验返回trun or false
    formValidate (formRef) {
      return new Promise(resolve => {
        this.$refs[formRef].validate((flag, obj) => {
          resolve({ flag, obj })
        })
      })
    },
    /*
     * @Description: 校验表单表格必填项
     * @return: {
     *   flag: true/false,  校验是否通过
     *   message: 返回填写信息
     * }
     */
    async getCheckForm () {
      const formFiled = await this.formValidate('requirementHeadRef')

      if (!formFiled.flag && Object.keys(formFiled.obj).length > 0) {
        const warnObj = Object.keys(formFiled.obj)[0]
        return {
          flag: formFiled.flag,
          message: formFiled.obj[warnObj][0].message
        }
      }
      return { flag: true }
    },
    // 抽离保存，提交方法校验及其他数据处理
    saveOtherDeal () {
      if (this.requirementHead.ceeaPrType === '01' && this.reqAttachList.length === 0) {
        this.$message.error(this.$t('purchaseDemand.saveBillTips3'))
        return
      }
      if (
        Number(this.requirementHead.thisYearBudgetAmount) > Number(this.requirementHead.totalBudget)
      ) {
        return this.$message.error(this.$t('purchaseDemand.saveBillTips4'))
      }
      this.requirementHead.nextYearBudgetAmount =
        this.requirementHead.totalBudget - this.requirementHead.thisYearBudgetAmount

      this.allreqLineList.map(v => {
        v.orgId = this.requirementHead.orgId
        v.orgCode = this.requirementHead.orgCode
        v.orgName = this.requirementHead.orgName
      })
    },
    async saveOrSubmitBill (type) {

        let allparam = {
          ...this.requirementHead,
        }

        if (type === 'SUBMIT') {
          const { flag, message } = await this.getCheckForm()
          if (flag) {
          //   await this.saveOtherDeal()
              this.submitEvent(allparam)
          } else {
            this.__focus_error__(message)
          }
        } else {
          this.saveBill(allparam)
        }

    },
    back () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('purchaseApplicationList.getQuerydata')
    }
  }
}
</script>
<style scoped lang="scss">
.list-page-query :deep(.el-form-item__label) {
  text-align: right !important;
}
.the-purchaseApplicationDetail-detail {
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .input-with-select .el-input-group__prepend {
    background-color: #fff;
  }
  .isDisabledimport {
    pointer-events: none;
    opacity: 0.5;
  }
  .the_btn_wrapper {
    display: inline-block;
    width: 111px;
  }
  .btn_line {
    display: flex;
    margin: 0 0 8px 0;
  }
  .el-tooltip :deep(.el-button) {
    min-width: 56px;
    font-size: 14px;
    border-radius: 2px;
    padding: 8px 16px;
  }
  .topComment {
    margin-top: 15px;
    text-align: right;
  }
  .input-number-precision {
    width: 100%;
    :deep(.el-input__inner) {
      text-align: left;
      padding-left: 8px;
    }
  }
}
:deep(.el-table td.el-table__cell .el-form-item__content) {
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}
.toRequired {
  color: #ff4949;
  padding-right: 2px;
}
</style>
