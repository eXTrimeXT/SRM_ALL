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
      <!-- <ApprovalProcess
        :business-id="workflowBusinessId"
        business-type="MQL_PR_SOU_REQ_CANCEL_INIT"
        :status-map="{
          DRAFT: 'DRAFT', // 拟定
          SUBMITTED: 'APPROVING', // 已提交
          APPROVED: 'APPROVED', // 审批通过
          REJECTED: 'REJECTED', // 已驳回
          WITHDRAW: 'WITHDRAW', // 已撤回
          ABANDONED: 'ABANDONED' // 已废弃
        }"
        :readonly="$attrs.params.showType === 'readOnly'"
        :approvalStatus="requirementHead.cancelStatus"
        :operation-pre-options="operationPreOptions"
        @approval-handler-callback="approvalHanlder"
      > -->
        <div class="form-container2">
          <el-form
            ref="requirementHeadRef"
            :model="requirementHead"
            label-width="80px"
            label-position="top"
            :rules="rules"
          >
            <el-collapse v-model="activeDims" class="tab-form-style">
              <el-collapse-item ref="aptInfo" :title="$t('purchaseDemand.purAppDetail')" name="1">
                <srm-row>
                  <srm-col>
                    <!-- 申请部门 -->
                    <el-form-item
                      :label="$t('purchaseDemand.ceeaDepartment')"
                    >
                      <el-input v-model="requirementHead.departmentName" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 申请日期 -->
                    <el-form-item
                      prop="applyDate"
                      :label="$t('purchaseDemand.applyDate')"
                    >
                      <el-date-picker
                        v-model="requirementHead.applyDate"
                        disabled
                        type="date"
                        :format="$formatDatePicker"
                        value-format="yyyy-MM-dd"
                      />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 申请编号 -->
                    <el-form-item
                      :label="$t('purchaseDemand.requirementHeadNum')"
                    >
                      <el-input v-model="requirementHead.requirementCancelNo" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col>
                    <!-- 申请人 -->
                    <el-form-item
                      :label="$t('purchaseDemand.applicant')"
                    >
                      <el-input v-model="requirementHead.applyByNickname" disabled />
                    </el-form-item>
                  </srm-col>
                  <srm-col :initCol="1">
                    <!-- 需求取消说明 -->
                    <el-form-item prop="ceeaAppointReason">
                      <span style="font-size:12px;">
                        <em class="toRequired" style="margin-left:0;">*</em>{{ $t('cusEntry.supplement20250121.ceeaAppointReason') }}
                      </span>
                      <el-input
                        v-model="requirementHead.cancelReason"
                        :disabled="isReadOnly"
                        type="textarea"
                        :rows="2"
                      />
                    </el-form-item>
                  </srm-col>
                </srm-row>
              </el-collapse-item>
              <!-- 需求取消明细行 -->
              <el-collapse-item ref="itemInfo" :title="$t('cusEntry.supplement20250121.itemInfoRow')" name="2">
                <el-table
                  ref="materialDetailRef"
                  :key="detailPag.currentPage"
                  v-loading="tableLoading"
                  row-key="requirementLineId"
                  :data="requirementHead.cancelLineList"
                  style="width: 100%"
                  border
                  :row-height="38"
                  max-height="390px"
                  highlight-current-row
                  @selection-change="handleItemSelection2"
                >
                  <el-table-column
                    align="center"
                    type="index"
                    :label="$t('contractMod.tabindex')"
                    width="50"
                  />
                  <el-table-column
                    align="center"
                    prop="projectName"
                    :label="$t('purchaseDemand.projectName')"
                    width="120"
                  />
                  <!-- 概算金额（卢布） -->
                  <el-table-column
                    align="center"
                    prop="totalAmountByTenKilo"
                    :label="$t('cusEntry.supplement20250121.totalAmountByTenKilo')"
                    min-width="130"
                  />
                  <!-- 项目所在地 -->
                  <el-table-column
                    align="center"
                    prop="projectAddress"
                    :label="$t('cusEntry.supplement20250121.projectAddress')"
                    min-width="120"
                  />
                  <!-- 项目概况及范围 -->
                  <el-table-column
                    align="center"
                    prop="projectOverview"
                    :label="$t('cusEntry.supplement20250121.projectOverview')"
                    width="150"
                  />
                  <!-- 招标专家 -->
                  <el-table-column
                    align="center"
                    prop="souGroupFullName"
                    :label="$t('cusEntry.bidSuperviseReport.souPrincipal')"
                    width="150"
                  />
                  <!-- 供应商专家 -->
                  <el-table-column
                    align="center"
                    prop="vendorGroupFullName"
                    :label="$t('cusEntry.bidSuperviseReport.vendorPrincipal')"
                    width="150"
                  />
                  <!-- 删除 -->
                  <el-table-column
                    v-if="requirementHead.auditStatus === 'DRAFT'"
                    :label="$t('common.operation')"
                    width="60"
                    fixed="right"
                  >
                    <template v-if="requirementHead.auditStatus === 'DRAFT'" slot-scope="scope">
                      <el-button :disabled="isReadOnly" type="text" @click="deleteOneContent(scope.$index, scope.row)">
                        {{ $t('common.delete') }}
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </el-collapse-item>
              <!--附件上传-->
              <el-collapse-item :title="$t('purSettlementMod.addUploadFile')" name="3">
                <p class="btn_line">
                  <el-button
                    v-if="requirementHead.cancelStatus !== 'APPROVED'"
                    :disabled="isReadOnly"
                    type="primary"
                    class="detail-pbtn"
                    @click="addUploadOne"
                  >
                    {{ $t('common.add') }}
                  </el-button>
                </p>
                <el-table :data="cancelAttachList" style="width: 100%" border max-height="250px">
                  <el-table-column
                    align="center"
                    type="index"
                    :label="$t('purSettlementMod.tabindex')"
                    width="50"
                  />
                  <!-- 附件 -->
                  <el-table-column
                    align="center"
                    prop="attachName"
                    :label="$t('purchaseDemand.attachment')"
                  >
                    <template slot-scope="scope">
                      <SrmCommonFile
                        :extra-data="fileInfo"
                        :default-file="{
                          fileId: scope.row.fileId,
                          fileName: scope.row.fileName
                        }"
                        :readonly="false"
                        @on-change="({file}) => outerHandleUploadSuccess(file,scope.row)"
                      />
                    </template>
                  </el-table-column>
                  <!-- 上传人 -->
                  <el-table-column
                    align="center"
                    prop="createdFullName"
                    :label="$t('purchaseDemand.attachmentCreatedBy')"
                    :show-overflow-tooltip="false"
                  />
                  <!-- 账号 -->
                  <el-table-column
                    align="center"
                    prop="createdBy"
                    :label="$t('vendorMod.account')"
                    :show-overflow-tooltip="false"
                  />
                  <!-- 上传时间 -->
                  <el-table-column
                    align="center"
                    prop="creationDate"
                    :label="$t('purchaseDemand.attachmentCreatedDate')"
                    :show-overflow-tooltip="false"
                  />
                  <el-table-column :label="$t('common.operation')" width="60">
                    <template slot-scope="scope">
                      <el-button
                        type="text"
                        :disabled="isReadOnly || requirementHead.cancelStatus === 'APPROVED'"
                        @click="handleDelClick(scope.$index, scope.row)"
                      >
                        {{ $t('common.delete') }}
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </el-collapse-item>
            </el-collapse>
          </el-form>
        </div>
      </CWorkflowMulti>
      <!-- </ApprovalProcess> -->
    </el-main>

    <!-- 物料明细新增 -->
    <MaterialSelectDialog
      :visible.sync="dialogVisible"
      :requirementHead="requirementHead"
      :queryForm="queryForm"
      :pageInfo="pageInfo"
      :displayItemTable="displayItemTable"
      @queryContent="queryContent"
      @addOneContent="addOneContent"
      @close="dialogVisible = false"
    />
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
import { purchaseApplicationApi } from 'modc@/buyer/purchasingDemand/api'
import MaterialSelectDialog from 'modc@/buyer/purchasingDemand/views/purchaseApplication/components/materialSelectDialog'
import CCategorySelect from 'lib@/components/c-category-select'
import { transformMQL } from '@/library/utils/util'
import ApprovalProcess from 'modc@/components/approval-process'
import { getMenuInfo } from '@/utils/menu-auth'

