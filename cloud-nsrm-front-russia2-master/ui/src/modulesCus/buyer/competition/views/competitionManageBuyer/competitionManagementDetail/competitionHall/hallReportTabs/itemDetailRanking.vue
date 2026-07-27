<template>
  <div class="item-detail-ranking">
    <p>
      <span style="margin-right: 8px">{{ $t('common.materialName') }}</span>

      <el-select v-model="filterItemSelect" @change="getQueryData">
        <el-option
          v-for="item in itemOptions"
          :key="item.souItemId"
          :label="item.itemDesc"
          :value="item.souItemId"
        />
      </el-select>
    </p>

    <el-table
      :data="itemDetailRankingData"
      border
      max-height="400px"
      highlight-current-row
    >
      <el-table-column
        align="center"
        type="index"
        width="40"
      />

      <!--名次-->
      <el-table-column
        align="center"
        prop="auctRanking"
        :label="$t('bidMod.competition')"
        min-width="100"
      />

      <!--报价供应商-->
      <el-table-column
        align="center"
        prop="vendorName"
        :label="$t('bidMod.quotedSupplier')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--未税单价-->
      <el-table-column
        align="center"
        prop="orderNotaxPrice"
        :label="$t('bidMod.quotenotaxPrice2')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--升降幅比例(%)-->
      <el-table-column
        align="center"
        prop="pricePercent"
        :label="`${getEvaluateMethodFlag(baseInfo.scoreRuleType)}(%)`"
        min-width="150"
        show-overflow-tooltip
      />

      <!-- 涨降金额 -->
      <el-table-column
        align="center"
        prop="orderNotaxPriceAmount"
        :label="`${getEvaluateMethodFlagAmount(baseInfo.scoreRuleType)}`"
        min-width="120"
      />

      <!--未税金额(元)-->
      <el-table-column
        align="center"
        prop="orderNotaxTotalPrice"
        :label="$t('bidMod.competitionLts.orderNotaxTotalPrice')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--税率-->
      <el-table-column
        align="center"
        prop="taxKey"
        :label="$t('bid_mod.taxRate')"
        min-width="120"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('tax', cellValue)"
      />

      <!--报价时间-->
      <el-table-column
        align="center"
        prop="submitTime"
        :label="$t('bidMod.quotedTime')"
        min-width="150"
        :formatter="(row, column, cellValue) => $parseTime(cellValue)"
      />

      <!--已报价次数-->
      <el-table-column
        align="center"
        prop="orderedCount"
        :label="$t('bidMod.competitionLts.orderCount')"
        min-width="100"
        show-overflow-tooltip
      />
    </el-table>
  </div>
</template>

<script>
/**
 * 物料明细排名
 */
import { compBuyerHttp } from 'modb@/competition/api'
import { getEvaluateMethodFlag, getEvaluateMethodFlagAmount } from 'lib@/composition/competition/utils'

export default {
  name: 'ItemDetailRanking',

  props: {
    baseInfo: {
      type: Object,
      required: true
    },
    orderItemList: {
      type: Array,
      default: () => [],
      required: true
    },
    itemOptions: {
      type: Array,
      required: true
    },
    // 是否当前tab页
    isActiveTab: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      filterItemSelect: '',
      itemDetailRankingData: [],
      getEvaluateMethodFlag,
      getEvaluateMethodFlagAmount
    }
  },

  watch: {
    isActiveTab: {
      handler (newValue, oldValue) {
        // 切换到当前标签页
        if (newValue && !oldValue) {
          this.getQueryData()
        }
      },
      immediate: true
    },
    orderItemList: {
      handler (nVal) {
        if (nVal) {
          this.itemDetailRankingData = this.orderItemList.filter(item => item.souItemId === this.filterItemSelect)
        }
      },
      immediate: true,
      deep: true
    }
  },

  methods: {
    /* 查询列表数据 */
    async getQueryData () {
      if (!this.filterItemSelect && this.itemOptions.length) {
        // 默认选第一个物料
        this.filterItemSelect = this.itemOptions[0].souItemId
      }
      this.$nextTick(() => {
        this.itemDetailRankingData = this.orderItemList.filter(item => item.souItemId === this.filterItemSelect)
      })
    }
  }
}
</script>
