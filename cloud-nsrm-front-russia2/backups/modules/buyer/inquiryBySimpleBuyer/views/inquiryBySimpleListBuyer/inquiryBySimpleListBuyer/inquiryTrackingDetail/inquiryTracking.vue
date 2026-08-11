<template>
  <div class="inquiry-tracking-tab">
    <div class="inquiry-tracking-top">
      <div class="inquiry-tracking-top-operation">
        <!--b 设定目标价-->
        <el-button type="primary" @click="openTargetPriceDetailDialog">
          {{ targetPriceStatus ? '设定目标价' : '查看目标价' }}
        </el-button>
      </div>
      <div class="inquiry-tracking-top-form">
        <srm-row>
          <!--公开报价 && 首轮 不展示-->
          <srm-col
            v-if="!(inquiryTrackingData.header.round === 1 && inquiryTrackingData.header.publishScope === 'OPEN_TENDER')"
            :init-col="2"
          >
            <!--本轮需报价供应商数量-->
            <span class="label">{{ $t('bidMod.inQvendorNums') }}:</span>
            <el-input
              v-model="inquiryTrackingData.currentRoundTotalCtn"
              type="text"
              disabled
              class="value-input"
            />
          </srm-col>

          <srm-col :init-col="2">
            <!--已提交报价供应商-->
            <span class="label">{{ $t('bidMod.inQsubmitNums') }}:</span>
            <el-input
              v-model="inquiryTrackingData.currentRoundQuotedCtn"
              type="text"
              disabled
              class="value-input red"
            />
          </srm-col>
        </srm-row>
      </div>
    </div>

    <el-table
      :data="inquiryTrackingTable"
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

      <!--t 有效报价-->
      <el-table-column
        align="center"
        :label="$t('bidMod.validQuote')"
        width="120"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <el-button
            v-if="vendorQuotePriceShow"
            type="text"
            @click="openTrackingDetailDialog(scope.row)"
          >
            {{ scope.row.quoteNo }}
          </el-button>
        </template>
      </el-table-column>

      <!--t 总价（未税/元）-->
      <el-table-column
        align="center"
        prop="totalNotaxPrice"
        :label="$t('bidMod.quotetotalAmount')"
        width="120"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => vendorQuotePriceShow ? cellValue : ''"
      />

      <!--t 报价状态-->
      <el-table-column
        align="center"
        prop="quoteStatus"
        :label="$t('bidMod.quoteStatus')"
        width="100"
        show-overflow-tooltip
        :formatter="row => $getDictLabel('QUOTE_STATUS', row.quoteStatus)"
      />

      <!--t 报价人-->
      <el-table-column
        align="center"
        prop="submitBy"
        :label="$t('bidMod.quoteMan')"
        min-width="120"
        show-overflow-tooltip
        :formatter="row => `${row.submitBy}-${row.submitFullName}`"
      />

      <!--t 报价时间-->
      <el-table-column
        align="center"
        prop="submitTime"
        :label="$t('bidMod.quotedTime')"
        width="150"
        show-overflow-tooltip
      />

      <!--t 手机号码-->
      <el-table-column
        align="center"
        prop="phone"
        :label="$t('bidMod.tel')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 邮箱-->
      <el-table-column
        align="center"
        prop="email"
        :label="$t('common.email')"
        width="150"
        show-overflow-tooltip
      />

      <!--t 供应商IP-->
      <el-table-column
        align="center"
        prop="quotationIp"
        label="供应商IP"
        width="120"
        show-overflow-tooltip
      >
        <template v-slot="{row}">
          <span :style="{ color: row.ipWarn ? 'red' : '' }">{{ row.quotationIp }}</span>
        </template>
      </el-table-column>

      <!--t 撤回原因-->
      <el-table-column
        align="center"
        prop="rollbackReason"
        :label="$t('bidMod.withdrawReason')"
        min-width="100"
        show-overflow-tooltip
      />

      <!--t 作废原因-->
      <el-table-column
        align="center"
        prop="cancelDescription"
        :label="$t('bidMod.cancelDescription')"
        min-width="100"
        show-overflow-tooltip
      />

      <el-table-column
        align="center"
        prop="operation"
        :label="$t('common.operation')"
        width="150"
        fixed="right"
      >
        <template v-slot="scope">
          <!--b 作废报价-->
          <el-button
            v-if="scope.row.quoteStatus === 'SUBMIT' && ['RECEI_QUOTATION', 'CLOSE_QUOTATION'].includes(inquiryTrackingData.header.status)"
            type="text"
            @click="cancelQuote(scope.row)"
          >
            {{ $t('bidMod.invalidQuotation') }}
          </el-button>

          <!--代理报价 [未报价 ROLLBACK] && 接收报价中-->
          <el-button
            v-if="['DRAFT', 'ROLLBACK'].includes(scope.row.quoteStatus) && inquiryTrackingData.header.status === 'RECEI_QUOTATION'"
            type="text"
            @click="openProxyQuoteDialog(scope.row)"
          >
            {{ $t('bid_mod.proxyQuoteHandle') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--设定目标价/目标价查看 弹窗-->
    <TargetPriceDetailDialog
      v-if="targetPriceDetailDialogVisible"
      :visible.sync="targetPriceDetailDialogVisible"
      :inquiry-id="inquiryTrackingData.header.inquiryId"
      :status="targetPriceStatus"
      @saveTargetPriceSuccess="emitParentGetDetailData"
    />

    <!--报价详情 弹窗-->
    <QuoteDetailDialog
      v-if="quoteDetailDialogVisible"
      :visible.sync="quoteDetailDialogVisible"
      :quote-row="quoteRow"
      :header="inquiryTrackingData.header"
    />

    <!--代理报价-->
    <ProxyQuoteDialog
      v-if="proxyQuoteDialogVisible"
      :visible.sync="proxyQuoteDialogVisible"
      :proxy-quote-params="proxyQuoteParams"
      @proxyQuoteSuccess="emitParentGetDetailData"
    />
  </div>
</template>

<script>
/**
 * 报价跟踪
 */
import TargetPriceDetailDialog from './inquiryTrackingTab/targetPriceDetailDialog'
import QuoteDetailDialog from './inquiryTrackingTab/quoteDetailDialog'
import ProxyQuoteDialog from './inquiryTrackingTab/proxyQuoteDialog'

export default {
  name: 'InquiryTrackingTab',

  components: {
    TargetPriceDetailDialog,
    QuoteDetailDialog,
    ProxyQuoteDialog
  },

  props: {
    inquiryTrackingData: {
      type: Object,
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
      }
    }
  },

  computed: {
    /* 目标价状态 */
    targetPriceStatus () {
      // 轮次 === 1，&& [已发布, 接收报价中, 已截止报价]
      const header = this.inquiryTrackingData.header
      return header.round === 1 && ['PUBLISHED', 'RECEI_QUOTATION', 'CLOSE_QUOTATION'].includes(header.status)
    },

    // 能否查看供应商报价
    vendorQuotePriceShow () {
      const header = this.inquiryTrackingData.header
      return header.needEncryptPrice !== 'Y' ||
        // 密封报价 已截止报价才允许看，接受报价中不允许看 RECEI_QUOTATION
        (header.needEncryptPrice === 'Y' && header.status !== 'RECEI_QUOTATION')
    },

    // 报价跟踪表格
    inquiryTrackingTable () {
      return this.inquiryTrackingData.trackingList || []
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
    cancelQuote (row) {
      this.$prompt('作废原因', '作废报价', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValidator: value => !(!value || value.length > 500),
        inputErrorMessage: '作废原因必填并且长度不能超过500字符！'
      }).then(({ value }) => {
        this.$api.inq.inquiryBySimple.cancelQuote({ quoteId: row.quoteId,
          cancelDescription: value }).then(() => {
          this.$message.success('作废成功！')
          this.emitParentGetDetailData()
        })
      })
    },

    /* 统一回调，更新列表数据 */
    emitParentGetDetailData () {
      this.$emit('updateDetailData')
    },

    /* 打开代理报价弹窗 */
    openProxyQuoteDialog (row) {
      this.proxyQuoteParams = {
        visible: true,
        inquiryId: this.inquiryTrackingData.header.inquiryId,
        inquiryNo: this.inquiryTrackingData.header.inquiryNo,
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
