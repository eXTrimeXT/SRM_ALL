<template>
  <div class="biding-detail">
    <div class="the_display_content">
      <SrmRow>
        <!--招标类型-->
        <SrmCol :init-col="3">
          <span>{{ $t("bidMod.bidingType1") }}</span>{{ $getDictLabel('SOU_BRG_TYPE', bargainBase.bargainType) }}
        </SrmCol>
        <!--评分规则-->
        <SrmCol :init-col="3">
          <span>{{ $t("bidMod.inquiryRule") }}</span>{{ $getDictLabel('SOU_SCORE_RULE_TYPE', bargainBase.scoreRuleType) }}
        </SrmCol>
        <!--决标方式-->
        <SrmCol :init-col="3">
          <span>{{ $t("bidMod.bidingAwardWay") }}</span>{{ $getDictLabel('SOU_ORDER_WAY', bargainBase.orderWay) }}
        </SrmCol>
        <SrmCol />
      </SrmRow>
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
        width="60"
      />

      <!--业务实体-->
      <el-table-column
        align="center"
        prop="orgOuName"
        :label="$t('bid_mod.businessEntity')"
        width="150"
        show-overflow-tooltip
      />

      <!--库存组织-->
      <el-table-column
        align="center"
        prop="orgInvName"
        :label="$t('bid_mod.inv')"
        width="150"
        show-overflow-tooltip
      />

      <!--物料编码-->
      <el-table-column
        align="center"
        prop="itemCode"
        :label="$t('bidMod.itemCode')"
        width="120"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => targetNumReveal(cellValue)"
      />

      <!--物料名称-->
      <el-table-column
        align="center"
        prop="itemDesc"
        :label="$t('bidMod.itemDesc')"
        width="150"
        show-overflow-tooltip
      />

      <!--组合-->
      <el-table-column
        align="center"
        prop="itemGroup"
        :label="$t('bidMod.itemGroup')"
        width="150"
        show-overflow-tooltip
      />

      <!--采购数量-->
      <el-table-column
        align="center"
        prop="requireQuantity"
        :label="$t('bid_mod.purQuantity')"
        width="100"
        show-overflow-tooltip
      />

      <!--单位-->
      <el-table-column
        align="center"
        prop="unit"
        :label="$t('bid_mod.unit')"
        width="70"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('unit', cellValue)"
      />

      <!--原币未税报价-->
      <el-table-column
        align="center"
        prop="orderNotaxPrice"
        :label="$t('bidMod.orderNotaxPriceVendor')"
        width="100"
        show-overflow-tooltip
      />

      <!--本币未税报价-->
      <el-table-column
        align="center"
        prop="standardNotaxPrice"
        :label="$t('bidMod.standardNotaxPriceVendor')"
        width="100"
        show-overflow-tooltip
      />

      <!--定价开始时间-->
      <el-table-column
        align="center"
        prop="priceStartTime"
        :label="$t('bidMod.priceStartTime')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $parseTime(cellValue)"
      />

      <!--定价结束时间-->
      <el-table-column
        align="center"
        prop="priceEndTime"
        :label="$t('bidMod.priceEndTime')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $parseTime(cellValue)"
      />

      <!--本轮入围情况-->
      <el-table-column
        align="center"
        prop="winStatus"
        :label="$t('bidMod.currentInList')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('SUPPLIER_SELECTED_STATES', cellValue)"
      />

      <!--排名-->
      <el-table-column
        align="center"
        prop="ranking"
        :label="$t('bidMod.rank')"
        width="70"
        show-overflow-tooltip
      />

      <!--价格类型-->
      <el-table-column
        align="center"
        prop="priceType"
        :label="$t('bid_mod.priceType')"
        width="150"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('PRICE_TYPE', cellValue)"
      />

      <!--是否代理报价-->
      <el-table-column
        align="center"
        prop="isProxy"
        :label="$t('bid_mod.isProxyBidding')"
        width="140"
        :formatter="(row, column, cellValue) => $getDictLabel('YES_OR_NO', cellValue)"
        show-overflow-tooltip
      />

      <!--备注-->
      <el-table-column
        align="center"
        prop="remark"
        :label="$t('bid_mod.remark')"
        width="100"
        show-overflow-tooltip
      />

      <el-table-column
        v-if="!pricingType.isSimplePricing"
        fixed="right"
        :label="$t('bidMod.operation')"
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
        </template>
      </el-table-column>
    </el-table>

    <!--附件信息-->
    <p><span style="padding-right: 11px">{{ $t("bidMod.fileInfo") }}</span></p>
    <el-table
      :data="orderFileList"
      style="width: 100%"
      border
      height="144px"
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />

      <!--附件名称-->
      <SrmCommonFile
        type="table-column"
        :table-column-options="{
          label: $t('bidMod.fileName'),
          prop: 'orderDocId',
          nameProp: 'orderFileName',
          minHeight: ''
        }"
        readonly
      />

      <!--备注-->
      <el-table-column
        align="center"
        prop="orderRemark"
        :label="$t('bidMod.remark')"
      />
    </el-table>

    <!--公式报价-->
    <FormulaPriceDialog
      v-if="formulaPriceDialogVisible"
      :visible.sync="formulaPriceDialogVisible"
      :view-row="viewRow"
      :show-all-supplier="false"
    />

    <!-- 模型报价 -->
    <ModelQuoteDialog
      v-if="modelQuoteDialogVisible"
      :visible.sync="modelQuoteDialogVisible"
      :is-proxy-quote="false"
      is-read-only-by-vendor
      :source-line="viewRow"
    />
  </div>
</template>

<script>
/**
 * 投标明细
 */
import { brgSupplierHttp } from 'mods@/bargainSupplier/api'
import { maxNumberOption } from 'lib@/composition/commonComposition'
import { targetNumReveal } from 'lib@/composition/origin/composition'
import FormulaPriceDialog from 'lib@/composition/bargain/formulaPriceDialog'
import ModelQuoteDialog from 'lib@/composition/bargain/modelQuote/modelQuoteDialog'
import FormWrapper from 'lib@/components/Table/FormWrapper'

export default {
  name: 'QuoteDetail',

  components: {
    FormulaPriceDialog,
    ModelQuoteDialog,
    FormWrapper
  },

  props: {
    isCurrentTab: {
      type: Boolean,
      required: true
    },
    projectId: {
      type: [Number, String],
      required: true
    },
    bargainBase: {
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
      orderFileList: [],
      formulaPriceDialogVisible: false,
      modelQuoteDialogVisible: false,
      viewRow: null,
      currentRound: 1,
      formWrapperConfig: [
        // 物料名称
        { prop: 'itemDesc', label: () => this.$t('bidMod.itemDesc') },
        // 轮次
        {
          prop: 'round',
          label: this.$t('bidMod.bidingRound'),
          type: 'select',
          options: () => this.roundOption
        }
      ],
      targetNumReveal
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
    /* 查询投标详情 */
    async getAllOrderLine (searchFormData = {}) {
      if (!this.projectId) {
        return
      }

      const response = await brgSupplierHttp.order.getOrderDetails({
        projectId: this.projectId,
        ...searchFormData
      })

      if (response && response.data) {
        const {
          currentRound = {},
          orderDetails = [],
          orderFileList = []
        } = response.data
        this.currentRound = currentRound
        this.orderDetails = orderDetails
        this.orderFileList = orderFileList
      }
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
    }
  }
}
</script>
