<template>
  <el-container class="flex-container-aside-comp competition-detail-wrapper">
    <!--左侧菜单区域-->
    <DetailMenu
      v-if="!approvalFlag"
      ref="detailMenu"
      :default-checked-keys="activeTab"
      :process-list="processList"
      :page-flag="pageFlag"
      :menu-data="menuDataByStatus"
      :default-openeds="menuDefaultOpeneds"
      @menu-item-click="menuClick"
      @set-enabled-flag-list="setEnabledNodeAndFlagList"
    />

    <!-- 右边-条目区域 -->
    <el-container
      class="flex-container flex-container-right"
      direction="vertical"
    >
      <!--顶部信息栏-->
      <!-- <DetailHeader
        v-if="baseInfo.souNo"
        :bargain-base="baseInfo"
        project-status-dict-code="SOU_AUCT_PROJECT_STATUS"
      /> -->

      <!--主内容区-->
      <el-main style="padding-right: 3px">
        <ApprovalProcess
          :business-id="workflowBusinessId"
          :business-type="bpmBusinessType"
          :approval-status="approvalStatus"
          :status-map="statusMap"
          :show-button-config="showButtonConfig"
          :show-tab-config="showTabConfig"
          :readonly="$attrs.params.flag === 'view'"
          :operation-pre-options="operationPreOptions"
          @get-flow-node-list="getFlowNodeList"
          @approval-handler-callback="approvalHandlerCallback"
        >
          <el-tabs v-model="activeTab" class="container-right-tabs">
            <!-- 基础信息 -->
            <el-tab-pane :label="$t('vendorMod.companyBaseInfo2')" name="projectInfo">
              <ProjectInfo
                ref="projectInfo"
                :base-info.sync="baseInfo"
                :process-list="processList"
                :project-info-data="projectInformationData"
                :readonly="isProjectReadOnly"
                :page-flag="pageFlag"
                :enabled-node-menu="enabledNodeMenu"
                @set-menu-config="setMenuNodeConfig"
              />
            </el-tab-pane>

            <!--竞价需求-->
            <el-tab-pane
              label="竞价需求"
              name="requireInfo"
              lazy
            >
              <RequireInfo
                ref="projectRequirements"
                :base-info="baseInfo"
                :is-current-active-tab="activeTab === 'requireInfo'"
                :readonly="isProjectReadOnly"
              />
            </el-tab-pane>

            <!--邀请供应商-->
            <el-tab-pane
              :label="$t('bidMod.inviteSupplier')"
              name="inviteVendor"
              lazy
            >
              <InviteVendor
                ref="inviteSuppliers"
                :base-info="baseInfo"
                :is-current-active-tab="activeTab === 'inviteVendor'"
                :readonly="isProjectReadOnly"
              />
            </el-tab-pane>

            <!--报名管理-->
            <el-tab-pane
              v-if="enabledNodeMenu.includes('signUpManagement')"
              :label="$t('sourcingBuyer.signUpDetail')"
              name="signUpManagement"
              lazy
            >
              <SignUpManagement
                ref="signUpManagement"
                :base-info="baseInfo"
                :project-id="baseInfo.projectId"
                :ext-project-status="baseInfo.projectStatus"
                :is-active-tab="activeTab === 'signUpManagement'"
                @refresh="getFormDetail"
                @refresh-process="getProcessNode"
              />
            </el-tab-pane>

            <!--评选-->
            <el-tab-pane
              v-if="enabledNodeMenu.includes('evaluation')"
              :label="$t('bidMod.competitionLts.evaluation')"
              name="evaluation"
              lazy
            >
              <Evaluation
                ref="tenderSelection"
                :base-info="baseInfo"
                :vendor-info-data="vendorInfoData"
                :require-info-data="requireInfoData"
                :is-active-menu="activeTab === 'evaluation'"
                :is-page-view="pageFlag.isView"
                :approval-flag="approvalFlag"
                :able-select="ableSelect"
                @getData="controlFooterBtn"
                @refresh="getFormDetail"
                @setFooterBtn="setFooterBtn"
              />
            </el-tab-pane>
            <!--编制中标结果-->
            <el-tab-pane
              v-if="enabledNodeMenu.includes('editCalibrateResult')"
              name="editCalibrateResult"
              lazy
            >
              <EditCalibrateResult :calibrateResultList.sync="calibrateResultList" :editable="!['LOA', 'FILE'].includes(baseInfo.projectStatus)" />
            </el-tab-pane>
            <!-- 中标通知 -->
            <el-tab-pane
              v-if="enabledNodeMenu.includes('bidNotice')"
              name="bidNotice"
              lazy
            >
              <BidNotice
                ref="bidNotice"
                :bid-notice-list.sync="bidNoticeList"
              />
            </el-tab-pane>
            <!-- 归档 -->
            <el-tab-pane
              v-if="enabledNodeMenu.includes('archives')"
              :label="$t('cusEntry.competition.archives')"
              name="archives"
              lazy
            >
              <Archives
                ref="archives"
                :file-list.sync="archivesFileList"
                :base-info="baseInfo"
                :is-page-view="pageFlag.isView"
              />
            </el-tab-pane>
          </el-tabs>

          <!--底部操作按钮区域-->
          <div slot="custom">
            <template v-if="['projectInfo', 'requireInfo', 'inviteVendor'].includes(activeTab) && !pageFlag.isView">
              <!--暂存-->
              <el-button
                v-if="!isProjectReadOnly && activeTab !== 'requireInfo'"
                type="primary"
                @click="tempStorage"
              >
                {{ $t('common.staging') }}
              </el-button>

              <!--上一步-->
              <el-button
                v-show="activeTab !== 'projectInfo'"
                type="primary"
                @click="prevOne"
              >
                {{ $t('bidMod.prevOne') }}
              </el-button>

              <!--下一步-->
              <el-button
                v-if="!(activeTab === 'inviteVendor' && baseInfo.projectStatus === 'DRAFT')"
                type="primary"
                @click="nextOne"
              >
                {{ $t('bidMod.nextOne') }}
              </el-button>

              <!--提交-->
              <el-popover
                v-if="activeTab === 'inviteVendor' && !isProjectReadOnly"
                v-model="submitPopoverVisible"
                placement="top"
                width="200"
                style="margin: 0 10px;"
              >
                <div style="padding: 5px">
                  <p>{{ $t('bidMod.competitionLts.submitPopoverTips') }}</p>

                  <div style="text-align: right; margin: 0">
                    <el-button @click="submitPopoverVisible = false">
                      {{ $t('common.cancel') }}
                    </el-button>

                    <!--提交审批，调下一步-->
                    <el-button type="primary" @click="nextOne">
                      {{ $t('common.confirm') }}
                    </el-button>
                  </div>
                </div>
                <el-button slot="reference" type="primary">
                  {{ $t('common.submit') }}
                </el-button>
              </el-popover>
            </template>
            <!-- <template v-if="activeTab === 'evaluation' && !pageFlag.isView && evaluationTab === 'priceApproval'">
              <el-button
                v-if="showSelectBtn"
                type="primary"
                @click="evaluationHandle('SAVE')"
              >
                {{ $t('common.save') }}
              </el-button>
              <el-button
                v-if="showSelectBtn"
                type="primary"
                @click="evaluationHandle('SUBMIT')"
              >
                {{ $t('common.submit') }}
              </el-button>
            </template> -->
            <template v-if="activeTab === 'editCalibrateResult' && !pageFlag.isView && !['LOA', 'FILE'].includes(baseInfo.projectStatus)">
              <el-button type="primary" @click="editCalibrateResultHandle('SAVE')">
                {{ $t('common.save') }}
              </el-button>
              <el-button type="primary" @click="editCalibrateResultHandle('SUBMIT')">
                {{ $t('common.submit') }}
              </el-button>
            </template>
            <!-- <template v-if="!pageFlag.isView && activeTab === 'bidNotice' && showBidNoticeBtn">
              <el-button type="primary" @click="bidNoticeSubmit">
                {{ $t('common.submit') }}
              </el-button>
            </template> -->
            <template v-if="activeTab === 'archives' && !pageFlag.isView && baseInfo.projectStatus !== 'FILE'">
              <el-button type="primary" @click="archiveSubmit">
                {{ $t('common.submit') }}
              </el-button>
            </template>
            <el-button style="margin-right: 8px;" @click="backTab">
              {{ $t('bidMod.backTo') }}
            </el-button>
          </div>
        </ApprovalProcess>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { carBuyerHttp } from 'modcb@/competition/api'
