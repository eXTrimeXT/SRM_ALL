<template>
  <el-container class="the-inquiryTrackingDetail-detail" direction="vertical">
    <el-main>
      <MainHeader>
        <template #right>
          <!--b 立即开始 报价未开始-->
          <el-button
            v-if="header.extProjectStatus === EXT_INQ_SOU_PROJECT_STATUS_ENMU.ORDER_NOT_START"
            type="primary"
            @click="startNow"
          >
            {{ $t('bidMod.startNow') }}
          </el-button>

          <!--b 立即结束-->
          <el-button
            v-if="!isDeadline"
            type="primary"
            @click="changeDeadline"
          >
            {{ $t('bidMod.endNow') }}
          </el-button>

          <!--b 修改报价时间 结束之后不允许修改-->
          <EditQuoteTime
            v-if="!isDeadline"
            :project-id="tabRow.projectId"
            :order-start-time="header.orderStartTime || ''"
            @success="getQuoteSelectionDetail"
          />

          <!--b 刷新-->
          <el-button type="primary" @click="refresh">
            {{ $t("common.refresh") }}
          </el-button>

          <!--返回-->
          <el-button @click="backTo">
            {{ $t("common.backTo") }}
          </el-button>
        </template>
      </MainHeader>

      <div class="the_progress form-container">
        <el-steps :active="activeNumber" finish-status="success">
          <el-step :title="$t('bidMod.inQstatus2')" :description="$parseTime(header.publishDate)" />
          <el-step :title="$t('bidMod.inQstatus3')" :description="$parseTime(header.orderStartTime)" />
          <el-step :title="$t('bidMod.inQstatus4')" :description="$parseTime(header.orderEndTime)" />
          <el-step :title="$t('bidMod.pingxuan')" />
          <el-step :title="$t('bidMod.inQstatus6')" />
        </el-steps>
      </div>

      <!--报价截止倒计时-->
      <div class="cur-quote-deadline">
        <DynamicCutoffTime
          :label="$t('bidMod.inDeadlineInfo') + ':'"
          :deadline-time="header.orderEndTime || ''"
          @isDeadline="val => isDeadline = val"
        />
      </div>

      <!--详细信息区域-->
      <InfoRow :info-row-list="inquiryTrackingInfoRow" />

      <el-collapse v-model="activeCollapse" class="tab-form-style">
        <!--报价跟踪-->
        <el-collapse-item :title="$t('bidMod.quoteTrack')" name="1">
          <InquiryTracking
            :header="header"
            :tracking-list="trackingList"
            :current-round-quoted-ctn="currentRoundQuotedCtn"
            :current-round-total-ctn="currentRoundTotalCtn"
            @refresh="refresh"
          />
        </el-collapse-item>

        <!--评选 已截止报价、评选中、定价中、已定价、定价驳回-->
        <el-collapse-item
          name="2"
          :title="$t('bidMod.quoteEvaluation')"
        >
          <QuoteSelection
            ref="quoteSelection"
            :header="header"
            :show-quote-evaluation="showQuoteEvaluation"
            @backTo="backTo"
          />
        </el-collapse-item>
      </el-collapse>
    </el-main>
  </el-container>
</template>

<script>
import { inqBuyerHttp } from 'modcb@/inquiry/api'
import { tabTodoMixin } from '@/utils/mixins'
import { judgeListRepeatValueWarnTag } from 'lib@/composition/origin/composition'
import { EXT_INQ_SOU_PROJECT_STATUS_ENMU } from 'lib@/composition/origin/extEnum'
import MainHeader from 'lib@/components/Table/MainHeader.vue'
import DynamicCutoffTime from 'lib@/components/dynamic-cutoff-time'
import InquiryTracking from './inquiryTrackingDetail/inquiryTracking.vue'
import EditQuoteTime from './inquiryTrackingDetail/editQuoteTime.vue'
import InfoRow from './inquiryTrackingDetail/infoRow.vue'
import QuoteSelection from './inquiryTrackingDetail/quoteSelection.vue'

