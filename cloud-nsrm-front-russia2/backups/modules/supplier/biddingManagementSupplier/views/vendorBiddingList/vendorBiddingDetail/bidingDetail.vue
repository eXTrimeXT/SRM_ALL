<template>
  <div class="biding-detail">
    <div class="the_display_content">
      <srm-row>
        <!--招标类型-->
        <srm-col :init-col="3">
          <span>{{ $t("bidMod.bidingType") }}</span>{{ $getDictLabel('BID_TYPE', bidingBase.bidingType) }}
        </srm-col>
        <!--评分规则-->
        <srm-col :init-col="3">
          <span>{{ $t("bidMod.inquiryRule") }}</span>{{ $getDictLabel('BID_GRADING', bidingBase.evaluateMethod) }}
        </srm-col>
        <!--决标方式-->
        <srm-col :init-col="3">
          <span>{{ $t("bidMod.bidingAwardWay") }}</span>{{ $getDictLabel('BID_DECIDE_METHOD', bidingBase.bidingAwardWay) }}
        </srm-col>
        <srm-col />
      </srm-row>
    </div>

    <p>
      <span style="padding-right: 11px">{{ $t("bidMod.businessInfo") }}</span>
    </p>

    <FormWrapper
      style="border-bottom: none"
      :form-array="formWrapperConfig"
      @getFormData="getAllOrderLine"
    />

    <el-table
      :data="orderDetails"
      style="width: 100%"
      border
      height="144px"
    >
      <!--轮次-->
      <el-table-column
        align="center"
        prop="round"
        :label="$t('bidMod.bidingRound')"
        min-width="60"
      />

      <!--业务实体-->
      <el-table-column
        align="center"
        prop="orgOuName"
        :label="$t('bid_mod.businessEntity')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--库存组织-->
      <el-table-column
        align="center"
        prop="orgInvName"
        :label="$t('bid_mod.inv')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--物料编码-->
      <el-table-column
        align="center"
        prop="targetNum"
        :label="$t('bidMod.itemCode')"
        min-width="120"
        show-overflow-tooltip
        :formatter="(row, column, value) => targetNumRevealFilter(value)"
      />

      <!--物料描述-->
      <el-table-column
        align="center"
        prop="targetDesc"
        :label="$t('bidMod.itemDesc')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--组合-->
      <el-table-column
        align="center"
        prop="itemGroup"
        :label="$t('bidMod.itemGroup')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--采购数量-->
      <el-table-column
        align="center"
        prop="quantity"
        :label="$t('bid_mod.purQuantity')"
        min-width="100"
        show-overflow-tooltip
      />

      <!--单位-->
      <el-table-column
        align="center"
        prop="unit"
        :label="$t('bid_mod.unit')"
        min-width="70"
        show-overflow-tooltip
        :formatter="(row, column, value) => $getDictLabel('unit', value)"
      />

      <!--原币未税报价-->
      <el-table-column
        align="center"
        prop="notaxPrice"
        label="原币未税报价"
        min-width="100"
        show-overflow-tooltip
      />

      <!--本币未税报价-->
      <el-table-column
        align="center"
        prop="bidNotaxPrice"
        label="本币未税报价"
        min-width="100"
        show-overflow-tooltip
      />

      <!--本轮最低价-->
      <el-table-column
        v-if="bidingBase.evaluateMethod !== 'HIGH_PRICE'"
        align="center"
        prop="currentRoundMinNotaxPrice"
        :label="$t('bidMod.lowestPriceRound')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--本轮最高价-->
      <el-table-column
        v-if="bidingBase.evaluateMethod === 'HIGH_PRICE'"
        align="center"
        prop="currentRoundMaxNotaxPrice"
        :label="$t('bidMod.maxPriceRound')"
        min-width="150"
        show-overflow-tooltip
      />
      <!--定价开始时间-->
      <el-table-column
        align="center"
        prop="priceStartTime"
        :label="$t('bidMod.priceStartTime')"
        min-width="100"
        show-overflow-tooltip
        :formatter="(row, column, value) => value ? value.slice(0, 10) : ''"
      />

      <!--定价结束时间-->
      <el-table-column
        align="center"
        prop="priceEndTime"
        :label="$t('bidMod.priceEndTime')"
        min-width="100"
        show-overflow-tooltip
        :formatter="(row, column, value) => value ? value.slice(0, 10) : ''"
      />

      <!--本轮入围情况-->
      <el-table-column
        align="center"
        prop="win"
        label="本轮入围情况"
        min-width="100"
        show-overflow-tooltip
        :formatter="(row, column, value) => $getDictLabel('SUPPLIER_SELECTED_STATES', value)"
      />

      <!--排名-->
      <el-table-column
        align="center"
        prop="rank"
        :label="$t('bidMod.rank')"
        min-width="70"
        show-overflow-tooltip
      />

      <!--价格类型-->
      <el-table-column
        align="center"
        prop="priceType"
        :label="$t('bid_mod.priceType')"
        min-width="150"
        show-overflow-tooltip
        :formatter="(row, column, value) => $getDictLabel('PRICE_TYPE', value)"
      />

      <!--是否代理报价-->
      <el-table-column
        align="center"
        prop="isProxyBidding"
        :label="$t('bid_mod.isProxyBidding')"
        min-width="140"
        :formatter="(...rest) => $getDictLabel('YES_OR_NO', rest[2])"
        show-overflow-tooltip
      />

      <!--备注-->
      <el-table-column
        align="center"
        prop="comments"
        :label="$t('bid_mod.remark')"
        min-width="100"
        show-overflow-tooltip
      />

      <el-table-column
        v-if="!pricingType.isSimplePricing"
        align="center"
        fixed="right"
        :label="$t('bidMod.operation')"
        min-width="140"
      >
        <template v-slot="{ row }">
          <!--公式报价-->
          <el-button
            v-if="pricingType.isFormulPricing"
            type="text"
            @click="openFormulaPriceDialog(row)"
          >
            {{ $t("bidMod.formulaQuote") }}
          </el-button>

          <!--模型报价-->
          <el-button
            v-if="pricingType.isModelPricing"
            type="text"
            @click="openModelQuoteDialog(row)"
          >
            {{ $t("bid_mod.modelQuoteTitle") }}
          </el-button>

          <!--模板报价-->
          <el-button
            v-if="pricingType.isTemplatePricing"
            type="text"
            @click="openTemplatePriceDialog(row)"
          >
            {{ $t('templatePrice.detailLabel') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--附件信息-->
    <p><span style="padding-right: 11px">{{ $t("bidMod.fileInfo") }}</span></p>
    <el-table
      :data="vendorFileList"
      style="width: 100%"
      border
      height="144px"
    >
      <el-table-column
        align="center"
        type="index"
        min-width="50"
      />

      <!--附件名称-->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          label: $t('bidMod.fileName'),
          prop: 'vendorDocId',
          nameProp: 'vendorFileName'
        }"
        readonly
      />

      <!--备注-->
      <el-table-column
        align="center"
        prop="vendorComments"
        :label="$t('bidMod.remark')"
      />
    </el-table>

    <!--公式报价-->
    <formula-price-dialog
      v-if="formulaPriceDialogVisible"
      :visible.sync="formulaPriceDialogVisible"
      :view-row="viewRow"
      :show-all-supplier="false"
    />

    <!-- 模型报价 -->
    <model-quote-dialog
      v-if="modelQuoteDialogVisible"
      :visible.sync="modelQuoteDialogVisible"
      :is-proxy-quote="false"
      is-read-only-by-vendor
      :source-line="viewRow"
    />

    <!--模板报价-->
    <TemplatePriceDialog
      v-if="templatePriceDialogVisible"
      :visible.sync="templatePriceDialogVisible"
      :business-type="BUSINESS_TYPE_ENUM.BIDING"
      :query-params="queryParams"
      readonly
    />
  </div>
</template>

<script>
/**
 * 投标明细
 */
import { maxNumberOption } from 'lib@/composition/commonComposition'
import { targetNumReveal } from 'lib@/composition/origin/composition'
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import formulaPriceDialog from 'lib@/composition/biddingManagement/formulaPriceDialog'
import modelQuoteDialog from 'lib@/composition/biddingManagement/modelQuote/modelQuoteDialog'
import FormWrapper from 'lib@/components/Table/FormWrapper'
import TemplatePriceDialog from 'lib@/composition/quoteTemplate/templatePriceDialog'

export default {
  name: 'BidingDetail',

  components: {
    formulaPriceDialog,
    modelQuoteDialog,
    FormWrapper,
    TemplatePriceDialog
  },

  props: {
    isCurrentTab: {
      type: Boolean,
      required: true
    },
    bidingId: {
      type: [Number, String],
      required: true
    },
    bidingBase: {
      type: Object,
      required: true
    },
    pricingType: {
      type: Object,
      required: true
    }
  },

  data () {
    return {
      orderDetails: [],
      isGetDataStatus: false,
      vendorFileList: [],
      formulaPriceDialogVisible: false,
      modelQuoteDialogVisible: false,
      viewRow: null,
      currentRound: 1,
      formWrapperConfig: [
        // 物料描述
        { prop: 'targetDesc', label: () => this.$t('bidMod.itemDesc') },
        // 轮次
        {
          prop: 'round',
          label: this.$t('bidMod.bidingRound'),
          type: 'select',
          options: () => this.roundOption
        }
      ],
      templatePriceDialogVisible: false,
      queryParams: null,
      BUSINESS_TYPE_ENUM
    }
  },

  computed: {
    roundOption () {
      return maxNumberOption(this.currentRound)
    }
  },

  watch: {
    isCurrentTab: {
      handler (newValue, oldValue) {
        if (newValue && !oldValue && !this.isGetDataStatus) {
          this.isGetDataStatus = true
          this.getAllOrderLine()
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 物料编码格式化 */
    targetNumRevealFilter (value) {
      return targetNumReveal(value)
    },

    /* 查询投标详情 */
    getAllOrderLine (searchFormData = {}) {
      if (!this.bidingId) return

      this.$http({
        url: '/api-bid/supplierCooperate/orderHead/getBiding/OrderDetails',
        method: 'POST',
        data: {
          bidingId: this.bidingId,
          ...searchFormData
        },
        loading: true
      }).then(data => {
        if (data && data.data) {
          const responseData = data.data
          this.currentRound = responseData.currentRound
          this.orderDetails = responseData.orderDetails || []
          this.vendorFileList = responseData.vendorFileList || []
        }
      })
    },

    /* 打开公式报价 */
    openFormulaPriceDialog (row) {
      this.viewRow = row
      this.formulaPriceDialogVisible = true
    },

    /* 打开模型报价 */
    openModelQuoteDialog (row) {
      this.viewRow = row
      this.modelQuoteDialogVisible = true
    },

    /* 打开模板报价明细弹窗 */
    openTemplatePriceDialog (row) {
      this.queryParams = { requirementLineId: row.requirementLineId }

      this.templatePriceDialogVisible = true
    }
  }
}
</script>
