<template>
  <div class="biding-result">
    <FormWrapper
      style="border-bottom: none"
      :form-array="formWrapperConfig"
      @getFormData="getBargainOrderResult"
    />

    <el-table
      :data="bargainInfoList"
      style="width: 100%"
      border
      height="345px"
    >
      <el-table-column
        align="center"
        type="index"
        :label="$t('bidMod.tableIndex')"
        width="65"
      />

      <!--轮次-->
      <el-table-column
        align="center"
        prop="round"
        :label="$t('bidMod.bidingRound')"
        width="60"
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

      <!--物料编码-->
      <el-table-column
        align="center"
        prop="targetNum"
        :label="$t('bidMod.itemCode')"
        width="120"
        show-overflow-tooltip
        :formatter="(row, column, value) => targetNumRevealFilter(value)"
      />

      <!--物料描述-->
      <el-table-column
        align="center"
        prop="targetDesc"
        :label="$t('bidMod.itemDesc')"
        min-width="150"
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

      <!--采购数量-->
      <el-table-column
        align="center"
        prop="quantity"
        :label="$t('bid_mod.purQuantity')"
        width="100"
        show-overflow-tooltip
      />

      <!--单位-->
      <el-table-column
        align="center"
        prop="unit"
        :label="$t('bidMod.unit')"
        width="70"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('unit', cellValue)"
      />

      <!--不含税中标价格-->
      <el-table-column
        align="center"
        prop="brgNotaxPrice"
        :label="$t('bidMod.bidPriceExcludingTax')"
        width="120"
        show-overflow-tooltip
      />

      <!--税率-->
      <el-table-column
        align="center"
        prop="taxKey"
        :label="$t('bidMod.taxRate')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('tax', cellValue)"
      />

      <!--中标供应商-->
      <el-table-column
        align="center"
        prop="vendorName"
        :label="$t('bidMod.wonBidVendor')"
        width="150"
        show-overflow-tooltip
      />

      <!--定价开始时间-->
      <el-table-column
        align="center"
        prop="priceStartTime"
        :label="$t('bidMod.priceStartTime')"
        width="150"
        show-overflow-tooltip
      />

      <!--定价结束时间-->
      <el-table-column
        align="center"
        prop="priceEndTime"
        :label="$t('bidMod.priceEndTime')"
        width="150"
        show-overflow-tooltip
      />

      <!--评选情况-->
      <el-table-column
        align="center"
        prop="selectionStatus"
        :label="$t('bidMod.selectSituation')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, value) => $getDictLabel('BIDDING_SELECT_STATES', value)"
      />

      <!--排名-->
      <el-table-column
        align="center"
        prop="rank"
        :label="$t('bidMod.rank')"
        width="70"
        show-overflow-tooltip
      />

      <!--价格类型-->
      <el-table-column
        align="center"
        prop="priceType"
        :label="$t('bid_mod.priceType')"
        width="150"
        show-overflow-tooltip
        :formatter="(row, column, value) => $getDictLabel('PRICE_TYPE', value)"
      />

      <!--备注-->
      <el-table-column
        align="center"
        prop="comments"
        :label="$t('bidMod.remark')"
        width="100"
        show-overflow-tooltip
      />
    </el-table>

    <CPagination
      :total="itemPageInfo.total"
      :page-num="itemPageInfo.pageNum"
      :page-size="itemPageInfo.pageSize"
      @current-change="itemCurrentChange"
      @size-change="itemSizeChange"
    />
  </div>
</template>

<script>
/**
 * 投标结果
 */
import { targetNumReveal } from 'lib@/composition/origin/composition'
import CPagination from '@/library/components/c-pagination'
import FormWrapper from '@/library/components/Table/FormWrapper'

export default {
  name: 'BargainResult',
  components: { CPagination, FormWrapper },
  props: {
    isCurrentTab: {
      type: Boolean
    },
    bargainId: {
      type: [Number, String]
    }
  },
  data () {
    return {
      bargainInfoList: [],
      isGetDataStatus: false,
      formWrapperConfig: [
        // 物料名称
        { prop: 'targetDesc', label: () => this.$t('bidMod.itemName') },
        // 中标供应商名称
        { prop: 'vendorName', label: () => this.$t('bidMod.wonBidVendor') }
      ],
      itemPageInfo: {
        total: 0,
        pageNum: 1,
        pageSize: 10
      }
    }
  },
  watch: {
    isCurrentTab: {
      handler (newValue, oldValue) {
        if (newValue && !oldValue && !this.isGetDataStatus) {
          this.isGetDataStatus = true
          this.getBargainOrderResult()
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

    /* 查询列表数据 */
    getBargainOrderResult (searchFormData = {}) {
      if (!this.bargainId) return

      this.$http({
        url: '/api-brg/supplierCooperate/orderHead/getBargain/orderResult',
        method: 'POST',
        data: {
          bargainId: this.bargainId,
          pageSize: this.itemPageInfo.pageSize,
          pageNum: this.itemPageInfo.pageNum,
          ...searchFormData
        },
        loading: true
      }).then(({ data }) => {
        if (data && data.list) {
          this.bargainInfoList = data.list
        }
      })
    },

    /* 当前页改变 */
    itemCurrentChange (num) {
      this.itemPageInfo.pageNum = num
      this.getBargainOrderResult()
    },

    /* 页码大小改变 */
    itemSizeChange (size) {
      this.itemPageInfo.pageSize = size
      this.getBargainOrderResult()
    }
  }
}
</script>