export default {
  name: 'InquiryTrackingDetail',

  components: {
    MainHeader,
    DynamicCutoffTime,
    InquiryTracking,
    EditQuoteTime,
    InfoRow,
    QuoteSelection
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      tabRow: this.$attrs.params.row,
      tabFlag: this.$attrs.params.flag,
      editableTabsValue: 'inquiryTrackingTab',
      activeCollapse: ['1', '2'],
      header: {},
      currentRoundTotalCtn: '',
      currentRoundQuotedCtn: '',
      trackingList: [],
      // 是否已截止
      isDeadline: false,
      EXT_INQ_SOU_PROJECT_STATUS_ENMU
    }
  },

  computed: {
    inquiryTrackingInfoRow () {
      return [
        // 询价单号
        { label: this.$t('bidMod.inquiryNo'), value: this.header.souNo },
        // 询价单状态
        { label: this.$t('bidMod.inQstatus'), value: this.$getDictLabel('EXT_INQ_SOU_PROJECT_STATUS', this.header.extProjectStatus) },
        { label: this.$t('cusEntry.bidMod.createdBy'), value: this.header.createdBy },
        { label: this.$t('cusEntry.bidMod.purchaser'), value: this.header.linkman },
        { label: this.$t('cusEntry.bidMod.applyDepartment'), value: this.header.extDepartmentName },
        // 审核状态
        { label: this.$t('bidMod.auditStatus'), value: this.$getDictLabel('SOU_APPROVAL_STATUS', this.header.createApprovalStatus) }
      ]
    },

    // 进度状态
    activeNumber () {
      // 默认在第2个节点, 已发布，报价开始中
      const STATUS_ENUM = {
        // 接受报价中
        [EXT_INQ_SOU_PROJECT_STATUS_ENMU.ACCEPT_ORDER]: 2,
        // 已截止报价
        [EXT_INQ_SOU_PROJECT_STATUS_ENMU.ORDER_END]: 3,
        // 评选中
        [EXT_INQ_SOU_PROJECT_STATUS_ENMU.EVALUATING]: 3,
        // 定价中
        [EXT_INQ_SOU_PROJECT_STATUS_ENMU.PRICING]: 3,
        // 已定价
        [EXT_INQ_SOU_PROJECT_STATUS_ENMU.PRICE_END]: 5
      }
      return STATUS_ENUM[this.header.extProjectStatus] || 1
    },

    showQuoteEvaluation () {
      return [
        EXT_INQ_SOU_PROJECT_STATUS_ENMU.ORDER_END,
        EXT_INQ_SOU_PROJECT_STATUS_ENMU.EVALUATING,
        EXT_INQ_SOU_PROJECT_STATUS_ENMU.PRICING,
        EXT_INQ_SOU_PROJECT_STATUS_ENMU.PRICE_REJECT
      ].includes(this.header.extProjectStatus)
    }
  },

  created () {
    if (this.tabFlag === 'edit') {
      this.getQuoteSelectionDetail()
    }
  },

  methods: {
    /* 刷新 */
    refresh () {
      this.getQuoteSelectionDetail()

      if (this.showQuoteEvaluation && this.$refs.quoteSelection) {
        this.$refs.quoteSelection.getSelectList()
      }
    },

    /* 查询详情 */
    async getQuoteSelectionDetail () {
      const response = await inqBuyerHttp.select.getManagementDetail(this.tabRow.projectId)
      if (response) {
        const {
          currentRoundTotalCtn,
          currentRoundQuotedCtn,
          header = {},
          trackingList = []
        } = response.data
        this.header = header
        this.currentRoundTotalCtn = header.currentRound
        this.currentRoundQuotedCtn = currentRoundQuotedCtn
        // 判断并标记是否存在相同IP的供应商
        this.trackingList = judgeListRepeatValueWarnTag(trackingList, 'submitByIp', 'ipWarn').map(item => {
          const {
            orderFileList2 = []
          } = item
          const {
            orderFileName,
            orderDocId
          } = orderFileList2[0] || {}
          return {
            orderFileName,
            orderDocId,
            ...item
          }
        })
      }
    },

    /* 立即开始 */
    async startNow () {
      const response = await inqBuyerHttp.select.changeBeginQuote({
        // 询价单ID
        projectId: this.tabRow.projectId,
        // 是否立即开始(Y/N)
        startNow: 'Y'
      })
      if (response) {
        this.$message.success(this.$t('bidMod.changeQuoteTime'))
        await this.getQuoteSelectionDetail()
      }
    },

    /* 立即结束 */
    async changeDeadline () {
      const response = await inqBuyerHttp.select.changeDeadline({
        // 询价单ID
        projectId: this.tabRow.projectId,
        // 是否立即结束(Y/N)
        endNow: 'Y'
      })
      if (response) {
        this.$message.success(this.$t('bidMod.endQuoteTime'))
        await this.getQuoteSelectionDetail()
        await this.$refs.quoteSelection.getSelectList()
      }
    },

    /* 返回 */
    backTo () {
      this.$emit('tab-remove', this.$attrs.params.tabName)
      this.__setTabTodo('InquiryList.getQueryData')
    }
  }
}
</script>

<style scoped lang="scss">
.the-inquiryTrackingDetail-detail {
  .cur-quote-deadline {
    padding-left: 16px;
  }
  .sub_header {
    padding: 4px 11px;
    background: #eee;
  }
  .el-table .el-date-editor {
    width: 135px;
  }
  .the_top_header {
    border-bottom: 0;
    padding-top: 10px;
    padding-bottom: 10px;
    margin-top: 10px;
    p > span {
      padding-left: 11px;
    }
  }
  .the_progress {
    width: 100%;
    height: 100px;
    margin-top: 10px;
    border: none;
    //background: #eee;
    .el-steps {
      padding-bottom: 0;
    }
  }
  .the_baojia_dialog > div > .el-dialog__body {
    padding: 0 20px !important;
  }
  .the_continue_dialog {
    position: relative;
  }
}
</style>
