<template>
  <div class="hall-require-quote">
    <el-table
      :data="itemList"
      border
      max-height="251px"
      highlight-current-row
    >
      <el-table-column
        type="index"
        :label="$t('common.sort')"
        width="50"
      />

      <!--无料号寻源-->
      <el-table-column
        prop="noCodeItem"
        :label="$t('sourcingBuyer.isMaterialSourcing')"
        min-width="150"
        :formatter="(row, column, value) => $getDictLabel('YES_OR_NO', value)"
      />

      <!--物料编码-->
      <el-table-column
        prop="itemCode"
        :label="$t('bidMod.targetNum')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--物料描述-->
      <el-table-column
        prop="itemDesc"
        :label="$t('bidMod.itemDesc')"
        min-width="200"
        show-overflow-tooltip
      />

      <!--单位-->
      <el-table-column
        prop="unit"
        :label="$t('bidMod.units_price')"
        min-width="60"
        :formatter="(row, column, value) => $getDictLabel('unit', value)"
        show-overflow-tooltip
      />

      <!--需求数量-->
      <el-table-column
        prop="requireQuantity"
        :label="$t('bidMod.appraisRequired')"
        min-width="80"
        show-overflow-tooltip
      />

      <!--报价币种-->
      <el-table-column
        prop="currency"
        :label="$t('bidMod.currencyType')"
        min-width="110"
        :formatter="() => $getDictLabel('currency', baseInfo.standardCurrency)"
        show-overflow-tooltip
      />

      <!--起拍价-->
      <el-table-column
        align="right"
        prop="orderStartPrice"
        :label="$t('bidMod.startingPrice')"
        min-width="80"
        show-overflow-tooltip
      />

      <!--本轮最低价 / 本轮最高价-->
      <el-table-column
        align="right"
        prop="latestOrderNotaxPrice"
        :label="priceLabel"
        min-width="100"
        show-overflow-tooltip
      />

      <!--降幅 / 升幅-->
      <el-table-column
        prop="pricePercent"
        :label="`${getEvaluateMethodFlag(baseInfo.scoreRuleType)}(%)`"
        min-width="100"
        show-overflow-tooltip
      />

      <!--降额 / 升额-->
      <el-table-column
        prop="orderNotaxPriceAmount"
        :label="`${getEvaluateMethodFlagAmount(baseInfo.scoreRuleType)}`"
        min-width="100"
        show-overflow-tooltip
      />

      <!--已报价供应商-->
      <el-table-column
        prop="orderedVendorCount"
        :label="$t('bidMod.quotatedSupplier')"
        min-width="120"
        show-overflow-tooltip
      >
        <template v-slot="{ row }">
          <el-popover
            v-if="row.orderedVendorCount && row.orderedVendorCount > 0"
            placement="left"
            width="400"
            trigger="click"
          >
            <el-table :data="row.orderSouVendorList || []">
              <!--供应商编号-->
              <el-table-column
                width="200"
                property="vendorCode"
                :label="$t('bidMod.vendorCode')"
                show-overflow-tooltip
              />
              <!--供应商名称-->
              <el-table-column
                width="200"
                property="vendorName"
                :label="$t('bidMod.vendorName')"
                show-overflow-tooltip
              />
            </el-table>
            <el-button slot="reference" type="text">
              {{ row.orderedVendorCount }}
            </el-button>
          </el-popover>
          <span v-else>{{ row.orderedVendorCount }}</span>
        </template>
      </el-table-column>
    </el-table>

    <CPagination
      class="c-query-table-pagination"
      :total="pageInfo.total"
      :page-num="pageInfo.pageNum"
      :page-size="pageInfo.pageSize"
      @current-change="handlePagerCurrentChange"
      @size-change="handlePagerSizeChange"
    />
  </div>
</template>

<script>
/**
 * 物料报价汇总
 */
import { getEvaluateMethodFlag, getEvaluateMethodFlagAmount } from 'lib@/composition/competition/utils'
import CPagination from 'lib@/components/c-pagination'

export default {
  name: 'HallRequireQuote',

  components: { CPagination },

  props: {
    baseInfo: {
      type: Object,
      required: true
    },
    pageInfo: {
      type: Object,
      required: true
    },
    itemList: {
      type: Array,
      required: true
    }
  },

  data () {
    return {
      getEvaluateMethodFlag,
      getEvaluateMethodFlagAmount
    }
  },

  computed: {
    priceLabel () {
      return this.baseInfo.scoreRuleType === 'MIN_PRICE' ? this.$t('bidMod.lowestPrice') : this.$t('bidMod.maxPriceRound')
    }
  },

  methods: {
    /* 页码改变 */
    handlePagerCurrentChange (page) {
      this.$emit('page-change', page)
    },

    /* 页码大小改变 */
    handlePagerSizeChange (size) {
      this.$emit('size-change', size)
    }
  }
}
</script>
