<template>
  <el-container class="flex-container-aside-biding the_biddingProjectApproval_wrapper">
    <!--左侧菜单区域-->
    <bidding-project-detail-menu
      ref="biddingProjectDetailMenu"
      :default-checked-keys="editableTabsValue"
      :bid-process-config-id-list="bidProcessConfigIdList"
      :page-flag="pageFlag"
      @menuClick="menuClick"
      @setEnabledNodeAndFlagList="setEnabledNodeAndFlagList"
    />

    <!-- 右边-条目区域 -->
    <el-container
      class="flex-container flex-container-right"
      direction="vertical"
    >
      <!-- 头部信息 -->
      <bidding-project-detail-header
        v-if="bidingBaseInfo.bidingNum"
        :biding-base="bidingBaseInfo"
      />

      <!--主内容区-->
      <el-main style="padding-right: 3px">
        <el-tabs
          v-model="editableTabsValue"
          style="height: 100%"
        >
          <!-- 项目信息 -->
          <el-tab-pane
            :label="$t('bidMod.projectInformation')"
            name="t11"
          >
            <project-information
              ref="projectInformation"
              :read-only="isProjectReadOnly"
              :scope-biding-id="scopeBidingId"
              :biding-base.sync="bidingBaseInfo"
              :bid-process-config-id-list="bidProcessConfigIdList"
              :project-information-data="projectInformationData"
              :enabled-node-menu="enabledNodeMenu"
              :bond-config-visible="bondConfigVisible"
              :page-flag="pageFlag"
              @fetchBaseInfo="fetchBaseInfoData"
              @setMenuNodeConfig="setMenuNodeConfig"
              @saveNextTodo="saveNextTodoBefore"
              @updateProcessNode="getProcessNode"
            />
          </el-tab-pane>

          <!-- 项目需求 -->
          <el-tab-pane
            :label="$t('bidMod.projectRequirement')"
            name="t12"
            lazy
          >
            <project-requirements
              ref="projectRequirements"
              :readonly="isProjectReadOnly"
              :scope-biding-id="scopeBidingId"
              :biding-base.sync="bidingBaseInfo"
              :is-active-menu="editableTabsValue === 't12'"
              :pricing-type="pricingType"
              @fetchBaseInfo="fetchBaseInfoData"
              @saveNextTodo="saveNextTodoBefore"
              @updateProcessNode="getProcessNode"
            />
          </el-tab-pane>

          <!-- 邀请供应商 -->
          <el-tab-pane
            v-if="enabledNodeMenu.includes('t13')"
            :label="$t('bidMod.inviteVendor')"
            name="t13"
            lazy
          >
            <invite-suppliers
              ref="inviteSuppliers"
              :read-only="isProjectReadOnly"
              :scope-biding-id="scopeBidingId"
              :active-menu="editableTabsValue"
              :biding-base="bidingBaseInfo"
              @fetchBaseInfo="fetchBaseInfoData"
              @saveNextTodo="saveNextTodoBefore"
              @updateProcessNode="getProcessNode"
            />
          </el-tab-pane>

          <!-- 评分规则 -->
          <el-tab-pane
            v-if="enabledNodeMenu.includes('t14')"
            :label="$t('bidMod.inquiryRule')"
            name="t14"
            lazy
          >
            <judging-rules
              ref="judgingRules"
              :read-only="isProjectReadOnly"
              :scope-biding-id="scopeBidingId"
              :biding-base="bidingBaseInfo"
              :active-menu="editableTabsValue"
              @fetchBaseInfo="fetchBaseInfoData"
              @saveNextTodo="saveNextTodoBefore"
              @updateProcessNode="getProcessNode"
            />
          </el-tab-pane>

          <!-- 流程审批 -->
          <el-tab-pane
            v-if="hasWorkflowNode"
            :label="$t('bidMod.processApproval')"
            name="t15"
            lazy
          >
            <orion-workflow-tab
              :scope-id="scopeBidingId"
              :is-current-active-tab="editableTabsValue === 't15'"
              :params="{ activeWorkflowTab: true }"
              :scope-prepare-status="workflowEntity.scopePrepareStatus"
              :workflow-model-id="workflowEntity.modelId"
              @afterProcessActionSuccess="afterProcessActionSuccess"
            />
          </el-tab-pane>

          <!-- 保证金管理 -->
          <el-tab-pane
            v-if="enabledNodeMenu.includes('t2')"
            :label="$t('bidMod.entryManagement')"
            name="t2"
            lazy
          >
            <orion-bond-management
              ref="bondManagement"
              :business-type="BUSINESS_TYPE_ENUM.BIDING"
              :page-flag="pageFlag"
              :base-info="{ id: scopeBidingId, idKey: 'bidingId' }"
              :is-active-menu="editableTabsValue === 't2'"
              @confirmBondsSuccess="getProcessNode"
            />
          </el-tab-pane>

          <!-- 报名管理 -->
          <el-tab-pane
            v-if="enabledNodeMenu.includes('t4')"
            :label="$t('bidMod.entryManagement')"
            name="t4"
            lazy
          >
            <apply-manage
              ref="applyManage"
              :page-flag="pageFlag"
              :scope-biding-id="scopeBidingId"
              :biding-status="bidingStatus"
              :biding-base="bidingBaseInfo"
              @fetchBaseInfo="fetchBaseInfoData"
              @updateProcessNode="getProcessNode"
            />
          </el-tab-pane>

          <!-- 投标控制 -->
          <el-tab-pane
            v-if="enabledNodeMenu.includes('t5')"
            :label="$t('bidMod.bidingControl')"
            name="t5"
            lazy
          >
            <bidding-control
              ref="bidControl"
              :scope-biding-id="scopeBidingId"
              :biding-base="bidingBaseInfo"
              :biding-status="bidingStatus"
              :audit-status="auditStatus"
              :active-menu="editableTabsValue"
              @fetchBaseInfo="fetchBaseInfoData"
              @updateProcessNode="getProcessNode"
            />
          </el-tab-pane>

          <!-- 技术标管理 -->
          <el-tab-pane
            v-if="enabledNodeMenu.includes('t62')"
            :label="$t('bidMod.technicalManagement')"
            name="t62"
            lazy
          >
            <technical-standard-ctrl
              ref="technicalStandardCtrl"
              :scope-biding-id="scopeBidingId"
              :biding-status="bidingStatus"
              :audit-status="auditStatus"
              :biding-base="bidingBaseInfo"
              @fetchBaseInfo="fetchBaseInfoData"
              @fetchParentNodeData="getProcessNode"
            />
          </el-tab-pane>

          <!-- 商务标管理 -->
          <el-tab-pane
            v-if="enabledNodeMenu.includes('t63')"
            :label="$t('bidMod.commercialManagement')"
            name="t63"
            lazy
          >
            <business-standard-ctrl
              ref="businessStandardCtrl"
              :is-view-or-approval="isViewOrApproval"
              :scope-biding-id="scopeBidingId"
              :biding-status="bidingStatus"
              :audit-status="auditStatus"
              :biding-base="bidingBaseInfo"
              :project-information-data="projectInformationData"
              :is-active-menu="editableTabsValue === 't63'"
              @fetchBaseInfo="fetchBaseInfoData"
              @fetchParentNodeData="getProcessNode"
            />
          </el-tab-pane>

          <!-- 评选 -->
          <el-tab-pane
            :label="$t('bidMod.bidEvaluation')"
            name="t64"
            lazy
          >
            <tender-selection
              ref="tenderSelection"
              :scope-biding-id="scopeBidingId"
              :biding-status="bidingStatus"
              :audit-status="auditStatus"
              :biding-base="bidingBaseInfo"
              :pricing-type="pricingType"
              :is-active-menu="editableTabsValue === 't64'"
              @tab-add="tab => $emit('tab-add', tab)"
              @fetchBaseInfo="fetchBaseInfoData"
              @fetchParentNodeData="getProcessNode"
            />
          </el-tab-pane>
        </el-tabs>

        <!--底部操作按钮区域-->
        <c-toolbar v-if="!pageFlag.isApproval || editableTabsValue !== 't15'">
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
              style="margin-left: 10px;margin-right: 10px"
            >
              <p>流程审批配置[{{ workflowEntity.modelId }}]已关闭，是否直接提交审批通过</p>
              <div style="text-align: right; margin: 0">
                <el-button
                  @click="submitApprovalPopoverVisible = false"
                >
                  {{ $t('common.cancel') }}
                </el-button>

                <!--提交审批，调下一步-->
                <el-button
                  type="primary"
                  @click="nextOne"
                >
                  {{ $t('common.confirm') }}
                </el-button>
              </div>

              <el-button
                v-if="showFlowButton"
                slot="reference"
                type="primary"
              >
                {{ $t('bidMod.submitapprovlaFlowing') }}
              </el-button>
            </el-popover>

            <!-- 返回 -->
            <el-button @click="backTo">
              {{ $t('common.backTo') }}
            </el-button>
          </template>
        </c-toolbar>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { tabTodoMixin } from '@/utils/mixins'
