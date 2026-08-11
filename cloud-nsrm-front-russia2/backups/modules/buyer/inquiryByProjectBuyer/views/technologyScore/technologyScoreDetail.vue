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
          <span>{{ $t('bidMod.bidingNum') }}</span>{{ bargainData.bargainNum }}
        </srm-col>
        <!--项目名称-->
        <srm-col :init-col="3">
          <span>{{ $t('bidMod.bidingName') }}</span>{{ bargainData.bargainName }}
        </srm-col>
        <!--发布时间-->
        <srm-col :init-col="3">
          <span>{{ $t('bidMod.releaseDatetime') }}</span>{{ bargainData.releaseDatetime }}
        </srm-col>
        <!--投标截止时间-->
        <srm-col :init-col="3">
          <span>{{ $t('bidMod.enrollEndDatetime') }}</span>{{ bargainData.bargainEndDatetime }}
        </srm-col>
        <!--编制人-->
        <srm-col :init-col="3">
          <span>{{ $t('bidMod.createdBy2') }}</span>{{ bargainData.createdUserName }}
        </srm-col>
        <!--投标开始时间-->
        <srm-col :init-col="3">
          <span>{{ $t('bidMod.bidingStartDatetime') }}</span>{{ bargainData.bargainStartDatetime }}
        </srm-col>
        <!--预计投标地点-->
        <srm-col :init-col="3">
          <span>{{ $t('bidMod.bidingSite') }}</span>{{ bargainData.bargainSite }}
        </srm-col>
        <!--预算金额-->
        <srm-col :init-col="3">
          <span>{{ $t('bidMod.budgetAmount') }}</span>{{ bargainData.budgetAmount }}
        </srm-col>
        <!--其他说明-->
        <srm-col :init-col="3">
          <span>{{ $t('bidMod.bondDesc') }}</span>{{ bargainData.bondDesc }}
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
        :formatter="(row, column, cellValue) => $getDictLabel('SCORE_PROGERESS', cellValue)"
      />
      <el-table-column
        align="center"
        prop="scoreStatus"
        :label="$t('common.operation')"
        width="80"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <!-- 评审 or 查看 -->
          <el-button
            type="primary"
            class="el-button-icon"
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
      :bargain-id="bargainId"
      :is-read-only="isScoringDetailsReadOnly"
      @scoringSubmitSuccess="getTechProgressReviewDetail"
    />
  </div>
</template>

<script>
/**
 * 技术评分详情
 */
import scoringDetailsDialog from 'lib@/composition/bargain/scoringDetailsDialog'

export default {
  name: 'TechnologyScoreDetail',
  components: {
    scoringDetailsDialog
  },
  data () {
    return {
      bargainData: {
        bargainNum: '',
        bargainName: '',
        releaseDatetime: '',
        bargainScope: '',
        targetType: '',
        enrollEndDatetime: '',
        approvalBy: '',
        createdBy: '',
        bargainStartDatetime: '',
        bargainSite: '',
        budgetAmount: '',
        bondDesc: ''
      },
      bargainId: this.$attrs.params.row.bargainId || '',
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
      if (!this.bargainId) return

      this.$api.brg.inquiryByProject.queryTechProgressReviewDetail(this.bargainId).then(data => {
        if (data && data.data) {
          this.bargainData = data.data.bargain
          this.vendorScoreDetailList = data.data.vendorScoreDetailList || []
        }
      })
    },

    /* 获取评审列表 */
    getTechScoreListPageData () {
      if (!this.bargainId) return

      this.$api.brg.inquiryByProject.techScoreList({
        bargainId: this.bargainId,
        pageNum: 1,
        pageSize: 10
      }).then(data => {
        if (data && data.data) {
          this.vendorScoreDetailList = data.data.list
        }
      })
    },

    /* 获取公告信息 */
    getDetailData () {
      if (!this.bargainId) return
      this.$api.brg.inquiryByProject.getInformation({ bargainId: this.bargainId }).then(data => {
        if (data && data.data) {
          this.bargainData = {
            ...data.data.bargain || {},
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
  overflow: auto;
  .technology-score-detail-h2 {
    text-align: center;
    line-height: 32px;
    margin: 0;
    .detail-pbtn {
      float: right;
    }
  }
  .display-form-content {
    :deep(.el-row){
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
