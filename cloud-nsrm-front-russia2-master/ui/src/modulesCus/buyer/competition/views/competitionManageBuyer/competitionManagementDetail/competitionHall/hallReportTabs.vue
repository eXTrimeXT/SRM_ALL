<template>
  <div class="hall-report-tabs">
    <el-tabs v-model="reportTab" type="border-card">
      <!--按物料明细排名-->
      <el-tab-pane :label="$t('bidMod.competitionLts.itemDetailRanking')" name="itemDetailRanking">
        <ItemDetailRanking
          ref="itemDetailRanking"
          :project-id="baseInfo.projectId"
          :item-options="itemOptions"
          :base-info="baseInfo"
          :order-item-list="orderItemList"
          :is-active-tab="reportTab === 'itemDetailRanking'"
        />
      </el-tab-pane>

      <!--按总价排名-->
      <!-- <el-tab-pane
        :label="$t('bidMod.competitionLts.totalPriceRanking')"
        name="totalPriceRanking"
        lazy
      >
        <TotalPriceRanking :project-id="baseInfo.projectId" :order-infos="orderInfos" :is-active-tab="reportTab === 'totalPriceRanking'" />
      </el-tab-pane> -->

      <!--价格走势-->
      <el-tab-pane
        :label="$t('bidMod.priceTrend')"
        name="priceTrend"
        lazy
      >
        <PriceTrend :base-info="baseInfo" :round="round" :item-options="itemOptions" :is-active-tab="reportTab === 'priceTrend'" />
      </el-tab-pane>

      <!--物料明细报价记录-->
      <el-tab-pane
        :label="$t('bidMod.competitionLts.itemDetailQuote')"
        name="itemDetailQuote"
        lazy
      >
        <ItemDetailQuote
          :project-id="baseInfo.projectId"
          :item-options="itemOptions"
          :vendor-info-data="vendorInfoData"
          :order-item-list="orderItemList"
          :is-active-tab="reportTab === 'itemDetailQuote'"
        />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
/**
 * 竞价大厅报表
 */
import ItemDetailRanking from './hallReportTabs/itemDetailRanking.vue'
import TotalPriceRanking from './hallReportTabs/totalPriceRanking.vue'
import PriceTrend from './hallReportTabs/priceTrend.vue'
import ItemDetailQuote from './hallReportTabs/itemDetailQuote.vue'

export default {
  name: 'HallReportTabs',

  components: {
    ItemDetailRanking,
    TotalPriceRanking,
    PriceTrend,
    ItemDetailQuote
  },

  props: {
    baseInfo: {
      type: Object,
      required: true
    },
    readonly: {
      type: Boolean,
      default: false
    },
    // 物料需求数据
    requireInfoData: {
      type: Array,
      default: () => []
    },
    // 邀请供应商数据
    vendorInfoData: {
      type: Array,
      default: () => []
    },
    //
    orderItemList: {
      type: Array,
      default: () => []
    },
    // 价格走势信息
    orderInfos: {
      type: Array,
      default: () => []
    },
    round: {
      type: Number
    }
  },

  data () {
    return {
      reportTab: 'itemDetailRanking'
    }
  },

  computed: {
    // 物料列表
    itemOptions () {
      return this.requireInfoData.map(item => {
        return {
          itemId: item.itemId,
          souItemId: item.souItemId,
          itemCode: item.itemCode,
          itemDesc: item.itemDesc
        }
      })
    }
  },

  methods: {
    /* 刷新物料明细排名 */
    refreshItemDetailRanking () {
      if (this.reportTab === 'itemDetailRanking' && this.$refs.itemDetailRanking) {
        this.$refs.itemDetailRanking.getQueryData()
      }
    }
  }
}
</script>
