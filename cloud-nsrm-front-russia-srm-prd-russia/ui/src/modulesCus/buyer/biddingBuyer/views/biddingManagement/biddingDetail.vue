<template>
  <el-container class="flex-container-aside-bidding the-bidding-detail-wrap">
    <!--左侧菜单区域-->
    <BiddingDetailMenu
      ref="biddingDetailMenu"
      :default-checked-keys="activeTab"
      :process-list="processList"
      :page-flag="pageFlag"
      @menu-item-click="menuItemClick"
      @set-enabled-flag-list="setEnabledNodeAndFlagList"
    />

    <!-- 右边-条目区域 -->
    <el-container class="flex-container flex-container-right" direction="vertical">
      <!-- 头部信息 -->
      <DetailHeader v-if="biddingBaseInfo.souNo" :bidding-base="biddingBaseInfo" />

      <!--主内容区-->
      <el-main style="padding-right: 3px">
        <el-tabs v-model="activeTab" style="height: 100%">
          <!-- 项目信息 -->
          <el-tab-pane :label="$t('bidMod.projectInformation')" name="projectInfo">
            <ProjectInfo
              ref="projectInfo"
              :readonly="isProjectReadOnly"
              :bidding-base.sync="biddingBaseInfo"
              :process-list="processList"
              :project-info-data="projectInfoData"
              :pack-name-list="packNameList"
              :enabled-node-menu="enabledNodeMenu"
              :bond-config-visible="bondConfigVisible"
              :page-flag="pageFlag"
              v-on="$listeners"
              @set-menu-config="setMenuNodeConfig"
              @temp-save-success="tempSaveSuccess"
            />
          </el-tab-pane>

          <!-- 项目需求 (报价信息 biddingInfo) -->
          <el-tab-pane
            :label="$t('cusEntry.bidMod.biddingInfo')"
            name="requireInfo"
            lazy
          >
            <RequireInfo
              ref="requireInfo"
              :readonly="isProjectReadOnly"
              :bidding-base.sync="biddingBaseInfo"
              :pack-name-list="packNameList"
              :is-active-menu="activeTab === 'requireInfo'"
              :pricing-type="pricingType"
              @temp-save-success="tempSaveSuccess"
            />
          </el-tab-pane>

          <!-- 评分规则 -->
          <el-tab-pane
            v-if="enabledNodeMenu.includes('scoreRule')"
            :label="$t('bidMod.inquiryRule')"
            name="scoreRule"
            lazy
          >
            <ScoreRule
              ref="scoreRule"
              :readonly="isProjectReadOnly"
              :bidding-base="biddingBaseInfo"
              :active-menu="activeTab"
              :is-active-menu="activeTab === 'scoreRule'"
              @temp-save-success="tempSaveSuccess"
            />
          </el-tab-pane>

          <!-- 邀请供应商 -->
          <el-tab-pane
            :label="$t('bidMod.inviteVendor')"
            name="inviteVendor"
            lazy
          >
            <InviteVendor
              ref="inviteVendor"
              :readonly="isProjectReadOnly"
              :is-active-menu="activeTab === 'inviteVendor'"
              :bidding-base="biddingBaseInfo"
              v-on="$listeners"
              @temp-save-success="tempSaveSuccess"
              @after-submit="backTo"
            />
          </el-tab-pane>

          <!-- 流程审批 -->
          <el-tab-pane
            v-if="hasWorkflowNode"
            :label="$t('bidMod.processApproval')"
            name="createApproval"
            lazy
          >
            <OrionWorkflowTab
              :scope-id="biddingBaseInfo.projectId"
              :params="{ activeWorkflowTab: true }"
              :workflow-model-id="workflowEntity.modelId"
              :workflow-enable="workflowEntity.scopePrepareStatus"
              :show-toolbar="createApprovalStatus === SOU_APPROVAL_STATUS_ENUM.DRAFT"
              @workflow-success="afterProcessActionSuccess"
              @afterProcessActionSuccess="afterProcessActionSuccess"
            />
          </el-tab-pane>

          <!-- 保证金管理 -->
          <el-tab-pane
            v-if="enabledNodeMenu.includes('bondManagement')"
            :label="$t('other.key29')"
            name="bondManagement"
            lazy
          >
            <BondManagement
              ref="bondManagement"
              :is-active-menu="activeTab === 'bondManagement'"
              :business-type="BUSINESS_TYPE_ENUM.BIDDING_LTS"
              :base-info="{ id: biddingBaseInfo.projectId, idKey: 'projectId' }"
              :bidding-base="biddingBaseInfo"
            />
          </el-tab-pane>

          <!-- 投标控制 -->
          <el-tab-pane
            v-if="enabledNodeMenu.includes('bidingControl')"
            :label="$t('bidMod.bidingControl')"
            name="bidingControl"
            lazy
          >
            <BidingControl
              ref="bidingControl"
              :bidding-base="biddingBaseInfo"
              :project-status="projectStatus"
              :create-approval-status="createApprovalStatus"
              :is-active-menu="activeTab === 'bidingControl'"
              @refresh="getFormDetail"
              @refresh-process="getProcessNode"
            />
          </el-tab-pane>

          <!-- 技术标管理 -->
          <el-tab-pane
            v-if="enabledNodeMenu.includes('techManagement')"
            :label="$t('bidMod.technicalManagement')"
            name="techManagement"
            lazy
          >
            <TechManagement
              ref="techManagement"
              :project-status="projectStatus"
              :create-approval-status="createApprovalStatus"
              :bidding-base="biddingBaseInfo"
              :is-active-menu="activeTab === 'techManagement'"
              :is-only-supply="isOnlySupplyFlag"
              @refresh="getFormDetail"
              @refresh-process="getProcessNode"
              @tab-add="tab => $emit('tab-add', tab)"
            />
          </el-tab-pane>

          <!-- 商务标管理 -->
          <el-tab-pane
            v-if="enabledNodeMenu.includes('businessManagement')"
            :label="$t('bidMod.commercialManagement')"
            name="businessManagement"
            lazy
          >
            <BusinessManagement
              ref="businessManagement"
              :project-status="projectStatus"
              :create-approval-status="createApprovalStatus"
              :bidding-base="biddingBaseInfo"
              :project-info-data="projectInfoData"
              :is-active-menu="activeTab === 'businessManagement'"
              @refresh="getFormDetail"
              @refresh-process="getProcessNode"
              @tab-add="tab => $emit('tab-add', tab)"
            />
          </el-tab-pane>

          <!-- 编制定标结果 -->
          <el-tab-pane
            v-if="enabledNodeMenu.includes('bidReuslt')"
            :label="$t('cusEntry.bidMod.bidReuslt')"
            name="bidReuslt"
            lazy
          >
            <CalibrationResult
              ref="bidReuslt"
              :project-status="projectStatus"
              :create-approval-status="createApprovalStatus"
              :bidding-base="biddingBaseInfo"
              :project-info-data="projectInfoData"
              :is-active-menu="activeTab === 'bidReuslt'"
              @refresh="getFormDetail"
              @refresh-process="getProcessNode"
            />
          </el-tab-pane>

          <!-- 中/落标通知 -->
          <el-tab-pane
            v-if="enabledNodeMenu.includes('bidWinOrLoss')"
            :label="$t('cusEntry.bidMod.bidWinOrLoss')"
            name="bidWinOrLoss"
            lazy
          >
            <BidWinOrLoss
              ref="bidWinOrLoss"
              :project-status="projectStatus"
              :create-approval-status="createApprovalStatus"
              :bidding-base="biddingBaseInfo"
              :project-info-data="projectInfoData"
              :is-active-menu="activeTab === 'bidWinOrLoss'"
              @refresh="getFormDetail"
              @refresh-process="getProcessNode"
            />
          </el-tab-pane>

          <!-- 归档 -->
          <el-tab-pane
            v-if="enabledNodeMenu.includes('bidArchive')"
            :label="$t('cusEntry.bidMod.bidArchive')"
            name="bidArchive"
            lazy
          >
            <BidArchive
              ref="bidArchive"
              :project-status="projectStatus"
              :create-approval-status="createApprovalStatus"
              :bidding-base="biddingBaseInfo"
              :project-info-data="projectInfoData"
              :is-active-menu="activeTab === 'bidArchive'"
              @refresh="getFormDetail"
              @refresh-process="getProcessNode"
              @after-submit="backTo"
            />
          </el-tab-pane>
        </el-tabs>

        <!--底部操作按钮区域-->
        <CToolbar v-if="activeTab !== 'createApproval'">
          <template slot="right">
            <!-- 审批状态为已驳回 才展示驳回说明 -->
            <p v-if="biddingBaseInfo.createApprovalStatus=='REJECTED'" style="margin-right:20px">
              驳回说明：{{ biddingBaseInfo.approveRejectDesc }}
            </p>
            <!-- 返回 -->
            <el-button @click="backTo">
              {{ $t('common.backTo') }}
            </el-button>
            <!-- 暂存 -->
            <el-button
              v-if="showStagingButton"
              type="primary"
              :disabled="isViewOrApproval"
              @click="tempStorage"
            >
              {{ $t('common.staging') }}
            </el-button>

            <!-- 上一步 ：项目需求、评分规则、邀请供应商-->
            <el-button
              v-if="showPrevOneButton"
              type="primary"
              @click="prevOne"
            >
              {{ $t('bidMod.prevOne') }}
            </el-button>

            <!-- 下一步 ：项目信息、项目需求、评分规则-->
            <el-button
              v-if="showNextOneButton"
              type="primary"
              @click="nextOne"
            >
              {{ $t('bidMod.nextOne') }}
            </el-button>
            <!--提交审批：邀请供应商-->
            <el-button
              v-if="showFlowButton"
              type="primary"
              @click="nextOne"
            >
              提交审批
            </el-button>
            <!-- 审批状态为：审批中 && 当前登录用户为招标书创建人 才能进行撤回操作-->
            <el-button
              v-if="biddingBaseInfo.createApprovalStatus == 'SUBMITTED' && userId == biddingBaseInfo.createdId"
              type="primary"
              @click="handleOperation('WITHDRAW')"
            >
              撤回
            </el-button>
            <!-- 审批状态为：审批中 && 当前登录用户为招标书审批人 才能进行审批操作（通过、驳回） -->
            <el-button
              v-if="biddingBaseInfo.createApprovalStatus == 'SUBMITTED' && userId == biddingBaseInfo.approveUserId"
              type="primary"
              @click="handleReject"
            >
              驳回
            </el-button>
            <el-button
              v-if="biddingBaseInfo.createApprovalStatus == 'SUBMITTED' && userId == biddingBaseInfo.approveUserId"
              type="primary"
              @click="handleOperation('APPROVED')"
            >
              通过
            </el-button>
            <!-- 发起定标审批：tab: 商务标管理 && 项目状态：商务已开标-->
            <el-button
              v-if="activeTab === 'businessManagement' && biddingBaseInfo.projectStatus == 'BUS_BID_OPEN'"
              type="primary"
              @click="bidApproval"
            >
              发起定标审批
            </el-button>
            <!-- 生成中/落标通知书：tab: 编制定标结果 && 项目状态：待中/落标通知 -->
            <el-button
              v-if="activeTab === 'bidReuslt' && biddingBaseInfo.projectStatus == 'WIN_LOSS_NOTICE'"
              type="primary"
              @click="bidNotice"
            >
              生成中/落标通知书
            </el-button>
            <!-- 进入项目归档: tab: 中/落标通知 && 项目状态：待归档-->
            <el-button
              v-if="activeTab === 'bidWinOrLoss' && biddingBaseInfo.projectStatus == 'ARCHIVE_TODO'"
              type="primary"
              @click="goToArchive"
            >
              进入项目归档
            </el-button>
            <!-- 项目归档：tab: 归档  && 项目状态：待归档--->
            <el-button
              v-if="activeTab === 'bidArchive' && biddingBaseInfo.projectStatus == 'ARCHIVE_TODO'"
              type="primary"
              @click="toBidArchive"
            >
              项目归档
            </el-button>
          </template>
        </CToolbar>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { bidBuyerHttp } from 'modcb@/biddingBuyer/api'
