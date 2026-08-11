<template>
  <el-container class="flex-container-aside-comp competition-management-wrapper">
    <!--左侧菜单区域-->
    <DetailMenu
      ref="detailMenu"
      :page-flag="pageFlag"
      :default-checked-keys="activeTab"
      @menuClick="menuClick"
      @setEnabledNodeAndFlagList="setEnabledNodeAndFlagList"
    />

    <!-- 右边-条目区域 -->
    <el-container class="flex-container flex-container-right" direction="vertical">
      <!--顶部信息栏-->
      <DetailHeader :base-info="baseInfo" />

      <!--主内容区-->
      <el-main style="padding-right: 3px">
        <el-tabs v-model="activeTab" class="container-right-tabs">
          <!-- 项目信息 -->
          <el-tab-pane :label="$t('bidMod.projectInformation')" name="t1-1">
            <ProjectInformation
              ref="projectInformation"
              :base-info.sync="baseInfo"
              :project-information-data="projectInformationData"
              :readonly="isProjectReadOnly"
            />
          </el-tab-pane>

          <!--项目需求-->
          <el-tab-pane
            :label="$t('bidMod.projectRequire')"
            name="t1-2"
            lazy
          >
            <ProjectRequirements
              ref="projectRequirements"
              :base-info="baseInfo"
              :currency-list="projectInformationData.currencyList"
              :is-current-active-tab="activeTab === 't1-2'"
              :readonly="isProjectReadOnly"
            />
          </el-tab-pane>

          <!--邀请供应商-->
          <el-tab-pane
            :label="$t('bidMod.inviteSupplier')"
            name="t1-3"
            lazy
          >
            <InviteSuppliers
              ref="inviteSuppliers"
              :base-info="baseInfo"
              :is-current-active-tab="activeTab === 't1-3'"
              :readonly="isProjectReadOnly"
            />
          </el-tab-pane>

          <!--报名管理-->
          <el-tab-pane
            :label="$t('bidMod.entryManagement')"
            name="t2"
            lazy
          >
            <ApplyManage
              ref="applyManage"
              :project-id="projectId"
              :project-status="baseInfo.projectStatus"
              :is-current-active-tab="activeTab === 't2'"
              @fetchBaseInfo="getFormDetail"
              @updateProcessNode="getProcessNode"
            />
          </el-tab-pane>

          <!--报价控制-->
          <el-tab-pane
            :label="$t('bidMod.priceControl')"
            name="t3"
            lazy
          >
            <PriceControl
              ref="priceControl"
              :project-id="projectId"
              :project-status="baseInfo.projectStatus"
              :is-current-active-tab="activeTab === 't3'"
              @fetchBaseInfo="getFormDetail"
              @updateProcessNode="getProcessNode"
            />
          </el-tab-pane>

          <!--商务标管理-->
          <el-tab-pane
            :label="$t('bidMod.businessManagement')"
            name="t4-1"
            lazy
          >
            <BusinessStandardCtrl
              ref="businessStandardCtrl"
              :project-id="projectId"
              :project-status="baseInfo.projectStatus"
              :is-current-active-tab="activeTab === 't4-1'"
            />
          </el-tab-pane>

          <!--竞价大厅-->
          <el-tab-pane
            :label="$t('bidMod.hall')"
            name="t4-2"
            lazy
          >
            <CompetitionHall
              ref="competitionHall"
              :base-info="baseInfo"
              :is-current-active-tab="activeTab === 't4-2'"
            />
          </el-tab-pane>

          <!--评选-->
          <el-tab-pane
            :label="$t('bidMod.quoteEvaluation')"
            name="t4-3"
            lazy
          >
            <TenderSelection
              ref="tenderSelection"
              :project-id="projectId"
              :base-info="baseInfo"
              :is-current-active-tab="activeTab === 't4-3'"
              @fetchBaseInfo="getFormDetail"
            />
          </el-tab-pane>
        </el-tabs>

        <!--底部操作按钮区域-->
        <CToolbar>
          <template #right>
            <template v-if="['t1-1', 't1-2', 't1-3'].includes(activeTab) && !pageFlag.isView">
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
                v-show="activeTab !== 't1-1'"
                type="primary"
                @click="prevOne"
              >
                {{ $t('bidMod.prevOne') }}
              </el-button>

              <!--下一步-->
              <el-button
                v-if="activeTab !== 't1-3'"
                type="primary"
                @click="nextOne"
              >
                {{ $t('bidMod.nextOne') }}
              </el-button>

              <!--提交-->
              <el-popover
                v-if="activeTab === 't1-3' && !isProjectReadOnly"
                v-model="submitPopoverVisible"
                placement="top"
                width="200"
                style="margin: 0 10px;"
              >
                <p>是否提交竞价单发起供应商报名？</p>

                <div style="text-align: right; margin: 0">
                  <el-button @click="submitPopoverVisible = false">
                    {{ $t('common.cancel') }}
                  </el-button>

                  <!--提交审批，调下一步-->
                  <el-button type="primary" @click="nextOne">
                    {{ $t('common.confirm') }}
                  </el-button>
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
import { tabTodoMixin } from '@/utils/mixins'
import { SOU_SOURCE_FROM_TYPE_ENUM, SOU_PROJECT_STATUS_ENUM } from 'lib@/composition/origin/enum'
import { compBuyerHttp } from 'modb@/competition/api'
import DetailMenu from './competitionManagementDetail/detailMenu'
import DetailHeader from './competitionManagementDetail/detailHeader'
import ProjectInformation from './competitionManagementDetail/projectInformation'
import ProjectRequirements from './competitionManagementDetail/projectRequirements'
import InviteSuppliers from './competitionManagementDetail/inviteSuppliers'
import ApplyManage from './competitionManagementDetail/applyManage'
import PriceControl from './competitionManagementDetail/priceControl'
import BusinessStandardCtrl from './competitionManagementDetail/businessStandardCtrl'
import CompetitionHall from './competitionManagementDetail/competitionHall'
import TenderSelection from './competitionManagementDetail/tenderSelection'
import CToolbar from 'lib@/components/c-toolbar'
export default {
  name: 'CompetitionManagementDetail',

  provide () {
    return {
      pageFlag: () => {
        const flag = this.$attrs.params.flag
        return {
          isAdd: flag === 'add',
          isEdit: flag === 'edit',
          isView: flag === 'view'
        }
      }
    }
  },

  components: {
    DetailMenu,
    DetailHeader,
    ProjectInformation,
    ProjectRequirements,
    InviteSuppliers,
    ApplyManage,
    PriceControl,
    BusinessStandardCtrl,
    CompetitionHall,
    TenderSelection,
    CToolbar
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      baseInfo: {
        minAmount: '',
        minPercent: '',
        extendTrigger: '',
        extendMinute: '',
        winCount: '',
        souNo: '',
        souName: '',
        compSite: '',
        budgetAmount: '',
        scoreRuleType: '',
        signUpEndTime: '',
        orderEndTime: '',
        orderStartTime: '',
        maxWinVendorCount: '',
        // 商务信息部分
        bondAmount: '0',
        bondMethod: '',
        bondEndTime: '',
        bankAccountNum: '',
        bankAccountName: '',
        bankBranchName: '',
        bondDesc: '',
        // 第三部分
        email: '',
        tel: '',
        linkman: '',
        standardCurrency: 'CNY',
        pricePrecision: '4',
        exchangeRateType: '',
        currencyExchangeDate: new Date().getTime(),
        // 写死来源
        sourceFromType: SOU_SOURCE_FROM_TYPE_ENUM.HAND_MAKE,
        // 写死普通报价
        orderType: 'SIMPLE'
      },
      activeTab: 't1-1',
      flagMenuList: [],
      projectId: '',
      enabledNodeMenu: [],
      processConfigId: '',
      projectInformationData: {
        // 内外部附件信息
        fileList: [],
        // 模板参考
        fileConfigList: [],
        // 可用外币列表
        currencyList: []
      },
      submitPopoverVisible: false
    }
  },

  computed: {
    pageFlag () {
      // 新增、编辑、只读
      // flag: ['add', 'edit', 'readonly']
      const flag = this.$attrs.params.flag
      return {
        isAdd: flag === 'add',
        isEdit: flag === 'edit',
        isView: flag === 'view'
      }
    },

    // 基础详细信息页只读
    isProjectReadOnly () {
      return this.pageFlag.isView || (this.baseInfo.projectStatus && this.baseInfo.projectStatus !== SOU_PROJECT_STATUS_ENUM.DRAFT)
    }
  },

  created () {
    if (!this.pageFlag.isAdd) {
      // 编辑 查看 审批
      this.projectId = this.$attrs.params.row.projectId
      this.getFormDetail('init')
    }
  },

  methods: {
    /* 查询单据详情 */
    async getFormDetail (type) {
      if (!this.projectId) {
        return
      }

      const response = await compBuyerHttp.init.getProjectInfo(this.projectId)
      if (response && response.data) {
        const {
          currencyList = [],
          fileConfigList = [],
          souFileList = [],
          processNodeList = [],
          ...competition
        } = response.data

        // 基础信息
        this.baseInfo = Object.assign({}, this.baseInfo, competition)

        // 项目列表信息
        this.projectInformationData = {
          fileList: souFileList.concat(),
          fileConfigList: fileConfigList.concat(),
          currencyList: currencyList.concat()
        }

        // 流程 ID
        this.processConfigId = competition.processConfigId
        this.$nextTick(() => {
          if (type === 'init') {
            this.getProcessConfig(processNodeList)
          }
        })
      }
    },

    /* 获取流程节点启用信息 */
    async getProcessConfig (processNodeList) {
      this.$refs.detailMenu.setMenuNodeConfig(processNodeList)
      this.$nextTick(() => {
        this.getProcessNode()
      })
    },

    /* 获取流程节点完成信息 */
    async getProcessNode () {
      if (!this.projectId) {
        return
      }

      const response = await compBuyerHttp.process.getNodesByProject(this.projectId)
      if (response && response.data && this.$refs.detailMenu) {
        this.$refs.detailMenu.updateTreeMenuData(response.data)
      }
    },

    /* 上一步 */
    prevOne () {
      switch (this.activeTab) {
        case 't1-2':
          // 项目信息
          this.activeTab = 't1-1'
          break
        case 't1-3':
          // 邀请供应商
          this.activeTab = 't1-2'
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
        case 't1-1':
          // 保存项目基础信息
          asyncData = await this.$refs.projectInformation.tempSaveProjectInfo(type)
          if (!asyncData || !asyncData.status) {
            return
          }

          // 更新ID
          this.projectId = asyncData.data
          break
        case 't1-2':
          // 需求信息
          asyncData = await this.$refs.projectRequirements.saveRequirement(type)
          if (!asyncData || !asyncData.status) {
            return
          }
          break
        case 't1-3':
          // 邀请供应商
          asyncData = await this.$refs.inviteSuppliers.saveInviteSuppliers(type)
          if (!asyncData || !asyncData.status) {
            return
          }
          break
      }

      if (!this.isProjectReadOnly && type === 'nextOne') {
        // 更新节点信息
        await this.getProcessNode()
      }

      if (this.activeTab !== 't1-2') {
        // 查询单据信息 需要更新项目状态
        await this.getFormDetail()
      }

      if (type === 'nextOne') {
        // 跳转下一步页签
        this.saveNextTodo()
      }
    },

    /* 项目左侧菜单点击 */
    menuClick (data) {
      // 没有单据ID || 没有流程ID
      if (!this.projectId || !this.processConfigId) {
        this.$message.warning(this.$t('bidMod.msgInputProInfo'))
        return
      }

      this.activeTab = data.id
    },

    /* 接收已完成的节点 */
    setEnabledNodeAndFlagList ({ enabledList = [], flagList = [] }) {
      this.flagMenuList = flagList || []
      this.enabledNodeMenu = enabledList
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
