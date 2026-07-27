<template>
  <el-container class="the-inquiryTrackingDetail-detail" direction="vertical">
    <el-main>
      <MainHeader>
        <template #right>
          <!--b 立即开始 已发布状态-->
          <el-button
            v-if="inquiryTrackingDetailData.header.status === 'PUBLISHED'"
            type="primary"
            @click="startNow"
          >
            立即开始
          </el-button>

          <!--b 立即结束-->
          <el-button v-if="!isDeadline" type="primary" @click="changeDeadline">
            立即结束
          </el-button>

          <!--b 修改报价时间 结束之后不允许修改-->
          <EditQuoteTime
            v-if="!isDeadline"
            :inquiry-id="tabRow.inquiryId"
            :begin-quote="inquiryTrackingDetailData.header.beginQuote || ''"
            @changeDeadlineSuccess="getQuoteSelectionDetail"
          />

          <!--b 刷新-->
          <el-button type="primary" @click="getQuoteSelectionDetail">
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
          <el-step
            :title="$t('bidMod.inQstatus2')"
            :description="inquiryTrackingDetailData.header.publishDate"
          />
          <el-step
            :title="$t('bidMod.inQstatus3')"
            :description="inquiryTrackingDetailData.header.beginQuote"
          />
          <el-step
            :title="$t('bidMod.inQstatus4')"
            :description="inquiryTrackingDetailData.header.deadline"
          />
          <el-step title="评选中" />
          <el-step :title="$t('bidMod.inQstatus6')" />
        </el-steps>
      </div>

      <!--报价截止倒计时-->
      <div class="cur-quote-deadline">
        <DynamicCutoffTime
          :label="$t('bidMod.inDeadlineInfo') + ':'"
          :deadline-time="inquiryTrackingDetailData.header.deadline || ''"
          @isDeadline="val => isDeadline = val"
        />
      </div>

      <!--详细信息区域-->
      <InfoRow :info-row-list="inquiryTrackingInfoRow" />

      <el-collapse v-model="activeCollapse" class="tab-form-style">
        <!--报价跟踪-->
        <el-collapse-item :title="$t('bidMod.quoteTrack')" name="1">
          <InquiryTracking
            :inquiry-tracking-data="inquiryTrackingDetailData"
            @updateDetailData="getQuoteSelectionDetail"
          />
        </el-collapse-item>

        <!--评选-->
        <el-collapse-item
          v-if="['CLOSE_QUOTATION', 'BEING_SELECTED', 'FIXED_PRICE', 'FIXING_PRICE', 'FIX_PRICE_REJECT'].includes(inquiryTrackingDetailData.header.status)"
          :title="$t('bidMod.quoteEvaluation')"
          name="2"
        >
          <QuoteSelection
            :inquiry-tracking-data="inquiryTrackingDetailData"
            @updateDetailData="getQuoteSelectionDetail"
            @tab-add="tab => $emit('tab-add', tab)"
          />
        </el-collapse-item>
      </el-collapse>
    </el-main>
  </el-container>
</template>

<script>
import { tabTodoMixin } from '@/utils/mixins'
import { judgeListRepeatValueWarnTag } from 'lib@/composition/origin/composition'
import MainHeader from 'lib@/components/Table/MainHeader'
import DynamicCutoffTime from 'lib@/components/dynamic-cutoff-time'
import InquiryTracking from './inquiryTrackingDetail/inquiryTracking'
import EditQuoteTime from './inquiryTrackingDetail/editQuoteTime'
import InfoRow from './inquiryTrackingDetail/infoRow'
import QuoteSelection from './inquiryTrackingDetail/quoteSelection'

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
      inquiryTrackingDetailData: {
        header: {},
        currentRoundTotalCtn: '',
        currentRoundQuotedCtn: '',
        trackingList: []
      },
      // 是否已截止
      isDeadline: false
    }
  },

  computed: {
    inquiryTrackingInfoRow () {
      const header = this.inquiryTrackingDetailData.header
      return [
        // 询价标题
        { label: this.$t('bidMod.inquiryTitle'), value: header.inquiryTitle },
        // 询价单号
        { label: this.$t('bidMod.inquiryNo'), value: header.inquiryNo },
        // 询价单状态
        { label: this.$t('bidMod.inQstatus'), value: this.$getDictLabel('RFQ_STATUS', header.status) },
        // 审核状态
        { label: this.$t('bidMod.auditStatus'), value: this.$getDictLabel('APPROVE_STATUS', header.auditStatus) },
        // 评分规则
        { label: this.$t('bidMod.inquiryRule'), value: this.$getDictLabel('RFQ_SCORE_RULE', header.inquiryRule) },
        // 报价方式
        { label: this.$t(('bidMod.quoteRule')), value: this.$getDictLabel('RFQ_QUOTE_TYPE', header.quoteRule) }
      ]
    },

    // 进度状态
    activeNumber () {
      // 默认在第2个节点, 已发布，报价开始中
      const STATUS_ENUM = {
        // 已发布
        PUBLISHED: 1,
        // 接受报价中
        RECEI_QUOTATION: 2,
        // 已截止报价
        CLOSE_QUOTATION: 3,
        // 评选中
        BEING_SELECTED: 3,
        // 定价中
        FIXING_PRICE: 3,
        // 已定价
        FIXED_PRICE: 5
      }
      return STATUS_ENUM[this.inquiryTrackingDetailData.header.status] || 1
    }
  },

  created () {
    if (this.tabFlag === 'edit') {
      this.getQuoteSelectionDetail()
    }
  },

  methods: {
    /* 查询详情 */
    getQuoteSelectionDetail () {
      this.$api.inq.inquiryBySimple.getQuoteSelectionDetail(this.tabRow.inquiryId).then(data => {
        if (data && data.data) {
          const responseData = data.data
          this.inquiryTrackingDetailData = {
            header: data.data.header || {},
            currentRoundTotalCtn: responseData.currentRoundTotalCtn,
            currentRoundQuotedCtn: responseData.currentRoundQuotedCtn,
            trackingList: judgeListRepeatValueWarnTag(responseData.trackingList, 'quotationIp', 'ipWarn')
          }
        }
      })
    },

    /* 立即开始 */
    startNow () {
      this.$api.inq.inquiryBySimple.changeBeginQuote({
        // 询价单ID
        inquiryId: this.tabRow.inquiryId,
        // 是否立即开始(Y/N)
        shouldBeginQuoteNow: 'Y'
      }).then(() => {
        this.getQuoteSelectionDetail()
      })
    },

    /* 立即结束 / 修改报价截止时间 */
    changeDeadline () {
      this.$api.inq.inquiryBySimple.changeDeadline({
        // 询价单ID
        inquiryId: this.tabRow.inquiryId,
        // 是否立即开始(Y/N)
        shouldDeadlineNow: 'Y'
      }).then(() => {
        this.getQuoteSelectionDetail()
      })
    },

    /* 返回 */
    backTo () {
      if (this.tabFlag === 'edit') {
        this.$emit('tab-remove', `inquiryTrackingDetail${this.tabRow.inquiryNo}`)
      } else {
        this.$emit('tab-remove', 'inquiryTrackingDetail')
      }
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