import { comBuyerHttp } from 'modb@/souConfiguration/api'
import { menuDefaultOpeneds, WORKFLOW_MODEL_ID, SOU_AUCT_PROJECT_STATUS_ENUM } from 'lib@/composition/competition/utils'
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import ProjectInfo from './competitionManagementDetail/projectInfo.vue'
import RequireInfo from './competitionManagementDetail/requireInfo.vue'
import InviteVendor from './competitionManagementDetail/inviteVendor.vue'
import SignUpManagement from './competitionManagementDetail/signUpManagement.vue'
import BusinessManagement from './competitionManagementDetail/businessManagement.vue'
import CompetitionHall from './competitionManagementDetail/competitionHall'
import Evaluation from './competitionManagementDetail/evaluation.vue'
import CToolbar from 'lib@/components/c-toolbar'
import DetailHeader from 'lib@/composition/competition/detailHeader'
import DetailMenu from './detailMenu'
import OrionBondManagement from 'lib@/composition/origin/bondPayMQL/bondManagementNew'
import OrionWorkflowTab from 'lib@/composition/origin/workflowTab'
import { transformMQL } from 'lib@/utils/util'
import { tabTodoMixin } from '@/utils/mixins'
import { menuData } from './menuData'
import Archives from './competitionManagementDetail/archives'
import EditCalibrateResult from './competitionManagementDetail/editCalibrateResult'
import BidNotice from './competitionManagementDetail/bidNotice'
import ApprovalProcess from 'modc@/components/approval-process'

