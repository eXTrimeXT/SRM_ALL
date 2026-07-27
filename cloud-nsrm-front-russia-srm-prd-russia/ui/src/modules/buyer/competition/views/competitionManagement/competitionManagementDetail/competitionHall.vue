<template>
  <div class="competition-hall">
    <div class="hall-info">
      <SrmRow>
        <!--招标方式-->
        <SrmCol>
          <p>
            <span>{{ $t('bidMod.evaluateMethod') }}:</span>{{ $getDictLabel('SOU_COMP_SCORE_RULE_TYPE', baseInfo.scoreRuleType) }}
          </p>
        </SrmCol>
        <!--报价截止时间-->
        <SrmCol>
          <p>
            <span>{{ $t('bidMod.quotedeadline') }}:</span>{{ baseInfo.orderEndTime }}
          </p>
        </SrmCol>
        <SrmCol v-if="baseInfo.minPercent || baseInfo.minPercent === 0">
          <p>
            <span>{{ minPercentLabel }}:</span>{{ baseInfo.minPercent }}
          </p>
        </SrmCol>
        <SrmCol v-if="baseInfo.minAmount || baseInfo.minAmount === 0">
          <p>
            <span>{{ minAmountLabel }}:</span>{{ baseInfo.minAmount }}
          </p>
        </SrmCol>
        <!--本位币-->
        <SrmCol>
          <p>
            <span>{{ $t('bid_mod.standardCurrency') }}:</span>{{ $getDictLabel('currency', baseInfo.standardCurrency) }}
          </p>
        </SrmCol>

        <!--距离结束-->
        <SrmCol>
          <DynamicCutoffTime
            :label="$t('bidMod.endDistance')"
            :deadline-time="baseInfo.orderEndTime"
            style="font-size: 13px;"
          />
        </SrmCol>
      </SrmRow>

      <p>
        <!--刷新-->
        <el-button
          type="primary"
          :disabled="readonly"
          @click="getHallData"
        >
          {{ $t('common.refresh') }}
        </el-button>
      </p>
    </div>

    <el-table
      :data="requirementLineList"
      style="width: 100%"
      border
      max-height="251px"
      highlight-current-row
      @row-click="rowClick"
    >
      <el-table-column
        align="center"
        type="index"
        width="40"
      />

      <!--物料编码-->
      <el-table-column
        align="center"
        prop="itemCode"
        :label="$t('bidMod.targetNum')"
        min-width="150"
        show-overflow-tooltip
      />

      <!--物料名称-->
      <el-table-column
        align="center"
        prop="itemDesc"
        :label="$t('bidMod.itemDesc')"
        min-width="200"
        show-overflow-tooltip
      />

      <!--单位-->
      <el-table-column
        align="center"
        prop="unit"
        :label="$t('bidMod.units_price')"
        width="60"
        :formatter="(row, column, value) => $getDictLabel('unit', value)"
        show-overflow-tooltip
      />

      <!--需求数量-->
      <el-table-column
        align="center"
        prop="requireQuantity"
        :label="$t('bidMod.appraisRequired')"
        width="80"
        show-overflow-tooltip
      />

      <!--报价币种-->
      <el-table-column
        align="center"
        prop="orderCurrency"
        :label="$t('bidMod.currencyType')"
        width="110"
        :formatter="(row, column, value) => $getDictLabel('currency', value)"
        show-overflow-tooltip
      />

      <!--起拍价-->
      <el-table-column
        align="center"
        prop="startOrderNotaxPrice"
        :label="$t('bidMod.startingPrice')"
        width="80"
        show-overflow-tooltip
      />

      <!--最低价 / 最高价-->
      <el-table-column
        align="center"
        prop="latestOrderNotaxPrice"
        :label="baseInfo.scoreRuleType === 'MIN_PRICE' ? '最低价' : '最高价'"
        width="100"
        show-overflow-tooltip
      />

      <!--降幅 / 升幅-->
      <el-table-column
        align="center"
        prop="pricePercent"
        :label="(baseInfo.scoreRuleType === 'MIN_PRICE' ? $t('bidMod.amplitude') : '升幅') + '(%)'"
        width="100"
        show-overflow-tooltip
      />

      <!--已报价供应商-->
      <el-table-column
        align="center"
        prop="compVendorCount"
        :label="$t('bidMod.quotatedSupplier')"
        width="120"
        show-overflow-tooltip
      />
    </el-table>

    <template v-if="filterHallVisible">
      <div class="filter-report-wrap">
        <div class="label">
          {{ $t('formula.type') }}:
        </div>
        <div class="content">
          <!--showType-->
          <el-select v-model="showType">
            <el-option
              v-for="item in showTypeList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </div>
      </div>

      <!--最新实际报价排名-->
      <el-table
        v-if="showType === 'rankList'"
        :data="rankList"
        style="width: 100%"
        border
        height="251px"
      >
        <el-table-column
          align="center"
          type="index"
          width="40"
        />

        <!--名次-->
        <el-table-column
          prop="ranking"
          :label="$t('bidMod.competition')"
          min-width="60"
          show-overflow-tooltip
        />

        <!--报价-->
        <el-table-column
          prop="orderNotaxPrice"
          :label="$t('bidMod.doBiding1')"
          min-width="60"
          show-overflow-tooltip
        />

        <!--金额-->
        <el-table-column
          prop="orderNotaxTotalPrice"
          :label="$t('orderMod.buyerOrderSynergy.amount')"
          min-width="60"
          show-overflow-tooltip
        />

        <!--供应商-->
        <el-table-column
          prop="vendorName"
          :label="$t('bidMod.provider')"
          min-width="150"
          show-overflow-tooltip
        />

        <!--升降幅比例-->
        <el-table-column
          prop="pricePercent"
          label="升降幅比例(%)"
          min-width="60"
          show-overflow-tooltip
        />
      </el-table>

      <!--最新实际报价排名-->
      <LineChart v-if="showType === 'trendList'" :chart-data="chartData" />

      <!--报价列表-->
      <el-table
        v-if="showType === 'hisList'"
        :data="hisList"
        style="width: 100%"
        border
        height="251px"
      >
        <el-table-column
          align="center"
          type="index"
          width="40"
        />

        <!--报价时间-->
        <el-table-column
          prop="submitTime"
          :label="$t('bidMod.quotedTime')"
          min-width="150"
          show-overflow-tooltip
        />

        <!--IP地址-->
        <el-table-column
          prop="submitByIp"
          :label="$t('bidMod.ipAddress')"
          min-width="100"
          show-overflow-tooltip
        />

        <!--报价-->
        <el-table-column
          prop="orderNotaxPrice"
          :label="$t('bidMod.doQuote')"
          min-width="60"
          show-overflow-tooltip
        />

        <!--金额-->
        <el-table-column
          prop="orderNotaxTotalPrice"
          :label="$t('orderMod.buyerOrderSynergy.amount')"
          min-width="60"
          show-overflow-tooltip
        />

        <!--供应商-->
        <el-table-column
          prop="vendorName"
          :label="$t('bidMod.provider')"
          min-width="150"
          show-overflow-tooltip
        />
      </el-table>
    </template>
  </div>
