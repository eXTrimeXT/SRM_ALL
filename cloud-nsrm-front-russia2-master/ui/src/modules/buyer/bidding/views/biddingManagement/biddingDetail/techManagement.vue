<template>
  <div>
    <SrmRow class="the_biding_control_row" style="margin-top: 5px">
      <!-- 当前需评审供应商 -->
      <SrmCol :init-col="3">
        <span class="label-title">{{ $t('bidMod.needToReviewCount') }}</span>
        <el-input class="inp" v-model="needToReviewCount" disabled />
      </SrmCol>

      <!-- 已完成评审的供应商 -->
      <SrmCol :init-col="3">
        <span class="label-title">{{ $t('bidMod.alreadyReviewCount') }}</span>
        <el-input class="inp" v-model="alreadyReviewCount" disabled />
      </SrmCol>
    </SrmRow>

    <SrmRow class="the_biding_control_row">
      <!-- 供应商名称 -->
      <SrmCol :init-col="3">
        <span class="label-title">{{ $t('common.vendorName') }}</span>
          <QuickSearch
            :show-input="preform.vendorId"
            show-key="companyName"
            :scope-data="preform"
            name="scc_sup_company_info2"
            @close-quicksearch="setVendorObj"
          />
      </SrmCol>

      <!-- 技术评标进度 -->
      <SrmCol :init-col="3">
        <span class="label-title">{{ $t('bidMod.progressOfTechBid') }}</span>
        <DictSelect
          v-model="preform.scoreStatus"
          code="SOU_TECH_SCORE_STATUS"
          clearable
        />
      </SrmCol>

      <SrmCol :init-col="3" >
        <!-- 查询 -->
        <el-button
          type="primary"
          :disabled="readonly"
          @click="getTechProgress"
        >
          {{ $t('common.search') }}
        </el-button>
      </SrmCol>
    </SrmRow>

    <h3 style="margin: 10px 0 5px">
      {{ $t('bidMod.progressOfTechBid') }}
      <!-- 技术开标 项目状态为投标截止 or 商务评标 -->
      <el-button
        v-if="!readonly && [SOU_PROJECT_STATUS_ENUM.BUSINESS_EVAL, SOU_PROJECT_STATUS_ENUM.ORDER_END].includes(projectStatus)"
        type="primary"
        @click="openTech"
      >
        {{ $t('bidMod.techopenBiding') }}
      </el-button>
    </h3>

    <el-table
      :data="progressOfTechBrgList"
      style="width: 100%"
      border
      height="180"
      highlight-current-row
    >
      <el-table-column
        type="index"
        :label="$t('common.sort')"
        width="50"
      />

      <!--供应商名称-->
      <el-table-column
        prop="vendorName"
        :label="$t('bidMod.vendorName')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--报价详情-->
      <el-table-column
        prop="orderNo"
        :label="$t('bidMod.bidDetail1')"
        min-width="150"
        show-overflow-tooltip
      >
        <template v-slot="{ row }">
          <el-button type="text" @click="openTechInfoDialog(row)">
            {{ row.orderNo }}
          </el-button>
        </template>
      </el-table-column>

      <!--技术评标进度-->
      <el-table-column
        prop="scoreStatus"
        :label="$t('bidMod.progressOfTechBid')"
        width="120"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('SOU_TECH_SCORE_STATUS', cellValue)"
      />

      <!--技术总得分-->
      <el-table-column
        prop="techScore"
        :label="$t('bidMod.techTotalScore')"
        width="100"
        show-overflow-tooltip
      />

      <!--技术标进度-->
      <el-table-column
        :label="$t('bidMod.techBidProgress')"
        width="100"
        show-overflow-tooltip
      >
        <template v-slot="{ row }">
          <el-button type="text" @click="openTechEvaluationProgressDialog(row)">
            {{ $t('common.view') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--技术标进度-->
    <TechProgressDialog
      v-if="techProgressDialogVisible"
      :visible.sync="techProgressDialogVisible"
      :project-id="biddingBase.projectId"
      :bidding-base="biddingBase"
      :edit-row="editRow"
    />

    <!--查看技术标详情-->
    <TechInfoDialog
      v-if="techInfoDialogVisible"
      :visible.sync="techInfoDialogVisible"
      :project-id="biddingBase.projectId"
      :edit-row="editRow"
    />
  </div>
</template>

<script>
import { bidBuyerHttp } from 'modb@/bidding/api'
import { SOU_PROJECT_STATUS_ENUM } from 'lib@/composition/origin/enum'
import { judgeManagement } from 'lib@/composition/biddingLts/utils'
import QuickSearch from 'lib@/components/QuickSearch'
import TechProgressDialog from './techManagement/techProgressDialog'
import TechInfoDialog from './techManagement/techInfoDialog'

export default {
  name: 'TechManagement',

  components: {
    QuickSearch,
    TechProgressDialog,
    TechInfoDialog
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
    }
  },

  data () {
    return {
      progressOfTechBrgList: [],
      techEvaluationProgress: [],
      preform: {
        vendorId: '',
        scoreStatus: ''
      },
      needToReviewCount: '',
      alreadyReviewCount: '',
      techProgressDialogVisible: false,
      techInfoDialogVisible: false,
      editRow: null,
      SOU_PROJECT_STATUS_ENUM
    }
  },

  computed: {
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
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 查询数据 */
    async getTechProgress () {
      if (!this.biddingBase.projectId) {
        return
      }
      const response = await bidBuyerHttp.tech.techProgress({
        projectId: this.biddingBase.projectId,
        vendorId: this.preform.vendorId,
        scoreStatus: this.preform.scoreStatus
      })
      if (response) {
        const {
          needReviewVendorCount = 0,
          alreadyReviewVendorCount = 0,
          techProgressList = []
        } = response.data || {}
        this.needToReviewCount = needReviewVendorCount
        this.alreadyReviewCount = alreadyReviewVendorCount
        this.progressOfTechBrgList = techProgressList
      }
    },

    /* 设置供应商查询对象 */
    setVendorObj (val) {
      const { companyId = '' } = val || {}
      this.preform.vendorId = companyId
    },

    /* 查看技术标详情 */
    openTechInfoDialog (row) {
      this.editRow = row
      this.techInfoDialogVisible = true
    },

    /* 打开技术标进度 */
    openTechEvaluationProgressDialog (row) {
      this.editRow = row
      this.techProgressDialogVisible = true
    },

    /* 技术开标 */
    async openTech () {
      // '确定发起技术开标吗？'
      const confirmResult = await this.$confirm(this.$t('bidMod.openTechBid'), {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).catch(() => { /* nothing */ })

      if (confirmResult !== 'confirm') {
        return
      }

      const response = await bidBuyerHttp.tech.openTech(this.biddingBase.projectId)
      if (response) {
        this.$message.success(this.$t('common.successSubmit'))
        // 更新节点信息
        this.$emit('refresh-process')
        // 更新招标基础数据
        this.$emit('refresh')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.the_biding_control_row ::v-deep {
  .el-col {
    display: flex;
    .label-title {
      width: 125px;
      text-align: right;
      line-height: 30px;
      padding-right: 8px;
    }
    .el-input,
    .el-select {
      flex: 1;
    }
  }
}
</style>
