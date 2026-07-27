<template>
  <SrmDialog
    :title="$t('bidMod.readQuote')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <!--详细信息区域-->
    <info-row :info-row-list="inquiryTrackingInfoRow" />

    <el-table
      :data="quoteTable"
      style="width: 100%"
      border
      height="200"
      highlight-current-row
    >
      <el-table-column align="center" type="index" width="50" />
      <!--t 业务实体-->
      <el-table-column
        align="center"
        prop="orgOuName"
        :label="$t('bidMod.affairsEntity')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 库存组织-->
      <el-table-column
        align="center"
        prop="orgInvName"
        :label="$t('bidMod.affairsInventoryOrg')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 物料编码-->
      <el-table-column
        align="center"
        prop="itemCode"
        :label="$t('bidMod.itemCode')"
        width="100"
        :formatter="(row, column, value) => row.noCodeItem === 'Y' ? '' : value"
        show-overflow-tooltip
      />

      <!--t 物料名称-->
      <el-table-column
        align="center"
        prop="itemDesc"
        :label="$t('bidMod.itemName')"
        width="150"
        show-overflow-tooltip
      />

      <!--t 单位-->
      <el-table-column
        align="center"
        prop="unit"
        :label="$t('bidMod.unit')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('unit', cellValue)"
      />

      <!--t 采购分类-->
      <el-table-column
        align="center"
        prop="categoryName"
        :label="$t('bidMod.purcategoryName')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 组合-->
      <el-table-column
        align="center"
        prop="itemGroup"
        :label="$t('bidMod.affairsCombination')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 预计采购量-->
      <el-table-column
        align="center"
        prop="requireQuantity"
        :label="$t('bidMod.requireQuantity')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 行类型-->
      <el-table-column
        align="center"
        prop="itemType"
        :label="$t('bidMod.itemType')"
        width="100"
        :formatter="(row, column, value) => $getDictLabel('DMAND_LINE_TYPE', value)"
        show-overflow-tooltip
      />

      <!--t 未税单价-->
      <el-table-column
        align="center"
        prop="orderNotaxPrice"
        :label="$t('bidMod.quotenotaxPrice2')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 未税总金额-->
      <el-table-column
        align="center"
        prop="standardNotaxTotalPrice"
        :label="$t('purSettlementMod.totalAmountNoTax')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 含税单价-->
      <el-table-column
        align="center"
        prop="orderTaxPrice"
        :label="$t('bidMod.quotetaxPrice2')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 税率-->
      <el-table-column
        align="center"
        prop="taxKey"
        :label="$t('bidMod.taxRate2')"
        width="100"
        :formatter="(row, column, value) => $getDictLabel('tax', value)"
        show-overflow-tooltip
      />

      <!--t 币种-->
      <el-table-column
        align="center"
        prop="orderCurrency"
        :label="$t('bidMod.currency_price')"
        width="100"
        :formatter="(row, column, value) => $getDictLabel('currency', value)"
        show-overflow-tooltip
      />

      <!--t 阶梯价类型-->
      <el-table-column
        align="center"
        prop="ladderType"
        :label="$t('bidMod.ladderType')"
        width="100"
        :formatter="row => formatterLadderType(row)"
        show-overflow-tooltip
      />

      <!--t 阶梯价明细-->
      <el-table-column
        align="center"
        prop="isLadder"
        :label="$t('bidMod.ladderQuoteDetail')"
        width="125"
      >
        <template v-slot="{ row }">
          <el-button
            v-if="row.isLadder === 'Y'"
            type="text"
            @click="openLadderPriceDialog(row)"
          >
            {{ $t('bidMod.viewLadderDetail') }}
          </el-button>
        </template>
      </el-table-column>

      <!--t 公式报价明细-->
      <el-table-column
        align="center"
        prop="isFormula"
        :label="$t('bidMod.formulaQuoteDetail')"
        width="125"
      >
        <template v-slot="{ row }">
          <el-button
            v-if="row.isFormula === 'Y'"
            type="text"
            @click="openFormulaQuotaDialog(row)"
          >
            {{ $t('bidMod.viewFormulaDetail') }}
          </el-button>
        </template>
      </el-table-column>

      <!-- 料费分离报价 -->
      <el-table-column
        align="center"
        prop="isSeparation"
        :label="$t('bidMod.templateQuoteDetail')"
        width="125"
      >
        <template v-slot="{ row }">
          <el-button
            v-if="isSeparation"
            type="text"
            @click="openSeparationPriceDialog(row)"
          >
            {{ $t('bidMod.viewSepDetail') }}
          </el-button>
        </template>
      </el-table-column>

      <!--t 备注-->
      <el-table-column
        align="center"
        prop="remark"
        :label="$t('common.remark')"
        width="100"
        show-overflow-tooltip
      />
    </el-table>

    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button>
    </div>

    <!--d 阶梯价-->
    <LadderPrice
      v-if="ladderPriceDialogVisible"
      :visible.sync="ladderPriceDialogVisible"
      :business-type="BUSINESS_TYPE_ENUM.INQUIRY_LTS"
      page-type="quote"
      :edit-row="viewRow"
      readonly
    />

    <FormulaPrice
      v-if="formulaPriceDialogVisible"
      :business-type="BUSINESS_TYPE_ENUM.INQUIRY_LTS"
      :visible.sync="formulaPriceDialogVisible"
      :detail-info="viewRow"
      :query-params="formulaPriceQueryParams"
      readonly
    />

    <!-- 料费分离模板报价 -->
    <SeparationPriceDialog
      v-if="separationPriceDialogVisible"
      role="buyer"
      :visible.sync="separationPriceDialogVisible"
      :businessType="BUSINESS_TYPE_ENUM.INQUIRY_LTS"
      :edit-row="editRow"
      readonly
    />
  </SrmDialog>