import { getFlowByIdFromListPage, scopePrepareWorkflow } from 'lib@/composition/origin/composition'
import { PRICING_TYPE_MAGIC } from '@/library/composition/biddingManagement/utils'
import { STORE_COMMON_CACHE } from '@/config/store-config'
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import ApplyManage from './biddingProjectDetail/applyManage.vue'
import BiddingControl from './biddingProjectDetail/biddingControl.vue'
import BusinessStandardCtrl from './biddingProjectDetail/businessStandardCtrl'
import TechnicalStandardCtrl from './biddingProjectDetail/technicalStandardCtrl'
import TenderSelection from './biddingProjectDetail/tenderSelection'
import InviteSuppliers from './biddingProjectDetail/inviteSuppliers'
import JudgingRules from './biddingProjectDetail/judgingRules'
import ProjectInformation from './biddingProjectDetail/projectInformation'
import ProjectRequirements from './biddingProjectDetail/projectRequirements'
import BiddingProjectDetailHeader from './biddingProjectDetail/biddingProjectDetailHeader'
import BiddingProjectDetailMenu from './biddingProjectDetail/biddingProjectDetailMenu'
import CToolbar from 'lib@/components/c-toolbar'
import OrionWorkflowTab from 'lib@/composition/origin/workflowTab'
import OrionBondManagement from 'lib@/composition/origin/bondPay/bondManagement'

