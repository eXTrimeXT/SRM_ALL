<template>
  <div class="quote-record-wrap">
    <el-table
      :data="tableData"
      style="width: 100%"
      border
      height="250px"
    >
    <!-- 序号 -->
      <el-table-column
        align="center"
        type="index"
        :label="$t('components.common.sort')"
        width="65"
      />

      <!--无料号寻源-->
      <el-table-column
        align="center"
        prop="noCodeItem"
        :label="$t('sourcingBuyer.isMaterialSourcing')"
        min-width="150"
      >
        <template v-slot="scope">
          <span>{{ $getDictLabel('YES_OR_NO', scope.row.noCodeItem) }}</span>
        </template>
      </el-table-column>

      <!--物料编码-->
      <el-table-column
        align="center"
        prop="itemCode"
        :label="$t('common.materialCode')"
        min-width="150"
      />

      <!--物料名称-->
      <el-table-column
        align="center"
        prop="itemDesc"
        :label="$t('common.materialName')"
        min-width="150"
      />

      <!--单位-->
      <el-table-column
        align="center"
        prop="unit"
        :label="$t('dataConfMod.settingGuide.step3.3')"
        min-width="150"
      >
        <template v-slot="scope">
          <span>{{ $getDictLabel('unit', scope.row.unit) }}</span>
        </template>
      </el-table-column>

      <!--需求数量-->
      <el-table-column
        align="center"
        prop="requireQuantity"
        :label="$t('bidMod.demandQuantity2')"
        min-width="150"
      />

      <!--未税单价-->
      <el-table-column
        align="center"
        prop="orderNotaxPrice"
        :label="$t('bid_mod.untaxedPrice')"
        min-width="150"
      />

      <!--报价时间-->
      <el-table-column
        align="center"
        prop="submitTime"
        :label="$t('bidMod.quotedTime')"
        min-width="150"
        :formatter="(row, column, cellValue) => $parseTime(cellValue)"
      />

      <!--涨降幅（%）-->
      <el-table-column
        align="center"
        prop="pricePercent"
        :label="`${getEvaluateMethodFlag(baseInfo.scoreRuleType)}(%)`"
        min-width="150"
      />

      <!--涨降额（%）-->
      <el-table-column
        align="center"
        prop="orderNotaxPriceAmount"
        :label="`${getEvaluateMethodFlagAmount(baseInfo.scoreRuleType)}`"
        min-width="150"
      />

      <!--附件-->
      <!-- <SrmCommonFile
        type="table-column"
        :table-column-options="{
          label: '附件',
          prop: 'docId',
          nameProp: 'fileName',
        }"
        readonly
      /> -->
    </el-table>
  </div>
</template>

<script>
/**
 * 报价记录
 */
import { carVendorHttp } from 'mods@/competitionSupplier/api'
import { getEvaluateMethodFlag, getEvaluateMethodFlagAmount } from 'lib@/composition/competition/utils'

export default {
  name: 'QuoteRecord',

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
  }
}
</script>
