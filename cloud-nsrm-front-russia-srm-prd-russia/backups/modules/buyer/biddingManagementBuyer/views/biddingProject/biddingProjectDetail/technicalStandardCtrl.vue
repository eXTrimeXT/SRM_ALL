<template>
  <div>
    <srm-row
      class="the_biding_control_row"
      style="margin-top: 5px"
    >
      <!-- 当前需评审供应商 -->
      <srm-col :init-col="3">
        <span class="labelTitle">{{ $t('bidMod.needToReviewCount') }}</span>
        <el-input
          v-model="needToReviewCount"
          disabled
        />
      </srm-col>

      <!-- 已完成评审的供应商 -->
      <srm-col :init-col="3">
        <span class="labelTitle">{{ $t('bidMod.alreadyReviewCount') }}</span>
        <el-input
          v-model="alreadyReviewCount"
          disabled
        />
      </srm-col>
    </srm-row>

    <srm-row class="the_biding_control_row">
      <!-- 供应商名称 -->
      <srm-col :init-col="3">
        <span class="labelTitle">{{ $t('common.vendorName') }}</span>
        <div class="the_QuickSearch_wrapper">
          <quick-search
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
        <span class="labelTitle">{{ $t('bidMod.progressOfTechBid') }}</span>
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
          :disabled="isdisabledTable"
          class="detail-pbtn"
          @click="getTechProgress"
        >
          {{ $t('common.search') }}
        </el-button>
      </srm-col>
    </srm-row>

    <h3 style="margin: 10px 0 5px">
      {{ $t('bidMod.progressOfTechBidList') }}
      <!-- 技术开标 项目状态为投标截止 or 商务评标 -->
      <el-button
        v-if="!isdisabledTable && ['BUSINESS_EVALUATION', 'TENDER_ENDING'].includes(bidingBase.bidingStatus)"
        type="primary"

        class="detail-pbtn"
        @click="openBiding"
      >
        {{ $t('bidMod.techopenBiding') }}
      </el-button>
    </h3>

    <el-table
      :data="progressOfTechBidList"
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

      <!--投标详情-->
      <el-table-column
        align="center"
        prop="bidDetail"
        :label="$t('bidMod.bidDetail')"
        min-width="150"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-button
            type="text"
            @click="openTechInfoDialog(scope.row)"
          >
            {{ scope.row.bidDetail }}
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
      :biding-id="scopeBidingId"
      :biding-base="bidingBase"
      :edit-row="editRow"
    />

    <!--查看技术标详情-->
    <tech-info-dialog
      :visible.sync="techInfoDialogVisible"
      :biding-id="scopeBidingId"
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
    scopeBidingId: {
      // 招标ID
      type: [Number, String],
      default () {
        return ''
      }
    },
    bidingStatus: {
      // 招标状态
      type: String,
      default: ''
    },
    auditStatus: {
      // 审批状态
      type: String,
      default: ''
    },
    bidingBase: {
      type: Object,
      default () {
        return {}
      }
    }
  },
  data () {
    return {
      progressOfTechBidList: [],
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
    isdisabledTable () {
      // 项目状态=='拟定' && 审批状态=='草稿、审批中'
      return this.bidingStatus === 'DRAW_UP' && ['DRAFT', 'SUBMITTED'].includes(this.auditStatus)
    }
  },
  methods: {
    /* 查询数据 */
    getTechProgress () {
      this.$http({
        url: '/api-bid/techProposal/queryTechProgress',
        method: 'POST',
        data: {
          bidingId: this.scopeBidingId,
          vendorId: this.preform.vendorId,
          scoreStatus: this.preform.scoreStatus
        },
        loading: true
      }).then(data => {
        if (data && data.data) {
          this.needToReviewCount = data.data.needReviewVendorCount || 0
          this.alreadyReviewCount = data.data.alreadyReviewVendorCount || 0
          this.progressOfTechBidList = data.data.techProgressList || []
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
    openBiding () {
      this.$confirm('确定发起技术开标吗？', {
        confirmButtonText: this.$t('common.confirm'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'warning'
      }).then(() => {
        this.$http({
          url: `/api-bid/techProposal/openBid/${this.scopeBidingId}`,
          method: 'GET',
          loading: true
        }).then(() => {
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
