<template>
  <el-container class="flex-container-aside-comp competition-detail-wrapper">
    <!--左侧菜单区域-->
    <DetailMenu
      ref="detailMenu"
      :default-checked-keys="activeTab"
      :process-list="processList"
      :page-flag="pageFlag"
      :menu-data="menuData"
      :default-openeds="menuDefaultOpeneds"
      @menu-item-click="menuClick"
      @set-enabled-flag-list="setEnabledNodeAndFlagList"
    />

    <!-- 右边-条目区域 -->
    <el-container
      class="flex-container flex-container-right"
      direction="vertical"
      style="padding-bottom: 20px"
    >
      <!--顶部信息栏-->
      <DetailHeader
        v-if="baseInfo.souNo"
        :bargain-base="baseInfo"
        project-status-dict-code="SOU_AUCT_PROJECT_STATUS"
      />

      <!--主内容区-->
      <el-main style="padding-right: 3px">
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
            :label="$t('competition.requireInfo')"
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

          <!-- 流程审批 -->
          <el-tab-pane
            v-if="hasWorkflowNode"
            :label="$t('bidMod.processApproval')"
            name="createApproval"
            lazy
          >
            <OrionWorkflowTab
              :scope-id="baseInfo.projectId"
              :params="{ activeWorkflowTab: true }"
              :workflow-model-id="workflowEntity.modelId"
              :workflow-enable="workflowEntity.scopePrepareStatus"
              :show-toolbar="createApprovalStatus === SOU_APPROVAL_STATUS_ENUM.DRAFT"
              @workflow-success="afterProcessActionSuccess"
              @afterProcessActionSuccess="afterProcessActionSuccess"
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

          <!--保证金详情-->
          <el-tab-pane
            v-if="enabledNodeMenu.includes('bondManagement')"
            :label="$t('bidMod.competitionLts.bondManagement')"
            name="bondManagement"
            lazy
          >
            <OrionBondManagement
              ref="bondManagement"
              :business-type="BUSINESS_TYPE_ENUM.COMPETITION"
              :base-info="{ id: baseInfo.projectId, idKey: 'projectId' }"
              :is-active-menu="activeTab === 'bondManagement'"
            />
          </el-tab-pane>

          <!--商务详情-->
          <el-tab-pane
            v-if="enabledNodeMenu.includes('businessManagement')"
            :label="$t('bidMod.competitionLts.businessManagement')"
            name="businessManagement"
            lazy
          >
            <BusinessManagement
              ref="businessStandardCtrl"
              :project-id="baseInfo.projectId"
              :project-status="baseInfo.projectStatus"
              :is-active-tab="activeTab === 'businessManagement'"
            />
          </el-tab-pane>

          <!--竞价大厅-->
          <el-tab-pane
            v-if="enabledNodeMenu.includes('auctHall')"
            :label="$t('bidMod.hall')"
            name="auctHall"
            lazy
          >
            <CompetitionHall
              ref="competitionHall"
              :project-id="baseInfo.projectId"
              :base-info="baseInfo"
              :is-active-tab="activeTab === 'auctHall'"
              :require-info-data="requireInfoData"
              :vendor-info-data="vendorInfoData"
              @refresh="getFormDetail"
              @refresh-process="getProcessNode"
            />
          </el-tab-pane>

          <!--评选 --定点会签-->
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
              @refresh="getFormDetail"
            />
          </el-tab-pane>
        </el-tabs>

        <!--底部操作按钮区域-->
        <CToolbar>
          <template slot="right">
            <template v-if="['projectInfo', 'requireInfo', 'inviteVendor'].includes(activeTab) && !pageFlag.isView">
              <!--暂存-->
              <el-button
                v-if="!isProjectReadOnly"
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
                v-if="activeTab !== 'inviteVendor'"
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

            <el-button @click="backTab">
              {{ $t('bidMod.backTo') }}
            </el-button>
          </template>
        </CToolbar>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { carBuyerHttp } from 'modb@/competition/api'
