<template>
  <div class="inquiry-tracking-tab">
    <div class="inquiry-tracking-top">
      <div class="inquiry-tracking-top-form">
        <SrmRow>
          <SrmCol :init-col="2">
            <!--轮次 -->
            <span class="label">{{ $t('cusEntry.bidMod.round') }}:</span>
            <el-input
              v-model="currentRoundTotalCtn"
              type="text"
              disabled
              class="value-input"
            />
          </SrmCol>
        </SrmRow>
      </div>
    </div>

    <el-table
      :data="trackingList"
      style="width: 100%; margin-top: 15px;"
      border
      height="200px"
    >
      <el-table-column align="center" type="index" width="50" />

      <!--t 供应商编码-->
      <el-table-column
        align="center"
        prop="vendorCode"
        :label="$t('bidMod.vendorCode')"
        width="120"
        show-overflow-tooltip
      />

      <!--t 供应商名称-->
      <el-table-column
        align="center"
        prop="vendorName"
        :label="$t('bidMod.vendorName')"
        min-width="150"
        show-overflow-tooltip
      />
      <!--t 报价状态-->
      <el-table-column
        align="center"
        prop="orderStatus"
        :label="$t('bidMod.quoteStatus')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('INQ_SOU_ORDER_STATUS', cellValue)"
      />

      <!--t 报价人-->
      <el-table-column
        align="center"
        :label="$t('bidMod.quoteMan')"
        min-width="120"
        show-overflow-tooltip
        :formatter="row => `${row.extOrderByNickname }-${row.extOrderPhone}`"
      />

      <!--t 报价时间-->
      <el-table-column
        align="center"
        prop="submitTime"
        :label="$t('bidMod.quotedTime')"
        width="150"
        :formatter="(row, column, cellValue) => $parseTime(cellValue)"
        show-overflow-tooltip
      />

      <!--t 供应商IP-->
      <el-table-column
        align="center"
        prop="submitByIp"
        :label="$t('bidMod.vendorIp')"
        width="100"
        show-overflow-tooltip
      >
        <template v-slot="{ row }">
          <span :style="{ color: row.ipWarn ? 'red' : '' }">{{ row.submitByIp }}</span>
        </template>
      </el-table-column>
      <!--附件名称-->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          label: $t('cusEntry.bidMod.file'),
          prop: 'orderDocId',
          nameProp: 'orderFileName'
        }"
        readonly
      />
    </el-table>

    <!--设定目标价/目标价查看 弹窗-->
    <TargetPriceDetailDialog
      v-if="targetPriceDetailDialogVisible"
      :visible.sync="targetPriceDetailDialogVisible"
      :project-id="header.projectId"
      :status="targetPriceStatus"
      @success="emitParentGetDetailData"
    />

    <!--报价详情 弹窗-->
    <QuoteDetailDialog
      v-if="quoteDetailDialogVisible"
      :visible.sync="quoteDetailDialogVisible"
      :quote-row="quoteRow"
      :header="header"
    />

    <!--代理报价-->
    <ProxyQuoteDialog
      v-if="proxyQuoteDialogVisible"
      :visible.sync="proxyQuoteDialogVisible"
      :proxy-quote-params="proxyQuoteParams"
      @success="emitParentGetDetailData"
    />
  </div>
</template>

<script>
/**
 * 报价跟踪
 */
import { inqBuyerHttp } from 'modb@/inquiry/api'
import {
  SOU_ORDER_STATUS_ENUM,
  EXT_INQ_SOU_PROJECT_STATUS_ENMU,
  SOU_PUBLISH_SCOPE_ENUM
} from 'lib@/composition/origin/extEnum'
import TargetPriceDetailDialog from './inquiryTrackingTab/targetPriceDetailDialog.vue'
import QuoteDetailDialog from './inquiryTrackingTab/quoteDetailDialog.vue'
import ProxyQuoteDialog from './inquiryTrackingTab/proxyQuoteDialog.vue'

export default {
  name: 'InquiryTrackingTab',

  components: {
    TargetPriceDetailDialog,
    QuoteDetailDialog,
    ProxyQuoteDialog
  },

  props: {
    header: {
      type: Object,
      required: true
    },
    currentRoundTotalCtn: {
      type: [String, Number],
      required: true
    },
    currentRoundQuotedCtn: {
      type: [String, Number],
      required: true
    },
    trackingList: {
      type: Array,
      required: true
    }
  },

  data () {
    return {
      targetPriceDetailDialogVisible: false,
      quoteRow: null,
      quoteDetailDialogVisible: false,
      proxyQuoteDialogVisible: false,
      proxyQuoteParams: {
        visible: false
      },
      SOU_ORDER_STATUS_ENUM,
      EXT_INQ_SOU_PROJECT_STATUS_ENMU,
      SOU_PUBLISH_SCOPE_ENUM
    }
  },

  computed: {
    /* 目标价状态 */
    targetPriceStatus () {
      // 轮次 === 1，&& [接收报价中, 已截止报价]
      return this.header.currentRound === 1 &&
        [EXT_INQ_SOU_PROJECT_STATUS_ENMU.ACCEPT_ORDER, EXT_INQ_SOU_PROJECT_STATUS_ENMU.ORDER_END].includes(this.header.extProjectStatus)
    },

    // 能否查看供应商报价
    vendorQuotePriceShow () {
      return this.header.needEncryptPrice !== 'Y' ||
        // 密封报价 已截止报价才允许看，接受报价中不允许看 ACCEPT_ORDER
        (this.header.needEncryptPrice === 'Y' && this.header.extProjectStatus !== EXT_INQ_SOU_PROJECT_STATUS_ENMU.ACCEPT_ORDER)
    }
  },

  methods: {
    /* 打开设定目标价 */
    openTargetPriceDetailDialog () {
      this.targetPriceDetailDialogVisible = true
    },

    /* 打开报价详情 */
    openTrackingDetailDialog (row) {
      this.quoteRow = row
      this.quoteDetailDialogVisible = true
    },

    /* 作废报价 */
    async cancelQuote (row) {
      const promptResult = await this.$prompt(
        this.$t('bidMod.discardReason'),
        this.$t('bidMod.discardQuote'),
        {
          confirmButtonText: this.$t('common.confirm'),
          cancelButtonText: this.$t('common.cancel'),
          inputValidator: value => !(!value || value.length > 500),
          inputErrorMessage: this.$t('bidMod.discardReasonMsg')
        }
      )

      if (!promptResult) {
        return
      }

      const response = await inqBuyerHttp.order.cancelOrder({
        projectId: this.header.projectId,
        vendorId: row.vendorId,
        cancelReason: promptResult.value
      })
      if (response) {
        this.$message.success(this.$t('bidMod.discardSuccess'))
        this.emitParentGetDetailData()
      }
    },

    /* 统一回调，更新列表数据 */
    emitParentGetDetailData () {
      this.$emit('refresh')
    },

    /* 打开代理报价弹窗 */
    openProxyQuoteDialog (row) {
      this.proxyQuoteParams = {
        visible: true,
        projectId: this.header.projectId,
        souNo: this.header.souNo,
        vendorId: row.vendorId
      }
      this.proxyQuoteDialogVisible = true
    }
  }
}
</script>

<style lang="scss" scoped>
.inquiry-tracking-top {
  display: flex;
  .inquiry-tracking-top-operation {
    width: 250px;
  }

  .inquiry-tracking-top-form {
    flex: 1;
    .label {
      display: inline-block;
      margin-right: 10px;
    }
    .value-input {
      width: 111px;
      &.red .el-input__inner {
        color: #f44;
      }
    }
  }
}
</style>