import {
  SOU_PROJECT_STATUS_ENUM,
  SOU_ORDER_TYPE_ENUM,
  BUSINESS_TYPE_ENUM,
  SOU_APPROVAL_STATUS_ENUM
} from 'lib@/composition/origin/enum'
import { isEmpty } from 'xe-utils'
import { tabTodoMixin } from '@/utils/mixins'
import { scopePrepareWorkflow } from 'lib@/composition/origin/composition'
import { WORKFLOW_MODEL_ID } from 'lib@/composition/biddingLts/utils'
import BidingControl from './biddingDetail/bidingControl.vue'
import BusinessManagement from './biddingDetail/businessManagement'
import TechManagement from './biddingDetail/techManagement'
import CalibrationResult from './biddingDetail/calibrationResult'
import BidWinOrLoss from './biddingDetail/bidWinOrLoss'
import BidArchive from './biddingDetail/bidArchive'
import InviteVendor from './biddingDetail/inviteVendor'
import ScoreRule from './biddingDetail/scoreRule'
import ProjectInfo from './biddingDetail/projectInfo'
import RequireInfo from './biddingDetail/requireInfo'
import BondManagement from './biddingDetail/bondManagement'
import DetailHeader from './biddingDetail/detailHeader'
import BiddingDetailMenu from './biddingDetail/biddingDetailMenu'
import CToolbar from 'lib@/components/c-toolbar'
import OrionWorkflowTab from 'lib@/composition/origin/workflowTab'

