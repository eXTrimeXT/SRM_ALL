<template>
  <div class="competition-hall">
    <div class="hall-info">
      <SrmRow>
        <!--招标方式-->
        <SrmCol>
          <p>
            <span>{{ $t('bidMod.biddingType') }}:</span>{{ $getDictLabel('SOU_COMP_SCORE_RULE_TYPE', baseInfo.scoreRuleType) }}
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
            <span>{{ $t('bid_mod.standardCurrency') }}:</span>
            {{ $getDictLabel('currency', baseInfo.standardCurrency) }}
          </p>
        </SrmCol>

        <SrmCol :init-col="2">
          <DynamicCutoffTime
            :label="$t('bidMod.endDistance')"
            :deadline-time="baseInfo.orderEndTime"
            style="font-size: 13px;"
          />
        </SrmCol>
      </SrmRow>
    </div>

    <el-table
      :data="requirementLineList"
      style="width: 100%"
      border
      max-height="251px"
      highlight-current-row
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
        :label="$t('bidMod.materialDesc')"
        min-width="200"
        show-overflow-tooltip
      />

      <!--单位-->
      <el-table-column
        align="center"
        prop="unit"
        :label="$t('bidMod.unit')"
        width="60"
        show-overflow-tooltip
        :formatter="(row, column, value) => $getDictLabel('unit', value)"
      />

      <!--需求数量-->
      <el-table-column
        align="center"
        prop="requireQuantity"
        :label="$t('bidMod.appraisRequired')"
        width="80"
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

      <!--本轮最低价 / 本轮最高价-->
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
    </el-table>
  </div>
</template>

<script>
/**
 * 竞价大厅
 */
import DynamicCutoffTime from '@/library/components/dynamic-cutoff-time'

export default {
  name: 'CompetitionHall',

  inject: ['attrsParamsRow'],

  components: {
    DynamicCutoffTime
  },

  props: {
    baseInfo: {
      type: Object,
      default: () => { /* noting */ }
    },
    // 是否当前tab页
    isCurrentActiveTab: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      requirementLineList: []
    }
  },

  computed: {
    minAmountLabel () {
      if (this.baseInfo.scoreRuleType === 'MIN_PRICE') {
        // 最小降价金额
        return this.$t('bidMod.minimumPriceReduction')
      }
      if (this.baseInfo.scoreRuleType === 'MAX_PRICE') {
        // 最小涨价金额
        return this.$t('bidMod.minimumPriceIncrease')
      }
      return ''
    },
    minPercentLabel () {
      if (this.baseInfo.scoreRuleType === 'MIN_PRICE') {
        // 最小降幅百分比
        return `${this.$t('bidMod.minimalPercentage')}(%)`
      }
      if (this.baseInfo.scoreRuleType === 'MAX_PRICE') {
        // 最小涨幅百分比
        return `${this.$t('bidMod.minimumPercentage')}(%)`
      }
      return ''
    }
  },

  watch: {
    isCurrentActiveTab: {
      handler (newValue, oldValue) {
        // 切换到当前标签页
        if (newValue && !oldValue) {
          this.getHallInfo()
        }
      },
      immediate: true
    }
  },

  methods: {
    async getHallInfo () {
      if (!this.attrsParamsRow.projectId) {
        return
      }

      const response = await this.$api.comp.share.queryHallInfo(this.attrsParamsRow.projectId)
      if (response && response.data) {
        this.requirementLineList = response.data.itemList || []
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.hall-info {
  border-bottom: 1px solid #eee;
  :deep(.el-col p > span)  {
    padding-right: 11px;
  }
}
</style>
