<template>
  <srm-dialog
    :title="$t('bidMod.bidDetail')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-table
      :data="bidingDetailItem"
      style="width: 100%"
      border
      height="300px"
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
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

      <!--交货地点-->
      <el-table-column
        align="center"
        prop="deliveryPlace"
        :label="$t('bid_mod.tradingLocations')"
        width="150"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <render-async-text :cell-value="scope.row.deliveryPlace" />
        </template>
      </el-table-column>

      <!--贸易条款-->
      <el-table-column
        align="center"
        prop="tradeTerm"
        :label="$t('bid_mod.tradeTerm')"
        width="150"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('trade_clause', cellValue)"
      />

      <!--运输方式-->
      <el-table-column
        align="center"
        prop="transportType"
        :label="$t('bid_mod.transportType')"
        width="150"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => $getDictLabel('TRANSF_TYPE', cellValue)"
      />

      <!--供货周期(自然天)-->
      <el-table-column
        align="center"
        prop="leadTime"
        :label="$t('bid_mod.leadTime')"
        width="150"
        show-overflow-tooltip
      />

      <!--质保期(月)-->
      <el-table-column
        align="center"
        prop="warrantyPeriod"
        :label="$t('bid_mod.warrantyPeriod')"
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
        :formatter=" (row, column, cellValue) => $getDictLabel('PURCHASE_TYPE', cellValue)"
      />

      <!--报价币种-->
      <el-table-column
        align="center"
        prop="currencyType"
        :label="$t('bidMod.bidingCurrency2')"
        width="150"
        show-overflow-tooltip
        :formatter=" (row, column, cellValue) => $getDictLabel('currency', cellValue)"
      />

      <!--mqo-->
      <el-table-column
        align="center"
        prop="mqo"
        :label="$t('bidMod.minOrderQuantity')"
        width="150"
        show-overflow-tooltip
      />

      <!--付款条款-->
      <el-table-column
        align="center"
        prop="paymentType"
        :label="$t('route.paymentType')"
        width="110"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-button
            type="text"
            @click="openPaymentTypeDialog(scope)"
          >
            {{ $t('common.view') }}
          </el-button>
        </template>
      </el-table-column>

      <!--物料编码-->
      <el-table-column
        align="center"
        prop="targetNum"
        :label="$t('bidMod.targetNum')"
        width="150"
        show-overflow-tooltip
        :formatter="(row, column, value) => targetNumRevealFilter(value)"
      />

      <!--物料名称-->
      <el-table-column
        align="center"
        prop="targetDesc"
        :label="$t('bidMod.targetDesc')"
        min-width="150"
        show-overflow-tooltip
      />

      <template v-if="biddingData.bidingAwardWay === 'COMBINED_DECISION'">
        <!--组合-->
        <el-table-column
          align="center"
          prop="itemGroup"
          :label="$t('bidMod.itemGroup')"
          width="100"
          show-overflow-tooltip
        />

        <!--配比-->
        <el-table-column
          align="center"
          prop="materialMatching"
          :label="$t('bid_mod.materialMatching')"
          width="100"
          show-overflow-tooltip
        />
      </template>

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
        :label="$t('bid_mod.unit')"
        width="100"
        show-overflow-tooltip
        :formatter=" (row, column, cellValue) => $getDictLabel('unit', cellValue)"
      />

      <!--未税单价-->
      <el-table-column
        align="center"
        prop="notaxPrice"
        :label="$t('bidMod.quotenotaxPrice2')"
        width="120"
      />

      <!--含税单价-->
      <el-table-column
        align="center"
        prop="taxPrice"
        :label="$t('bidMod.quotetaxPrice2')"
        width="120"
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

      <!--承诺交货期-->
      <el-table-column
        align="center"
        prop="deliverDate"
        :label="$t('bid_mod.deliverDate')"
        width="150"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => cellValue ? cellValue.slice(0, 10) : ''"
      />

      <!--定价开始日期-->
      <el-table-column
        align="center"
        prop="priceStartTime"
        :label="$t('bidMod.fixedPriceBegin')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => cellValue ? cellValue.slice(0, 10) : ''"
      />

      <!--定价结束日期-->
      <el-table-column
        align="center"
        prop="priceEndTime"
        :label="$t('bidMod.fixedPriceEnd')"
        width="100"
        show-overflow-tooltip
        :formatter="(row, column, cellValue) => cellValue ? cellValue.slice(0, 10) : ''"
      />

      <el-table-column
        align="center"
        prop="comments"
        :label="$t('bidMod.remark')"
        width="150"
        show-overflow-tooltip
      />
    </el-table>

    <div style="height: 33px">
      <el-button
        style="float: right; margin: 3px"

        @click="dialogVisible = false"
      >
        {{ $t("common.close") }}
      </el-button>
    </div>

    <!--付款条款-->
    <PaymentTypeDialog
      v-if="paymentTypeDialogVisible"
      :visible.sync="paymentTypeDialogVisible"
      :business-type="BUSINESS_TYPE_ENUM.BIDING"
      :edit-row="editRow"
      readonly
    />
  </srm-dialog>
</template>

<script>
/**
 * 投标详情
 */
import { targetNumReveal } from 'lib@/composition/origin/composition'
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import RenderAsyncText from '@/library/components/provice-city/renderAsyncText'
import PaymentTypeDialog from 'lib@/composition/origin/paymentTypeDialog'

export default {
  name: 'BiddingDetailDialog',

  components: {
    RenderAsyncText,
    PaymentTypeDialog
  },

  props: {
    visible: {
      type: Boolean
    },
    editRow: {
      type: Object
    }
  },

  data () {
    return {
      bidingStatus: '',
      pricingType: '',
      bidingDetailItem: [],
      biddingData: {},
      paymentTypeDialogVisible: false,
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

  watch: {
    dialogVisible: {
      handler (newVal) {
        if (newVal && this.editRow) {
          this.getOrderDetails()
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

    /* 查询详情数据 */
    getOrderDetails () {
      if (!this.editRow) return

      this.$http({
        url: `/api-bid/businessProposal/getOrderDetails/${this.editRow.orderHeadId}`,
        method: 'GET',
        loading: true
      }).then(data => {
        if (data && data.data) {
          const responseData = data.data
          this.biddingData = responseData.biding
          this.bidingStatus = (responseData.biding || {}).bidingStatus
          this.pricingType = responseData.biding.pricingType || ''
          this.bidingDetailItem = responseData.reqLineList || []
        }
      })
    },

    /* 打开付款条款弹窗 */
    openPaymentTypeDialog ({ row }) {
      this.editRow = row
      this.paymentTypeDialogVisible = true
    }
  }
}
</script>
