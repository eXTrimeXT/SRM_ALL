<template>
  <el-table
    :data="tableData"
    style="width: 100%"
    border
    height="250px"
  >
    <el-table-column
      type="index"
      :label="$t('common.sort')"
      width="50"
    />

    <!--报价供应商-->
    <el-table-column
      prop="vendorName"
      label="报价供应商"
      min-width="150"
      show-overflow-tooltip
    >
      <template v-slot="scope">
        <span v-if="scope.row.vendorId !== userInfo.companyId && hideVendor">xxx</span>
        <span v-else>{{ computedVendorObj(scope.row.vendorId).vendorName }}</span>
      </template>
    </el-table-column>

    <!--未税单价-->
    <el-table-column
      align="right"
      prop="orderNotaxPrice"
      label="未税单价"
      min-width="150"
    >
      <template v-slot="scope">
        <span v-if="scope.row.vendorId !== userInfo.companyId && hidePrice">xxx</span>
        <span v-else>{{ scope.row.orderNotaxPrice }}</span>
      </template>
    </el-table-column>

    <!--涨降幅（%）-->
    <el-table-column
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
      align="right"
      prop="orderNotaxTotalPrice"
      label="未税金额"
      min-width="150"
    >
      <template v-slot="scope">
        <span v-if="scope.row.vendorId !== userInfo.companyId && hidePrice">xxx</span>
        <span v-else>{{ scope.row.orderNotaxTotalPrice }}</span>
      </template>
    </el-table-column>

    <!-- 报价时间 -->
    <el-table-column
      prop="submitTime"
      label="报价时间"
      min-width="150"
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
    computedVendorObj (vendorId) {
      return (vendorId) => {
        return this.baseInfo.vendorList.find(item => item.vendorId === vendorId) || {}
      }
    }
  }
}
</script>