</template>

<script>
/**
 * 竞价大厅
 */
import { compBuyerHttp } from 'modb@/competition/api'
import LineChart from './competitionHall/lineChart'
import DynamicCutoffTime from 'lib@/components/dynamic-cutoff-time'

export default {
  name: 'CompetitionHall',

  components: {
    LineChart,
    DynamicCutoffTime
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
    // 是否当前tab页
    isCurrentActiveTab: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      filterHallVisible: true,
      showType: 'trendList',
      showTimeText: '',
      requirementLineList: [],
      rankList: [],
      trendList: [],
      rowItemName: null,
      hisList: [],
      showTypeList: [
        // 最新实际报价排名
        {
          value: 'rankList',
          label: this.$t('bidMod.quoteRankings'),
          id: 1
        },
        // 价格走势
        {
          value: 'trendList',
          label: this.$t('bidMod.priceTrend'),
          id: 2
        },
        // 报价列表
        {
          value: 'hisList',
          label: this.$t('bidMod.quotationList'),
          id: 3
        }
      ]
    }
  },

  computed: {
    minPercentLabel () {
      if (this.baseInfo.scoreRuleType === 'MIN_PRICE') {
        return `${this.$t('bidMod.minimalPercentage')}(%)`
      }
      if (this.baseInfo.scoreRuleType === 'MAX_PRICE') {
        return `${this.$t('bidMod.minimumPercentage')}(%)`
      }
      return ''
    },

    minAmountLabel () {
      if (this.baseInfo.scoreRuleType === 'MIN_PRICE') {
        return this.$t('bidMod.minimumPriceReduction')
      }
      if (this.baseInfo.scoreRuleType === 'MAX_PRICE') {
        return this.$t('bidMod.minimumPriceIncrease')
      }
      return ''
    },

    // 折线图数据
    chartData () {
      const json = {
        xAxisData: [],
        seriesData: [],
        vendorInfos: [],
        legend: this.rowItemName
      }
      this.trendList.forEach(val => {
        json.xAxisData.push(this.$dayjsParse(val.submitTime))
        json.seriesData.push(val.orderNotaxPrice)
        json.vendorInfos.push(val.vendorName)
      })
      return json
    }
  },

  watch: {
    isCurrentActiveTab: {
      handler (newValue, oldValue) {
        // 切换到当前标签页
        if (newValue && !oldValue) {
          this.getHallData()
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 获取列表数据 */
    async getHallData () {
      if (!this.baseInfo.projectId) {
        return
      }

      const response = await compBuyerHttp.hall.getHallInfo(this.baseInfo.projectId)
      if (response && response.data) {
        this.requirementLineList = response.data.itemList || []

        this.$nextTick(() => {
          if (
            !this.rankList.length &&
            !this.trendList.length &&
            !this.hisList.length &&
            this.requirementLineList.length
          ) {
            this.rowClick(this.requirementLineList[0])
          }
        })
      }
    },

    /* 行点击 */
    async rowClick (row) {
      if (!row.compVendorCount || row.compVendorCount < 0) {
        this.filterHallVisible = false
        return
      }

      // 物料名称
      this.rowItemName = row.itemDesc

      const response = await compBuyerHttp.hall.getItemDetails(row.souItemId)
      if (response && response.data) {
        const {
          rankList = [],
          trendList = [],
          hisList = []
        } = response.data
        this.rankList = rankList
        this.trendList = trendList
        this.hisList = hisList
        this.filterHallVisible = true
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.hall-info {
  :deep(.el-col p > span) {
    padding-right: 11px;
  }
}

.filter-report-wrap {
  display: flex;
  width: 100%;
  margin: 15px 0;
  .label {
    width: 65px;
    padding-right: 10px;
    text-align: right;
    line-height: 30px;
  }
  .content {
    width: 150px;
  }
}
</style>
