<template>
  <div class="project-requirement">
    <srm-row>
      <srm-col :span="24">
        <div style="padding-bottom: 10px; font-size: 13px">
          {{ $t("bidMod.bondDesc2") }}: {{ bargainBase.requireDesc }}
        </div>
      </srm-col>
    </srm-row>

    <p>
      <span style="font-size: 14px">{{ $t("bidMod.demandDetail") }}</span>
      <!--导出 暂时隐藏-->
      <el-button
        v-if="false"
        type="primary"
        class="detail-pbtn"
        @click="exportList"
      >
        {{ $t("common.export") }}
      </el-button>
    </p>

    <el-table
      :data="brgRequirementLineList"
      style="width: 100%"
      border
      height="345px"
    >
      <el-table-column
        align="center"
        type="index"
        :label="$t('common.sort')"
        width="65"
      />

      <!--业务实体-->
      <el-table-column
        align="center"
        prop="orgOuName"
        :label="$t('bid_mod.businessEntity')"
        width="150"
        show-overflow-tooltip
      />

      <!--库存组织-->
      <el-table-column
        align="center"
        prop="orgInvName"
        :label="$t('bid_mod.inv')"
        width="150"
        show-overflow-tooltip
      />

      <!--物料编码 无料号不显示-->
      <el-table-column
        align="center"
        prop="targetNum"
        :label="$t('bidMod.targetNum')"
        width="120"
        show-overflow-tooltip
        :formatter="(row, column, value) => targetNumRevealFilter(value)"
      />

      <!--物料描述-->
      <el-table-column
        align="center"
        prop="targetDesc"
        :label="$t('bidMod.itemName')"
        width="150"
        show-overflow-tooltip
      />

      <!--采购分类-->
      <el-table-column
        align="center"
        prop="categoryName"
        :label="$t('bidMod.purcategoryName')"
        width="150"
        show-overflow-tooltip
      />

      <!--组合-->
      <el-table-column
        align="center"
        prop="itemGroup"
        :label="$t('bidMod.itemGroup')"
        width="100"
        show-overflow-tooltip
      />

      <!--交货地点(仅外协)-->
      <el-table-column
        align="center"
        prop="deliveryPlace"
        :label="$t('bid_mod.deliveryPlace')"
        width="150"
      >
        <template v-slot="scope">
          <RenderAsyncText :cell-value="scope.row.deliveryPlace" />
        </template>
      </el-table-column>

      <!--价格类型-->
      <el-table-column
        align="center"
        prop="priceType"
        :label="$t('bid_mod.priceType')"
        width="150"
        show-overflow-tooltip
        :formatter="(row, column, value) => $getDictLabel('PRICE_TYPE', value)"
      />

      <!--需求数量-->
      <el-table-column
        align="center"
        prop="quantity"
        :label="$t('bidMod.demandQuantity2')"
        width="110"
        show-overflow-tooltip
      />

      <!--公式值 公式报价才显示-->
      <el-table-column
        v-if="pricingType.isFormulPricing"
        align="center"
        prop="formulaValue"
        :label="$t('bid_mod.formulaValue')"
        width="150"
        show-overflow-tooltip
      />

      <!--采购类型-->
      <el-table-column
        align="center"
        prop="purchaseType"
        :label="$t('bid_mod.purchaseType')"
        width="150"
        show-overflow-tooltip
        :formatter="(row, column, value) => $getDictLabel('PURCHASE_TYPE', value)"
      />

      <!--运输方式-->
      <el-table-column
        align="center"
        prop="transportType"
        show-overflow-tooltip
        :label="$t('bid_mod.transportType')"
        width="150"
        :formatter="(row, column, value) => $getDictLabel('TRANSF_TYPE', value)"
      />

      <!--贸易条款-->
      <el-table-column
        align="center"
        prop="tradeTerm"
        :label="$t('bid_mod.tradeTerm')"
        show-overflow-tooltip
        width="150"
        :formatter="(row, column, value) => $getDictLabel('trade_clause', value)"
      />

      <!--定价开始时间-->
      <el-table-column
        align="center"
        prop="priceStartTime"
        :label="$t('bidMod.priceStartTime')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, value) => value ? value.slice(0, 10) : ''"
      />

      <!--定价结束时间-->
      <el-table-column
        align="center"
        prop="priceEndTime"
        :label="$t('bidMod.priceEndTime')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, value) => value ? value.slice(0, 10) : ''"
      />

      <!--备注-->
      <el-table-column
        align="center"
        prop="comments"
        :label="$t('bid_mod.remark')"
        width="100"
      />
    </el-table>
  </div>
</template>

<script>
/**
 * 项目需求
 */
import { targetNumReveal } from 'lib@/composition/origin/composition'
import RenderAsyncText from '@/library/components/provice-city/renderAsyncText'

export default {
  name: 'ProjectRequirement',
  components: {
    RenderAsyncText
  },
  props: {
    isCurrentTab: {
      type: Boolean
    },
    bargainId: {
      type: [Number, String]
    },
    bargainBase: {
      type: Object
    },
    pricingType: {
      type: Object
    }
  },
  data () {
    return {
      isGetDataStatus: false,
      brgRequirementLineList: []
    }
  },
  watch: {
    isCurrentTab: {
      handler (newValue, oldValue) {
        if (newValue && !oldValue && !this.isGetDataStatus) {
          this.isGetDataStatus = true
          this.getDidRequirementLine()
        }
      },
      immediate: true
    }
  },
  methods: {
    /* 物料编码格式化 */
    targetNumRevealFilter (value) {
      return targetNumReveal(value)
    },

    /* 获取需求列表 */
    getDidRequirementLine () {
      this.$http({
        url: `/api-brg/supplierCooperate/orderHead/getBargain/requireInfo/${this.bargainId}`,
        method: 'GET',
        loading: true
      }).then(({ data }) => {
        this.brgRequirementLineList = (data || []).map(
          ({ deliveryPlace, ...rest }) => {
            let d = null
            if (deliveryPlace) {
              try {
                d = JSON.parse(deliveryPlace)
              } catch (e) {
                console.log(e)
              }
            }
            return {
              ...rest,
              deliveryPlace: d
            }
          }
        )
      })
    },

    exportList () {}
  }
}
</script>
