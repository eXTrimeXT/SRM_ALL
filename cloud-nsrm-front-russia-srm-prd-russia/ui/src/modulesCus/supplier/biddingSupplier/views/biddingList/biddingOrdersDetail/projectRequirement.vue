<template>
  <div class="project-requirement">
    <SrmRow>
      <SrmCol>
        <div style="padding-bottom: 10px; font-size: 13px">
          {{ $t("bidMod.bondDesc2") }}: {{ biddingBase.requireDesc }}
        </div>
      </SrmCol>
    </SrmRow>

    <p>
      <span style="font-size: 14px">{{ $t("bidMod.demandDetail") }}</span>
    </p>

    <el-table
      :data="itemList.slice((currentPage - 1) * pageSize, currentPage * pageSize)"
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
        prop="itemCode"
        :label="$t('bidMod.targetNum')"
        width="120"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => targetNumReveal(cellValue)"
      />

      <!--物料名称-->
      <el-table-column
        align="center"
        prop="itemDesc"
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
        :formatter="(row, column, cellValue) => $getDictLabel('PRICE_TYPE', cellValue)"
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
        v-if="pricingType.isFormulaPricing"
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
        :formatter="(row, column, cellValue) => $getDictLabel('PURCHASE_TYPE', cellValue)"
      />

      <!--运输方式-->
      <el-table-column
        align="center"
        prop="transportType"
        show-overflow-tooltip
        :label="$t('bid_mod.transportType')"
        width="150"
        :formatter="(row, column, cellValue) => $getDictLabel('TRANSF_TYPE', cellValue)"
      />

      <!--贸易条款-->
      <el-table-column
        align="center"
        prop="tradeTerm"
        :label="$t('bid_mod.tradeTerm')"
        show-overflow-tooltip
        width="150"
        :formatter="(row, column, cellValue) => $getDictLabel('trade_clause', cellValue)"
      />

      <!--定价开始时间-->
      <el-table-column
        align="center"
        prop="priceStartTime"
        :label="$t('bidMod.priceStartTime')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $dayjsParse(cellValue)"
      />

      <!--定价结束时间-->
      <el-table-column
        align="center"
        prop="priceEndTime"
        :label="$t('bidMod.priceEndTime')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $dayjsParse(cellValue)"
      />

      <!--备注-->
      <el-table-column
        align="center"
        prop="remark"
        :label="$t('bid_mod.remark')"
        width="100"
      />
    </el-table>
    <div style="width: 100%; margin-top: 10px">
      <CPagination
        style="margin: 0"
        class="c-query-table-pagination"
        :total="itemList.length"
        :page-num="currentPage"
        layout="total, sizes, prev, pager, next"
        :page-size="pageSize"
        :page-sizes="[5, 10, 15]"
        @current-change="handleCurrentChange"
        @size-change="handleSizeChange"
      />
    </div>
  </div>
</template>

<script>
/**
 * 项目需求
 */
import { bidSupplierHttp } from 'modcs@/biddingSupplier/api'
import { targetNumReveal } from 'lib@/composition/origin/composition'
import RenderAsyncText from '@/library/components/provice-city/renderAsyncText'
import CPagination from 'lib@/components/c-pagination'

export default {
  name: 'ProjectRequirement',

  components: { RenderAsyncText, CPagination },

  props: {
    isCurrentTab: {
      type: Boolean,
      required: true
    },
    projectId: {
      type: [Number, String],
      required: true
    },
    biddingBase: {
      type: Object,
      required: true
    },
    pricingType: {
      type: Object,
      required: true
    }
  },

  data () {
    return {
      isGetDataStatus: false,
      itemList: [],
      targetNumReveal,
      currentPage: 1,
      pageSize: 15
    }
  },

  watch: {
    isCurrentTab: {
      handler (newValue, oldValue) {
        if (newValue && !oldValue && !this.isGetDataStatus) {
          this.isGetDataStatus = true
          this.getRequireInfoData()
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 获取需求列表 */
    async getRequireInfoData () {
      const response = await bidSupplierHttp.order.requireInfo(this.projectId)
      if (response && response.data && Array.isArray(response.data)) {
        this.itemList = response.data.concat()
      }
    },

    /* 物料列表页码大小改变 */
    handleSizeChange (val) {
      this.currentPage = 1
      this.pageSize = val
    },

    /* 物料列表翻页 */
    handleCurrentChange (val) {
      this.currentPage = val
    }
  }
}
</script>
