<template>
  <el-container>
    <el-main>
      <!-- <CWorkflowMulti
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
        @close-tab="goBack"
      > -->
      <!-- <ApprovalProcess
        :business-id="workflowBusinessId"
        business-type="INSPECT_REPORT"
        :approval-status="detailForm.inspectStatus"
        :status-map="statusMap"
        :readonly="$attrs.params.flag === 'view'"
        :operation-pre-options="operationPreOptions"
        @approval-handler-callback="approvalHandlerCallback"
      > -->
        <el-form
          ref="detailForm"
          :model="detailForm"
          :disabled="isReadOnly"
        >
          <el-collapse v-model="activeNum">
            <el-collapse-item :title="$t('meeting.baseInfo')" name="1">
              <srm-row>
                <srm-col :init-col="3">
                  <el-form-item
                    prop="bidingName"
                    :label="$t('bidMod.bidingName')"
                    :rules="[{ required: true, message: this.$t('common.pleaseSelect'), trigger: ['blur', 'change'] }]"
                  >
                    <QuickSearch
                      disabled
                      :showInput="detailForm.bidingName"
                      show-key="bidingName"
                      :scope-data="detailForm"
                      :placeholder="$t('common.pleaseSelect')"
                      name="scc_npm_sou_project"
                    />
                  </el-form-item>
                </srm-col>
                <!-- 招标单号 -->
                <srm-col :init-col="3">
                  <el-form-item
                    prop="bidingNum"
                    :label="$t('cusEntry.biddingSettings.bidingNum')"
                  >
                    <el-input v-model="detailForm.bidingNum" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 招标专家 -->
                <srm-col :init-col="3">
                  <el-form-item
                    prop="bidingHead"
                    :label="$t('cusEntry.biddingSettings.bidChargePerson')"
                  >
                    <el-input v-model="detailForm.bidingHead" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 出行方式 -->
                <srm-col :init-col="3">
                  <el-form-item
                    prop="comeType"
                    :label="$t('cusEntry.biddingSettings.travelMode')"
                  >
                    <el-input v-model="detailForm.comeType" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 公司名称 -->
                <srm-col :init-col="3">
                  <el-form-item
                    prop="orgName"
                    :label="$t('vendorMod.corporateName')"
                  >
                    <el-input v-model="detailForm.orgName" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 申请部门 -->
                <srm-col :init-col="3">
                  <el-form-item
                    prop="departmentName"
                    :label="$t('purchaseDemand.ceeaDepartment')"
                  >
                    <el-input v-model="detailForm.departmentName" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 考察单号 -->
                <srm-col :init-col="3">
                  <el-form-item
                    prop="inspectNum"
                    :label="$t('cusEntry.biddingSettings.inspectBill')"
                  >
                    <el-input v-model="detailForm.inspectNum" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 招标部是否参加 -->
                <srm-col :init-col="3">
                  <el-form-item
                    prop="bidingDepartmentFlag"
                    :label="$t('cusEntry.biddingSettings.ifParticipate')"
                  >
                    <dict-select v-model="detailForm.bidingDepartmentFlag" code="YES_OR_NO" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col :init-col="3">
                  <!-- <el-form-item
                    prop="bidingTechHead"
                    label="技术专家"
                  > -->
                  <el-form-item
                    prop="bidingTechHead"
                    :label="$t('cusEntry.technicalFlow.technicalDirector')"
                  >
                    <el-input v-model="detailForm.bidingTechHead" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 考察报告单据号 -->
                <srm-col :init-col="3">
                  <el-form-item
                    prop="reportNum  "
                    :label="$t('cusEntry.biddingSettings.inspectReportBill')"
                  >
                    <el-input v-model="detailForm.reportNum" disabled />
                  </el-form-item>
                </srm-col>
                <!-- 拟参加人员 -->
                <srm-col :init-col="1">
                  <el-form-item
                    prop="comment"
                    :label="$t('cusEntry.biddingSettings.intendedPerson')"
                    :rules="[{ required: true, message: this.$t('common.pleaseInput'), trigger: ['blur', 'change'] }]"
                  >
                    <el-input v-model="detailForm.comment" disabled />
                  </el-form-item>
                </srm-col>
                <srm-col v-if="detailForm.inspectStatus==='ABANDON'" :init-col="1">
                  <!-- <el-form-item label="废弃说明"> -->
                  <el-form-item :label="$t('cusEntry.supplement20250121.reasonDesc')">
                    <el-input v-model="detailForm.reasonDesc" disabled />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
            <el-collapse-item :title="$t('cusEntry.biddingSettings.inspectionUnitEva')" name="2">
              <el-table
                border
                max-height="300px"
                :data="vendorList"
              >
                <el-table-column
                  align="center"
                  type="index"
                  fixed="left"
                  :label="$t('common.sort')"
                  width="50"
                />
                <el-table-column
                  align="center"
                  prop="vendorName"
                  :label="$t('common.vendorName')"
                  minWidth="120"
                />
                <el-table-column
                  align="center"
                  prop="extVendorLinkMan"
                  :label="$t('vendorMod.vendorContact')"
                  minWidth="120"
                />
                <el-table-column
                  align="center"
                  prop="extVendorLinkPhone"
                  :label="$t('contractMod.mobileNumber')"
                  minWidth="120"
                />
                <el-table-column
                  align="center"
                  prop="inspectAddress"
                  :label="$t('cusEntry.common.location')"
                  minWidth="120"
                />
                <el-table-column
                  align="center"
                  prop="inspectCause"
                  :label="$t('cusEntry.biddingSettings.inspectionReason')"
                  minWidth="120"
                />
                <el-table-column
                  align="center"
                  prop="inspectTime"
                  :label="$t('cusEntry.biddingSettings.inspectionTime')"
                  :formatter="(row, column, cellValue) => $parseTime(cellValue)"
                  minWidth="120"
                />
                <el-table-column
                  align="center"
                  prop="inspectContent"
                  :label="$t('cusEntry.biddingSettings.inspectionContent')"
                  minWidth="120"
                />
                <!-- 现场管理评价 -->
                <el-table-column
                  align="center"
                  prop="manageEvaluate"
                  :label="$t('cusEntry.biddingSettings.siteManageEva')"
                  :render-header="_addStarToColumn"
                  minWidth="150"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.manageEvaluate"
                      :placeholder="$t('cusEntry.common.pleaseFill')"
                      :disabled="isReadOnly"
                    />
                  </template>
                </el-table-column>
                <!-- 主要生产设备评价 -->
                <el-table-column
                  align="center"
                  prop="deviceEvaluate"
                  :label="$t('cusEntry.biddingSettings.prodEquipEva')"
                  :render-header="_addStarToColumn"
                  minWidth="150"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.deviceEvaluate"
                      :placeholder="$t('cusEntry.common.pleaseFill')"
                      :disabled="isReadOnly"
                    />
                  </template>
                </el-table-column>
                <!-- 人员状况 -->
                <el-table-column
                  align="center"
                  prop="staffEvaluate"
                  :label="$t('cusEntry.biddingSettings.personStatus')"
                  :render-header="_addStarToColumn"
                  minWidth="150"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.staffEvaluate"
                      :placeholder="$t('cusEntry.common.pleaseFill')"
                      :disabled="isReadOnly"
                    />
                  </template>
                </el-table-column>
                <!-- 业绩情况 -->
                <el-table-column
                  align="center"
                  prop="performanceEvaluate"
                  :label="$t('cusEntry.biddingSettings.performance')"
                  :render-header="_addStarToColumn"
                  minWidth="150"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.performanceEvaluate"
                      :placeholder="$t('cusEntry.common.pleaseFill')"
                      :disabled="isReadOnly"
                    />
                  </template>
                </el-table-column>
                <!-- 综合意见 -->
                <el-table-column
                  align="center"
                  prop="comprehensiveEvaluation"
                  :label="$t('cusEntry.biddingSettings.comprehensiveOp')"
                  :render-header="_addStarToColumn"
                  minWidth="150"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.comprehensiveEvaluation"
                      :placeholder="$t('cusEntry.common.pleaseFill')"
                      :disabled="isReadOnly"
                    />
                  </template>
                </el-table-column>
                <!-- 其他方面 -->
                <el-table-column
                  align="center"
                  prop="comment"
                  :label="$t('cusEntry.biddingSettings.otherAspects')"
                  :render-header="_addStarToColumn"
                  minWidth="150"
                >
                  <template slot-scope="scope">
                    <el-input
                      v-model="scope.row.comment"
                      :placeholder="$t('cusEntry.common.pleaseFill')"
                      :disabled="isReadOnly"
                    />
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
            <!-- <el-collapse-item title="参与人员" name="3"> -->
            <el-collapse-item :title="$t('cusEntry.supplement20250205.participants')" name="3">
              <el-table
                border
                max-height="300px"
                :data="userList"
              >
                <el-table-column
                  align="center"
                  type="index"
                  fixed="left"
                  :label="$t('common.sort')"
                  width="50"
                />
                <!-- <el-table-column
                  align="center"
                  prop="username"
                  label="姓名"
                  minWidth="150"
                > -->
                <el-table-column
                  align="center"
                  prop="username"
                  :label="$t('vendorMod.nickname')"
                  minWidth="150"
                >
                  <template slot-scope="scope">
                    <QuickSearch
                      disabled
                      :showInput="scope.row.username"
                      show-key="nickname"
                      :scope-data="scope"
                      :placeholder="$t('common.pleaseSelect')"
                      name="scc_rbac_user_display"
                    />
                  </template>
                </el-table-column>
                <!-- <el-table-column
                  align="center"
                  prop="jobDesc"
                  label="岗位"
                  minWidth="150"
                /> -->
                <el-table-column
                  align="center"
                  prop="jobDesc"
                  :label="$t('components.orgPositionSel.position')"
                  minWidth="150"
                />
                <!-- <el-table-column
                  align="center"
                  prop="company"
                  label="所属单位"
                  minWidth="150"
                /> -->
                <el-table-column
                  align="center"
                  prop="company"
                  :label="$t('cusEntry.competition.belongCompany')"
                  minWidth="150"
                />
                <!-- <el-table-column
                  align="center"
                  prop="department"
                  label="部门"
                  minWidth="150"
                /> -->
                <el-table-column
                  align="center"
                  prop="department"
                  :label="$t('cusEntry.centralizedPurchase.department')"
                  minWidth="150"
                />
                <!-- <el-table-column
                  align="center"
                  prop="bidFlag"
                  label="是否评标组成员"
                  minWidth="150"
                > -->
                <el-table-column
                  align="center"
                  prop="bidFlag"
                  :label="$t('cusEntry.supplement20250205.isBidEvaluationGroupMember')"
                  minWidth="150"
                >
                  <template slot-scope="scope">
                    <dict-select
                      v-model="scope.row.bidFlag"
                      code="YES_OR_NO"
                      disabled
                    />
                  </template>
                </el-table-column>
                <!-- <el-table-column
                  align="center"
                  prop="responsibility"
                  label="负责内容"
                  minWidth="150"
                /> -->
                <el-table-column
                  align="center"
                  prop="responsibility"
                  :label="$t('cusEntry.supplement20250205.responsibleContent')"
                  minWidth="150"
                />
              </el-table>
            </el-collapse-item>
            <el-collapse-item :title="$t('cusEntry.biddingSettings.inspectionOR')" name="4">
              <srm-row>
                <!-- 综合意见 -->
                <!-- <srm-col :init-col="1">
                  <el-form-item
                    prop="comprehensiveEvaluation"
                    :label="$t('cusEntry.biddingSettings.comprehensiveOp')"
                    :rules="[{ required: true, message: this.$t('cusEntry.common.pleaseFill'), trigger: ['blur', 'change'] }]"
                  >
                    <el-input v-model="detailForm.comprehensiveEvaluation" />
                  </el-form-item>
                </srm-col> -->
                <!-- 评标委员会主席意见 -->
                <srm-col :init-col="1">
                  <el-form-item
                    prop="leaderEvaluation"
                    :label="$t('cusEntry.biddingSettings.bidLeaderOp')"
                    :rules="[{ required: true, message: this.$t('cusEntry.common.pleaseFill'), trigger: ['blur', 'change'] }]"
                  >
                    <el-input v-model="detailForm.leaderEvaluation" />
                  </el-form-item>
                </srm-col>
              </srm-row>
            </el-collapse-item>
            <el-collapse-item :title="$t('contractMod.addUploadFile')" name="5">
              <div style="margin-bottom:12px;">
                <el-button type="primary" :disabled="isReadOnly" @click="addUploadOne">
                  {{ $t('common.add') }}
                </el-button>
                <el-button
                  type="primary"
                  @click="downloadTemplate"
                >
                  {{ $t('cusEntry.common.downloadTemplate') }}
                </el-button>
              </div>
              <el-table
                border
                :data="attachList"
                style="width: 100%"
                max-height="250px"
              >
                <el-table-column
                  align="center"
                  type="index"
                  :label="$t('common.sort')"
                  width="60"
                />
                <el-table-column
                  align="center"
                  prop="attachName"
                  :label="$t('bidMod.fileName')"
                >
                  <template slot-scope="scope">
                    <SrmCommonFile
                      :extra-data="fileInfo"
                      :default-file="{
                        fileId: scope.row.attachId,
                        fileName: scope.row.attachName
                      }"
                      :readonly="isReadOnly"
                      @on-change="({file}) => uploadSuccess(file,scope.row)"
                    />
                  </template>
                </el-table-column>
                <el-table-column
                  align="center"
                  prop="createdFullName"
                  :label="$t('quota.uploadBy')"
                />
                <el-table-column
                  align="center"
                  prop="creationDate"
                  :label="$t('quota.uploadDate')"
                >
                  <template slot-scope="scope">
                    {{$parseTime(scope.row.creationDate)}}
                  </template>
                </el-table-column>
                <el-table-column
                  :label="$t('common.operation')"
                  width="80"
                >
                  <template slot-scope="scope">
                    <el-button
                      type="text"
                      :disabled="isReadOnly"
                      @click="deleteItem(scope.$index,scope.row)"
                    >
                      {{ $t('common.delete') }}
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-collapse-item>
          </el-collapse>
        </el-form>
      <!-- </ApprovalProcess> -->
      <!-- </CWorkflowMulti> -->
    </el-main>
    <CToolbar>
      <template slot="right">
        <el-button @click="goBack">
          {{ $t('common.backTo') }}
        </el-button>
        <el-button
          v-if="viewUpdateButton"
          type="primary"
          @click="saveOrSubmitBill('SAVE')"
        >
          {{ $t('flowMod.temporaryView') }}
        </el-button>
        <el-button
          v-if="viewUpdateButton"
          type="primary"
          @click="saveOrSubmitBill('SUBMIT')"
        >
          {{ $t('common.submit') }}
        </el-button>
      </template>
    </CToolbar>
  </el-container>
