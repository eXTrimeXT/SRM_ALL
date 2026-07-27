<template>
  <el-table
    :data="tableData"
    style="width: 100%"
    border
    height="250px"
  >
  <!-- 名次 -->
    <el-table-column
      align="center"
      type="index"
      width="65"
      :label="$t('bidMod.competition')"
    />

    <!--报价供应商-->
    <el-table-column
      align="center"
      prop="vendorName"
      :label="$t('bidMod.bidingvendorName1')"
      min-width="150"
      show-overflow-tooltip
    >
      <template v-slot="scope">
        <span v-if="scope.row.vendorId !== userInfo.companyId && hideVendor">xxx</span>
        <span v-else>{{ computedVendorObj(scope.row.souItemid).vendorName }}</span>
      </template>
    </el-table-column>

    <!--未税单价-->
    <el-table-column
      align="center"
      prop="orderNotaxPrice"
      :label="$t('bid_mod.untaxedPrice')"
      min-width="150"
    >
      <template v-slot="scope">
        <span v-if="scope.row.vendorId !== userInfo.companyId && hidePrice">xxx</span>
        <span v-else>{{ scope.row.orderNotaxPrice }}</span>
      </template>
    </el-table-column>

    <!--涨降幅（%）-->
    <el-table-column
      align="center"
      prop="pricePercent"
      :label="`${getEvaluateMethodFlag(baseInfo.scoreRuleType)}(%)`"
      min-width="150"
    >
      <template v-slot="scope">
        <span v-if="scope.row.vendorId !== userInfo.companyId && hidePrice">xxx</span>
        <span v-else>{{ scope.row.pricePercent }}</span>
      </template>
    </el-table-column>

    <!--涨降额 -->
    <el-table-column
      align="center"
      prop="orderNotaxPriceAmount"
      :label="`${getEvaluateMethodFlagAmount(baseInfo.scoreRuleType)}`"
      min-width="150"
    >
      <template v-slot="scope">
        <span v-if="scope.row.vendorId !== userInfo.companyId && hidePrice">xxx</span>
        <span v-else>{{ scope.row.orderNotaxPriceAmount }}</span>
      </template>
    </el-table-column>

    <!--未税金额-->
    <el-table-column
      align="center"
      prop="orderNotaxTotalPrice"
      :label="$t('contractMod.unAmount')"
      min-width="150"
    >
      <template v-slot="scope">
        <span v-if="scope.row.vendorId !== userInfo.companyId && hidePrice">xxx</span>
        <span v-else>{{ scope.row.orderNotaxTotalPrice }}</span>
      </template>
    </el-table-column>

    <!-- 报价时间 -->
    <el-table-column
      align="center"
      prop="submitTime"
      :label="$t('bidMod.quotedTime')"
      min-width="150"
      :formatter="(row, column, cellValue) => $parseTime(cellValue)"
    />
  </el-table>
</template>

<script>
/**
 * 单价实时排名
 */
import { getEvaluateMethodFlag, getEvaluateMethodFlagAmount } from 'lib@/composition/competition/utils'
import { mapState } from 'vuex'
/**
 * HIDDEN_USER_HIDDEN_PRICE 隐藏身份隐藏报价
 * HIDDEN_USER_OPEN_PRICE 隐藏身份公开报价
 * OPEN_USER_HIDDEN_PRICE 公开身份隐藏报价
 * OPEN_USER_OPEN_PRICE 公开身份公开报价
 */
export default {
  name: 'PriceRanking',

  props: {
    tableData: {
      type: Array,
      default: () => []
    },
    baseInfo: {
      type: Object,
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
    ...mapState({
      userInfo: state => state.user.userInfo
    }),
    hideVendor () {
      return ['HIDDEN_USER_HIDDEN_PRICE', 'HIDDEN_USER_OPEN_PRICE'].includes(this.baseInfo.scopeRule)
    },
    hidePrice () {
      return ['HIDDEN_USER_HIDDEN_PRICE', 'OPEN_USER_HIDDEN_PRICE'].includes(this.baseInfo.scopeRule)
    },
    computedVendorObj (souItemId) {
      return (souItemId) => {
        return this.baseInfo.vendorList.find(item => item.souItemId === souItemId) || {}
      }
    }
  }
}
</script>
