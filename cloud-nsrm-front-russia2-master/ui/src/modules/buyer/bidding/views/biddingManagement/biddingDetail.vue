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
              :enabled-node-menu="enabledNodeMenu"
              :bond-config-visible="bondConfigVisible"
              :page-flag="pageFlag"
              @set-menu-config="setMenuNodeConfig"
              @temp-save-success="tempSaveSuccess"
            />
          </el-tab-pane>

          <!-- 项目需求 -->
          <el-tab-pane
            :label="$t('bidMod.projectRequirement')"
            name="requireInfo"
            lazy
          >
            <RequireInfo
              ref="requireInfo"
              :readonly="isProjectReadOnly"
              :bidding-base.sync="biddingBaseInfo"
              :is-active-menu="activeTab === 'requireInfo'"
              :pricing-type="pricingType"
              @temp-save-success="tempSaveSuccess"
            />
          </el-tab-pane>

          <!-- 邀请供应商 -->
          <el-tab-pane
            v-if="enabledNodeMenu.includes('inviteVendor')"
            :label="$t('bidMod.inviteVendor')"
            name="inviteVendor"
            lazy
          >
            <InviteVendor
              ref="inviteVendor"
              :readonly="isProjectReadOnly"
              :is-active-menu="activeTab === 'inviteVendor'"
              :bidding-base="biddingBaseInfo"
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
            v-if="bondConfigVisible"
            :label="$t('bidMod.bondManagement')"
            name="bondManagement"
            lazy
          >
            <OrionBondManagement
              ref="bondManagement"
              :business-type="BUSINESS_TYPE_ENUM.BIDDING_LTS"
              :base-info="{ id: biddingBaseInfo.projectId, idKey: 'projectId' }"
              :is-active-menu="activeTab === 'bondManagement'"
            />
          </el-tab-pane>

          <!-- 报名管理 -->
          <el-tab-pane
            v-if="enabledNodeMenu.includes('signUpManagement')"
            :label="$t('bidMod.entryManagement')"
            name="signUpManagement"
            lazy
          >
            <SignUpManagement
              ref="signUpManagement"
              :page-flag="pageFlag"
              :project-status="projectStatus"
              :bidding-base="biddingBaseInfo"
              :is-active-menu="activeTab === 'signUpManagement'"
              @refresh="getFormDetail"
              @refresh-process="getProcessNode"
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
              @refresh="getFormDetail"
              @refresh-process="getProcessNode"
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
            />
          </el-tab-pane>

          <!-- 评选 -->
          <el-tab-pane
            v-if="enabledNodeMenu.includes('evaluation')"
            :label="$t('bidMod.bidEvaluation')"
            name="evaluation"
            lazy
          >
            <Evaluation
              ref="evaluation"
              :project-status="projectStatus"
              :create-approval-status="createApprovalStatus"
              :bidding-base="biddingBaseInfo"
              :pricing-type="pricingType"
              :is-active-menu="activeTab === 'evaluation'"
              @tab-add="tab => $emit('tab-add', tab)"
              @refresh="getFormDetail"
              @refresh-process="getProcessNode"
            />
          </el-tab-pane>
        </el-tabs>

        <!--底部操作按钮区域-->
        <CToolbar v-if="activeTab !== 'createApproval'">
          <template slot="right">
            <!-- 暂存 -->
            <el-button
              v-if="showStagingButton"
              type="primary"
              :disabled="isViewOrApproval"
              @click="tempStorage"
            >
              {{ $t('common.staging') }}
            </el-button>

            <!-- 上一步 ：项目需求、邀请供应商、评分规则-->
            <el-button
              v-if="showPrevOneButton"
              type="primary"
              @click="prevOne"
            >
              {{ $t('bidMod.prevOne') }}
            </el-button>

            <!-- 下一步 ：项目信息、项目需求、邀请供应商、评分规则-->
            <el-button
              v-if="showNextOneButton"
              type="primary"
              @click="nextOne"
            >
              {{ $t('bidMod.nextOne') }}
            </el-button>

            <!--提交审批-->
            <el-popover
              v-if="showFlowButton"
              v-model="submitApprovalPopoverVisible"
              placement="top"
              width="200"
            >
              <div style="padding: 10px">
                <p>当前无审批流程，是否直接提交通过？</p>
                <div style="text-align: right; margin: 0">
                  <el-button @click="submitApprovalPopoverVisible = false">
                    {{ $t('common.cancel') }}
                  </el-button>

                  <!--提交审批，调下一步-->
                  <el-button type="primary" @click="nextOne">
                    {{ $t('common.confirm') }}
                  </el-button>
                </div>
              </div>

              <!--提交审批-->
              <el-button
                slot="reference"
                type="primary"
                style="margin: 0 5px"
              >
                {{ $t('bidMod.submitapprovlaFlowing') }}
              </el-button>
            </el-popover>

            <!-- 返回 -->
            <el-button @click="backTo">
              {{ $t('common.backTo') }}
            </el-button>
          </template>
        </CToolbar>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { bidBuyerHttp } from 'modb@/bidding/api'
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
import SignUpManagement from './biddingDetail/signUpManagement.vue'
import BidingControl from './biddingDetail/bidingControl.vue'
import BusinessManagement from './biddingDetail/businessManagement'
import TechManagement from './biddingDetail/techManagement'
import Evaluation from './biddingDetail/evaluation'
import InviteVendor from './biddingDetail/inviteVendor'
import ScoreRule from './biddingDetail/scoreRule'
import ProjectInfo from './biddingDetail/projectInfo'
import RequireInfo from './biddingDetail/requireInfo'
import DetailHeader from './biddingDetail/detailHeader'
import BiddingDetailMenu from './biddingDetail/biddingDetailMenu'
import CToolbar from 'lib@/components/c-toolbar'
import OrionWorkflowTab from 'lib@/composition/origin/workflowTab'
import OrionBondManagement from 'lib@/composition/origin/bondPay/bondManagementNew'

