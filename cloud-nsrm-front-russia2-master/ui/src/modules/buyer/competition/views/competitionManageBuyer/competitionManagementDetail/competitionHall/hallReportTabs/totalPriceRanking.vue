<template>
  <div class="total-price-ranking">
    <el-table
      :data="totalPriceRankingData"
      border
      max-height="400px"
      highlight-current-row
    >
      <el-table-column
        type="index"
        :label="$t('common.sort')"
        width="50"
      />

      <!--名次-->
      <el-table-column
        prop="auctRanking"
        :label="$t('bidMod.competition')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--报价供应商-->
      <el-table-column
        prop="vendorName"
        :label="$t('bidMod.quotedSupplier')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--未税总价-->
      <el-table-column
        align="right"
        prop="orderNotaxTotalPrice"
        :label="$t('competition.orderNotaxTotalPrice')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--升降幅比例(%)-->
      <el-table-column
        align="right"
        prop="pircePercent"
        :label="$t('bidMod.competitionLts.floating')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--未税总金额(元)-->
      <el-table-column
        align="right"
        prop="orderNotaxTotalPrice"
        :label="$t('bidMod.competitionLts.orderNotaxTotalPrice')"
        min-width="150"
        show-overflow-tooltip
      />
    </el-table>
  </div>
</template>

<script>
/**
 * 按总价排名
 */
import { compBuyerHttp } from 'modb@/competition/api'

export default {
  name: 'TotalPriceRanking',

  props: {
    projectId: {
      type: [Number, String],
      default: ''
    },
    // 是否当前tab页
    isActiveTab: {
      type: Boolean,
      default: false
    },
    orderInfos: {
      type: Array,
      default: () => []
    }
  },

  data () {
    return {
      totalPriceRankingData: []
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
    orderInfos: {
      handler (nVal) {
        if (nVal) {
          this.totalPriceRankingData = nVal
        }
      },
      immediate: true,
      deep: true
    }
  },

  methods: {
    /* 查询数据 */
    async getQueryData () {

    }
  }
}
</script>
