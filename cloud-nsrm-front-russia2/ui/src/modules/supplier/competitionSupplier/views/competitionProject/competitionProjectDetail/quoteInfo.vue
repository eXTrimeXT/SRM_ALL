<template>
  <div class="quote-info">
    <div class="the_display_content">
      <SrmRow>
        <SrmCol :init-col="3">
          <span>{{ $t('bidMod.scoringRubric') }}</span>{{ $getDictLabel('SOU_COMP_SCORE_RULE_TYPE', baseInfo.scoreRuleType) }}
        </SrmCol>
      </SrmRow>
    </div>

    <p>
      <span style="padding-right: 11px">{{ $t('bidMod.commercialMessage') }}</span>
    </p>

    <el-table
      :data="orderLineList"
      style="width: 100%"
      border
      height="180px"
    >
      <el-table-column
        align="center"
        type="index"
        :label="$t('bidMod.sequence')"
        width="65"
      />

      <!--评选情况-->
      <el-table-column
        align="center"
        prop="winStatus"
        :label="$t('bidMod.selection')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, value) => $getDictLabel('SOU_WIN_STATUS', value)"
      />

      <!--排名-->
      <el-table-column
        align="center"
        prop="ranking"
        :label="$t('bidMod.mathematics')"
        width="70"
        show-overflow-tooltip
      />

      <!--业务实体-->
      <el-table-column
        align="center"
        prop="orgOuName"
        :label="$t('bid_mod.businessEntity')"
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
      />

      <!--物料名称-->
      <el-table-column
        align="center"
        prop="itemDesc"
        :label="$t('bidMod.itemDesc')"
        width="150"
        show-overflow-tooltip
      />

      <!--采购数量-->
      <el-table-column
        align="center"
        prop="requireQuantity"
        :label="$t('bidMod.orderSize')"
        width="100"
        show-overflow-tooltip
      />

      <!--单位-->
      <el-table-column
        align="center"
        prop="unit"
        :label="$t('bidMod.tech_Unit')"
        width="70"
        show-overflow-tooltip
        :formatter="(row, column, value) => $getDictLabel('unit', value)"
      />

      <!--未税报价-->
      <el-table-column
        align="center"
        prop="orderNotaxPrice"
        :label="$t('bidMod.quotenotaxPrice')"
        width="100"
        show-overflow-tooltip
      />

      <!--最低价 反向竞价-->
      <el-table-column
        v-if="baseInfo.scoreRuleType === 'MIN_PRICE'"
        align="center"
        prop="latestOrderNotaxPrice"
        :label="baseInfo.scoreRuleType === 'MIN_PRICE' ? $t('competition.lowestPrice') : $t('competition.highestPrice')"
        width="150"
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

      <!--备注-->
      <el-table-column
        align="center"
        prop="remark"
        :label="$t('common.remark')"
        width="100"
        show-overflow-tooltip
      />
    </el-table>
  </div>
</template>

<script>
/**
 * 报价明细
 */
import { compVendorHttp } from 'mods@/competitionSupplier/api'

export default {
  name: 'QuoteInfo',

  inject: ['attrsParamsRow'],

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
      orderLineList: []
    }
  },

  watch: {
    isCurrentActiveTab: {
      handler (newValue, oldValue) {
        // 切换到当前标签页
        if (newValue && !oldValue) {
          this.getOrderDetails()
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 获取详情 */
    async getOrderDetails () {
      if (!this.attrsParamsRow.projectId) {
        return
      }

      const response = await compVendorHttp.order.getOrderDetails({
        projectId: this.attrsParamsRow.projectId
      })
      if (response && response.data) {
        this.orderLineList = response.data.orderDetails || []
      }
    }
  }
}
</script>