export default {
  name: 'BiddingProjectDetail',

  components: {
    ApplyManage,
    BiddingControl,
    BusinessStandardCtrl,
    TechnicalStandardCtrl,
    TenderSelection,
    InviteSuppliers,
    JudgingRules,
    ProjectInformation,
    ProjectRequirements,
    BiddingProjectDetailHeader,
    BiddingProjectDetailMenu,
    CToolbar,
    OrionWorkflowTab,
    OrionBondManagement
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      bidProcessConfigIdList: [],
      auditStatus: null,
      bidingStatus: 'DRAW_UP',
      editableTabsValue: 't11',
      scopeBidingId: '',
      submitApprovalPopoverVisible: false,
      bidingBaseInfo: {
        // 项目信息
        processConfigId: '',
        bidingNum: '',
        bidingName: '',
        bidingSite: '',
        bidingScope: '',
        budgetAmount: '',
        targetType: 'SERVICE',
        bidingType: '',
        evaluateMethod: '',
        bidingAwardWay: '',
        enrollEndDatetime: '',
        bidingStartDatetime: '',
        sourceFrom: '',
        orgId: '',
        orgCode: '',
        orgName: '',
        currentRound: '',
        isSyncToPriceLibrary: '',
        // 投标控制
        withdrawBiding: 'N',
        partPrice: 'Y',
        publicTargetPrice: 'N',
        publicCodeRanking: 'N',
        publicLowestPrice: 'N',
        publicTotalRank: 'N',
        needEncryptPrice: 'N',
        visibleTargetPrice: 'N',
        visibleRankResult: 'N',
        visibleFinalPrice: 'N',
        visibleTotalRanking: 'N',
        visibleWinVendor: 'N',
        // 邀请供应控制
        excludeBlackVendors: 'Y',
        excludeNoCurrentOrgVendors: 'N',
        excludeOrgQuitVendors: 'N',
        excludeOrgCategoryStatus: '',
        // 商务要求
        bondAmount: '',
        bondMethod: '',
        bondEndDatetime: '',
        taxInclusivePrice: 'N',
        bidingCurrency: '',
        decimalAccuracy: '',
        bankAccountNum: '',
        bankAccountName: '',
        bankBranchName: '',
        bondDesc: '',
        // 向供应商展示的联系方式
        bidEmail: '',
        bidMobilePhone: '',
        bidContactName: '',
        standardCurrency: 'CNY',
        pricePrecision: '4',
        exchangeRateType: '',
        showRateType: 'N',
        currencyChangeDate: new Date().getTime(),
        // 需求信息
        requireDesc: '',
        // 价格类型默认普通报价
        pricingType: PRICING_TYPE_MAGIC.SIMPLE_PRICING
      },
      projectInformationData: {
        // 内外部附件信息
        fileList: [],
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
        // 外部开关
        enable: undefined,
        // 业务状态
        scopePrepareStatus: false,
        // 绑定的模板id
        modelId: 'SOUBIDCREATE',
        // 是否已查询流程
        checkStatus: false
      },
      bondConfigVisible: false,
      BUSINESS_TYPE_ENUM
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
        this.auditStatus !== 'APPROVED' &&
        this.bidingStatus === 'DRAW_UP' &&
        // 项目信息、项目需求、邀请供应商、评分规则
        ['t11', 't12', 't13', 't14'].includes(this.editableTabsValue)
    },

    // 是否显示上一步按钮
    showPrevOneButton () {
      // 非查看 项目需求、邀请供应商、评分规则
      return !this.isProjectReadOnly &&
        ['t12', 't13', 't14'].includes(this.editableTabsValue)
    },

    // 是否显示下一步按钮
    showNextOneButton () {
      // 非查看 && 项目信息、邀请供应商
      return !this.isProjectReadOnly &&
        !this.showFlowButton &&
        ['t11', 't12', 't13', 't14'].includes(this.editableTabsValue)
    },

    // 是否启用了流程审批节点
    hasWorkflowNode () {
      return this.enabledNodeMenu.includes('t15')
    },

    // 是否显示提交审批按钮
    showFlowButton () {
      // 非查看
      return !this.isViewOrApproval &&
        // 拟定状态
        this.auditStatus !== 'APPROVED' &&
        this.bidingStatus === 'DRAW_UP' &&
        (
          // 未启用审批节点
          !this.hasWorkflowNode ||
          // 启用了审批流程 && 审批流程已关闭
          (this.hasWorkflowNode && !this.workflowEntity.enable)
        ) &&
        (
          // 评分规则
          this.editableTabsValue === 't14' ||
          // 没有启用评分规则 邀请供应商提交审批
          (this.editableTabsValue === 't13' && this.enabledNodeMenu.indexOf('t14') === -1) ||
          // 没有启用评分规则和邀请供应商 项目需求提交审批
          (this.editableTabsValue === 't12' && this.enabledNodeMenu.every(item => !['t13', 't14'].includes(item)))
        )
    },

    // 报价类型
    pricingType () {
      return {
        // 普通报价
        isSimplePricing: this.bidingBaseInfo.pricingType === PRICING_TYPE_MAGIC.SIMPLE_PRICING,
        // 公式报价
        isFormulPricing: this.bidingBaseInfo.pricingType === PRICING_TYPE_MAGIC.FORMULA_PRICING,
        // 模型报价
        isModelPricing: this.bidingBaseInfo.pricingType === PRICING_TYPE_MAGIC.MODEL_PRICING,
        // 模板报价
        isTemplatePricing: this.bidingBaseInfo.pricingType === PRICING_TYPE_MAGIC.TEMPLATE_PRICING
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
      return this.isViewOrApproval || this.bidingStatus !== 'DRAW_UP'
    }
  },

  async created () {
    const dictSet = []
    dictSet.push('REFERENCE_FILE_TYPE')
    await this.$store.dispatch(STORE_COMMON_CACHE.LIST_DICT_BATCH, {
      dictCodeList: dictSet
    })

    // 查询招标流程配置
    this.getBidProcessConfigIdList()

    if (!this.pageFlag.isAdd) {
      // 编辑 查看 审批
      this.scopeBidingId = this.$attrs.params.row.bidingId
      await this.getFormDetail('init')
    }
  },

  methods: {
    /* 字符串时间转时间戳 */
    getDateTime (val) {
      return val ? new Date(val).getTime() : null
    },

    /* 查询单据信息 */
    async getFormDetail (type) {
      const response = await this.$http({
        url: `/api-bid/bidInitiating/biding/getProjectInfo/${this.scopeBidingId}`,
        method: 'GET',
        loading: true
      })

      if (response && response.data) {
        // 基础信息
        const bidingData = response.data.biding
        if (bidingData) {
          // 更新一下bidingId，新建时可能没有
          this.scopeBidingId = bidingData.bidingId
          // 基础信息
          this.bidingBaseInfo = {
            ...bidingData,
            currencyChangeDate: this.getDateTime(bidingData.currencyChangeDate),
            bidingEndDatetime: this.getDateTime(bidingData.bidingEndDatetime),
            bidingStartDatetime: this.getDateTime(bidingData.bidingStartDatetime),
            enrollEndDatetime: this.getDateTime(bidingData.enrollEndDatetime),
            processConfigId: bidingData.processConfigId || this.bidingBaseInfo.processConfigId,
            standardCurrency: bidingData.standardCurrency || this.bidingBaseInfo.standardCurrency,
            pricePrecision: bidingData.pricePrecision || this.bidingBaseInfo.pricePrecision,
            bondAmount: bidingData.bondAmount || this.bidingBaseInfo.bondAmount
          }
          this.$nextTick(() => {
            // 清除表单校验提示 默认不需要
            this.$refs.projectInformation.clearFormValidate()
          })
          if (!this.bidingBaseInfo.pricingType) {
            // 其他来源的单据，没有报价类型，给默认普通报价
            this.bidingBaseInfo.pricingType = PRICING_TYPE_MAGIC.SIMPLE_PRICING
          }

          // 招标状态
          this.bidingStatus = bidingData.bidingStatus
          // 审批状态
          this.auditStatus = bidingData.auditStatus
        }

        // 额外信息
        this.projectInformationData = {
          // 内外部附件信息
          fileList: response.data.fileList || [],
          // 模板参考
          fileConfigList: response.data.fileConfigList || [],
          // 工作小组
          groupList: response.data.groupList || [],
          // 可用外币列表 过滤掉本币
          currencyList: (response.data.currencyList || []).filter(item => item.currencyCode !== bidingData.standardCurrency)
        }

        this.setCurrentTabInit()
        if (type === 'init' && this.bidingBaseInfo.processConfigId) {
          // 查询当前单据招标流程配置
          this.getProcessNode()
        }
      }
    },

    /* 手动跳转页签处理，在查询详情之后 */
    setCurrentTabInit () {
      // 商务标管理跳转
      if (
        this.bidingStatus === 'BUSINESS_EVALUATION' &&
        this.bidingBaseInfo.decryptFlag !== 'Y' &&
        this.bidingBaseInfo.createdId !== this.userId &&
        (this.projectInformationData.groupList || []).find(item => item.userId === this.userId && item.canDecrypt === 'Y')
      ) {
        // 判断 商务评标，报价未解密，当前用户拥有解密权限且不是单据创建人
        this.editableTabsValue = 't63'
      }
    },

    /* 查询招标流程配置 */
    getBidProcessConfigIdList () {
      this.$http({
        url: '/api-bid/bidProcessConfig/bidProcessConfig/listPage',
        method: 'POST',
        data: {
          pageNum: 1,
          pageSize: 1000,
          status: 'VALID'
        },
        loading: true
      }).then(data => {
        if (data && data.data) {
          this.bidProcessConfigIdList = data.data.list || []
        }
      })
    },

    /* 设置菜单节点 */
    setMenuNodeConfig (processId, type) {
      const process = this.bidProcessConfigIdList.filter(v => v.processConfigId === processId)
      if (process) {
        // 是否存在保证金节点
        this.bondConfigVisible = process[0].bondManagement === 'Y'
        this.$refs.biddingProjectDetailMenu.setMenuNodeConfig(processId, type)
      }
    },

    /* 设置启用菜单节点列表 */
    async setEnabledNodeAndFlagList ({ enabledList = [], flagList = [] }) {
      // 存在流程审批节点
      if (enabledList.includes('t15')) {
        // 判断流程审批节点之前已生效的节点是否都已完成
        this.workflowEntity.scopePrepareStatus = scopePrepareWorkflow(enabledList, flagList, 't15')
        if (!this.workflowEntity.checkStatus) {
          this.workflowEntity.checkStatus = true
          this.workflowEntity.enable = await getFlowByIdFromListPage(this.workflowEntity.modelId)
        }
      }

      this.enabledNodeMenu = enabledList
    },

    /* 查询单据头信息 */
    fetchBaseInfoData (bidId) {
      if (!this.scopeBidingId) {
        this.scopeBidingId = bidId
      }
      this.getFormDetail()
    },

    /* 项目左侧菜单点击 */
    async menuClick (data) {
      // 没有单据ID || 没有流程ID
      if (!this.scopeBidingId || !this.bidingBaseInfo.processConfigId) {
        this.$message.warning(this.$t('bidMod.msgInputProInfo'))
        return
      }

      this.editableTabsValue = data.id
      await this.$nextTick()
      switch (data.id) {
        case 't13':
          // 邀请供应商
          this.$refs.inviteSuppliers.getInviteSupplier()
          break
        case 't14':
          // 评分规则
          this.$refs.judgingRules.getScoreRule()
          break
        case 't15':
          // 立项流程审批
          break
        case 't2':
          // 保证金管理
          this.$refs.bondManagement.getBondsData()
          break
        case 't4':
          // 报名管理tab
          this.$refs.applyManage.getQueryData()
          break
        case 't5':
          // 投标控制tab
          this.$refs.bidControl.getBiddingControlDetailData()
          break
        case 't62':
          // 技术标管理 查询
          this.$refs.technicalStandardCtrl.getTechProgress()
          break
        case 't63':
          // 商务标管理
          this.$refs.businessStandardCtrl.getBusinessOrders()
          break
        case 't64':
          // 评选
          this.$refs.tenderSelection.getQueryData()
          break
      }
    },

    /* 暂存 基础信息 、需求信息 、供应商信息 */
    tempStorage (type = '') {
      switch (this.editableTabsValue) {
        case 't11':
          // 保存项目基础信息
          this.$refs.projectInformation.tempSaveProjectInfo(type)
          break
        case 't12':
          // 需求信息
          this.$refs.projectRequirements.saveRequirement(type)
          break
        case 't13':
          // 供应商信息
          this.$refs.inviteSuppliers.saveInviteSuppliers(type)
          break
        case 't14':
          // 评分规则
          this.$refs.judgingRules.saveJudgingRules(type)
          break
      }
    },

    /* 上一步 */
    prevOne () {
      switch (this.editableTabsValue) {
        case 't12':
          // 项目信息
          this.editableTabsValue = 't11'
          break
        case 't13':
          // 邀请供应商
          this.editableTabsValue = 't12'
          break
        case 't14':
          // 评分规则
          if (this.enabledNodeMenu.includes('t13')) {
            this.editableTabsValue = 't13'
            this.$refs.inviteSuppliers.getInviteSupplier()
          } else {
            this.editableTabsValue = 't12'
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

    /* 跳转下一步之前的操作 */
    async saveNextTodoBefore (bidingId) {
      // 节点是按顺序执行的
      // 传入当前节点，判断当前节点在已启用节点中的位置，按顺序执行下一个启用的节点
      // 写个方法判断每跳转下一步需要执行的操作
      const currentNodeIndex = this.enabledNodeMenu.indexOf(this.editableTabsValue)
      const nextNodeIndex = currentNodeIndex + 1
      // 必须存在下一个启用节点
      if ((currentNodeIndex || currentNodeIndex === 0) && nextNodeIndex < this.enabledNodeMenu.length) {
        // 拿到下一个启用节点
        const nextNode = this.enabledNodeMenu[nextNodeIndex]
        // 跳转下一个节点
        this.editableTabsValue = nextNode
        await this.$nextTick()
        this.saveNextTodoAfter(nextNode, bidingId)
      }
    },

    /* 跳转下一步之后操作 */
    saveNextTodoAfter (nodeId, bidingId) {
      if (!this.isProjectReadOnly) {
        // 更新节点信息
        this.getProcessNode()
      }
      switch (nodeId) {
        case 't13':
          // 邀请供应商
          this.$refs.inviteSuppliers.getInviteSupplier()
          break
        case 't14':
          // 评分规则
          this.$refs.judgingRules.getScoreRule()
          break
        case 't15':
          // 立项审批流程
          break
        case 't4':
          this.menuClick({ id: 't4' })
          // 报名管理
          break
        case 't5':
          // 投标控制
          this.menuClick({ id: 't5' })
          break
      }
    },

    /* 获取流程节点 包含状态节点更新 */
    getProcessNode () {
      if (!this.scopeBidingId) return

      this.$http({
        url: '/api-bid/bidProcessConfig/processNode/listByBidingId',
        method: 'GET',
        params: { bidingId: this.scopeBidingId },
        loading: true
      }).then(data => {
        if (!data.data) {
          return
        }

        // 是否存在保证金节点
        this.bondConfigVisible = !!data.data.find(item => item.nodeCode === 'bondManagement')

        if (this.$refs.biddingProjectDetailMenu) {
          this.$refs.biddingProjectDetailMenu.updateTreeMenuData(data.data)
        }
      })
    },

    /* 立项审批完成 */
    afterProcessActionSuccess () {
      // 更新基础信息
      this.fetchBaseInfoData()
      // 更新流程节点信息
      this.getProcessNode()
    },

    /* 返回 */
    backTo () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('BiddingProjectList.getQueryData')
    }
  }
}
</script>

<style lang="scss" src="./biddingProjectDetail/biddingProjectDetail.scss"></style>
<style>
#app .hideSidebar .bidMentLeftSlide .el-submenu > .el-submenu__title {
  padding-left: 15px !important;
}
.paddingBt {
  padding-bottom: 5px;
}
.topComment {
  margin-top: 15px;
  float: right;
}
</style>
