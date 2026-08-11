<template>
  <SrmDialog
    :title="$t('bidMod.techEvaluationProgress')"
    size="large"
    :visible.sync="dialogVisible"
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
        type="index"
        :label="$t('common.sort')"
        width="50"
      />

      <!--评委-->
      <el-table-column
        prop="fullName"
        :label="$t('bidMod.fullName2')"
        min-width="120"
        show-overflow-tooltip
      />

      <!--岗位-->
      <el-table-column
        prop="position"
        :label="$t('bidMod.position')"
        min-width="120"
        show-overflow-tooltip
      />

      <!--电话-->
      <el-table-column
        prop="phone"
        :label="$t('bidMod.phone')"
        min-width="120"
        show-overflow-tooltip
      />

      <!--电子邮箱-->
      <el-table-column
        prop="email"
        :label="$t('bidMod.email')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--总分值-->
      <el-table-column
        prop="totalScore"
        :label="$t('bidMod.totalScore')"
        min-width="100"
        show-overflow-tooltip
      />

      <!--是否代理评分-->
      <el-table-column
        prop="isProxy"
        :label="$t('bid_mod.isProxyScore')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('YES_OR_NO', cellValue)"
      />

      <!--技术评分进度-->
      <el-table-column
        prop="scoreStatus"
        :label="$t('bidMod.techEvaluationProgress')"
        width="120"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('SOU_TECH_SCORE_STATUS', cellValue)"
      />

      <!--评分明细-->
      <el-table-column
        prop="groupId"
        :label="$t('bidMod.techScoreHeadId')"
        width="100"
        show-overflow-tooltip
      >
        <template v-slot="{ row }">
          <el-button type="text" @click="openScoringDetailsDialog(row)">
            {{ row.groupId ? $t('common.view') : '' }}
          </el-button>
        </template>
      </el-table-column>

      <el-table-column
        :label="$t('bid_mod.operation')"
        width="140"
        show-overflow-tooltip
      >
        <template v-slot="{ row }">
          <!--代理评分-->
          <el-button
            v-if="row.scoreStatus !== SOU_TECH_SCORE_STATUS_ENUM.FINISHED"
            type="text"
            @click="proxyScore(row)"
          >
            {{ $t('bid_mod.proxyScore') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.close') }}
      </el-button>
    </div>

    <!--评分弹框-->
    <ScoringDetailsDialog
      v-if="scoringDetailsDialogVisible"
      :visible.sync="scoringDetailsDialogVisible"
      :project-id="projectId"
      :vendor-id="(editRow || {}).vendorId"
      :edit-row="currentReviewRow"
      :readonly="isReadOnlyScoringDetails"
      is-proxy-score
      @success="getTechProgressInfo"
    />
  </SrmDialog>
</template>

<script>
/**
 * 技术标进度
 */
import { bidBuyerHttp } from 'modb@/bidding/api'
import { SOU_TECH_SCORE_STATUS_ENUM } from 'lib@/composition/origin/enum'
import ScoringDetailsDialog from 'lib@/composition/biddingLts/scoringDetailsDialog'

export default {
  name: 'TechEvaluationProgressDialog',

  components: { ScoringDetailsDialog },

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    projectId: {
      type: [Number, String],
      required: true
    },
    biddingBase: {
      type: Object,
      default: () => ({})
    },
    editRow: {
      type: Object,
      default: () => ({})
    }
  },

  data () {
    return {
      techEvaluationProgress: [],
      currentReviewRow: null,
      scoringDetailsDialogVisible: false,
      isReadOnlyScoringDetails: true,
      SOU_TECH_SCORE_STATUS_ENUM
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

  mounted () {
    this.getTechProgressInfo()
  },

  methods: {
    /* 查询数据 */
    async getTechProgressInfo () {
      const response = await bidBuyerHttp.tech.techProgressInfo({
        projectId: this.projectId,
        vendorId: this.editRow.vendorId
      })
      if (response) {
        this.techEvaluationProgress = response.data || []
      }
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