export default {
  name: 'BiddingDetail',

  components: {
    SignUpManagement,
    BidingControl,
    BusinessManagement,
    TechManagement,
    Evaluation,
    InviteVendor,
    ScoreRule,
    ProjectInfo,
    RequireInfo,
    DetailHeader,
    BiddingDetailMenu,
    CToolbar,
    OrionWorkflowTab,
    OrionBondManagement
  },

  mixins: [tabTodoMixin],

  provide(){
    return {
      pricingType: computed(() => this.pricingType)
    }
  },

  data () {
    return {
      // 流程配置信息
      processList: [],
      submitApprovalPopoverVisible: false,
      createApprovalStatus: null,
      // 招标状态 默认拟定
      projectStatus: SOU_PROJECT_STATUS_ENUM.DRAFT,
      activeTab: 'projectInfo',
      biddingBaseInfo: {
        // 项目信息
        projectId: '',
        processConfigId: '',
        souNo: '',
        souName: '',
        orderSite: '',
        publishScope: '',
        budgetAmount: '',
        biddingType: '',
        scoreRuleType: '',
        orderWay: '',
        signUpEndTime: '',
        orderStartTime: '',
        isSyncToPriceLibrary: '',
        // 写死来源
        sourceFromType: 'HAND_MAKE',
        // 投标控制
        allowWithdraw: 'N',
        allowPartPrice: 'Y',
        publicLowestPrice: 'N',
        publicTotalRank: 'N',
        needEncryptPrice: 'N',
        visibleFinalPrice: 'N',
        visibleWinVendor: 'N',
        // 邀请供应控制
        excludeBlackVendors: 'Y',
        excludeNoCurrentOrgVendors: 'N',
        excludeOrgQuitVendors: 'N',
        excludeOrgCategoryStatus: '',
        // 商务要求
        bondAmount: '',
        bondMethod: '',
        bondEndTime: '',
        bankAccountNum: '',
        bankAccountName: '',
        bankBranchName: '',
        bondDesc: '',
        // 向供应商展示的联系方式
        linkman: '',
        tel: '',
        email: '',
        standardCurrency: 'CNY',
        pricePrecision: '4',
        exchangeRateType: '',
        showRateType: 'N',
        currencyExchangeDate: new Date().getTime(),
        // 需求简述
        requireDesc: '',
        // 报价类型 默认普通报价
        orderType: SOU_ORDER_TYPE_ENUM.SIMPLE,
        quoteTempName: null,
        quoteTempId: null

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
        this.createApprovalStatus !== SOU_APPROVAL_STATUS_ENUM.APPROVED &&
        this.projectStatus === SOU_PROJECT_STATUS_ENUM.DRAFT &&
        // 项目信息、项目需求、邀请供应商、评分规则
        ['projectInfo', 'requireInfo', 'inviteVendor', 'scoreRule'].includes(this.activeTab)
    },

    // 是否显示上一步按钮
    showPrevOneButton () {
      // 非查看 项目需求、邀请供应商、评分规则
      return !this.isProjectReadOnly &&
        ['requireInfo', 'inviteVendor', 'scoreRule'].includes(this.activeTab)
    },

    // 是否显示下一步按钮
    showNextOneButton () {
      // 非查看 && 项目信息、邀请供应商
      return !this.isProjectReadOnly &&
        !this.showFlowButton &&
        ['projectInfo', 'requireInfo', 'inviteVendor', 'scoreRule'].includes(this.activeTab)
    },

    // 是否启用了流程审批节点
    hasWorkflowNode () {
      return this.enabledNodeMenu.includes('createApproval')
    },

    // 是否显示提交审批按钮
    showFlowButton () {
      // 非查看
      return !this.isViewOrApproval &&
        // 拟定状态
        this.createApprovalStatus !== SOU_APPROVAL_STATUS_ENUM.APPROVED &&
        this.projectStatus === SOU_PROJECT_STATUS_ENUM.DRAFT &&
        // 未启用审批节点
        !this.hasWorkflowNode &&
        (
          // 评分规则
          this.activeTab === 'scoreRule' ||
          // 没有启用评分规则 邀请供应商提交审批
          (this.activeTab === 'inviteVendor' && this.enabledNodeMenu.indexOf('scoreRule') === -1) ||
          // 没有启用评分规则和邀请供应商 项目需求提交审批
          (this.activeTab === 'requireInfo' && this.enabledNodeMenu.every(item => !['inviteVendor', 'scoreRule'].includes(item)))
        )
    },

    // 报价类型
    pricingType () {
      return {
        // 普通报价
        isSimplePricing: this.biddingBaseInfo.orderType === SOU_ORDER_TYPE_ENUM.SIMPLE,
        // 公式报价
        isFormulaPricing: this.biddingBaseInfo.orderType === SOU_ORDER_TYPE_ENUM.FORMULA,
        // 模型报价
        isModelPricing: this.biddingBaseInfo.orderType === SOU_ORDER_TYPE_ENUM.MODEL,
        // 料费分离-模板报价
        isSeparation: this.biddingBaseInfo.orderType === SOU_ORDER_TYPE_ENUM.MATERIAL_COST_SEPARATION
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
    isProjectReadOnly () {
      return this.isViewOrApproval || this.projectStatus !== SOU_PROJECT_STATUS_ENUM.DRAFT
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
    }
  },

  methods: {
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

    /* 查询单据信息 */
    async getFormDetail (type) {
      const response = await bidBuyerHttp.init.getProjectInfo(this.biddingBaseInfo.projectId)

      if (!response || !response.data) {
        return
      }

      const {
        groupList = [],
        currencyList = [],
        souFileList = [],
        fileConfigList = [],
        processNodeList = [],
        ...project
      } = response.data

      if (!project || isEmpty(project)) {
        return
      }

      const {
        projectStatus,
        createApprovalStatus,
        standardCurrency
      } = project

      // 基础信息
      this.biddingBaseInfo = JSON.parse(JSON.stringify(project))
      this.$nextTick(() => {
        // 清除表单校验提示 默认不需要
        if (this.$refs.projectInfo) {
          this.$refs.projectInfo.clearFormValidate()
        }
      })

      // 状态
      this.projectStatus = projectStatus
      // 审批状态
      this.createApprovalStatus = createApprovalStatus

      // 额外信息
      this.projectInfoData = {
        // 内外部附件信息
        fileList: {
          innerFiles: souFileList.filter(item => item.fileType === 'INNER'),
          outerFiles: souFileList.filter(item => item.fileType === 'OUTER')
        },
        // 模板参考
        fileConfigList,
        // 工作小组
        groupList,
        // 可用外币列表 过滤掉本币
        currencyList: currencyList.filter(item => item.currencyCode !== standardCurrency)
      }

      this.setCurrentTabInit()
      if (type === 'init' && this.biddingBaseInfo.processConfigId) {
        // 查询当前单据招标流程配置
        await this.getProcessNode(processNodeList)
      }
    },

    /* 手动跳转页签处理，在查询详情之后 */
    setCurrentTabInit () {
      // 商务标管理跳转
      if (
        // 状态： 商务评标
        this.projectStatus === SOU_PROJECT_STATUS_ENUM.BUSINESS_EVAL &&
        // 未解密报价
        this.biddingBaseInfo.decryptFlag !== 'Y' &&
        // 单据创建者不是当前用户
        this.biddingBaseInfo.createdId !== this.userId &&
        // 当前用户在用户小组里，并且存在解密权限
        (this.projectInfoData.groupList || []).find(item => item.userId === this.userId && item.canDecrypt === 'Y')
      ) {
        // 判断 商务评标，报价未解密，当前用户拥有解密权限且不是单据创建人
        this.activeTab = 'businessManagement'
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

      // 判断单据状态，拟定状态不能点后面环节

      this.activeTab = data.key
      await this.$nextTick()
      if (data.key === 'bondManagement') {
        // 保证金管理
        await this.$refs.bondManagement.getBondsData()
      }
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
        case 'inviteVendor':
          // 供应商信息
          this.$refs.inviteVendor.saveInviteSuppliers(type)
          break
        case 'scoreRule':
          // 评分规则
          this.$refs.scoreRule.saveScoreRule(type)
          break
      }
    },

    /* 上一步 */
    prevOne () {
      switch (this.activeTab) {
        case 'requireInfo':
          // 项目信息
          this.activeTab = 'projectInfo'
          break
        case 'inviteVendor':
          // 邀请供应商
          this.activeTab = 'requireInfo'
          this.$refs.requireInfo.getProjectRequirementsData()
          break
        case 'scoreRule':
          // 评分规则
          if (this.enabledNodeMenu.includes('inviteVendor')) {
            this.activeTab = 'inviteVendor'
            this.$refs.inviteVendor.getInviteSupplier()
          } else {
            this.activeTab = 'requireInfo'
            this.$refs.requireInfo.getProjectRequirementsData()
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
