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
        type="index"
        :label="$t('common.sort')"
        width="50"
      />

      <!--业务实体-->
      <el-table-column
        prop="orgOuName"
        :label="$t('bid_mod.businessEntity')"
        width="150"
        show-overflow-tooltip
      />

      <!--库存组织-->
      <el-table-column
        prop="orgInvName"
        :label="$t('bid_mod.inv')"
        width="150"
        show-overflow-tooltip
      />

      <!--物料编码 无料号不显示-->
      <el-table-column
        prop="itemCode"
        :label="$t('bidMod.targetNum')"
        width="120"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => targetNumReveal(cellValue)"
      />

      <!--物料名称-->
      <el-table-column
        prop="itemDesc"
        :label="$t('bidMod.itemName')"
        width="150"
        show-overflow-tooltip
      />

      <!--采购分类-->
      <el-table-column
        prop="categoryName"
        :label="$t('bidMod.purcategoryName')"
        width="150"
        show-overflow-tooltip
      />

      <!--组合-->
      <el-table-column
        prop="itemGroup"
        :label="$t('bidMod.itemGroup')"
        width="100"
        show-overflow-tooltip
      />

      <!--交货地点(仅外协)-->
      <el-table-column
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
        prop="priceType"
        :label="$t('bid_mod.priceType')"
        width="150"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('PRICE_TYPE', cellValue)"
      />

      <!--需求数量-->
      <el-table-column
        prop="requireQuantity"
        :label="$t('bidMod.demandQuantity2')"
        width="110"
        show-overflow-tooltip
      />

      <!--公式值 公式报价才显示-->
      <el-table-column
        v-if="pricingType.isFormulaPricing"
        prop="formulaValue"
        :label="$t('bid_mod.formulaValue')"
        width="150"
        show-overflow-tooltip
      />

      <!--t 是否阶梯报价-->
      <el-table-column
        v-if="pricingType.isSimplePricing"
        prop="isLadder"
        :label="$t('bidMod.isLadder')"
        width="120"
        show-overflow-tooltip
        :formatter="(row,column,cellValue) => $getDictLabel('YES_OR_NO', cellValue)"
      />

      <!--t 阶梯价报价-->
      <el-table-column
        prop="ladderList"
        :label="$t('bidMod.ladderQuote')"
        width="110"
      >
        <template v-slot="scope">
          <el-button
            type="text"
            :disabled="scope.row.isLadder !== 'Y'"
            @click="ladderPriceClick(scope.$index,scope.row)"
          >
            {{ $t('bidMod.ladderPrice') }}
          </el-button>
        </template>
      </el-table-column>

      <!--采购类型-->
      <el-table-column
        prop="purchaseType"
        :label="$t('bid_mod.purchaseType')"
        width="150"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('PURCHASE_TYPE', cellValue)"
      />

      <!--运输方式-->
      <el-table-column
        prop="transportType"
        show-overflow-tooltip
        :label="$t('bid_mod.transportType')"
        width="150"
        :formatter="(row, column, cellValue) => $getDictLabel('TRANSF_TYPE', cellValue)"
      />

      <!--贸易条款-->
      <el-table-column
        prop="tradeTerm"
        :label="$t('bid_mod.tradeTerm')"
        show-overflow-tooltip
        width="150"
        :formatter="(row, column, cellValue) => $getDictLabel('trade_clause', cellValue)"
      />

      <!--定价开始时间-->
      <el-table-column
        prop="priceStartTime"
        :label="$t('bidMod.priceStartTime')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $dayjsParse(cellValue)"
      />

      <!--定价结束时间-->
      <el-table-column
        prop="priceEndTime"
        :label="$t('bidMod.priceEndTime')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $dayjsParse(cellValue)"
      />

      <!--备注-->
      <el-table-column
        prop="remark"
        :label="$t('bid_mod.remark')"
        width="100"
      />
    </el-table>

    <!--阶梯价-->
    <LadderPriceDetail
      v-if="ladderPriceDetailVisible"
      :visible.sync="ladderPriceDetailVisible"
      :business-type="BUSINESS_TYPE_ENUM.BIDDING_LTS"
      :edit-row="editRow"
      :readonly="true"
    />
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
import { bidSupplierHttp } from 'mods@/biddingSupplier/api'
import { targetNumReveal } from 'lib@/composition/origin/composition'
import RenderAsyncText from '@/library/components/provice-city/renderAsyncText'
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import LadderPriceDetail from 'lib@/composition/origin/ladderPrice'
import CPagination from 'lib@/components/c-pagination'

export default {
  name: 'ProjectRequirement',

  components: { RenderAsyncText, LadderPriceDetail, CPagination },

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
      ladderPriceDetailVisible: false,
      editRow: null,
      BUSINESS_TYPE_ENUM,
      currentPage:1,
      pageSize:15
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
    /* 阶梯价 */
    ladderPriceClick (index, row) {
      console.log('row', row)
      this.editRow = row
      this.ladderPriceDetailVisible = true
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
