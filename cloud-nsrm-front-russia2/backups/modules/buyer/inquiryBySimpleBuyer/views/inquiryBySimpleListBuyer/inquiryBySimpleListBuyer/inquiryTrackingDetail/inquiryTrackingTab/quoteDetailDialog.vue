<template>
  <srm-dialog
    title="查看报价"
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
        prop="demandQuantity"
        label="预计采购量"
        width="100"
        show-overflow-tooltip
      />
      <!--t 价格类型-->
      <el-table-column
        align="center"
        prop="itemType"
        :label="$t('bidMod.priceType')"
        width="100"
        :formatter="row => $getDictLabel('DMAND_LINE_TYPE', row.itemType)"
        show-overflow-tooltip
      />
      <!--t 未税单价-->
      <el-table-column
        align="center"
        prop="notaxPrice"
        :label="$t('bidMod.quotenotaxPrice2')"
        width="100"
        show-overflow-tooltip
      />
      <!--t 未税总金额-->
      <el-table-column
        align="center"
        prop="totalNotaxPrice"
        label="未税总金额"
        width="100"
        show-overflow-tooltip
      />
      <!--t 含税单价-->
      <el-table-column
        align="center"
        prop="taxPrice"
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
        prop="currency"
        :label="$t('bidMod.currency_price')"
        width="100"
        :formatter="row => $getDictLabel('currency', row.currency)"
        show-overflow-tooltip
      />

      <!--t 阶梯价明细-->
      <el-table-column
        align="center"
        prop="isLadder"
        label="报价明细"
        width="125"
      >
        <template v-slot="{ row }">
          <!--阶梯报价-->
          <el-button
            v-if="row.isLadder === 'Y'"
            type="text"
            @click="openLadderPriceDialog(row)"
          >
            查看阶梯价明细
          </el-button>

          <!--公式报价-->
          <el-button
            v-if="row.isFormula === 'Y'"
            type="text"
            @click="openFormulaQuotaDialog(row)"
          >
            查看公式报价明细
          </el-button>

          <!--模板报价-->
          <el-button
            v-if="row.isTemplate === 'Y'"
            type="text"
            @click="openTemplatePriceDialog(row)"
          >
            {{ $t('templatePrice.detailLabel') }}
          </el-button>
        </template>
      </el-table-column>

      <!--t 备注-->
      <el-table-column
        align="center"
        prop="remark"
        label="备注"
        width="100"
        show-overflow-tooltip
      />
    </el-table>

    <div slot="footer" class="dialog-footer">
      <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button>
    </div>

    <!--查看阶梯价-->
    <LadderPriceDetail
      v-if="ladderPriceDialogVisible"
      :visible.sync="ladderPriceDialogVisible"
      :business-type="BUSINESS_TYPE_ENUM.INQUIRY"
      :edit-row="viewRow"
      readonly
      page-type="quote"
    />

    <!--查看公式报价-->
    <FormulaPriceDialog
      v-if="formulaPriceDialogVisible"
      :visible.sync="formulaPriceDialogVisible"
      :view-row="viewRow"
    />

    <!--模板报价-->
    <TemplatePriceDialog
      v-if="templatePriceDialogVisible"
      :visible.sync="templatePriceDialogVisible"
      :business-type="BUSINESS_TYPE_ENUM.INQUIRY"
      :query-params="queryParams"
      readonly
    />
  </srm-dialog>
</template>

<script>
/**
 * 报价详情弹窗
 */
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import infoRow from '../infoRow'
import FormulaPriceDialog from 'lib@/composition/inquiryBySimple/formulaPriceDialog'
import Big from 'big.js'
import TemplatePriceDialog from 'lib@/composition/quoteTemplate/templatePriceDialog'
import LadderPriceDetail from 'lib@/composition/origin/ladderPrice'

export default {
  name: 'QuoteDetailDialog',

  components: {
    infoRow,
    FormulaPriceDialog,
    TemplatePriceDialog,
    LadderPriceDetail
  },

  props: {
    visible: Boolean,
    quoteRow: Object,
    header: Object
  },

  data () {
    return {
      quoteTable: [],
      ladderPriceDialogVisible: false,
      formulaPriceDialogVisible: false,
      templatePriceDialogVisible: false,
      viewRow: null,
      queryParams: null,
      BUSINESS_TYPE_ENUM
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
        {
          label: this.$t('bidMod.inquiryNo'),
          value: this.header.inquiryNo
        },
        // 报价单号
        {
          label: this.$t('bidMod.quoteNo'),
          value: this.quoteRow.quoteNo
        },
        // 供应商编码
        {
          label: this.$t('bidMod.vendorCode'),
          value: this.quoteRow.vendorCode
        },
        // 供应商名称
        {
          label: this.$t('bidMod.vendorName'),
          value: this.quoteRow.vendorName
        },
        // 报价币种
        {
          label: this.$t('bidMod.currency'),
          value: this.$getDictLabel('currency', this.header.currency)
        },
        // 价格精度
        {
          label: this.$t(('bidMod.pricePrecision')),
          value: this.header.priceNum
        }
      ]
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
    getTrackingDetail () {
      this.$api.inq.inquiryBySimple.getTrackingDetail(this.quoteRow.quoteId).then(data => {
        if (data && data.data && Array.isArray(data.data)) {
          this.quoteTable = data.data.map(item => {
            let totalNotaxPrice = ''
            if (!isNaN(Number(item.notaxPrice)) && !isNaN(Number(item.demandQuantity))) {
              const bigNoTaxPrice = new Big(item.notaxPrice)
              const bigDemandQuantity = new Big(item.demandQuantity)
              totalNotaxPrice = bigNoTaxPrice.times(bigDemandQuantity).round(10).toString()
            }
            return {
              ...item,
              // 计算未税总金额
              totalNotaxPrice
            }
          })
        }
      })
    },

    /* 打开阶梯价明细弹窗 */
    openLadderPriceDialog (row) {
      this.viewRow = { ...row, round: this.header.round }
      this.ladderPriceDialogVisible = true
    },

    /* 打开公式报价明细弹窗 */
    openFormulaQuotaDialog (row) {
      this.viewRow = {
        ...row,
        round: this.header.round,
        // 报价币种
        quoteCurrency: row.currency
      }
      this.formulaPriceDialogVisible = true
    },

    /* 打开模板报价明细弹窗 */
    openTemplatePriceDialog (row) {
      this.queryParams = {
        inquiryId: row.inquiryId,
        inquiryItemId: row.inquiryItemId,
        vendorId: this.quoteRow.vendorId
      }

      this.templatePriceDialogVisible = true
    },

    /* 格式化阶梯价类型 */
    formatterLadderType (row) {
      if (row.isLadder !== 'Y') {
        return ''
      }
      return row.ladderType === 'standard' ? this.$t('bidMod.standardladderPrice') : this.$t('bidMod.sumladderPrice')
    }
  }
}
</script>