export default {
  name: 'PurchaseApplicationDetail',
  components: {
    MImport,
    QuickSearch,
    CPagination,
    OrganizationSelector,
    MaterialSelectDialog,
    CCategorySelect,
    ApprovalProcess
  },
  mixins: [tabTodoMixin, WorkflowCommon],
  data () {
    return {
      cancelLineListDelete: [],
      cancelAttachListDelete: [],
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
        fileFunction: 'vendorBiddingManagement',
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
        demandDepartmentCode: null, // 部门
        applyByNickname: null, // 申请人
        extBidFlag: 'N',
        budgetManagementNum: null,
        extOrgBuName: null,
        demandType: null,
        cancelLineList: [],
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
        requirementCancelNo: null,
        cancelStatus: 'DRAFT',
        departmentId: null,
        ceeaDepartmentCode: null,
        departmentName: null,
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
        applyDate: parseTime(new Date(), '{y}-{m}-{d}', true)
      },
      currentPage: 1,
      allcancelLineList: [],
      activeDims: ['1', '2', '3'],
      rules: {
        vendorName: [{ required: true, message: this.$t('purchaseDemand.selectVendor') }], // 指定供应商
        receiveAddress: [{ required: true, message: this.$t('purchaseDemand.selectReceiveAddress') }], // 收货地址（仅收货地点）
        requirementDate: [{ required: true, message: this.$t('purchaseDemand.selectRequireDate') }], // 需求日期
        requirementQuantity: [{ required: true, message: this.$t('purchaseDemand.selectRequireQuantity') }], // 需求数量
        orgId: [{ required: true, message: this.$t('purchaseDemand.orgIdTips') }], // 请选择业务实体
        organizationId: [{ required: true, message: this.$t('purchaseDemand.organizationIdTips')}], // 请选择库存组织
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
      cancelAttachList: [],
      multipleSelection: [],
      multipleSelection2: [],
      Viewflag: '',
      messagePage: false,
      tableLoading: false,
      operationPreOptions: {
        nextStep: this.preNextStepHandler
      },
      outerBmpFlag: false // 是否外部链接跳转
    }
  },
  computed: {
    hideReSubmit () {
      return this.allcancelLineList.find(v => v.applyStatus === 'RETURNING')
    },
    importAbled () {
      return (
        this.requirementHead.cancelStatus === 'APPROVED' ||
        !this.requirementHead.categoryId ||
        !this.requirementHead.organizationId ||
        !this.requirementHead.orgId
      )
    },
    workflowBusinessId () {
      return this.requirementHead ? this.requirementHead.requirementCancelId : null
    },
    viewUpdateButton () {
      return ['add', 'edit'].includes(this.$attrs.params.flag)
    },
    isReadOnly () {
      return !['add', 'edit'].includes(this.$attrs.params.flag)
    }
  },
  watch: {
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    }
  },
  created () {
    this.Viewflag = this.$attrs.params.flag
    if (this.$attrs.params.flag === 'add') {
      const { nickname, ceeaDeptId, department, ceeaCompany, companyId } = this.$store.getters.userInfo
      console.log(this.$store.getters.userInfo, 'userInfo')
      this.requirementHead.applyByNickname = nickname
      this.requirementHead.createdFullName = nickname
      this.requirementHead.departmentId = ceeaDeptId
      this.requirementHead.departmentName = department
      this.$set(this.requirementHead, 'demandType', this.$attrs.params?.demandType)
      const row = this.$attrs.params?.row
      this.requirementHead.extSource = this.$attrs.params.extSource
      let params = {}
      params.extBidFlag = 'Y'
      params.pageSize = 10
      params.pageNum = 1
      params.requirementHeadNum = row.requirementHeadNum
      const searchData = transformMQL.save('PrSouRequirementPoolForBuyer', { ...params }, 'querySouPool')
      this.$http({
        url: '/api-sup-ce/api-ql/PrSouRequirementPoolForBuyer/querySouPool',
        method: 'POST',
        data: searchData
      }).then(datas => {
        let data = datas.data.records[0]
        let username1 = null
        let username2 = null
        let souReqHead = data
        souReqHead.username = username1
        souReqHead.dmandLineRequest = username2
        this.requirementHead.cancelLineList = [{ ...souReqHead }]
        this.cancelAttachList = data.souAttachList || []
      })
    } else {
      this.getFormDetail(this.$attrs.params.row.requirementCancelId)
    }
    this.buttonConfigInfo.save.view = this.viewUpdateButton
    this.buttonConfigInfo.submit.view = this.viewUpdateButton
    this.buttonConfigInfo.cancel.view = false
    this.buttonConfigInfo.close.view = false
  },
  methods: {
    // 确认选择品类
    comfirmSelect (node, scope) {
      scope.categoryId = node ? node.categoryId : ''
      scope.categoryName = node ? node.categoryFullName : ''
      scope.categoryCode = node ? node.categoryCode : ''
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
      return 'MQL_PR_SOU_REQ_CANCEL_INIT'
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
        'requirementCancelNo'
      ])
      this.extraData = { ...header, ...extraData }
    },
    handleSuccess (data) {
      console.log(data)
      // 导入成功就刷新界面
      data?.data.forEach(row => {
        this.setTotalAmount2(row)
        this.requirementHead.cancelLineList.unshift(row)
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

      this.$set(this.requirementHead, 'cancelLineList', this.allcancelLineList.slice(
        (val - 1) * 15,
        val * 15
      ))
      setTimeout(() => {
        this.tableLoading = false
        this.multipleSelection2 = []
      })
      this.$forceUpdate()
    },
    async getFormDetail (requirementCancelId) {
      const searchData = transformMQL.save(
        'PrSouRequirementCancelForBuyer',
        [{ requirementCancelId: requirementCancelId }],
        'getCancelInfo',
        { '*': {}, 'cancelLineList': { '*': {} }, 'cancelAttachList': { '*': {} } }
      )
      purchaseApplicationApi.getCancelInfo(searchData).then((datas) => {
        console.log(datas, 'datas')
        let data = datas.data.records[0]
        if (data?.cancelLineList == null) {
          data['cancelLineList'] = []
        }
        const { cancelAttachList, cancelLineList, ...res } = data
        this.cancelAttachList = cancelAttachList
        this.requirementHead = res
        let username = ''
        let dmandLineRequest = ''
        const souGroupList = cancelLineList[0].reqHead.souGroupList
        console.log(souGroupList)
        if (souGroupList) {
          souGroupList.forEach(e => {
            if (e.groupType == 'SOU') {
              username = e.fullName
            }
            if (e.groupType == 'VENDOR') {
              dmandLineRequest = e.fullName
            }
          })
        }
        this.requirementHead.cancelLineList = [{ ...cancelLineList[0].reqHead.souReqHead, username, dmandLineRequest, souGroupFullName: cancelLineList[0].souGroupFullName, vendorGroupFullName: cancelLineList[0].vendorGroupFullName }]
        this.buttonConfigInfo.withdraw.view = this.viewWithDrawButton
      })
    },
    // 删除
    deleteOneContent (index, row) {
      if (row.requirementCancelLineId) {
        this.cancelLineListDelete.push({ '$delete': row.requirementCancelLineId })
      }
      this.requirementHead.cancelLineList.splice(index, 1)
    },
    addUploadOne () {
      this.cancelAttachList.push({
        requirementCancelAttachId: null,
        fileId: null,
        fileName: ''
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
      const { fileId = '', fileName = '', createdBy = '', creationDate = '', createdFullName = '' } = file || {}
      row.fileId = fileId.toString()
      row.fileName = fileName
      row.createdBy = createdBy
      row.createdFullName = createdFullName
      row.creationDate = creationDate
    },
    outerHandleUploadSuccess2 (file, row) {
      const { fileId = '', fileName = '' } = file || {}
      row.extAttachId = fileId.toString()
      row.extAttachName = fileName
    },
    // 行删除
    handleDelClick (index, row) {
      if (row?.requirementCancelAttachId) {
        this.cancelAttachListDelete.push({ $delete: row?.requirementCancelAttachId })
      }
      this.cancelAttachList.splice(index, 1)
    },
    // 重置对象条件
    resetForm (form) {
      for (let i in form) {
        form[i] = ''
      }
    },
    async openDialog () {
      this.requirementHead.cancelLineList.push({
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
      const { organizationTypeCode = null, organizationName = null } = node
      scope.extUseDepartmentCode = organizationTypeCode
      scope.extUseDepartmentName = organizationName
      scope.requirementDepartment = organizationName
      console.log(this.requirementHead.cancelLineList, 'cancelLineList')
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

      this.allcancelLineList.unshift(...newArr)
      this.handleCurrentChange()
      this.dialogVisible = false
    },
    checkMaterialList (categoryId) {
      return new Promise(resolve => {
        if (
          this.allcancelLineList.length &&
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
              this.allcancelLineList = []
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
        // 请选择公司
        this.$message.error(this.$t('cusEntry.supplement20250121.promptTips20'))
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
        console.log(datas, 'datas')
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
        // this.$set(this.requirementHead.cancelLineList[index], 'extPredictAmount', total)
      }
    },
    async setTotalAmount (row, index) {
      const formFiled = await this.formValidate('requirementHeadRef')
      const fieldKeys = Object.keys(formFiled.obj)
      if (!formFiled.flag && fieldKeys.length > 0 && fieldKeys[0].includes('cancelLineList')) {
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
        const totalAmountArr = this.allcancelLineList.map(v => v.totalAmount || 0)
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
      if (this.allcancelLineList.length === 0) {
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
        this.$t('purchaseDemand.clickConfirmToApproval'),
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
      if (!allparam.cancelReason) {
        this.$message({
          type: 'warning',
          message: this.$t('orderMod.pleaseFillrequired')
        })
        return false
      }
      const saveData = transformMQL.save('PrSouRequirementCancelForBuyer', [allparam], 'submitReqCancel')
      purchaseApplicationApi.submitReqCancel(saveData).then((datas) => {
        this.$message({
          message: this.$t('common.success'),
          type: 'success'
        })
        this.$emit('tab-remove', this.$attrs.params.tabName)
        this.__setTabTodo('WithdrawZhaobiaoList.getQuerydata')
        // const requirementCancelId = datas.data.records[0].requirementCancelId
        // this.$set(this.requirementHead, 'requirementCancelId', requirementCancelId)
        // this.handlerAfter('SUBMIT')
      })
    },
    saveBill (allparam) {
      const saveData = transformMQL.save('PrSouRequirementCancelForBuyer', [allparam], 'tempSaveReqCancel')
      purchaseApplicationApi.tempSaveReqCancel(saveData).then(() => {
        this.$message({
          message: this.$t('vendorMod.temporarySuccess'),
          type: 'success'
        })
        if (this.$attrs.params.flag === 'add') {
          this.$router.push({ path: '/purchasingDemand/withdrawZhaobiao' })
        } else {
          this.back()
        }
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
      if (this.requirementHead.ceeaPrType === '01' && this.cancelAttachList.length === 0) {
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

      this.allcancelLineList.map(v => {
        v.orgId = this.requirementHead.orgId
        v.orgCode = this.requirementHead.orgCode
        v.orgName = this.requirementHead.orgName
      })
    },
    initParams () {
      let allparam = {
        ...this.requirementHead,
        cancelAttachList: this.cancelAttachList
        // cancelStatus: type === 'SUBMIT' ? 'SUBMITTED' : 'DRAFT'
      }
      allparam.cancelLineList = allparam.cancelLineList.concat(this.cancelLineListDelete)
      allparam.cancelAttachList = allparam.cancelAttachList.concat(this.cancelAttachListDelete)
      return allparam
    },
    async preNextStepHandler () {
      const { flag, message } = await this.getCheckForm()
      if (flag) {
        let allparam = this.initParams()
        allparam.cancelStatus = 'SUBMITTED'
        if (!allparam.cancelReason) {
          this.$message({
            type: 'warning',
            message: this.$t('orderMod.pleaseFillrequired')
          })
          return false
        }
        const saveData = transformMQL.save('PrSouRequirementCancelForBuyer', [allparam], 'submitReqCancel')
        const datas = await purchaseApplicationApi.submitReqCancel(saveData)
        if (datas && datas.data.records.length) {
          this.$message({
            message: this.$t('common.successSave'),
            type: 'success'
          })
          const requirementCancelId = datas.data.records[0].requirementCancelId
          this.$set(this.requirementHead, 'requirementCancelId', requirementCancelId)
          return true
        } else {
          return false
        }
      } else {
        this.__focus_error__(message)
        return false
      }
    },
    approvalHanlder (type) {
      let allparam = this.initParams()
      switch (type) {
      case 'save':
        allparam.cancelStatus = 'DRAFT'
        this.saveBill(allparam)
        break
      case 'submit':
        this.$emit('tab-remove', this.$attrs.params.tabName)
        this.__setTabTodo('WithdrawZhaobiaoList.getQuerydata')
        break
      case 'abandon':
        this.$emit('tab-remove', this.$attrs.params.tabName)
        this.__setTabTodo('WithdrawZhaobiaoList.getQuerydata')
        break
      case 'recall':
        this.getFormDetail(this.workflowBusinessId)
        break
      case 'pass':
        this.getFormDetail(this.workflowBusinessId)
        break
      default:
        break
      }
    },
    async saveOrSubmitBill (type) {
      let allparam = {
        ...this.requirementHead,
        cancelAttachList: this.cancelAttachList,
        cancelStatus: type === 'SUBMIT' ? 'SUBMITTED' : 'DRAFT'
      }
      allparam.cancelLineList = allparam.cancelLineList.concat(this.cancelLineListDelete)
      allparam.cancelAttachList = allparam.cancelAttachList.concat(this.cancelAttachListDelete)

      if (type === 'SUBMIT') {
        const { flag, message } = await this.getCheckForm()
        if (flag) {
          this.submitEvent(allparam)
        } else {
          this.__focus_error__(message)
        }
      } else {
        this.saveBill(allparam)
      }
    },
    back () {
      if (this.$attrs.params.flag === 'add') {
        this.$router.push({ path: '/purchasingDemand/withdrawZhaobiao' })
      } else {
        this.$emit('tab-remove', this.$attrs.params.tabName)
        this.__setTabTodo('purchaseApplicationList.getQuerydata')
      }
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