export default {
  name: 'CompetitionManagementDetail',

  components: {
    DetailMenu,
    DetailHeader,
    ProjectInfo,
    RequireInfo,
    InviteVendor,
    SignUpManagement,
    BusinessManagement,
    CompetitionHall,
    Evaluation,
    CToolbar,
    OrionBondManagement,
    OrionWorkflowTab,
    Archives,
    EditCalibrateResult,
    BidNotice,
    ApprovalProcess
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      ableSelect: false,
      showSelectBtn: true,
      evaluationReadOnly: false,
      showBidNoticeBtn: true,
      archivesFileList: [],
      calibrateResultList: [],
      bidNoticeList: [],
      evaluationTab: 'historyQuote',
      createApprovalStatus: null,
      baseInfo: {
        projectStatus: 'DRAFT',
        projectId: '',
        // 基本信息
        processConfigId: '',
        processInit: false,
        souNo: '',
        souName: '',
        signUpEndTime: '',
        orderStartTime: '',
        orderEndTime: '',
        scoreRuleType: '',
        publishScope: '', // 竞价范围
        standardCurrency: '',
        priceTax: '', // 对人民币汇率
        remark: '',
        sourceFromType: 'HAND_MAKE', // 来源类型
        auctSouProject: {
          // 规则
          orderNum: '',
          quoteCap: '',
          souRules: 'FORWARD_RULE',
          noAllowSamePriceCount: '', // 前几名不允许相同价格
          publicRules: 'HIDDEN_HIGHEST_PRICR_AND_RANK',
          minPercent: '',
          minAmount: '',
          allowExtendTime: 'N',
          extendTrigger: '',
          extendMinute: '',
          extendMaxOrderCount: '',
          extendTriggerCount: '',
          extendMaxMinute: '',
          // 商务信息部分-保证金
          bondAmount: '0',
          bondMethod: 'WIRE_TRANSFER',
          bondEndTime: '',
          bankAccountNum: '',
          bankAccountName: '',
          bankBranchName: '',
          bondDesc: '',
          //  智能推荐供应商
          excludeBlackVendors: 'Y',
          excludeOrgQuitVendors: 'N',
          excludeNoCurrentOrgVendors: 'N',
          excludeOrgCategoryStatus: ''
        },

        // 联系人
        linkman: '',
        email: '',
        tel: ''

      },
      activeTab: 'projectInfo',
      enabledNodeMenu: [
        'projectInfo',
        'requireInfo',
        'inviteVendor',
        'signUpManagement',
        'evaluation',
        'editCalibrateResult',
        'bidNotice',
        'archives'
      ],
      processConfigId: '',
      projectInformationData: {
        // 内外部附件信息
        fileList: [],
        // 模板参考
        fileConfigList: []
      },
      // 物料需求数据
      requireInfoData: [],
      // 邀请供应商数据
      vendorInfoData: [],
      submitPopoverVisible: false,
      processList: [],
      menuData,
      menuDefaultOpeneds,
      BUSINESS_TYPE_ENUM,
      // 审批流实体
      workflowEntity: {
        // 业务状态
        scopePrepareStatus: false,
        // 绑定的模板id
        modelId: WORKFLOW_MODEL_ID
      },
      approvalStatus: 'DRAFT', // 审批流程状态
      bpmBusinessType: '', // 审批流程编码
      operationPreOptions: {
        pass: this.prePassHandler,
        nextStep: this.preNextStepHandler
      },
      statusMap: { // 中标通知对应审批流状态
        DRAFT: 'DRAFT', // 拟定
        SUBMITTED: 'SUBMITTED', // 已提交
        APPROVED: 'APPROVED', // 审批通过
        REJECTED: 'REJECTED', // 已驳回
        WITHDRAW: 'WITHDRAW', // 已撤回
        ABANDONED: 'ABANDONED' // 已废弃
      },
      canSelectBidVendor: ['副总裁', '董事长']
    }
  },

  computed: {
    showTabConfig () { // 审批页面展示控制
      return {
        approval: ['PRICING', 'LOA'].includes(this.baseInfo.projectStatus) &&
          ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(this.approvalStatus) &&
          ((this.activeTab === 'evaluation' && this.evaluationTab === 'priceApproval') || this.activeTab === 'bidNotice')
      }
    },
    showButtonConfig () { // 审批流按钮展示
      let saveAndNextStepFlag = !this.pageFlag.isView && ['DRAFT', 'REJECTED', 'WITHDRAW'].includes(this.approvalStatus) &&
        ((this.activeTab === 'evaluation' && this.evaluationTab === 'priceApproval' && this.baseInfo.projectStatus === 'PRICING') || (this.activeTab === 'bidNotice' && this.baseInfo.projectStatus === 'LOA'))
      return {
        saveAndNextStep: saveAndNextStepFlag
      }
    },
    workflowBusinessId () { // 工作流单据ID
      return this.baseInfo ? this.baseInfo.projectId : null
    },
    approvalFlag () { // 审批流页面字段展示标识
      return this.$attrs.params.approvalFlag || false
    },
    pageFlag () {
      // 新增、编辑、只读
      // flag: ['add', 'edit', 'view']
      const flag = this.$attrs.params.flag
      return {
        isAdd: flag === 'add',
        isEdit: flag === 'edit',
        isView: flag === 'view'
      }
    },

    // 基础详细信息页只读
    isProjectReadOnly () {
      return this.pageFlag.isView || (this.baseInfo.projectStatus && this.baseInfo.projectStatus !== SOU_AUCT_PROJECT_STATUS_ENUM.DRAFT)
    },

    // 是否启用了流程审批节点
    hasWorkflowNode () {
      return this.enabledNodeMenu.includes('createApproval')
    },
    menuDataByStatus () {
      return ['ORDER_END', 'PRICING', 'PRICE_END', 'PRICE_REJECT', 'LOA', 'FILE'].includes(this.baseInfo.projectStatus) ? this.menuData : this.menuData.filter(item => !['competitiveTender', 'calibrate'].includes(item.id))
    }
  },

  created () {
    if (!this.pageFlag.isAdd) {
      // 编辑 查看 审批
      this.baseInfo.projectId = this.$attrs.params.row.projectId
      this.getFormDetail('init')
    }
  },

  methods: {
    // 获取审批流程节点
    getFlowNodeList (flowNodeList = []) {
      const obj = flowNodeList.find(item => item.taskStatus === '1') // 当前节点 taskStatus = '1'
      if (this.canSelectBidVendor.includes(obj.activityName) && obj.executorId === this.$store.getters.userInfo.username) {
        this.ableSelect = true
      } else {
        this.ableSelect = false
      }
    },
    // 通过前置处理
    async prePassHandler () {
      if (this.bpmBusinessType === 'BIDDING_RESULT') {
        const {
          priceApprovalForm,
          priceApprovalList
        } = this.$refs.tenderSelection
        const data = {
          projectId: this.baseInfo.projectId,
          selects: priceApprovalList,
          selectFileList: [priceApprovalForm],
          isTempSave: true
        }
        const valid = priceApprovalList.some(item => item.failureBidFlag === 'N' && (!item.winVendorId || !item.winReason))
        if (valid && this.ableSelect) {
          this.$message.warning('中标供应商与中标原因必填')
          return false
        }
        // 保存数据
        await carBuyerHttp.select.priceApprovalSubmit(data)
      }
      return true
    },
    // 下一步前置处理
    async preNextStepHandler () {
      if (this.bpmBusinessType === 'BIDDING_RESULT') {
        const {
          priceApprovalForm,
          priceApprovalList
        } = this.$refs.tenderSelection
        const data = {
          projectId: this.baseInfo.projectId,
          selects: priceApprovalList,
          selectFileList: [priceApprovalForm],
          isTempSave: true
        }
        // 保存数据
        await carBuyerHttp.select.priceApprovalSubmit(data)
      } else if (this.bpmBusinessType === 'BIDDING_SUCCESS') {
        const data = {
          projectId: this.baseInfo.projectId,
          selects: this.$refs.bidNotice.bidNoticeList
        }
        await carBuyerHttp.calibrate.bidNoticeSubmit(data)
        const res = await carBuyerHttp.calibrate.bidNotice({ projectId: this.baseInfo.projectId })
        this.bidNoticeList = res.data.list || []
      }
      return true
    },
    // 审批流操作回调
    approvalHandlerCallback (type) {
      switch (type) {
      case 'save':
        if (this.bpmBusinessType === 'BIDDING_RESULT') {
          this.evaluationHandle('SAVE')
        } else if (this.bpmBusinessType === 'BIDDING_SUCCESS') {
          return this.$message.warning('中标通知状态下无需点击保存')
        }
        break
      case 'submit':
        this.backTab()
        break
      default:
        break
      }
    },
    /* 流程撤回 */
    undo (bussinessType) {
      this.$prompt('', this.$t('cusEntry.competition.undoReason'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        inputType: 'textarea',
        inputValidator: (value) => {
          if (!value) {
            return this.$t('cusEntry.tipMessage.undoReason')
          }
          return true
        }
      }).then(({ value }) => {
        const data = {
          bussinessType,
          dataId: this.baseInfo.projectId,
          commentmsg: value
        }
        carBuyerHttp.process.undo(data).then(res => {
          if (res.data) {
            this.$message.warning(this.$t('cusEntry.tipMessage.recallSuccess'))
            if (bussinessType === 'BIDDING_RESULT') {
              // 重新查询数据
              this.$refs.tenderSelection.tabClick({ name: 'priceApproval' })
            } else {
              // 重新查询数据
              carBuyerHttp.calibrate.bidNotice({ projectId: this.baseInfo.projectId }).then(res => {
                if (res.data) {
                  this.bidNoticeList = res.data.list || []
                  this.showBidNoticeBtn = res.data.list?.[0].winNoticeStatus !== 'SUBMITTED'
                }
              })
            }
          }
        })
      })
    },
    /* 控制底下操作按钮 */
    controlFooterBtn (data) {
      this.approvalStatus = data?.[0]?.resultStatus
      this.bpmBusinessType = 'BIDDING_RESULT'
      this.showSelectBtn = data?.[0]?.resultStatus !== 'SUBMITTED'
    },
    /* 暂存/提交评选结果 */
    evaluationHandle (type) {
      const {
        priceApprovalForm,
        priceApprovalList
      } = this.$refs.tenderSelection
      // if (type === 'SUBMIT') {
      //   if (!priceApprovalForm?.selectDocId) {
      //     this.$message.warning(this.$t('cusEntry.tipMessage.selectFile'))
      //     return false
      //   }
      // }
      const data = {
        projectId: this.baseInfo.projectId,
        selects: priceApprovalList,
        selectFileList: [priceApprovalForm],
        isTempSave: type === 'SAVE'
      }
      carBuyerHttp.select.priceApprovalSubmit(data).then(async res => {
        if (type === 'SAVE') {
          this.$message.success(this.$t('common.successSave'))
        } else {
          this.$message.success(this.$t('common.successSubmit'))
          if (type === 'SUBMIT') {
            const submitEngineData = {
              businessId: this.baseInfo.projectId,
              businessType: 'BIDDING_RESULT'
            }
            await carBuyerHttp.calibrate.submitEngine(submitEngineData)
          }
        }
      })
    },
    /* 提交定价结果 */
    editCalibrateResultHandle (type) {
      const data = {
        projectId: this.baseInfo.projectId,
        selects: this.calibrateResultList,
        isTempSave: type === 'SAVE'
      }
      carBuyerHttp.calibrate.calibrateResultSubmit(data).then(res => {
        this.$message.success(this.$t('common.successSubmit'))
      })
    },
    /* 中标通知提交 */
    bidNoticeSubmit () {
      const data = {
        projectId: this.baseInfo.projectId,
        selects: this.$refs.bidNotice.bidNoticeList
      }
      carBuyerHttp.calibrate.bidNoticeSubmit(data).then(async res => {
        this.$message.success(this.$t('common.successSubmit'))
        const submitEngineData = {
          businessId: this.baseInfo.projectId,
          businessType: 'BIDDING_SUCCESS'
        }
        await carBuyerHttp.calibrate.submitEngine(submitEngineData)
        const params = {
          projectId: this.baseInfo.projectId
        }
        carBuyerHttp.calibrate.bidNotice(params).then(res => {
          if (res.data) {
            this.bidNoticeList = res.data.list || []
          }
        })
      })
    },
    /* 控制操作按钮 */
    setFooterBtn (activeName) {
      this.evaluationTab = activeName
    },
    /* 归档提交 */
    archiveSubmit () {
      const fileList = this.$refs.archives.getFileList()
      if (fileList.length === 0) {
        this.$message.warning(this.$t('cusEntry.tipMessage.atLeastOneLineOfFile'))
        return false
      }
      /* 校验附件必填 */
      let validFlag = true
      fileList.some(item => {
        if (!item.docId) {
          validFlag = false
          return true
        }
      })
      if (!validFlag) {
        this.$message.warning(this.$t('cusEntry.tipMessage.selectFile'))
        return false
      }
      const data = {
        projectId: this.baseInfo.projectId,
        placeOnFileList: fileList.map(item => ({ projectId: this.baseInfo.projectId, ...item }))
      }
      carBuyerHttp.calibrate.archiveSubmit(data).then(res => {
        this.$message.success(this.$t('common.successSubmit'))
      })
    },
    /* 查询单据详情 */
    async getFormDetail (type) {
      if (!this.baseInfo.projectId) {
        return
      }
      // let transformParams = transformMQL.save('AuctSouProjectForBuyer', [this.baseInfo.projectId], 'getInitInfo')
      const response = await carBuyerHttp.init.getInitInfo(this.baseInfo.projectId)
      if (response && response.data) {
        const {
          currencyList = [],
          fileConfigList = [],
          souFileList = [],
          compSouProject = {},
          processNodeList = [],
          groupList = [],
          vendorList,
          itemList,
          ...competition
        } = response.data
        /* 解构获取相应数据构造前端数据 */
        const {
          souRules,
          orderNum,
          quoteCap,
          publicRules
        } = competition
        const {
          bondAmount,
          bondMethod,
          bondEndTime,
          bankAccountNum,
          bankAccountName,
          bankBranchName,
          bondDesc
        } = compSouProject || {}
        const auctSouProject = { souRules, orderNum, quoteCap, publicRules, bondAmount, bondMethod, bondEndTime, bankAccountNum, bankAccountName, bankBranchName, bondDesc }
        // 基础信息
        this.baseInfo = Object.assign({}, this.baseInfo, competition)
        /* 竞价规则 */
        this.baseInfo.auctSouProject = Object.assign({}, this.baseInfo.auctSouProject, auctSouProject)
        this.vendorInfoData = vendorList
        this.requireInfoData = itemList
        // 项目列表信息
        this.projectInformationData = {
          fileList: souFileList.concat(),
          fileConfigList: fileConfigList.concat()
        }

        if (this.baseInfo.projectStatus === 'PRICING') {
          if (this.approvalFlag) {
            this.activeTab = 'evaluation'
            this.evaluationTab = 'priceApproval'
          }
        } else if (this.baseInfo.projectStatus === 'LOA') {
          const params = { projectId: this.baseInfo.projectId }
          carBuyerHttp.calibrate.bidNotice(params).then(res => {
            if (res.data) {
              this.bidNoticeList = res.data.list || []
              this.showBidNoticeBtn = res.data.list?.[0].winNoticeStatus !== 'SUBMITTED'
              this.approvalStatus = res.data.list?.[0].winNoticeStatus
              this.bpmBusinessType = 'BIDDING_SUCCESS'
            }
          })
          if (this.approvalFlag) {
            this.activeTab = 'bidNotice'
          }
        }

        this.$nextTick(() => {
          this.$refs.projectInfo.clearFormValidate()
        })

        // 流程 ID
        // this.processConfigId = competition.processConfigId
        // this.$nextTick(() => {
        //   if (type === 'init') {
        //     this.getProcessConfig(processNodeList)
        //   }
        // })
      }
    },
    /* 获取流程节点启用信息 */
    async getProcessConfig (processNodeList) {
      if (!processNodeList.length) return
      this.$refs.detailMenu.setMenuNodeConfig(processNodeList)
      this.$nextTick(() => {
        this.getProcessNode()
      })
    },

    /* 获取流程节点完成信息 */
    async getProcessNode () {
      if (!this.baseInfo.projectId) {
        return false
      }
      // let transformParams = transformMQL.save('AuctSouProjectForBuyer', [this.baseInfo.projectId], 'listProcessNodes')
      // const response = await carBuyerHttp.init.listProcessNodes(transformParams)
      // if (response.data?.records.length && this.$refs.detailMenu) {
      //   this.$refs.detailMenu.updateTreeMenuData(response.data.records)
      // }
    },

    /* 设置菜单节点 */
    setMenuNodeConfig (processId, type) {
      const process = this.processList.find(v => v.processConfigId === processId)
      if (process) {
        let needInitAttrs = ['projectInfo', 'requireInfo', 'inviteVendor', 'signUpManagement', 'evaluation', 'calibrate']
        const { processConfigId } = process
        let processList = []
        for (let key of needInitAttrs) {
          processList.push({
            enabled: process[key],
            nodeStatus: 'N',
            processConfigId,
            processNode: key,
            processNodeId: null
          })
        }
        this.$refs.detailMenu.setMenuNodeConfig(processList, type)
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
        this.saveNextTodo()
      }
    },

    /* 跳转下一步 */
    saveNextTodo () {
      // 节点是按顺序执行的
      // 传入当前节点，判断当前节点在已启用节点中的位置，按顺序执行下一个启用的节点
      // 写个方法判断每跳转下一步需要执行的操作
      const currentNodeIndex = this.enabledNodeMenu.indexOf(this.activeTab)
      const nextNodeIndex = currentNodeIndex + 1
      // 必须存在下一个启用节点
      if ((currentNodeIndex || currentNodeIndex === 0) && nextNodeIndex < this.enabledNodeMenu.length) {
        // 拿到下一个启用节点 跳转下一个节点
        this.activeTab = this.enabledNodeMenu[nextNodeIndex]
      }
    },

    /* 暂存 基础信息 、需求信息 、供应商信息 */
    async tempStorage (type = '') {
      let asyncData
      switch (this.activeTab) {
      case 'projectInfo':
        // 保存项目基础信息
        asyncData = await this.$refs.projectInfo.tempSaveProjectInfo(type)
        if (!asyncData || !asyncData.status) {
          return
        }

        // 更新ID
        this.baseInfo.projectId = asyncData.data
        break
      case 'requireInfo':
        // 需求信息
        // asyncData = await this.$refs.projectRequirements.saveRequirement(type)
        // if (!asyncData || !asyncData.status) {
        //   return
        // }
        break
      case 'inviteVendor':
        // 邀请供应商
        asyncData = await this.$refs.inviteSuppliers.saveInviteSuppliers(type)
        if (!asyncData || !asyncData.status) {
          return
        }
        break
      }

      // if (!this.isProjectReadOnly) {
      //   await this.getProcessNode()
      // }

      // 查询单据信息 需要更新项目状态
      await this.getFormDetail()

      if (type === 'nextOne') {
        // 跳转下一步页签
        this.saveNextTodo()
      }
    },

    /* 项目左侧菜单点击 */
    menuClick (data) {
      // 没有单据ID
      if (!this.baseInfo.projectId) {
        this.$message.warning(this.$t('bidMod.msgInputProInfo'))
        return
      }
      this.activeTab = data.id
      const params = {
        projectId: this.baseInfo.projectId
      }
      switch (data.id) {
      case 'editCalibrateResult':
        carBuyerHttp.select.getQuoteResult(params).then(res => {
          if (res.data) {
            this.calibrateResultList = res.data.list || []
          }
        })
        break
      case 'bidNotice':
        carBuyerHttp.calibrate.bidNotice(params).then(res => {
          if (res.data) {
            this.bidNoticeList = res.data.list || []
            this.showBidNoticeBtn = res.data.list?.[0].winNoticeStatus !== 'SUBMITTED'
            this.approvalStatus = res.data.list?.[0].winNoticeStatus
            this.bpmBusinessType = 'BIDDING_SUCCESS'
          }
        })
        break
      case 'archives':
        carBuyerHttp.calibrate.getArchiveList(params).then(res => {
          if (res.data) {
            this.archivesFileList = res.data.list || []
          }
        })
        break
      }
    },

    /* 接收已完成的节点 */
    setEnabledNodeAndFlagList ({ enabledList = [], flagList = [] }) {
      this.enabledNodeMenu = enabledList
    },

    /* 立项审批完成 */
    afterProcessActionSuccess () {
      // 更新基础信息
      this.getFormDetail()
      // 更新流程节点信息
      this.getProcessNode()
    },

    /* 返回 */
    backTab () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('CompetitionManagementList.getQueryData')
    }
  }
}
</script>

<style lang="scss" src="./competitionManagementDetail/competitionManagementDetail.scss" scoped></style>
