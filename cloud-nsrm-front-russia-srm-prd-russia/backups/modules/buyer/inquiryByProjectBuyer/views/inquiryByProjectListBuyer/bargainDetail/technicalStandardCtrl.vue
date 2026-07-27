<template>
  <div>
    <srm-row
      :gutter="16"
      class="the_biding_control_row"
      style="margin-top: 5px"
    >
      <!-- 当前需评审供应商 -->
      <srm-col :init-col="3">
        <span class="label-title">{{ $t('bidMod.needToReviewCount') }}</span>
        <el-input
          v-model="needToReviewCount"
          disabled
        />
      </srm-col>

      <!-- 已完成评审的供应商 -->
      <srm-col :init-col="3">
        <span class="label-title">{{ $t('bidMod.alreadyReviewCount') }}</span>
        <el-input
          v-model="alreadyReviewCount"
          disabled
        />
      </srm-col>
    </srm-row>

    <srm-row
      :gutter="16"
      class="the_biding_control_row"
    >
      <!-- 供应商名称 -->
      <srm-col :init-col="3">
        <span class="label-title">{{ $t('common.vendorName') }}</span>
        <div class="the_QuickSearch_wrapper">
          <QuickSearch
            :show-input="preform.vendorName"
            show-key="companyName"
            :scope-data="preform"
            name="scc_sup_company_info"
            @close-quicksearch="setVendorObj"
          />
        </div>
      </srm-col>

      <!-- 技术评标进度 -->
      <srm-col :init-col="3">
        <span class="label-title">{{ $t('bidMod.progressOfTechBid') }}</span>
        <dict-select
          v-model="preform.scoreStatus"
          code="SCORE_PROGERESS"
          clearable
        />
      </srm-col>

      <srm-col
        :init-col="3"
        style="line-height: 32px"
      >
        <!-- 查询 -->
        <el-button
          type="primary"
          :disabled="isDisabledTable"
          class="detail-pbtn"
          @click="getTechProgress"
        >
          {{ $t('common.search') }}
        </el-button>
      </srm-col>
    </srm-row>

    <h3 style="margin: 10px 0 5px">
      {{ $t('bidMod.progressOfTechBid') }}
      <!-- 技术开标 项目状态为投标截止 or 商务评标 -->
      <el-button
        v-if="!isDisabledTable && ['BUSINESS_EVALUATION', 'TENDER_ENDING'].includes(bargainBase.bargainStatus)"
        type="primary"
        class="detail-pbtn"
        @click="openBargain"
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
        align="center"
        type="index"
        width="40"
      />
      <!--供应商名称-->
      <el-table-column
        align="center"
        prop="vendorName"
        :label="$t('bidMod.vendorName')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--报价详情-->
      <el-table-column
        align="center"
        prop="brgDetail"
        :label="$t('bidMod.bidDetail1')"
        min-width="150"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-button
            type="text"
            @click="openTechInfoDialog(scope.row)"
          >
            {{ scope.row.brgDetail }}
          </el-button>
        </template>
      </el-table-column>

      <!--技术评标进度-->
      <el-table-column
        align="center"
        prop="scoreStatus"
        :label="$t('bidMod.progressOfTechBid')"
        width="120"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('SCORE_PROGERESS', cellValue)"
      />

      <!--技术总得分-->
      <el-table-column
        align="center"
        prop="techScore"
        :label="$t('bidMod.techTotalScore')"
        width="100"
        show-overflow-tooltip
      />

      <!--技术标进度-->
      <el-table-column
        align="center"
        :label="$t('bidMod.techBidProgress')"
        width="100"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-button
            type="text"
            @click="openTechEvaluationProgressDialog(scope.row)"
          >
            {{ $t('common.view') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--技术标进度-->
    <tech-evaluation-progress-dialog
      :visible.sync="techEvaluationProgressDialogVisible"
      :bargain-id="scopeBargainId"
      :bargain-base="bargainBase"
      :edit-row="editRow"
    />

    <!--查看技术标详情-->
    <tech-info-dialog
      :visible.sync="techInfoDialogVisible"
      :bargain-id="scopeBargainId"
      :edit-row="editRow"
    />
  </div>
</template>

<script>
import QuickSearch from 'lib@/components/QuickSearch'
import techEvaluationProgressDialog from './technicalStandardCtrl/techEvaluationProgressDialog'
import techInfoDialog from './technicalStandardCtrl/techInfoDialog'

export default {
  name: 'TechnicalStandardCtrl',
  components: {
    QuickSearch,
    techEvaluationProgressDialog,
    techInfoDialog
  },
  props: {
    scopeBargainId: {
      // 招标ID
      type: [Number, String],
      default () {
        return ''
      }
    },
    bargainStatus: {
      // 招标状态
      type: String,
      default: ''
    },
    auditStatus: {
      // 审批状态
      type: String,
      default: ''
    },
    bargainBase: {
      type: Object,
      default () {
        return {}
      }
    }
  },
  data () {
    return {
      progressOfTechBrgList: [],
      techEvaluationProgress: [],
      preform: {
        vendorId: '',
        vendorName: '',
        scoreStatus: ''
      },
      needToReviewCount: '',
      alreadyReviewCount: '',
      techEvaluationProgressDialogVisible: false,
      techInfoDialogVisible: false,
      editRow: null
    }
  },
  computed: {
    isDisabledTable () {
      // 项目状态=='拟定' && 审批状态=='草稿、审批中'
      return this.bargainStatus === 'DRAW_UP' && ['DRAFT', 'SUBMITTED'].includes(this.auditStatus)
    }
  },
  methods: {
    /* 查询数据 */
    getTechProgress () {
      this.$api.brg.inquiryByProject.queryTechProgress({
        bargainId: this.scopeBargainId,
        vendorId: this.preform.vendorId,
        scoreStatus: this.preform.scoreStatus
      }).then(data => {
        if (data && data.data) {
          this.needToReviewCount = data.data.needReviewVendorCount || 0
          this.alreadyReviewCount = data.data.alreadyReviewVendorCount || 0
          this.progressOfTechBrgList = data.data.techProgressList || []
        }
      })
    },

    /* 设置供应商查询对象 */
    setVendorObj (val) {
      const { companyId = '', companyName = '' } = val || {}
      this.preform.vendorId = companyId
      this.preform.vendorName = companyName
    },

    /* 查看技术标详情 */
    openTechInfoDialog (row) {
      this.editRow = row
      this.techInfoDialogVisible = true
    },

    /* 打开技术标进度 */
    openTechEvaluationProgressDialog (row) {
      this.editRow = row
      this.techEvaluationProgressDialogVisible = true
    },

    /* 技术开标 */
    openBargain () {
      this.$confirm('确定发起技术开标吗？', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$api.brg.inquiryByProject.techProposalOpenBrg(this.scopeBargainId).then(() => {
          this.$t('common.successSubmit')
          // 更新节点信息
          this.$emit('fetchParentNodeData')
          // 更新招标基础数据
          this.$emit('fetchBaseInfo')
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.the_biding_control_row {
  :deep(.el-col) {
    display: flex;
    .label-title {
      width: 125px;
      text-align: right;
      line-height: 30px;
      padding-right: 8px;
    }
    .el-input,
    .el-select,
    .the_QuickSearch_wrapper {
      flex: 1;
    }
  }
}
</style>