</template>

<script>
/**
 * 报价详情弹窗
 */
import { inqBuyerHttp } from 'modb@/inquiry/api'
import { BUSINESS_TYPE_ENUM, SOU_ORDER_TYPE_ENUM } from 'lib@/composition/origin/enum'
import Big from 'big.js'
import LadderPrice from 'lib@/composition/origin/ladderPrice'
import infoRow from '../infoRow.vue'
import FormulaPrice from 'lib@/composition/origin/formulaPrice/index.vue'
import SeparationPriceDialog from 'lib@/composition/quoteSeparation/templatePriceDialog'

export default {
  name: 'QuoteDetailDialog',

  components: {
    infoRow,
    LadderPrice,
    FormulaPrice,
    SeparationPriceDialog
  },

  props: {
    visible: {
      type: Boolean,
      default: false
    },
    quoteRow: {
      type: Object,
      required: true
    },
    header: {
      type: Object,
      required: true
    }
  },

  data () {
    return {
      quoteTable: [],
      ladderPriceDialogVisible: false,
      formulaPriceDialogVisible: false,
      viewRow: null,
      BUSINESS_TYPE_ENUM,
      formulaPriceQueryParams: null,
      editRow: null,
      separationPriceDialogVisible: false
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
    },
    inquiryTrackingInfoRow () {
      return [
        // 询价单号
        { label: this.$t('bidMod.inquiryNo'), value: this.header.souNo },
        // 报价单号
        { label: this.$t('bidMod.quoteNo'), value: this.quoteRow.orderNo },
        // 供应商编码
        { label: this.$t('bidMod.vendorCode'), value: this.quoteRow.vendorCode },
        // 供应商名称
        { label: this.$t('bidMod.vendorName'), value: this.quoteRow.vendorName },
        // 报价币种
        {
          label: this.$t('bidMod.currency'),
          value: this.$getDictLabel('currency', this.header.standardCurrency)
        },
        // 价格精度
        { label: this.$t(('bidMod.pricePrecision')), value: this.header.pricePrecision }
      ]
    },
    // 料费分离
    isSeparation () {
      return this.header.orderType === SOU_ORDER_TYPE_ENUM.MATERIAL_COST_SEPARATION
    }
  },

  created () {
    // dialogVisible 为true才挂载渲染的
    if (this.dialogVisible && this.quoteRow) {
      this.getTrackingDetail()
    }
  },

  methods: {
    /* 查看报价单详情 */
    async getTrackingDetail () {
      const response = await inqBuyerHttp.select.getVendorQuoteDetail(this.quoteRow.orderId)
      if (response && response.data && Array.isArray(response.data)) {
        this.quoteTable = response.data.map(item => {
          let standardNotaxTotalPrice = ''
          if (!isNaN(Number(item.orderNotaxPrice)) && !isNaN(Number(item.requireQuantity))) {
            const bigNoTaxPrice = new Big(item.orderNotaxPrice)
            const bigDemandQuantity = new Big(item.requireQuantity)
            standardNotaxTotalPrice = bigNoTaxPrice.times(bigDemandQuantity).round(10).toString()
          }
          return {
            ...item,
            // 计算未税总金额
            standardNotaxTotalPrice
          }
        })
      }
    },

    /* 打开阶梯价明细弹窗 */
    openLadderPriceDialog (row) {
      this.viewRow = {
        ...row,
        currentRound: this.header.currentRound,
        ladderList: row.ladderPriceList || []
      }
      this.ladderPriceDialogVisible = true
    },

    /* 打开公式报价明细弹窗 */
    openFormulaQuotaDialog (row) {
      this.viewRow = {
        ...row,
        currency: row.orderCurrency
      }
      this.formulaPriceQueryParams = {
        souItemId: row.souItemId || '',
        orderItemId: row.orderItemId || '',
        // 币种，用于基材价格根据汇率转换
        currencyCode: row.orderCurrency,
        vendorId: row.vendorId
      }
      this.formulaPriceDialogVisible = true
    },

    /* 格式化阶梯价类型 */
    formatterLadderType (row) {
      if (row.isLadder !== 'Y' || !row.ladderType) {
        return ''
      }
      return row.ladderType === 'standard' ? this.$t('bidMod.standardladderPrice') : this.$t('bidMod.sumladderPrice')
    },

    /* 打开报价模板弹窗 */
    openSeparationPriceDialog (row) {
      const { quoteTempId, quoteTempName, currentRound } = this.header
      this.editRow = {
        ...row,
        quoteTempId,
        quoteTempName,
        currentRound
      }
      this.separationPriceDialogVisible = true
    }
  }
}
</script>
