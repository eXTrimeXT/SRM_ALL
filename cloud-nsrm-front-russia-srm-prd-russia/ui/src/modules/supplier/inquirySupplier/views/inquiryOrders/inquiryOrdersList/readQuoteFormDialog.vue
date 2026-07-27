<template>
  <SrmDialog
    :title="$t('bidMod.readQuoteForm')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <SrmRow class="the_top_header">
      <SrmCol :init-col="3">
        <p>{{ $t('bidMod.inquiryNo') }}:<span style="padding-left: 11px">{{ quoteRow.souNo }}</span></p>
      </SrmCol>
      <SrmCol :init-col="3">
        <p>{{ $t('bidMod.quoteNo') }}:<span style="padding-left: 11px">{{ quoteRow.orderNo }}</span></p>
      </SrmCol>
      <SrmCol :init-col="3" />
      <SrmCol :init-col="3" />
    </SrmRow>

    <el-table
      :data="displayScoreItem"
      style="width: 100%"
      border
      height="333px"
      highlight-current-row
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />

      <!--t 报价单号-->
      <el-table-column
        align="center"
        prop="orderNo"
        :label="$t('bidMod.quoteNo')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 物料编码-->
      <el-table-column
        align="center"
        prop="itemCode"
        :label="$t('bidMod.itemCode')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 物料名称-->
      <el-table-column
        align="center"
        prop="itemDesc"
        :label="$t('bidMod.itemDesc')"
        min-width="150"
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

      <!--t 采购组织-->
      <el-table-column
        align="center"
        prop="organizationName"
        :label="$t('bidMod.orgName')"
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
      >
        <template v-slot="scope">
          <span>{{ $getDictLabel('INQ_SOU_ORDER_STATUS', scope.row.orderStatus) }}</span>
        </template>
      </el-table-column>

      <!--t 需求数量-->
      <el-table-column
        align="center"
        prop="requireQuantity"
        :label="$t('bidMod.demandQuantity2')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 价格类型-->
      <el-table-column
        align="center"
        prop="itemType"
        :label="$t('bidMod.priceType')"
        width="100"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <span>{{ $getDictLabel('DMAND_LINE_TYPE', scope.row.itemType) }}</span>
        </template>
      </el-table-column>

      <!--t 未税报价-->
      <el-table-column
        align="center"
        prop="orderNotaxPrice"
        :label="$t('bidMod.quotenotaxPrice')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 含税报价-->
      <el-table-column
        align="center"
        prop="orderTaxPrice"
        :label="$t('bidMod.quotetaxPrice')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 是否阶梯报价-->
      <el-table-column
        align="center"
        prop="isLadder"
        :label="$t('bidMod.isLadder')"
        width="100"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <span>{{ scope.row.isLadder === "Y" ? "是" : "否" }}</span>
        </template>
      </el-table-column>

      <!--阶梯价报价-->
      <el-table-column
        align="center"
        :label="$t('bidMod.ladderQuote')"
        width="110"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <el-popover
            placement="right"
            width="500"
            trigger="click"
          >
            <p class="the_title1">
              {{ $t('bidMod.materialCode') }}：{{ thisItemCode }}
              <span style="padding-left: 31px">{{ $t('bidMod.materialName') }}：</span>
              {{ thisItemName }}
              <span style="padding-left: 31px">{{ thisItemcategoryName }}</span>
            </p>
            <p><span>{{ $t('bidMod.demandQuantity') }}：</span><span>{{ thisDemandQuality }}</span></p>
            <el-table
              :data="ladderPriceTable"
              style="width: 100%"
              border
              height="155px"
            >
              <el-table-column
                align="center"
                type="index"
                width="50"
              />
              <el-table-column
                align="center"
                prop="beginQuantity"
                :label="$t('bidMod.beginQuantity')"
                width="100"
                show-overflow-tooltip
              />
              <el-table-column
                align="center"
                prop="endQuantity"
                :label="$t('bidMod.endQuantity')"
                width="100"
                show-overflow-tooltip
              />
              <el-table-column
                align="center"
                prop="unit"
                :label="$t('bidMod.unit')"
                width="100"
                show-overflow-tooltip
                :formatter="(row, column, cellValue) => $getDictLabel('unit', cellValue)"
              />
              <el-table-column
                align="center"
                prop="taxprice"
                :label="$t('bidMod.quotetaxPrice')"
                width="120"
                show-overflow-tooltip
              />
            </el-table>
            <!--b 阶梯价-->
            <el-button
              slot="reference"
              type="primary"
              :disabled="scope.row.isLadder !== 'Y'"
              @click="ladderClick(scope.row)"
            >
              {{ $t('bidMod.ladderPrice') }}
            </el-button>
          </el-popover>
        </template>
      </el-table-column>

      <!--t 税率-->
      <el-table-column
        align="center"
        prop="taxRate"
        :label="$t('bidMod.taxRate2')"
        width="100"
        show-overflow-tooltip
      />

      <!--t 询报价结果-->
      <el-table-column
        align="center"
        prop="quoteResult"
        :label="$t('bidMod.quoteResult')"
        width="100"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <span>{{ $getDictLabel('RFQ_RESULT', scope.row.quoteResult) }}</span>
        </template>
      </el-table-column>
    </el-table>
    <div
      slot="footer"
      class="dialog-footer"
    >
      <el-button @click="dialogVisible = false">
        {{ $t("common.backTo") }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
/**
 * 查看报价单
 */

export default {
  name: 'ReadQuoteFormDialog',
  props: {
    visible: Boolean,
    quoteRow: Object,
    displayScoreItem: [Array, Object]
  },
  data () {
    return {
      thisItemCode: '',
      thisItemName: '',
      thisDemandQuality: '',
      thisItemcategoryName: '',
      ladderPriceTable: []
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
    }
  },
  methods: {
    ladderClick (row) {
      this.thisItemCode = row ? row.itemCode : ''
      this.thisItemName = row ? row.itemName : ''
      this.thisItemcategoryName = row ? row.categoryName : ''
      this.thisDemandQuality = row ? row.orderQuantity : ''
      this.ladderPriceTable = row.quoteLadderPrices || []
      for (const x of this.ladderPriceTable) {
        x.unit = row.unit
      }
    }
  }
}
</script>