export default {
  name: 'BiddingDetail',

  components: {
    CToolbar,
    BiddingDetailMenu,
    DetailHeader,
    ProjectInfo,
    RequireInfo,
    ScoreRule,
    InviteVendor,
    OrionWorkflowTab,
    BondManagement,
    BidingControl,
    TechManagement,
    BusinessManagement,
    CalibrationResult,
    BidWinOrLoss,
    BidArchive
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      isOnlySupplyFlag: false,
      // 包名下拉列表
      packNameList: [],
      // 流程配置信息
      processList: [],
      submitApprovalPopoverVisible: false,
      createApprovalStatus: null,
      // 招标状态 默认为拟定
      projectStatus: 'DRAW_UP',
      activeTab: 'projectInfo',
      biddingBaseInfo: {
        // 项目信息
        projectId: '',
        processConfigId: '',
        extRecommendNo: '', // 推荐供应商单号
        souNo: '',
        souName: '',
        projectStatus: '',
        createdFullName: '',
        currentRound: '',
        extProjectNo: '',
        orderSite: '',
        extOrgBuName: '',
        extOrgOuName: '',
        extSouProcess: '',
        publishScope: '',
        extSouMode: '',
        extScoreRule: '',
        extInvestNo: '',
        extCategoryName: '',
        extTechPrincipal: '',
        extTechPrincipalTel: '',
        extBudget: '',
        orderType: '',
        extScaleQuantity: '',
        extApplicant: '',
        extApplicantDepart: '',
        extAssignEvaluator: '',
        creationDate: '',
        lastUpdateDate: '',
        applicantNo: '', // 合并申请单号
        approveUserId: null, // 招标书审批人ID
        cancelReason: null,
        approveUserName: null,
        approveFullName: null,
        approveRejectDesc: null, // 驳回说明
        mergeFlag: false, // 合并招标标识，true 或者 false
        // 招标保证金
        extEarnestFlag: 'Y', // 默认开启保证金
        extEarnestAmount: '',
        extBankName: '',
        extBankNumber: '',
        extBankAccount: '',
        extBankAccountName: '',
        // 向供应商展示联系人
        linkman: '',
        tel: '',
        email: '',
        // 标书控制
        extHideKeyInfo: 'N'
      },
      projectInfoData: {
        // 内外部附件信息
        fileList: {
          innerFiles: [],
          outerFiles: []
        },
        // 模板参考
        fileConfigList: [],
        // 工作小组
        groupList: [],
        // 可用外币列表
        currencyList: []
      },
      enabledNodeMenu: [],
      // 审批流实体
      workflowEntity: {
        // 业务状态
        scopePrepareStatus: false,
        // 绑定的模板id
        modelId: WORKFLOW_MODEL_ID
      },
      processNodeList: [], // 节点完成情况(点亮标识)
      BUSINESS_TYPE_ENUM,
      SOU_APPROVAL_STATUS_ENUM
    }
  },

  computed: {
    userId () {
      return this.$store.getters.userInfo.userId || ''
    },

    // 是否显示暂存按钮
    showStagingButton () {
      // 非查看
      return !this.isViewOrApproval &&
        // 拟定状态
        this.createApprovalStatus !== 'SUBMITTED' &&
        this.projectStatus === 'DRAW_UP' &&
        // 项目信息、项目需求、邀请供应商、评分规则
        ['projectInfo', 'requireInfo', 'inviteVendor', 'scoreRule'].includes(this.activeTab)
    },

    // 是否显示上一步按钮
    showPrevOneButton () {
      // 非查看 项目需求、邀请供应商、评分规则
      return !this.isProjectReadOnly &&
        this.createApprovalStatus !== 'SUBMITTED' &&
        ['requireInfo', 'inviteVendor', 'scoreRule'].includes(this.activeTab)
    },

    // 是否显示下一步按钮
    showNextOneButton () {
      // 非查看 && 项目信息、邀请供应商
      return !this.isProjectReadOnly &&
        this.createApprovalStatus !== 'SUBMITTED' &&
        !this.showFlowButton &&
        ['projectInfo', 'requireInfo', 'scoreRule'].includes(this.activeTab)
    },

    // 是否启用了流程审批节点
    hasWorkflowNode () {
      return this.enabledNodeMenu.includes('createApproval')
    },

    // 是否显示提交审批按钮
    showFlowButton () {
      // 非查看
      return !this.isViewOrApproval &&
        // 审批状态：拟定、已驳回、已撤回
        // 拟定状态
        ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(this.createApprovalStatus) &&
        this.projectStatus === 'DRAW_UP' &&
        // 未启用审批节点
        !this.hasWorkflowNode && (this.activeTab === 'inviteVendor')
    },

    // 报价类型
    pricingType () {
      return {
        // 普通报价
        isSimplePricing: this.biddingBaseInfo.orderType === SOU_ORDER_TYPE_ENUM.SIMPLE,
        // 公式报价
        isFormulaPricing: this.biddingBaseInfo.orderType === SOU_ORDER_TYPE_ENUM.FORMULA,
        // 模型报价
        isModelPricing: this.biddingBaseInfo.orderType === SOU_ORDER_TYPE_ENUM.MODEL
      }
    },

    /* 当前页面状态 */
    pageFlag () {
      // 新增、编辑、只读、审批
      // flag: ['add', 'edit', 'readonly', 'approval']
      const flag = this.$attrs.params.flag
      return {
        isAdd: flag === 'add',
        isEdit: flag === 'edit',
        isView: flag === 'view',
        isApproval: flag === 'approval'
      }
    },

    // 只读 或 审批
    isViewOrApproval () {
      return this.pageFlag.isView || this.pageFlag.isApproval
    },

    // 基础详细信息页只读
    // 只读: 审批状态为 已提交、已废弃、已审批
    isProjectReadOnly () {
      return this.isViewOrApproval || this.projectStatus !== 'DRAW_UP' || ['SUBMITTED', 'ABANDONED', 'APPROVED'].includes(this.createApprovalStatus)
    },

    // 是否存在保证金节点
    bondConfigVisible () {
      return this.enabledNodeMenu.includes('bondManagement')
    }
  },

  async created () {
    // 查询招标流程配置
    await this.getBidProcessConfigIdList()

    if (!this.pageFlag.isAdd) {
      // 编辑 查看 审批
      this.biddingBaseInfo.projectId = this.$attrs.params.row.projectId
      await this.getFormDetail('init')
      this.biddingBaseInfo.mergeFlag && await this.getProjectPackName()
      // 判断是否只有一个供应商参与
      await this.getIsOnlySupply()
    }
  },

  methods: {
    getIsOnlySupply () {
      bidBuyerHttp.control.orderInfos(this.biddingBaseInfo.projectId).then(res => {
        if (res && res.data) {
          const arr = res.data.filter(item => item.orderStatus === 'SUBMISSION')
          if (arr.length == 1) {
            this.isOnlySupplyFlag = true
          } else {
            this.isOnlySupplyFlag = false
          }
        }
      })
    },
    /* 查询流程配置 */
    async getBidProcessConfigIdList (pageSize = 1000) {
      const response = await bidBuyerHttp.process.page({
        pageNum: 1,
        pageSize,
        status: 'VALID'
      })
      if (response && response.data) {
        const { list = [], total = 0 } = response.data
        if (total > pageSize) {
          // 默认1000，如果大于1000，就直接拿总数再次查询
          await this.getBidProcessConfigIdList(total)
          return
        }
        this.processList = list
      }
    },

    // 查询包名下拉项
    getProjectPackName () {
      this.$http({
        url: `/api-sou/ext/buyer/bid/init/getProjectPackName?projectId=${this.biddingBaseInfo.projectId}`,
        method: 'GET',
        loading: true
      }).then(res => {
        if (res && res.data) {
          this.packNameList = res.data
        }
      })
    },

    /* 查询单据信息 */
    async getFormDetail (type) {
      const response = await bidBuyerHttp.init.getProjectInfo(this.biddingBaseInfo.projectId)
      if (!response || !response.data) {
        return
      }

      const {
        groupList = [],
        applyFileList = [],
        bidFileList = [],
        planList = [],
        project = {}
      } = response.data

      if (!project || isEmpty(project)) {
        return
      }

      const { projectStatus, createApprovalStatus, processNodeList } = project
      let extScoreRule = (!project.extScoreRule && project.extSouProcess == 'INQUIRY') ? 'LOW_PRICE' : project.extScoreRule
      let resProject = {
        ...project,
        extScoreRule
      }

      // 基础信息
      this.biddingBaseInfo = JSON.parse(JSON.stringify(resProject))
      this.$nextTick(() => {
        // 清除表单校验提示 默认不需要
        if (this.$refs.projectInfo) {
          this.$refs.projectInfo.clearFormValidate()
        }
      })

      // 单据状态
      this.projectStatus = projectStatus
      // 审批状态
      this.createApprovalStatus = createApprovalStatus
      // 节点完成情况
      this.processNodeList = processNodeList
      // 额外信息
      this.projectInfoData = {
        // 内外部附件信息
        fileList: {
          applyFileList: applyFileList.filter(item => item.fileType === 'APPLY'),
          bidFileList: bidFileList.filter(item => item.fileType === 'BID')
        },
        // 工作小组
        groupList,
        // 招标计划
        planList
      }
      this.setCurrentTabInit()
      if (type === 'init' && this.biddingBaseInfo.processConfigId) {
        // 查询当前单据招标流程配置
        await this.getProcessNode(processNodeList)
      }
    },
    /* 手动跳转页签处理，在查询详情之后 */
    setCurrentTabInit () {
      // 首页待办/已办跳转至指定tab页
      if (this.$attrs.params.row?.formTab) {
        this.activeTab = this.$attrs.params.row.formTab
      }
      // 评标组长只有技术标查看权限,直接跳转技术标
      let leaderObj = this.projectInfoData?.groupList?.find(item => item.groupRole == 'LEADER') || {}
      if (this.userId === leaderObj.userId) {
        this.activeTab = 'techManagement'
      }
    },

    /* 设置菜单节点 */
    setMenuNodeConfig (processId, type) {
      const process = this.processList.filter(v => v.processConfigId === processId)
      if (process) {
        this.$refs.biddingDetailMenu.setMenuNodeConfig(processId, type)
      }
    },

    /* 设置启用菜单节点列表 */
    async setEnabledNodeAndFlagList ({ enabledList = [], flagList = [] }) {
      // 存在流程审批节点
      if (enabledList.includes('createApproval')) {
        // 判断流程审批节点之前已生效的节点是否都已完成
        this.workflowEntity.scopePrepareStatus = scopePrepareWorkflow(enabledList, flagList, 'createApproval')
      }
      this.enabledNodeMenu = enabledList
    },

    /* 项目左侧菜单点击 */
    async menuItemClick (data) {
      // 没有单据ID || 没有流程ID
      if (!this.biddingBaseInfo.projectId || !this.biddingBaseInfo.processConfigId) {
        this.$message.warning(this.$t('bidMod.msgInputProInfo'))
        return
      }
      // 项目未发布时(审批状态为拟定) 按节点顺序填写项目信息
      // if (this.biddingBaseInfo.createApprovalStatus == 'DRAFT') {
      //   const processNode = this.processNodeList.find(item => item.processNode === data.key) || {}
      //   if (['requireInfo', 'scoreRule', 'inviteVendor'].includes(data.key) && processNode.nodeStatus !== 'Y') {
      //     this.$message.error('点击下一步完成标前准备各环节信息')
      //     return
      //   }
      // }

      // 判断单据状态，拟定状态不能点招标立项后面环节
      if (this.biddingBaseInfo.projectStatus == 'DRAW_UP' && ['bondManagement', 'bidingControl', 'techManagement',
        'businessManagement', 'bidReuslt', 'bidWinOrLoss', 'bidArchive'].includes(data.key)) {
        this.$message.error('招标状态为拟定，不可查看标前准备后续环节')
        return
      }

      // 评标组长只有技术标查看权限
      let leaderObj = this.projectInfoData?.groupList?.find(item => item.groupRole == 'LEADER') || {}
      if (this.userId === leaderObj.userId && !['techManagement'].includes(data.key)) {
        this.$message.error('无查看权限')
        return
      }

      // 未商务开标，不可查看编制定标结果
      if (data.key == 'bidReuslt' &&
      !['BUS_BID_OPEN', 'CONFIRM_BID', 'WIN_LOSS_NOTICE', 'NOTICE_ING', 'ARCHIVE_TODO', 'ARCHIVE_DONE'].includes(this.biddingBaseInfo.projectStatus)) {
        this.$message.error('未商务开标，不可查看编制定标结果')
        return
      }

      this.activeTab = data.key
      await this.$nextTick()
      // if (data.key === 'bondManagement') {
      //   // 保证金管理
      //   await this.$refs.bondManagement.getBondsData()
      // }
    },

    /* 暂存 基础信息 、需求信息 、供应商信息 */
    tempStorage (type = '') {
      switch (this.activeTab) {
      case 'projectInfo':
        // 保存项目基础信息
        this.$refs.projectInfo.tempSaveProjectInfo(type)
        break
      case 'requireInfo':
        // 需求信息
        this.$refs.requireInfo.saveRequirement(type)
        break
      case 'scoreRule':
        // 评分规则
        this.$refs.scoreRule.saveScoreRule(type)
        break
      case 'inviteVendor':
        // 供应商信息
        this.$refs.inviteVendor.saveInviteSuppliers(type)
        break
      }
    },

    /* 上一步 */
    prevOne () {
      switch (this.activeTab) {
      case 'requireInfo':
        // 报价信息
        this.activeTab = 'projectInfo'
        break
      case 'scoreRule':
        // 评分规则
        this.activeTab = 'requireInfo'
        // this.$refs.requireInfo.getProjectRequirementsData()
        break
      case 'inviteVendor':
        // 邀请供应商
        if (this.enabledNodeMenu.includes('scoreRule')) {
          this.activeTab = 'scoreRule'
          // this.$refs.scoreRule.getScoreRule()
        } else {
          this.activeTab = 'requireInfo'
          // this.$refs.requireInfo.getProjectRequirementsData()
        }
        break
      }
    },

    /* 下一步 */
    nextOne () {
      // 可编辑状态下下一步保存当前信息:项目状态=='拟定' && 审批状态=='草稿' 驳回、撤回
      if (!this.isProjectReadOnly) {
        // 需要保存
        this.tempStorage('nextOne')
      } else {
        // 不需要保存
        this.saveNextTodoBefore()
      }
    },

    // 操作审批通过 APPROVED  撤回 WITHDRAW
    handleOperation (type) {
      let confirmStr = null
      if (type == 'WITHDRAW') {
        confirmStr = '确定撤回？'
      } else if (type == 'APPROVED') {
        confirmStr = '确定通过审批？'
      }
      this.$confirm(confirmStr, {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        const params = {
          businessId: this.biddingBaseInfo.projectId,
          operate: type
        }
        bidBuyerHttp.init.approveOperate(params).then(res => {
          this.$message.success(res.message)
          this.backTo()
        })
      })
    },
    // 驳回审批 REJECTED
    handleReject () {
      this.$prompt('驳回原因', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        closeOnClickModal: false,
        inputPlaceholder: '请输入驳回原因',
        inputValidator: (value) => {
          if (!value) {
            return '请输入驳回原因'
          }
        } }).then(({ value }) => {
        const params = {
          businessId: this.biddingBaseInfo.projectId,
          operate: 'REJECTED',
          descrption: value
        }
        bidBuyerHttp.init.approveOperate(params).then(res => {
          this.$message.success(res.message)
          this.backTo()
        })
      })
    },

    // 发起定标审批
    bidApproval () {
      this.$confirm('确定发起定标审批？', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        bidBuyerHttp.control.bidApproval(this.biddingBaseInfo.projectId).then(res => {
          // 刷新单据状态
          this.afterProcessActionSuccess()
          if (!res.data.caId) return
          this.$router.push({
            name: 'calibrationApply',
            params: {
              from: 'biddingManagementNew', // 来源路由name
              row: {
                caId: res.data.caId,
                caNo: res.data.caNo
              }
            }
          })
        })
      })
    },

    // 生成中/落标通知书
    bidNotice () {
      this.$confirm('确定生成中/落标通知书？', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        bidBuyerHttp.calibration.bidNotice(this.biddingBaseInfo.projectId).then(res => {
          // 刷新单据状态
          this.afterProcessActionSuccess()
          if (!res.data.bidNoticeId) return
          this.$router.push({
            name: 'bidNotice',
            params: {
              from: 'biddingManagementNew', // 来源路由name
              row: {
                bidNoticeId: res.data.bidNoticeId,
                bidNoticeNo: res.data.bidNoticeNo
              }
            }
          })
        })
      })
    },

    // 进入项目归档
    goToArchive () {
      this.$confirm('确定进入项目归档？', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        bidBuyerHttp.notice.editNotice(this.biddingBaseInfo.projectId).then(res => {
          this.$message.success(res.message)
          this.afterProcessActionSuccess()
          this.activeTab = 'bidArchive'
        })
      })
    },

    // 项目归档
    toBidArchive () {
      this.$confirm('确定归档？', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$refs.bidArchive.saveArchiveFile('nextOne')
      })
    },

    /* 暂存 / 下一步 数据保存成功后处理 */
    tempSaveSuccess (type) {
      // 查询
      this.getFormDetail()

      if (type === 'nextOne') {
        // 保存后下一步操作
        this.saveNextTodoBefore()
      } else {
        // 暂存触发 更新节点
        this.getProcessNode()
      }
    },

    /* 跳转下一步之前的操作 */
    async saveNextTodoBefore () {
      // 节点是按顺序执行的
      // 传入当前节点，判断当前节点在已启用节点中的位置，按顺序执行下一个启用的节点
      // 写个方法判断每跳转下一步需要执行的操作
      const currentNodeIndex = this.enabledNodeMenu.indexOf(this.activeTab)
      const nextNodeIndex = currentNodeIndex + 1
      // 必须存在下一个启用节点
      if ((currentNodeIndex || currentNodeIndex === 0) && nextNodeIndex < this.enabledNodeMenu.length) {
        // 跳转下一个节点
        this.activeTab = this.enabledNodeMenu[nextNodeIndex]
        await this.$nextTick()

        if (!this.isProjectReadOnly) {
          // 更新节点信息
          await this.getProcessNode()
        }
      }
    },

    /* 获取流程节点 包含状态节点更新 */
    async getProcessNode (processNodeList) {
      if (!this.biddingBaseInfo.projectId) {
        return
      }

      let response = {}
      if (processNodeList && Array.isArray(processNodeList)) {
        response.data = processNodeList.concat()
      } else {
        response = await bidBuyerHttp.process.projectNodes(this.biddingBaseInfo.projectId)
      }

      if (response && response.data) {
        if (this.$refs.biddingDetailMenu) {
          this.$refs.biddingDetailMenu.updateTreeMenuData(response.data)
        }
      }
    },

    /* 立项审批完成 */
    afterProcessActionSuccess () {
      // 更新基础信息
      this.getFormDetail()
      // 更新流程节点信息
      this.getProcessNode()
    },

    /* 返回 */
    backTo () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('BiddingList.getQueryData')
    }
  }
}
</script>

<style lang="scss" src="./biddingDetail/biddingDetail.scss"></style>
