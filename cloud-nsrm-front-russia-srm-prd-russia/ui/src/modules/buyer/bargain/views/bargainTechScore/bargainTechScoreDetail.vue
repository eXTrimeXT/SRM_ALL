<template>
  <div class="technology-score-detail">
    <h2 class="technology-score-detail-h2">
      {{ $t('bidMod.publicInfo') }}
      <!--刷新-->
      <el-button type="primary" @click="getTechProgressReviewDetail">
        {{ $t('common.refresh') }}
      </el-button>
    </h2>

    <div class="display-form-content">
      <SrmRow>
        <!--项目编号-->
        <SrmCol :init-col="3">
          <span>{{ $t('bidMod.bidingNum') }}</span>{{ bargainData.souNo }}
        </SrmCol>
        <!--项目名称-->
        <SrmCol :init-col="3">
          <span>{{ $t('bidMod.bidingName') }}</span>{{ bargainData.souName }}
        </SrmCol>
        <!--发布时间-->
        <SrmCol :init-col="3">
          <span>{{ $t('bidMod.releaseDatetime') }}</span>{{ bargainData.publishTime }}
        </SrmCol>
        <!--投标截止时间-->
        <SrmCol :init-col="3">
          <span>{{ $t('bidMod.enrollEndDatetime') }}</span>{{ bargainData.orderEndTime }}
        </SrmCol>
        <!--编制人-->
        <SrmCol :init-col="3">
          <span>{{ $t('bidMod.createdBy2') }}</span>{{ bargainData.createdUserName }}
        </SrmCol>
        <!--投标开始时间-->
        <SrmCol :init-col="3">
          <span>{{ $t('bidMod.bidingStartDatetime') }}</span>{{ bargainData.orderStartTime }}
        </SrmCol>
        <!--预计投标地点-->
        <SrmCol :init-col="3">
          <span>{{ $t('bidMod.bidingSite') }}</span>{{ bargainData.orderSite }}
        </SrmCol>
        <!--预算金额-->
        <SrmCol :init-col="3">
          <span>{{ $t('bidMod.budgetAmount') }}</span>{{ bargainData.budgetAmount }}
        </SrmCol>
        <!--其他说明-->
        <SrmCol :init-col="3">
          <span>{{ $t('bidMod.bondDesc') }}</span>{{ bargainData.bondDesc }}
        </SrmCol>
      </SrmRow>
    </div>

    <!-- 评审列表 -->
    <h3>{{ $t('bidMod.reviewList') }}</h3>
    <el-table
      :data="vendorScoreDetailList"
      style="width: 100%"
      border
      height="200"
      highlight-current-row
    >
      <el-table-column
        align="center"
        type="index"
        width="40"
      />

      <!-- 供应商编码 -->
      <el-table-column
        align="center"
        prop="vendorCode"
        :label="$t('common.vendorCode')"
        min-width="120"
        show-overflow-tooltip
      />

      <!-- 供应商名称 -->
      <el-table-column
        align="center"
        prop="vendorName"
        :label="$t('common.vendorName')"
        min-width="200"
        show-overflow-tooltip
      />

      <!-- 联系人 -->
      <el-table-column
        align="center"
        prop="linkManName"
        :label="$t('bidMod.contactMan')"
        width="100"
        show-overflow-tooltip
      />

      <!--电话-->
      <el-table-column
        align="center"
        prop="phone"
        :label="$t('bidMod.phone')"
        width="120"
        show-overflow-tooltip
      />

      <!--邮箱-->
      <el-table-column
        align="center"
        prop="email"
        :label="$t('bidMod.email2')"
        width="180"
        show-overflow-tooltip
      />

      <!--技术评标进度-->
      <el-table-column
        align="center"
        prop="scoreStatus"
        :label="$t('bidMod.progressOfTechBid')"
        width="120"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('SOU_TECH_SCORE_STATUS', cellValue)"
      />

      <el-table-column
        align="center"
        :label="$t('common.operation')"
        width="80"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <!-- 评审 or 查看 -->
          <el-button type="text" @click="openScoringDetailsDialog(scope.row)">
            {{ scope.row.scoreStatus === SOU_TECH_SCORE_STATUS_ENUM.FINISHED ? $t('common.view') : $t('bidMod.scoreStatus') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--技术评分评审弹窗-->
    <ScoringDetailsDialog
      v-if="scoringDetailsDialogVisible"
      :visible.sync="scoringDetailsDialogVisible"
      :edit-row="editRow"
      :project-id="projectId"
      :readonly="isScoringDetailsReadOnly"
      @success="getTechProgressReviewDetail"
    />
  </div>
</template>

<script>
/**
 * 技术评分详情
 */
import { brgBuyerHttp } from 'modb@/bargain/api'
import { SOU_TECH_SCORE_STATUS_ENUM } from 'lib@/composition/origin/enum'
import ScoringDetailsDialog from 'lib@/composition/bargainLts/scoringDetailsDialog'

export default {
  name: 'TechnologyScoreDetail',

  components: { ScoringDetailsDialog },

  data () {
    return {
      bargainData: {
        souNo: '',
        souName: '',
        publishTime: '',
        publishScope: '',
        signUpEndTime: '',
        approvalBy: '',
        createdBy: '',
        orderStartTime: '',
        orderSite: '',
        budgetAmount: '',
        bondDesc: ''
      },
      group: {},
      projectId: this.$attrs.params.row.projectId || '',
      vendorScoreDetailList: [],
      scoringDetailsDialogVisible: false,
      editRow: null,
      isScoringDetailsReadOnly: false,
      SOU_TECH_SCORE_STATUS_ENUM
    }
  },

  created () {
    this.getTechProgressReviewDetail()
  },

  methods: {
    /* 查询数据 */
    async getTechProgressReviewDetail () {
      if (!this.projectId) {
        return
      }

      const response = await brgBuyerHttp.tech.techProgressReviewDetail(this.projectId)
      if (response && response.data) {
        const {
          group = {},
          project = {},
          vendorScoreDetailList = []
        } = response.data
        this.group = group
        this.bargainData = project
        this.vendorScoreDetailList = vendorScoreDetailList || []
      }
    },

    /* 打开评分弹窗 */
    openScoringDetailsDialog (row) {
      this.editRow = {
        ...row,
        groupId: this.group.groupId || ''
      }
      this.isScoringDetailsReadOnly = row.scoreStatus === SOU_TECH_SCORE_STATUS_ENUM.FINISHED
      this.scoringDetailsDialogVisible = true
    }
  }
}
</script>

<style lang="scss" scoped>
.technology-score-detail {
  width: 100%;
  height: 100%;
  padding: 16px 16px 40px;
  overflow: auto;
  .technology-score-detail-h2 {
    text-align: center;
    line-height: 32px;
    margin: 0;
  }
  .display-form-content ::v-deep {
    .el-row {
      margin-top: 10px;
      .el-col {
        margin-bottom: 10px;
        font-size: 14px;
        min-height: 22px;
        span {
          padding-right: 10px;
          display: inline-block;
          color: #999;
        }
      }
    }
  }
}
</style>
