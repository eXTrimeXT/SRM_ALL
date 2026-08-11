<template>
  <div>
    <h3 style="margin: 10px 0 20px 0">
      <!-- 随机抽取专家 -->
      <!-- 技术开标后不可点击 -->
      <!-- <el-button
        type="primary"
        :disabled="biddingBase.techOpen=='Y' || isLeader"
        @click="randomExtractExpert"
      >
        {{ $t('cusEntry.bidMod.randomExtract') }}
      </el-button> -->
      <!-- 指定专家 -->
      <!-- 技术开标后不可点击 -->
      <!-- <el-button
        type="primary"
        :disabled="biddingBase.techOpen=='Y' || isLeader"
        @click="designExpertDialogVisible=true"
      >
        {{ $t('cusEntry.bidMod.designExpert') }}
      </el-button> -->
      <!-- 技术开标 -->
      <!-- 招标专家+评标委员会主席才可以点击，技术开标后不可点击 -->
      <el-button
        type="primary"
        :disabled="userDisabled"
        @click="openTech"
      >
        {{ $t('bidMod.techopenBiding') }}
      </el-button>
      <!-- 查看技术方案 -->
      <!-- 12.14新加校验，技术已开标及后续状态才可以查看（因为一个人点了技术开标，技术开标标识就变成Y了, 而此时招标状态未变 -->
      <!-- 1.3新加校验，盲评时，评标委员会主席应不可查看技术方案 -->
      <!-- 1.11 补充校验，[简易招标、竞争性谈判]：技术已开标及后续状态才可以查看（招标状态!=商务投标状态已截止） -->
      <el-button
        type="primary"
        :disabled="biddingBase.techOpen!=='Y' ||
          biddingBase.projectStatus==='TECH_BID_END' ||
          (biddingBase.extHideKeyInfo==='Y' && isLeader) ||
          (['SIMPLE', 'COMPETE'].includes(biddingBase.extSouProcess) && biddingBase.projectStatus==='BUS_BID_END')
        "
        @click="techSchemeDialogVisible=true"
      >
        {{ $t('cusEntry.bidMod.viewTechScheme') }}
      </el-button>
      <!-- 技术方案分析 -->
      <!-- 技术已开标及后续状态才可以查看（因为一个人点了技术开标，技术开标标识就变成Y了, 而此时招标状态未变 -->
      <!-- 补充校验，[简易招标、竞争性谈判]：技术已开标及后续状态才可以查看（招标状态!=商务投标状态已截止） -->
      <!-- 技术开标后才可点击且对评标委员会主席隐藏该按钮 -->
      <!-- <el-button
        v-if="!isLeader"
        type="primary"
        :disabled="biddingBase.techOpen!=='Y' || biddingBase.projectStatus==='TECH_BID_END' || (['SIMPLE', 'COMPETE'].includes(biddingBase.extSouProcess) && biddingBase.projectStatus==='BUS_BID_END')"
        @click="openTechAnalysisTab"
      >
        {{ $t('cusEntry.bidMod.techSoluteAnalysis') }}
      </el-button> -->
      <!-- 方案脱敏上传 -->
      <!-- 仅隐藏评标关键信息时显示 -->
      <!-- 1.3新加校验，确认评标后，方案脱敏上传置灰 -->
      <!-- <el-button
        v-if="biddingBase.extHideKeyInfo==='Y'"
        type="primary"
        :disabled="isLeader || biddingBase.extConfirmFlag==='Y'"
        @click="techSolutionFileDialogVisible=true"
      >
        {{ $t('cusEntry.bidMod.uploadSolution') }}
      </el-button> -->
      <!-- 开始评标 -->
      <!-- 单据状态：技术已开标才可以点击 && 隐藏评标关键信息时显示-->
      <el-button
        v-if="biddingBase.extHideKeyInfo==='Y'"
        type="primary"
        :disabled="biddingBase.projectStatus!=='TECH_BID_OPEN' || isLeader"
        @click="startEvaTech"
      >
        {{ $t('cusEntry.bidMod.startEvaTech') }}
      </el-button>
      <!-- 确认评标 -->
      <!-- 单据状态：技术评标中 && 未点击确认评标：extConfirmFlag!=='Y'才可以点击 -->
      <el-button
        type="primary"
        :disabled="biddingBase.projectStatus!=='TECH_BID_EVA' || biddingBase.extConfirmFlag =='Y' || isLeader"
        @click="confirmTechEva"
      >
        {{ $t('cusEntry.bidMod.confirmEvaTech') }}
      </el-button>
      <!-- 查看抽取历史 -->
      <!-- <el-button :disabled="isLeader" @click="techHistoryDialogVisible=true">
        {{ $t('cusEntry.bidMod.viewExtractHistory') }}
      </el-button> -->
      <!-- 抽取风险 -->
      <!-- <el-button :disabled="isLeader" @click="extractRiskDialogVisible=true">
        {{ $t('cusEntry.bidMod.extractRisk', { h: detailForm.extractRiskNum }) }}
      </el-button> -->
      <el-button @click="handlerRefresh">
        {{ $t('common.refresh') }}
      </el-button>
    </h3>

    <SrmRow>
      <!-- 专家抽取范围 -->
      <!-- <SrmCol :init-col="3">
        <span style="color: #ff4949; padding-right: 2px;">*</span>
        <span>{{ $t('cusEntry.bidMod.extExpertRange') }}</span>
        <p>
          <dict-select
            v-model="detailForm.extExpertRange"
            code="SOU_EXPERT_RANGE"
            style="width: 100%;"
          />
        </p>
      </SrmCol> -->

      <!-- 评标总人数 -->
      <SrmCol :init-col="3">
        <span>{{ $t('cusEntry.bidMod.extBidEvaluatorNum') }}</span>
        <p><el-input v-model="detailForm.extBidEvaluatorNum" disabled /></p>
      </SrmCol>

      <!-- 要求高级专家人数 -->
      <SrmCol :init-col="3">
        <span>{{ $t('cusEntry.bidMod.extAskSeniorExpertNum') }}</span>
        <p><el-input v-model="detailForm.extAskSeniorExpertNum" disabled /></p>
      </SrmCol>
    </SrmRow>

    <div style="margin: 16px 0">
      <!-- 评标小组 -->
      <span style="font-size:14px; font-weight:bold">{{ $t('cusEntry.bidMod.evaGroupList') }}</span>
      <!-- 专家抽取提示：当前抽取范围下，高级专家X个，普通专家Y个。 -->
      <!-- <span style="margin-left:8px; color:red">
        {{ $t('cusEntry.bidMod.evaGroupListTip', { h: detailForm.expertNum, m: detailForm.commonNum }) }}
      </span> -->
    </div>
    <el-table
      border
      max-height="180"
      style="width: 100%"
      :data="evaGroupList"
      highlight-current-row
    >
      <el-table-column
        align="center"
        type="index"
        fixed="left"
        :label="$t('common.sort')"
        width="55"
      />
      <el-table-column
        align="center"
        prop="userName"
        :label="$t('dataConfMod.jobNum')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="fullName"
        :label="$t('bidMod.fullName')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="phone"
        :label="$t('bidMod.phone')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="email"
        :label="$t('perfMod.scoreUserEmail')"
        min-width="120"
        show-overflow-tooltip
      />
      <!-- <el-table-column
        align="center"
        prop="position"
        :label="$t('vendorMod.userPost')"
        min-width="120"
        show-overflow-tooltip
      /> -->
      <el-table-column
        align="center"
        prop="groupRole"
        :label="$t('meeting.role')"
        min-width="120"
        :formatter="(row, column, cellValue) => $getDictLabel('SOU_GROUP_ROLE', cellValue)"
        show-overflow-tooltip
      />
      <!-- 专家等级 -->
      <el-table-column
        align="center"
        prop="extExpertLevel"
        :label="$t('cusEntry.bidMod.expertLevel')"
        min-width="120"
        :formatter="(row, column, cellValue) => $getDictLabel('SOU_BID_EXPERT_LEVEL', cellValue)"
        show-overflow-tooltip
      />
      <!-- 评分权限 -->
      <el-table-column
        align="center"
        prop="scoreAuth"
        :label="$t('cusEntry.bidMod.scoreAuth')"
        min-width="120"
        :formatter="(row, column, cellValue) => $getDictLabel('SCC_SOU_SCORE_DIMENSION_CODE', cellValue)"
        show-overflow-tooltip
      />
      <!-- 招标状态：技术已开标后，不允许再进行移除 -->
      <!-- <el-table-column
        v-if="biddingBase.techOpen!=='Y'"
        align="center"
        fixed="right"
        :label="$t('common.operation')"
        width="80"
      >
        <template slot-scope="{ row }"> -->
          <!-- 移除 -->
          <!-- 评标委员会主席不可移除 -->
          <!-- <el-button
            v-if="row.groupRole!=='LEADER'"
            type="text"
            :disabled="isLeader"
            @click="handleRemove(row)"
          >
            {{ $t('cusEntry.common.remove') }}
          </el-button>
        </template>
      </el-table-column> -->
    </el-table>

    <div style="margin: 16px 0">
      <span style="font-size:14px; font-weight:bold">{{ $t('cusEntry.bidMod.evaTechScoreList') }}</span>
    </div>
    <el-table
      border
      max-height="180"
      style="width: 100%"
      :data="progressList"
      highlight-current-row
    >
      <el-table-column
        align="center"
        type="index"
        fixed="left"
        :label="$t('common.sort')"
        width="55"
      />
      <el-table-column
        align="center"
        prop="userName"
        :label="$t('dataConfMod.jobNum')"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="fullName"
        :label="$t('bidMod.fullName')"
        min-width="120"
        show-overflow-tooltip
      />
      <!-- 专家等级 -->
      <!-- <el-table-column
        align="center"
        prop="extExpertLevel"
        :label="$t('cusEntry.bidMod.expertLevel')"
        :formatter="(row, column, cellValue) => $getDictLabel('SOU_BID_EXPERT_LEVEL', cellValue)"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="groupRole"
        :label="$t('meeting.role')"
        :formatter="(row, column, cellValue) => $getDictLabel('SOU_GROUP_ROLE', cellValue)"
        min-width="120"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="phone"
        :label="$t('bidMod.phone')"
        min-width="120"
        show-overflow-tooltip
      /> -->
      <!-- 评审状态 -->
      <el-table-column
        align="center"
        prop="scoreStatus"
        :label="$t('cusEntry.bidMod.reviewStatus')"
        min-width="120"
        :formatter="(row, column, cellValue) => $getDictLabel('SOU_TECH_SCORE_STATUS', cellValue)"
        show-overflow-tooltip
      />
      <!--评审详情-->
      <el-table-column
        align="center"
        prop="orderNo"
        :label="$t('cusEntry.bidMod.reviewDetail')"
        min-width="120"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-button type="text" @click="openTechScoreTab(scope.row)">
            {{ $t('common.view') }}
          </el-button>
        </template>
      </el-table-column>
      <!-- 确认评标之后不可操作退回评标 -->
      <el-table-column
        v-if="biddingBase.extConfirmFlag!=='Y'"
        align="center"
        fixed="right"
        :label="$t('common.operation')"
        width="80"
      >
        <template slot-scope="{ row }">
          <!-- 退回评标 -->
          <!-- 只有已评分才展示退回评标 -->
          <el-button
            v-if="row.scoreStatus=='FINISHED'"
            type="text"
            :disabled="isLeader"
            @click="handleReturnBid(row)"
          >
            {{ $t('cusEntry.bidMod.returnBidEva') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 指定专家 -->
    <DesignExpertDialog
      v-if="designExpertDialogVisible"
      :visible.sync="designExpertDialogVisible"
      :project-id="biddingBase.projectId"
      @success="getTechProgress"
    />

    <!-- 查看技术方案 -->
    <TechSchemeDialog
      v-if="techSchemeDialogVisible"
      :visible.sync="techSchemeDialogVisible"
      :project-id="biddingBase.projectId"
      :merge-flag="biddingBase.mergeFlag"
      :ext-project-no="biddingBase.extProjectNo"
    />

    <!--技术方案脱敏上传-->
    <TechSolutionFileDialog
      v-if="techSolutionFileDialogVisible"
      :visible.sync="techSolutionFileDialogVisible"
      :project-id="biddingBase.projectId"
      :merge-flag="biddingBase.mergeFlag"
    />
    <!--查看抽取历史-->
    <TechHistoryDialog
      v-if="techHistoryDialogVisible"
      :visible.sync="techHistoryDialogVisible"
      :project-id="biddingBase.projectId"
    />
    <!-- 抽取风险 -->
    <ExtractRiskDialog
      v-if="extractRiskDialogVisible"
      :visible.sync="extractRiskDialogVisible"
      :project-id="biddingBase.projectId"
    />
  </div>
</template>

<script>
import { getDictList } from '@/api/user'
import { bidBuyerHttp } from 'modcb@/biddingBuyer/api'
import { SOU_PROJECT_STATUS_ENUM } from 'lib@/composition/origin/enum'
import { judgeManagement } from 'lib@/composition/biddingLts/utils'
import TechHistoryDialog from './techManagement/techHistoryDialog'
import ExtractRiskDialog from './techManagement/extractRiskDialog'
import TechSolutionFileDialog from './techManagement/techSolutionFileDialog'
import TechScoreDetail from './techManagement/techScoreDetail'
import TechSchemeDialog from './techManagement/techSchemeDialog'
import DesignExpertDialog from './techManagement/designExpertDialog'
import TechAnalysis from './techManagement/TechAnalysis'
import { getOrderFileCheckDetail } from 'modcb@/biddingBuyer/api/analysis'

export default {
  name: 'TechManagement',

  components: {
    TechSchemeDialog,
    TechHistoryDialog,
    ExtractRiskDialog,
    TechSolutionFileDialog,
    DesignExpertDialog
  },

  props: {
    projectStatus: {
      // 招标状态
      type: String,
      default: ''
    },
    createApprovalStatus: {
      // 审批状态
      type: String,
      default: ''
    },
    biddingBase: {
      type: Object,
      default: () => ({})
    },
    isActiveMenu: {
      type: Boolean,
      required: true
    },
    // 是否只有一个供应商
    isOnlySupply: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      textNumList: [],
      detailForm: {
        extBidEvaluatorNum: null,
        extAskSeniorExpertNum: null,
        extExpertRange: 'OU', // OU-按公司抽取
        expertNum: null,
        commonNum: null,
        extractRiskNum: 0
      },
      evaGroupList: [],
      progressList: [],
      techHistoryDialogVisible: false,
      extractRiskDialogVisible: false,
      techSolutionFileDialogVisible: false,
      techSchemeDialogVisible: false,
      designExpertDialogVisible: false,
      editRow: {},
      techOpenUserList: [],
      SOU_PROJECT_STATUS_ENUM,
      isBidEval: true
    }
  },

  computed: {
    userId () {
      return this.$store.getters.userInfo.userId || ''
    },
    userDisabled () {
      let techOpenUserList = this.techOpenUserList?.map(item => item.userId)
      return (
        !techOpenUserList.includes(this.userId) ||
        this.techOpenUserList.some(item => item.userId === this.userId && item.openStatus === 'COMPLETED')
      )
    },
    // 当前登录人为评标委员会主席 (评标委员会主席在技术标页面只有两个按钮权限：技术开标、查看技术方案)
    isLeader () {
      let leaderObj = this.evaGroupList?.find(item => item.groupRole == 'LEADER') || {}
      return this.userId === leaderObj.userId
    },
    readonly () {
      // 项目状态=='拟定' && 审批状态=='草稿、审批中'
      return judgeManagement(this.projectStatus, this.createApprovalStatus)
    }
  },

  watch: {
    isActiveMenu: {
      handler (val) {
        if (val) {
          this.getTechProgress()
          // 只有一个供应商进行报名投标，不需要进行技术方案分析， 可直接确认评标
          // if (this.isOnlySupply) {
          //   this.isBidEval = false
          // } else {
          //   this.getOrderFileCheckDetail()
          // }
        }
      },
      immediate: true
    }
  },
  async created () {
    // 查询字数下拉列表
    await this.getTextNumList()
  },

  methods: {
    // 判断确认评标按钮是否可点击
    // 发起过技术方案分析且结果回传后才可以点击
    getOrderFileCheckDetail () {
      const params = {
        type: 'OrderFileCheck',
        action: 'detail',
        payload: {
          projectId: this.biddingBase.projectId
        },
        lang: 'zh-cn',
        query: {
          '*': {}
        },
        tree: true
      }
      getOrderFileCheckDetail(params).then(res => {
        if (res && res.data) {
          const data = res.data.records[0]
          this.isBidEval = !data.taskList.length
        }
      })
    },
    // 获取字数下拉列表
    getTextNumList () {
      getDictList({ code: 'AI_CHECK_FILE_PART_TYPE' }).then(res => {
        if (res && res.data) {
          this.textNumList = res.data
        }
      })
    },
    // 技术方案分析
    openTechAnalysisTab () {
      let tab = {
        component: TechAnalysis,
        params: {
          projectId: this.biddingBase.projectId,
          extProjectNo: this.biddingBase.extProjectNo,
          souName: this.biddingBase.souName,
          textNumList: this.textNumList,
          tabName: this.$t('cusEntry.bidMod.techSoluteAnalysis') // 技术方案分析
        },
        title: `${this.$t('cusEntry.bidMod.techSoluteAnalysis')}-${this.biddingBase.extProjectNo}`,
        name: `${this.$t('cusEntry.bidMod.techSoluteAnalysis')}-${this.biddingBase.extProjectNo}`
      }
      this.$emit('tab-add', tab)
    },
    // 刷新
    handlerRefresh () {
      // 更新节点信息
      this.$emit('refresh-process')
      // 更新招标基础数据
      this.$emit('refresh')
      // 更新当前页面数据
      this.getTechProgress()
    },
    /* 查询数据 */
    getTechProgress () {
      bidBuyerHttp.tech.getTechManagement(this.biddingBase.projectId).then(res => {
        if (res && res.data) {
          this.detailForm = res.data
          this.evaGroupList = res.data.evaGroupList
          this.progressList = res.data.evaTechScoreList
          this.techOpenUserList = res.data.openUserList
        }
      })
    },
    // 评标小组-移除
    handleRemove (row) {
      this.$prompt(this.$t('cusEntry.bidMod.extRemoveReason'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        closeOnClickModal: false,
        inputPlaceholder: this.$t('cusEntry.common.pleaseFill'),
        inputValidator: (value) => {
          if (!value) {
            return this.$t('cusEntry.bidMod.inputExtRemoveReason')
          }
        } }).then(({ value }) => {
        const params = {
          groupId: row.groupId,
          extRemoveReason: value
        }
        bidBuyerHttp.tech.remove(params).then(res => {
          this.$message.success(res.message)
          this.getTechProgress()
        })
      })
    },
    // 评标进度跟踪-退回评标
    handleReturnBid (row) {
      this.$prompt(this.$t('cusEntry.bidMod.extRejectReason'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        closeOnClickModal: false,
        inputPlaceholder: this.$t('cusEntry.common.pleaseFill'),
        inputValidator: (value) => {
          if (!value) {
            return this.$t('cusEntry.bidMod.inputExtRejectReason')
          }
        } }).then(({ value }) => {
        const params = {
          techScoreHeadId: row.techScoreHeadId,
          extRejectReason: value
        }
        bidBuyerHttp.tech.returnBid(params).then(res => {
          this.$message.success(res.message)
          this.getTechProgress()
        })
      })
    },
    // 查看评分详情
    openTechScoreTab (row) {
      let tab = {
        component: TechScoreDetail,
        params: {
          editRow: {
            ...row,
            extProjectNo: this.biddingBase.extProjectNo,
            souNo: this.biddingBase.souNo,
            souName: this.biddingBase.souName
          },
          tabName: `TechScoreDetail${row.projectId}${row?.groupId}`
        },
        title: this.biddingBase.extProjectNo + '-' + this.$t('cusEntry.bidMod.techScoreDetail'), // 评分详情
        name: `TechScoreDetail${row.projectId}${row?.groupId}`
      }
      this.$emit('tab-add', tab)
    },
    // 随机抽取专家
    randomExtractExpert () {
      if (!this.detailForm.extExpertRange) {
        this.$message.error(this.$t('cusEntry.bidMod.selectExpertRange'))
        return
      }
      bidBuyerHttp.tech.randomExtractExpert({
        projectId: this.biddingBase.projectId,
        extExpertRange: this.detailForm.extExpertRange
      }).then(res => {
        this.$message.success(res.message)
        this.getTechProgress()
      })
    },
    // 开始评标
    async startEvaTech () {
      const confirmResult = await this.$confirm(this.$t('cusEntry.supplement20250205.startBid'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })
      if (confirmResult !== 'confirm') {
        return
      }
      bidBuyerHttp.tech.evaTech(this.biddingBase.projectId).then(res => {
        this.$message.success(res.message)
        // 更新节点信息
        this.$emit('refresh-process')
        // 更新招标基础数据
        this.$emit('refresh')
        // 更新当前页面数据
        this.getTechProgress()
      })
    },
    // 确认评标
    async confirmTechEva () {
      const confirmResult = await this.$confirm(this.$t('cusEntry.supplement20250205.confirmBid'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })
      if (confirmResult !== 'confirm') {
        return
      }
      bidBuyerHttp.tech.confirmTechEva(this.biddingBase.projectId).then(res => {
        this.$message.success(res.message)
        // 更新节点信息
        this.$emit('refresh-process')
        // 更新招标基础数据
        this.$emit('refresh')
        // 更新当前页面数据
        this.getTechProgress()
      })
    },
    /* 技术开标 */
    async openTech () {
      let extBidEvaluatorNum = this.detailForm.extBidEvaluatorNum
      let evaGroupListNum = this.evaGroupList.length
      if (extBidEvaluatorNum != evaGroupListNum) {
        // 评标小组人数与评标总人数不一致，不可开技术标
        this.$message.error(this.$t('cusEntry.tipMessage.inconsistentNumberOfPeople'))
        return
      }
      // 确定发起技术开标？
      const confirmResult = await this.$confirm(this.$t('cusEntry.supplement20250205.startTechBid'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmResult !== 'confirm') {
        return
      }

      const response = await bidBuyerHttp.tech.openTech(this.biddingBase.projectId)
      if (response) {
        this.$message.success(response.message)
        // 更新节点信息
        this.$emit('refresh-process')
        // 更新招标基础数据
        this.$emit('refresh')
        // 更新当前页面数据
        this.getTechProgress()
      }
    }
  }
}
</script>

<style lang="scss" scoped>
</style>
