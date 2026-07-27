<template>
  <div class="project-requirement">
    <el-collapse v-model="activeDims" class="tab-form-style">
      <!--项目需求-->
      <el-collapse-item title="项目需求" name="1">
        <srm-row>
          <srm-col :init-col="1">
            <div style="padding-bottom: 10px; font-size: 13px">
              {{ $t("bidMod.bondDesc2") }}: {{ bidingBase.requireDesc }}
            </div>
          </srm-col>
        </srm-row>

        <el-table :data="list" style="width: 100%" border height="345px">
          <el-table-column align="center" type="index" :label="$t('common.sort')" width="65" />

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
            prop="itemCode"
            :label="$t('bidMod.targetNum')"
            width="120"
            show-overflow-tooltip
          >
            <template slot-scope="{row}">
              {{ row.noCodeItem !== 'Y' ? row.itemCode : '' }}
            </template>
          </el-table-column>

          <!--物料名称-->
          <el-table-column
            align="center"
            prop="itemDesc"
            :label="$t('bidMod.targetDesc')"
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

          <!--价格类型-->
          <!-- <el-table-column
            align="center"
            prop="priceType"
            :label="$t('bid_mod.priceType')"
            width="150"
            show-overflow-tooltip
            :formatter="(row, column, value) => $getDictLabel('PRICE_TYPE', value)"
          /> -->
          <el-table-column
            align="center"
            prop="priceTypeName"
            :label="$t('bid_mod.priceType')"
            width="150"
            show-overflow-tooltip
          />

          <!--需求数量-->
          <el-table-column
            align="center"
            prop="requireQuantity"
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
          <!-- <el-table-column
            align="center"
            prop="purchaseType"
            :label="$t('bid_mod.purchaseType')"
            width="150"
            show-overflow-tooltip
            :formatter="(row, column, value) => $getDictLabel('PURCHASE_TYPE', value)"
          /> -->
          <el-table-column
            align="center"
            prop="purchaseTypeName"
            :label="$t('bid_mod.purchaseType')"
            width="150"
            show-overflow-tooltip
          />

          <!--运输方式-->
          <!-- <el-table-column
            align="center"
            prop="transportType"
            show-overflow-tooltip
            :label="$t('bid_mod.transportType')"
            width="150"
            :formatter="(row, column, value) => $getDictLabel('TRANSF_TYPE', value)"
          /> -->
          <el-table-column
            align="center"
            prop="transportTypeName"
            show-overflow-tooltip
            :label="$t('bid_mod.transportType')"
            width="150"
          />

          <!--贸易条款-->
          <!-- <el-table-column
            align="center"
            prop="tradeTerm"
            :label="$t('bid_mod.tradeTerm')"
            show-overflow-tooltip
            width="150"
            :formatter="(row, column, value) => $getDictLabel('trade_clause', value)"
          /> -->
          <el-table-column
            align="center"
            prop="tradeTermName"
            :label="$t('bid_mod.tradeTerm')"
            show-overflow-tooltip
            width="150"
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
            prop="remark"
            :label="$t('bid_mod.remark')"
            width="100"
          />
        </el-table>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script>
/**
 * 项目需求
 */
import { targetNumReveal } from 'lib@/composition/origin/composition'
import RenderAsyncText from '@/library/components/provice-city/renderAsyncText'
import ouInfoPopover from '@/library/components/ou-info-popover'
import { getToken } from '@/utils/auth'

export default {
  name: 'ProjectRequirement',
  components: {
    RenderAsyncText,
    ouInfoPopover
  },
  props: {
    bidingBase: {
      type: Object
    },
    pricingType: {
      type: Object
    },
    requireInfo: {
      type: Array
    }
  },
  data () {
    return {
      activeDims: ['1']
    }
  },
  computed: {
    list () {
      return this.requireInfo
    }
  },
  methods: {
    /* 物料编码格式化 */
    targetNumRevealFilter (value) {
      return targetNumReveal(value)
    }
  }
}
</script>