import { comBuyerHttp } from 'modb@/souConfiguration/api'
import { menuData, menuDefaultOpeneds, WORKFLOW_MODEL_ID, SOU_AUCT_PROJECT_STATUS_ENUM } from 'lib@/composition/competition/utils'
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
import DetailMenu from 'lib@/composition/competition/detailMenu'
import OrionBondManagement from 'lib@/composition/origin/bondPayMQL/bondManagementNew'
import OrionWorkflowTab from 'lib@/composition/origin/workflowTab'
import { transformMQL } from 'lib@/utils/util'
import { tabTodoMixin } from '@/utils/mixins'

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
    OrionWorkflowTab
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      createApprovalStatus: null,
      baseInfo: {
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
          auctRule: '',
          noAllowSamePriceCount: '', // 前几名不允许相同价格
          scopeRule: '',
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
          bondMethod: '',
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
      enabledNodeMenu: [],
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
      }
    }
  },

  computed: {
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
    }
  },

  created () {
    if (!this.pageFlag.isAdd) {
      // 编辑 查看 审批
      this.baseInfo.projectId = this.$attrs.params.row.projectId
      this.getFormDetail('init')
    }

    // 查询竞价模板列表
    this.getProcessConfigList()
  },

  methods: {
    /* 查询单据详情 */
    async getFormDetail (type) {
      if (!this.baseInfo.projectId) {
        return
      }
      let transformParams = transformMQL.save('AuctSouProjectForBuyer', [this.baseInfo.projectId], 'getInitInfo')
      const response = await carBuyerHttp.init.getInitInfo(transformParams)
      if (response && response.data && response.data.records.length) {
        const {
          currencyList = [],
          fileConfigList = [],
          fileList = [],
          processNodeList = [],
          groupList = [],
          vendorList,
          itemList,
          ...competition
        } = response.data.records[0]

        // 基础信息
        this.baseInfo = Object.assign({}, this.baseInfo, competition)
        this.vendorInfoData = vendorList
        this.requireInfoData = itemList
        if (currencyList.length) {
          this.baseInfo.standardCurrency = currencyList[0].auctSouCurrency?.currencyCode
          this.baseInfo.priceTax = currencyList[0].auctSouCurrency?.priceTax
        }

        // 项目列表信息
        this.projectInformationData = {
          fileList: fileList.concat(),
          fileConfigList: fileConfigList.concat()
        }

         this.$nextTick(() => {
            this.$refs.projectInfo.clearFormValidate()
          })

        // 流程 ID
        this.processConfigId = competition.processConfigId
        this.$nextTick(() => {
          if (type === 'init') {
            this.getProcessConfig(processNodeList)
          }
        })
      }
    },
    /** 获取竞价模板列表 */
    async getProcessConfigList () {
      let transformParams = transformMQL.listPageData({
        type: 'SouProcessConfig',
        params: {
          processStatus: 'VALID',
          souType: 'auct'
        },
        filterOperator: {
          processStatus: 'eq',
          souType: 'eq'
        },
        query: {
          '*': {},
          'auctProcessConfig': {
            '*': {}
          }
        },
        action: 'query',
        pageNum: 1,
        pageSize: 10000
      })
      const response = await comBuyerHttp.process.page(transformParams)
      if (response && response.data) {
        const { records = [] } = response.data
        this.processList = records.map(item => ({
          ...item,
          auctHall: item.auctProcessConfig?.auctHall,
          bondManagement: item.auctProcessConfig?.bondManagement
        }))
      }
    },
    /* 获取流程节点启用信息 */
    async getProcessConfig (processNodeList) {
      console.log('processNodeList', processNodeList.map(item => ({ node: item.processNode, enabled: item.enabled })))
      if(!processNodeList.length) return
      this.$refs.detailMenu.setMenuNodeConfig(processNodeList)
      this.$nextTick(() => {
        this.getProcessNode()
      })
    },

    /* 获取流程节点完成信息 */
    async getProcessNode () {
      if (!this.baseInfo.projectId) {
        return
      }
      let transformParams = transformMQL.save('AuctSouProjectForBuyer', [this.baseInfo.projectId], 'listProcessNodes')
      const response = await carBuyerHttp.init.listProcessNodes(transformParams)
      if (response.data?.records.length && this.$refs.detailMenu) {
        this.$refs.detailMenu.updateTreeMenuData(response.data.records)
      }
    },

    /* 设置菜单节点 */
    setMenuNodeConfig (processId, type) {
      const process = this.processList.find(v => v.processConfigId === processId)
      if (process) {
        let needInitAttrs = ['projectInfo', 'requireInfo', 'inviteVendor', 'createApproval', 'bondManagement', 'signUpManagement', 'businessManagement', 'auctHall', 'evaluation']
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
        console.log('processList', processList)
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
      console.log('enabledNodeMenu', this.enabledNodeMenu)
      const currentNodeIndex = this.enabledNodeMenu.indexOf(this.activeTab)
      const nextNodeIndex = currentNodeIndex + 1
      console.log('nextNodeIndex', nextNodeIndex)
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
        this.baseInfo.projectId = asyncData.data?.projectId
        break
      case 'requireInfo':
        // 需求信息
        asyncData = await this.$refs.projectRequirements.saveRequirement(type)
        if (!asyncData || !asyncData.status) {
          return
        }
        break
      case 'inviteVendor':
        // 邀请供应商
        asyncData = await this.$refs.inviteSuppliers.saveInviteSuppliers(type)
        if (!asyncData || !asyncData.status) {
          return
        }
        break
      }

      if (!this.isProjectReadOnly) {
        // 更新节点信息
        await this.getProcessNode()
      }

      // 查询单据信息 需要更新项目状态
      await this.getFormDetail()

      if (type === 'nextOne') {
        // 跳转下一步页签
        this.saveNextTodo()
      }
    },

    /* 项目左侧菜单点击 */
    menuClick (data) {
      // 没有单据ID || 没有流程ID
      if (!this.baseInfo.projectId || !this.processConfigId) {
        this.$message.warning(this.$t('bidMod.msgInputProInfo'))
        return
      }
      console.log('menuItem:::', data)
      this.activeTab = data.id
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
