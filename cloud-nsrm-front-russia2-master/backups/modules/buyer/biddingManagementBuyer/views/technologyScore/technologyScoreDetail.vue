<template>
  <div class="technology-score-detail">
    <h2 class="technology-score-detail-h2">
      {{ $t('bidMod.publicInfo') }}
      <!--刷新-->
      <el-button
        type="primary"
        class="detail-pbtn"
        @click="getTechProgressReviewDetail"
      >
        {{ $t('common.refresh') }}
      </el-button>
    </h2>

    <div class="display-form-content">
      <srm-row>
        <!--项目编号-->
        <srm-col :init-col="3">
          <span>{{ $t('bidMod.bidingNum') }}</span>{{ bidingData.bidingNum }}
        </srm-col>
        <!--项目名称-->
        <srm-col :init-col="3">
          <span>{{ $t('bidMod.bidingName') }}</span>{{ bidingData.bidingName }}
        </srm-col>
        <!--发布时间-->
        <srm-col :init-col="3">
          <span>{{ $t('bidMod.releaseDatetime') }}</span>{{ bidingData.releaseDatetime }}
        </srm-col>

        <srm-col :init-col="3">
          <span>{{ $t('bidMod.enrollEndDatetime') }}</span>{{ bidingData.enrollEndDatetime }}
        </srm-col>

        <srm-col :init-col="3">
          <span>{{ $t('bidMod.createdBy2') }}</span>{{ bidingData.createdBy }}
        </srm-col>

        <srm-col :init-col="3">
          <span>{{ $t('bidMod.bidingStartDatetime') }}</span>{{ bidingData.bidingStartDatetime }}
        </srm-col>

        <srm-col :init-col="3">
          <span>{{ $t('bidMod.bidingSite') }}</span>{{ bidingData.bidingSite }}
        </srm-col>

        <srm-col :init-col="3">
          <span>{{ $t('bidMod.budgetAmount') }}</span>{{ bidingData.budgetAmount }}
        </srm-col>
        <srm-col :init-col="3">
          <span>{{ $t('bidMod.bondDesc') }}</span>{{ bidingData.bondDesc }}
        </srm-col>
      </srm-row>
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
        min-width="80"
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
        :formatter="(row, column, cellValue) => $getDictLabel('SCORE_PROGERESS', cellValue)"
      />
      <el-table-column
        align="center"
        prop="scoreStatus"
        :label="$t('common.operation')"
        width="100"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <!-- 评审 or 查看 -->
          <el-button
            type="text"
            @click="openScoringDetailsDialog(scope.row)"
          >
            {{ scope.row.scoreStatus === 'FINISHED' ? $t('common.view') : $t('bidMod.scoreStatus') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--技术评分评审弹窗-->
    <scoring-details-dialog
      :visible.sync="scoringDetailsDialogVisible"
      :edit-row="editRow"
      :biding-id="bidingId"
      :is-read-only="isScoringDetailsReadOnly"
      @scoringSubmitSuccess="getTechProgressReviewDetail"
    />
  </div>
</template>

<script>
/**
 * 技术评分详情
 */
import scoringDetailsDialog from 'lib@/composition/biddingManagement/scoringDetailsDialog'

export default {
  name: 'TechnologyScoreDetail',
  components: {
    scoringDetailsDialog
  },
  data () {
    return {
      bidingData: {
        bidingNum: '',
        bidingName: '',
        releaseDatetime: '',
        bidingScope: '',
        targetType: '',
        enrollEndDatetime: '',
        approvalBy: '',
        createdBy: '',
        bidingStartDatetime: '',
        bidingSite: '',
        budgetAmount: '',
        bondDesc: ''
      },
      bidingId: this.$attrs.params.row.bidingId || '',
      vendorScoreDetailList: [],
      scoringDetailsDialogVisible: false,
      editRow: null,
      isScoringDetailsReadOnly: false
    }
  },
  created () {
    this.getTechProgressReviewDetail()
  },
  methods: {
    /* 查询数据 */
    getTechProgressReviewDetail () {
      if (!this.bidingId) return

      this.$http({
        url: `/api-bid/techProposal/queryTechProgressReviewDetail/${this.bidingId}`,
        method: 'GET',
        loading: true
      }).then(data => {
        if (data && data.data) {
          this.bidingData = data.data.biding
          this.vendorScoreDetailList = data.data.vendorScoreDetailList || []
        }
      })
    },

    /* 获取评审列表 */
    getTechScoreListPageData () {
      if (!this.bidingId) return

      this.$http({
        url: '/api-bid/techScore/listPage',
        method: 'GET',
        params: {
          bidingId: this.bidingId,
          pageNum: 1,
          pageSize: 10
        },
        loading: true
      }).then(data => {
        if (data && data.data) {
          this.vendorScoreDetailList = data.data.list
        }
      })
    },

    /* 获取公告信息 */
    getDetailData () {
      if (!this.bidingId) return

      this.$http({
        url: '/api-bid/projectManagement/projectPublish/getInformation',
        method: 'GET',
        params: { bidingId: this.bidingId },
        loading: true
      }).then(data => {
        if (data && data.data) {
          this.bidingData = {
            ...data.data.biding || {},
            approvalBy: data.data.approvalBy
          }
        }
      })
    },

    /* 打开评分弹窗 */
    openScoringDetailsDialog (row) {
      this.editRow = row
      this.isScoringDetailsReadOnly = row.scoreStatus === 'FINISHED'
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
  overflow-y: auto;
  .technology-score-detail-h2 {
    text-align: center;
    line-height: 32px;
    margin: 0;
    .detail-pbtn {
      float: right;
    }
  }
  .display-form-content {
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
