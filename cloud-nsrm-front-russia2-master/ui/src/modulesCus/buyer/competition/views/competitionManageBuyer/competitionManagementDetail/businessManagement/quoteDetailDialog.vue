<template>
  <SrmDialog
    :title="$t('bidMod.quoteDetail')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-table
      :data="orderDetailList.slice((currentPage - 1) * pageSize, currentPage * pageSize)"
      style="width: 100%"
      border
      height="345px"
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
        fixed="left"
      />

      <!--业务实体-->
      <el-table-column
        align="center"
        prop="orgOuName"
        :label="$t('bidMod.businessEntity')"
        width="150"
        show-overflow-tooltip
      />

      <!--库存组织-->
      <el-table-column
        align="center"
        prop="orgInvName"
        :label="$t('bidMod.inventory')"
        width="150"
        show-overflow-tooltip
      />

      <!--交货地点-->
      <!-- <el-table-column
        align="center"
        prop="deliveryPlace"
        :label="$t('bidMod.deliveryPoints')"
        width="150"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <RenderAsyncText :cell-value="scope.row.deliveryPlace" />
        </template>
      </el-table-column> -->

      <!--贸易条款-->
      <el-table-column
        align="center"
        prop="tradeTerm"
        :label="$t('bidMod.tradeClause')"
        width="150"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('trade_clause', cellValue)"
      />

      <!--物料编码-->
      <el-table-column
        align="center"
        prop="itemCode"
        :label="$t('bidMod.targetNum')"
        width="120"
        show-overflow-tooltip
      />

      <!--物料名称-->
      <el-table-column
        align="center"
        prop="itemDesc"
        :label="$t('bidMod.targetDesc')"
        min-width="180"
        show-overflow-tooltip
      />

      <!--采购数量-->
      <el-table-column
        align="center"
        prop="requireQuantity"
        :label="$t('bidMod.purchaseAmount')"
        width="100"
        show-overflow-tooltip
      />

      <!--单位-->
      <el-table-column
        align="center"
        prop="unit"
        :label="$t('bidMod.bidsUnit')"
        width="100"
        :formatter="(row, column, cellValue) => $getDictLabel('unit', cellValue)"
        show-overflow-tooltip
      />

      <!--含税报价-->
      <el-table-column
        align="center"
        prop="orderNotaxPrice"
        :label="$t('bidMod.quotetaxPrice')"
        width="100"
      />

      <!--报价币种-->
      <el-table-column
        align="center"
        prop="orderCurrency"
        :label="$t('bidMod.currencyType')"
        width="100"
        :formatter="(row, column, cellValue) => $getDictLabel('currency', cellValue)"
        show-overflow-tooltip
      />

      <!--税率-->
      <el-table-column
        align="center"
        prop="taxKey"
        :label="$t('bidMod.taxRate')"
        width="100"
        :formatter="(row, column, cellValue) => $getDictLabel('tax', cellValue)"
        show-overflow-tooltip
      />

      <!--质保期(月)-->
      <el-table-column
        align="center"
        prop="warrantyPeriod"
        :label="$t('bidMod.appraisGuarantee')"
        width="100"
        show-overflow-tooltip
      />

      <!--价格类型-->
      <el-table-column
        align="center"
        prop="priceType"
        :label="$t('bid_mod.priceType')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('PRICE_TYPE', cellValue)"
      />

      <!--付款条款-->
      <el-table-column
        align="center"
        prop="paymentType"
        :label="$t('bidMod.paymentTerms')"
        width="100"
        show-overflow-tooltip
      >
        <template v-slot="{ row }">
          <el-button
            type="text"
            @click="openPaymentTypeDialog(row)"
          >
            {{ $t('common.view') }}
          </el-button>
        </template>
      </el-table-column>

      <!--定价开始日期-->
      <el-table-column
        align="center"
        prop="priceStartTime"
        :label="$t('bidMod.priceStartDate')"
        width="100"
        :formatter="(row, column, cellValue) => $parseTime(cellValue)"
        show-overflow-tooltip
      />

      <!--定价结束日期-->
      <el-table-column
        align="center"
        prop="priceEndTime"
        :label="$t('bidMod.priceEndDate')"
        width="100"
        :formatter="(row, column, cellValue) => $parseTime(cellValue)"
        show-overflow-tooltip
      />

      <!--备注-->
      <el-table-column
        align="center"
        prop="remark"
        :label="$t('bidMod.remark')"
        width="150"
        show-overflow-tooltip
      />
    </el-table>

    <div style="width: 100%; margin-bottom: 10px">
      <CPagination
        ref="queryPagination"
        style="margin: 0"
        :total="orderDetailList.length"
        :page-num="currentPage"
        layout="total, prev, pager, next"
        :page-sizes="[10]"
        :page-size="pageSize"
        @current-change="currentPageChange"
        @size-change="pageSizeChange"
      />
    </div>

    <template #footer>
      <el-button @click="dialogVisible = false">
        {{ $t('common.close') }}
      </el-button>
    </template>

    <!--批量维护付款条款-->
    <PaymentTypeDialog
      v-if="paymentTypeDialogVisible"
      :visible.sync="paymentTypeDialogVisible"
      :business-type="BUSINESS_TYPE_ENUM.COMPETITION"
      :edit-row="editRow"
      readonly
    />
  </SrmDialog>
</template>

<script>
/**
 * 报价详情
 */
import { carBuyerHttp } from 'modb@//competition/api'
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import CPagination from 'lib@/components/c-pagination'
import RenderAsyncText from 'lib@/components/provice-city/renderAsyncText'
import PaymentTypeDialog from 'lib@/composition/origin/paymentTypeDialog'
import { transformMQL } from 'lib@/utils/util'

export default {
  name: 'QuoteDetailDialog',

  components: {
    CPagination,
    RenderAsyncText,
    PaymentTypeDialog
  },

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    projectId: {
      type: [Number, String],
      default: ''
    },
    viewRow: {
      type: Object,
      required: true
    }
  },

  data () {
    return {
      orderDetailList: [],
      currentPage: 1,
      pageSize: 10,
      paymentTypeDialogVisible: false,
      editRow: null,
      BUSINESS_TYPE_ENUM
    }
  },

  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },

  created () {
    this.queryOrderDetailList()
  },

  methods: {
    /* 查询报价详情 */
    async queryOrderDetailList () {
      if (!this.viewRow) {
        return
      }
      let transformParams = transformMQL.save('AuctSouProjectForBuyer', [{ orderId: this.viewRow.orderId }], 'getVendorOrderInfo')
      const response = await carBuyerHttp.control.getOrderInfo(transformParams)
      if (response.data.records?.length) {
        let { orderItemList = [] } = response.data.records[0]
        orderItemList = orderItemList.map(item => ({ ...item, ...item.auctSouOrderItem,...item.souItem, ...item.souItem.auctSouItem }))
        this.orderDetailList = orderItemList
      }
    },

    /* 当前页改变 */
    currentPageChange (val) {
      this.currentPage = val
    },

    /* 页码大小改变 */
    pageSizeChange (val) {
      this.currentPage = 1
      this.pageSize = val
    },

    /* 打开付款条款弹窗 */
    openPaymentTypeDialog (row) {
      this.editRow = {
        paymentList: row.paymentList || []
      }
      this.paymentTypeDialogVisible = true
    }
  }
}
</script>