</template>
<script>
import { tabTodoWatch, tabTodoMixin } from '@/utils/mixins'
import CToolbar from 'lib@/components/c-toolbar'
import QuickSearch from 'lib@/components/QuickSearch'
import { transformMQL } from 'lib@/utils/util'
import WorkflowCommon from '@/library/mixins/workflow-common'
import { downloadWithParam } from 'lib@/utils/file'
import ApprovalProcess from 'modc@/components/approval-process'

export default {
  name: 'InspectReportDetail',
  components: {
    CToolbar,
    QuickSearch,
    ApprovalProcess
  },
  mixins: [tabTodoWatch, tabTodoMixin, WorkflowCommon],
  data () {
    return {
      isReadOnly: false,
      activeNum: ['1', '2', '3', '4', '5'],
      detailForm: {
        inspectStatus: 'APPLY_APPROVED',
        bidingTechHead: null,
        bidingName: null,
        bidingNum: null,
        bidingHead: null,
        comeType: null,
        orgId: null,
        orgCode: null,
        orgName: null,
        departmentId: null,
        departmentCode: null,
        departmentName: null,
        inspectNum: null,
        reportNum: null,
        bidingDepartmentFlag: null,
        comment: null,
        reasonDesc: null,
        comprehensiveEvaluation: null,
        leaderEvaluation: null
      },
      userList: [],
      vendorList: [],
      attachList: [],
      attachListDelete: [],
      fileInfo: {
        uploadType: 'DEF', // 固定
        sourceType: 'WEB_APP', // 固定
        fileModular: 'base', // 模块
        fileFunction: 'commonFile', // 功能
        fileType: 'images' // 类型
      },
      operationPreOptions: {
        nextStep: this.preNextStepHandler
      },
      statusMap: {
        DRAFT: 'APPLY_APPROVED', // 拟定
        SUBMITTED: 'REPORT_APPROVING', // 已提交
        APPROVED: 'REPORT_APPROVED', // 审批通过
        REJECTED: 'REPORT_REJECTED', // 已驳回
        WITHDRAW: 'REPORT_WITHDRAW', // 已撤回
        ABANDONED: 'ABANDON' // 已废弃
      }
    }
  },
  computed: {
    workflowBusinessId () {
      // 用来指定工作流的业务ID
      return this.detailForm ? this.detailForm.inspectId : null
    },
    workflowTabDisabled () {
      // [待提交报告] 状态的单据 流程审批按钮失效
      return ['APPLY_APPROVED'].includes(this.detailForm.inspectStatus)
    },
    viewUpdateButton () {
      return ['APPLY_APPROVED', 'REPORT_WITHDRAW', 'REPORT_REJECTED'].includes(this.detailForm.inspectStatus) && !this.isReadOnly
    },
    viewWithDrawButton () { // 用于控制撤回按钮是否显示
      return ['REPORT_APPROVING'].includes(this.detailForm.inspectStatus) &&
        this.workflowParamsInfo.integrationMode === 'Push' &&
        this.$attrs.params.flag === 'approval'
    }
  },
  watch: {
    // 监听保存提交 按钮变更状态，如果自定义按钮则无需添加
    viewUpdateButton () {
      this.buttonConfigInfo.save.view = this.viewUpdateButton
      this.buttonConfigInfo.submit.view = this.viewUpdateButton
    },
    viewWithDrawButton () {
      this.buttonConfigInfo.withdraw.view = this.viewWithDrawButton
    }
  },
  async created () {
    const { flag, row } = this.$attrs.params
    this.isReadOnly = ['view', 'approval'].includes(flag)
    if (flag !== 'add') {
      await this.getTemplateFile()
      this.getFormDetail(row.inspectId)
    }

    // this.buttonConfigInfo.withdraw.view = this.viewWithDrawButton
    // this.buttonConfigInfo.save.view = this.viewUpdateButton
    // this.buttonConfigInfo.submit.view = this.viewUpdateButton
    // this.buttonConfigInfo.cancel.view = !this.isReadOnly
    // this.buttonConfigInfo.close.view = this.isReadOnly
  },
  methods: {
    // 下一步前置处理
    async preNextStepHandler () {
      let validForm = false
      await this.$refs.detailForm.validate(valid => {
        validForm = valid
      })
      if (!validForm) {
        // this.$message.warning('请填写完必填项')
        this.$message.warning(this.$t('cusEntry.supplement20250121.pleaseCompleteTheRequiredFields'))
        return false
      }
      // 校验 考察单位评价
      let flag = this.vendorList.some(item => !item.manageEvaluate || !item.deviceEvaluate || !item.staffEvaluate || !item.performanceEvaluate || !item.comment)
      if (flag) {
        this.$message.warning(this.$t('common.pleasefinishRequired'))
        return false
      }
      // 校验附件
      let fileFlag = this.attachList.some(item => !item.attachId)
      if (fileFlag) {
        // this.$message.warning('请上传附件')
        this.$message.warning(this.$t('cusEntry.supplement20250121.pleaseUploadTheAttachment'))
        return false
      }
      // 调用暂存接口
      const params = {
        ...this.detailForm,
        userList: this.userList,
        vendorList: this.vendorList,
        attachList: [...this.attachList, ...this.attachListDelete]
      }
      const saveData = transformMQL.save('Inspect', [params], 'report')
      const response = await this.$http({
        url: '/api-sou/api-ql/Inspect/report',
        method: 'POST',
        data: saveData,
        loading: true
      })
      this.detailForm = response.data[0]
      return true
    },
    // 审批流操作回调
    approvalHandlerCallback (type) {
      switch (type) {
      case 'save':
        this.saveInspect('SAVE')
        break
      case 'submit':
        this.goBack()
        break
      default:
        break
      }
    },
    getCWorkflowRefName () {
      return 'workflowMulti' // 对应CWorkflowMulti标签中的ref
    },
    // 指定工作流的业务类型，在定义工作流时指定
    getWorkflowBusinessType () {
      return 'INSPECT_REPORT' // 这里之后是和后端对接配置好的流程审批模块
    },
    goBack () {
      if (this.$attrs.params.flag !== 'add') {
        this.$emit('tab-remove', 'inspectReportDetail' + this.$attrs.params.row.inspectId)
      } else {
        this.$emit('tab-remove', 'inspectReportDetail')
      }
      this.__setTabTodo('inspectManageList.getQueryData')
    },
    getTemplateFile () {
      this.$http({
        url: '/api-sup-ce/purchaseConfig/get/inspectReportFile',
        method: 'GET',
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.configValue = res.data.configValue
        }
      })
    },
    downloadTemplate () {
      downloadWithParam(
        this.configValue.fileUploadId,
        this.configValue.fileName,
      ).catch(() => {
        this.$message.error(this.$t('components.eio.downloadFail')) // 下载失败
      })
    },
    getFormDetail (inspectId) {
      const searchData = transformMQL.save(
        'Inspect',
        [inspectId],
        'read',
        {
          '*': {},
          'userList': { '*': {} },
          'attachList': { '*': {} },
          'vendorList': { '*': {} }
        }
      )
      this.$http({
        url: '/api-sou/api-ql/Inspect/read',
        method: 'POST',
        data: searchData,
        loading: true
      }).then(res => {
        if (res && res.data && res.data.length) {
          const { vendorList = [], attachList = [], userList = [], ...rest } = res.data[0]
          this.detailForm = { ...rest }
          this.userList = userList
          this.vendorList = vendorList
          this.attachList = attachList
        }
      })
    },
    saveOrSubmitBill (type) { // 点击 提交审批或保存按钮
      if (type === 'SUBMIT') {
        this.publishInspect(type)
      } else if (type === 'SAVE') {
        this.saveInspect(type)
      }
    },
    saveInspect (type) {
      const params = {
        ...this.detailForm,
        inspectStatus: type === 'SUBMIT' ? 'REPORT_APPROVED' : this.detailForm.inspectStatus,
        userList: this.userList,
        vendorList: this.vendorList,
        attachList: [...this.attachList, ...this.attachListDelete]
      }
      const saveData = transformMQL.save('Inspect', [params], 'report')
      this.$http({
        url: '/api-sou/api-ql/Inspect/report',
        method: 'POST',
        data: saveData,
        loading: true
      }).then(async res => {
        this.detailForm = res.data[0]
        this.getFormDetail(this.detailForm.inspectId)
        if (type === 'SAVE') {
          this.$message.success(this.$t('common.successSave'))
          this.__setTabTodo('inspectManageList.getQueryData')
        } else {
          // await this.handlerAfter(type, 'Y', () => {
          //   this.__setTabTodo('inspectManageList.getQueryData')
          // })
          this.$message.success(this.$t('common.successSubmit'))
          this.goBack()
        }
      })
    },
    publishInspect (type) {
      this.$refs.detailForm.validate(valid => {
        if (valid) {
          // 校验 考察单位评价
          let flag = this.vendorList.some(item =>
            !item.manageEvaluate || !item.deviceEvaluate || !item.staffEvaluate || !item.performanceEvaluate || !item.comment)
          if (flag) {
            this.$message.warning(this.$t('common.pleasefinishRequired'))
            return
          }
          // 校验附件
          let fileFlag = this.attachList.some(item => !item.attachId)
          if (fileFlag) {
            // this.$message.warning('请上传附件')
            this.$message.warning(this.$t('cusEntry.supplement20250121.pleaseUploadTheAttachment'))
            return
          }
          this.saveInspect(type)
        } else {
          return this.$message.warning(this.$t('common.pleasefinishRequired'))
        }
      })
    },
    addUploadOne () {
      this.attachList.push({
        attachId: null,
        attachName: null,
        createdFullName: null,
        creationDate: null
      })
    },
    uploadSuccess (file, row) {
      const { fileId = '', fileName = '', createdFullName = '', creationDate = '' } = file || {}
      row.attachId = fileId.toString()
      row.attachName = fileName
      row.createdFullName = createdFullName
      row.creationDate = creationDate
    },
    // 删除
    deleteItem (index, row) {
      if (row.inspectAttachId) {
        this.attachListDelete.push({ '$delete': row.inspectAttachId })
      }
      this.attachList.splice(index, 1)
    }
  }
}
</script>
<style lang="scss" scoped>
</style>
