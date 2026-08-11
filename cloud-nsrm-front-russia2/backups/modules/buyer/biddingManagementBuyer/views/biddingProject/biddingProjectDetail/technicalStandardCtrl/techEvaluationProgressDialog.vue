<template>
  <srm-dialog
    :title="$t('bidMod.techEvaluationProgress')"
    size="large"
    :visible.sync=" dialogVisible"
    :close-on-click-modal="false"
    append-to-body
  >
    <el-table
      :data="techEvaluationProgress"
      style="width: 100%"
      border
      height="180"
      highlight-current-row
    >
      <el-table-column
        align="center"
        type="index"
        width="40"
      />

      <!--评委-->
      <el-table-column
        align="center"
        prop="fullName"
        :label="$t('bidMod.fullName2')"
        min-width="120"
        show-overflow-tooltip
      />

      <!--岗位-->
      <el-table-column
        align="center"
        prop="position"
        :label="$t('bidMod.position')"
        min-width="120"
        show-overflow-tooltip
      />

      <!--电话-->
      <el-table-column
        align="center"
        prop="phone"
        :label="$t('bidMod.phone')"
        min-width="120"
        show-overflow-tooltip
      />

      <!--电子邮箱-->
      <el-table-column
        align="center"
        prop="email"
        :label="$t('bidMod.email')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--总分值-->
      <el-table-column
        align="center"
        prop="totalScore"
        :label="$t('bidMod.totalScore')"
        min-width="100"
        show-overflow-tooltip
      />

      <!--是否代理报价-->
      <el-table-column
        align="center"
        prop="isProxy"
        :label="$t('bid_mod.isProxyBidding')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, value) => $getDictLabel('YES_OR_NO', value)"
      />

      <!--技术评分进度-->
      <el-table-column
        align="center"
        prop="scoreStatus"
        :label="$t('bidMod.techEvaluationProgress')"
        width="120"
        show-overflow-tooltip
        :formatter="(row, column, value) => $getDictLabel('OVERALL_PROGRESS', value)"
      />

      <!--评分明细-->
      <el-table-column
        align="center"
        prop="groupId"
        :label="$t('bidMod.techScoreHeadId')"
        width="100"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-button
            type="text"
            @click="openScoringDetailsDialog(scope.row)"
          >
            {{ scope.row.groupId ? $t('common.view') : '' }}
          </el-button>
        </template>
      </el-table-column>

      <el-table-column
        align="center"
        prop="opration"
        :label="$t('bid_mod.operation')"
        width="140"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <!--代理评分-->
          <el-button
            v-if="scope.row.scoreStatus !== 'FINISHED'"
            type="text"
            @click="proxyScore(scope.row)"
          >
            {{ $t('bid_mod.proxyScore') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div
      slot="footer"
      class="dialog-footer"
    >
      <el-button @click="dialogVisible = false">
        {{ $t('common.close') }}
      </el-button>
    </div>

    <!--评分弹框-->
    <scoring-details-dialog
      :visible.sync="scoringDetailsDialogVisible"
      :biding-id="bidingId"
      :vendor-id="(editRow || {}).vendorId"
      :edit-row="currentReviewRow"
      :is-read-only="isReadOnlyScoringDetails"
      is-proxy-score
      @scoringSubmitSuccess="getProgressOfTechScoreList"
    />
  </srm-dialog>
</template>

<script>
/**
 * 技术标进度
 */
import scoringDetailsDialog from 'lib@/composition/biddingManagement/scoringDetailsDialog'

export default {
  name: 'TechEvaluationProgress',
  components: {
    scoringDetailsDialog
  },
  props: {
    visible: {
      type: Boolean
    },
    bidingId: {
      type: [Number, String]
    },
    bidingBase: {
      type: Object,
      default () {
        return {}
      }
    },
    editRow: {
      type: Object
    }
  },
  data () {
    return {
      currentUserName: this.$store.getters.userInfo.username,
      techEvaluationProgress: [],
      currentReviewRow: null,
      scoringDetailsDialogVisible: false,
      techScoreFormVisible: false,
      vendorInfoForm: {
        address: '',
        companyName: '',
        companyCreationDate: '',
        overseasRelationName: '',
        registeredCapital: '',
        companyType: '',
        legalPerson: '',
        businessStartDate: '',
        businessEndDate: '',
        businessScope: ''
      },
      isDisplayReview: true,
      isReadOnlyScoringDetails: true
    }
  },
  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },
  watch: {
    dialogVisible: {
      handler (newVal) {
        if (newVal) {
          this.getProgressOfTechScoreList()
        }
      },
      immediate: true
    }
  },
  methods: {
    /* 查询数据 */
    getProgressOfTechScoreList () {
      this.$http({
        url: '/api-bid/techProposal/queryTechProgressInfo',
        method: 'GET',
        params: {
          bidingId: this.bidingId,
          vendorId: this.editRow.vendorId
        },
        loading: true
      }).then(data => {
        this.techEvaluationProgress = data.data
      })
    },

    /* 打开评分明细 */
    openScoringDetailsDialog (row) {
      this.currentReviewRow = {
        ...row,
        vendorId: this.editRow.vendorId
      }
      this.isReadOnlyScoringDetails = true
      this.scoringDetailsDialogVisible = true
    },

    /* 代理评分 */
    proxyScore (row) {
      this.currentReviewRow = {
        ...row,
        vendorId: this.editRow.vendorId
      }
      this.isReadOnlyScoringDetails = false
      this.scoringDetailsDialogVisible = true
    }
  }
}
</script>
