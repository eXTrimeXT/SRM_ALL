<template>
  <el-table
    :data="tableData"
    style="width: 100%"
    border
    height="250px"
  >
    <el-table-column
      align="center"
      type="index"
      width="65"
      label="名次"
    />

    <!--报价供应商-->
    <el-table-column
      align="center"
      prop="vendorName"
      label="报价供应商"
      min-width="150"
      show-overflow-tooltip
    />

    <!--未税总价-->
    <el-table-column
      align="center"
      prop="orderNotaxPrice"
      label="未税总价"
      min-width="150"
    />

    <!--涨降幅（%）-->
    <el-table-column
      align="center"
      prop="floating"
      :label="`${getEvaluateMethodFlag(baseInfo.evaluateMethod)}(%)`"
      min-width="150"
    />

    <!--未税总金额-->
    <el-table-column
      align="center"
      prop="orderNotaxTotalPrice"
      label="未税总金额"
      min-width="150"
    />
  </el-table>
</template>

<script>
/**
 * 总价实时排名
 */
import { getEvaluateMethodFlag } from 'lib@/composition/competition/utils'

export default {
  name: 'TotalPriceRanking',

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
      getEvaluateMethodFlag
    }
  }
}
</script>
