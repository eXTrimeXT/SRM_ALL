<template>
  <SrmDialog
    :title="$t('cusEntry.inq.changeBidSupplier')"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    size="large"
  >
    <SrmRow class="change-bid-supplier">
      <SrmCol :init-col="3">
        <p class="info-col-p">
          <span class="label">{{ $t('bidMod.itemCode') }}:</span>
          <span class="value">{{ header.itemCode }}</span>
        </p>
      </SrmCol>
      <SrmCol :init-col="3">
        <p class="info-col-p">
          <span class="label">{{ $t('bidMod.itemDesc') }}:</span>
          <span class="value">{{ header.materialNameShow }}</span>
        </p>
      </SrmCol>
      <SrmCol :init-col="3">
        <p class="info-col-p">
          <span class="label">{{ $t('cusEntry.inq.quantity') }}:</span>
          <span class="value">{{ header.requireQuantity }}</span>
        </p>
      </SrmCol>
    </SrmRow>
    <el-table
      border
      :data="vendorList"
      max-height="250"
    >
      <el-table-column
        type="index"
        width="50"
        fixed="left"
      />
      <!--供应商编码-->
      <el-table-column
        prop="vendorCode"
        :label="$t('bidMod.vendorCode')"
        align="center"
        min-width="120"
        show-overflow-tooltip
      />
      <!--供应商名称-->
      <el-table-column
        prop="vendorName"
        :label="$t('bidMod.vendorName')"
        align="center"
        min-width="120"
        show-overflow-tooltip
      />
      <!-- 币种 -->
      <el-table-column
        align="center"
        prop="orderCurrency"
        :label="$t('bidMod.currency_price')"
        min-width="120"
        show-overflow-tooltip
        :formatter="row => {
          return orderStatus === 'ACCEPT_ORDER' ? '' : $getDictLabel('currency', row.orderCurrency)
        }"
      />
      <!-- 原未税单价 -->
      <el-table-column
        align="center"
        prop="orderNotaxPrice"
        :label="$t('cusEntry.supplement20250314.originalTaxExcludedPrice')"
        min-width="120"
        show-overflow-tooltip
        :formatter="row => {
          return orderStatus === 'ACCEPT_ORDER' ? '' : row.orderNotaxPrice
        }"
      />
      <!-- 转换币种 -->
      <el-table-column
        align="center"
        prop="convertOrderCurrency"
        :label="$t('cusEntry.supplement20250314.convertCurrency')"
        min-width="120"
        show-overflow-tooltip
        :formatter="row => {
          return orderStatus === 'ACCEPT_ORDER' ? '' : $getDictLabel('currency', row.convertOrderCurrency)
        }"
      />
      <!-- 汇率 -->
      <el-table-column
        align="center"
        prop="extExchangeRate"
        :label="$t('bid_mod.priceTax')"
        min-width="120"
        show-overflow-tooltip
        :formatter="row => {
          return orderStatus === 'ACCEPT_ORDER' ? '' : row.extExchangeRate
        }"
      />
      <!--税率-->
      <el-table-column
        prop="taxRate"
        :label="$t('bidMod.taxRate2')"
        align="center"
        min-width="120"
        show-overflow-tooltip
        :formatter="row => {
          return orderStatus === 'ACCEPT_ORDER' ? '' : row.taxRate
        }"
      />
      <!--未税单价-->
      <el-table-column
        prop="standardNotaxPrice"
        :label="$t('bidMod.quotenotaxPrice2')"
        align="center"
        min-width="120"
        show-overflow-tooltip
        :formatter="row => {
          return orderStatus === 'ACCEPT_ORDER' ? '' : row.standardNotaxPrice
        }"
      />
      <!--含税单价-->
      <el-table-column
        prop="orderTaxPrice"
        :label="$t('bidMod.quotetaxPrice2')"
        align="center"
        min-width="120"
        show-overflow-tooltip
        :formatter="row => {
          return orderStatus === 'ACCEPT_ORDER' ? '' : row.orderTaxPrice
        }"
      />
      <!--价税合计-->
      <el-table-column
        prop="priceTaxTotal"
        :label="$t('cusEntry.bidMod.taxAmount')"
        align="center"
        min-width="120"
        show-overflow-tooltip
        :formatter="row => {
          return orderStatus === 'ACCEPT_ORDER' ? '' : row.priceTaxTotal
        }"
      />
      <!--到货周期-->
      <el-table-column
        prop="extLeadTime"
        :label="$t('cusEntry.bidMod.deliveryCycle')"
        align="center"
        min-width="120"
        show-overflow-tooltip
        :formatter="row => {
          return orderStatus === 'ACCEPT_ORDER' ? '' : row.extLeadTime
        }"
      />
      <!--质保期-->
      <el-table-column
        prop="extWarrantyPeriod"
        :label="$t('cusEntry.bidMod.warrantyPeriod')"
        align="center"
        min-width="120"
        show-overflow-tooltip
        :formatter="row => {
          return orderStatus === 'ACCEPT_ORDER' ? '' : row.extWarrantyPeriod
        }"
      />
      <!-- 是否预付 -->
      <el-table-column
        align="center"
        prop="extIsPrepaid"
        :label="$t('cusEntry.bidMod.extIsPrepaid')"
        min-width="120"
        show-overflow-tooltip
        :formatter="row => {
          return orderStatus === 'ACCEPT_ORDER' ? '' : $getDictLabel('YES_OR_NO', row.extIsPrepaid)
        }"
      />
      <!-- 预付比例% -->
      <el-table-column
        align="center"
        prop="extPrepaidRatio"
        :label="$t('cusEntry.bidMod.extPrepaidRatio')"
        min-width="120"
        show-overflow-tooltip
        :formatter="row => {
          return orderStatus === 'ACCEPT_ORDER' ? '' : row.extPrepaidRatio
        }"
      />
      <!-- 物流费 -->
      <el-table-column
        align="center"
        prop="extLogisticsCost"
        :label="$t('cusEntry.bidMod.extLogisticsCost')"
        min-width="120"
        show-overflow-tooltip
        :formatter="row => {
          return orderStatus === 'ACCEPT_ORDER' ? '' : row.extLogisticsCost
        }"
      />
      <!--供应商备注-->
      <el-table-column
        prop="orderRemark"
        :label="$t('cusEntry.bidMod.orderRemark')"
        align="center"
        min-width="120"
        show-overflow-tooltip
        :formatter="row => {
          return orderStatus === 'ACCEPT_ORDER' ? '' : row.orderRemark
        }"
      />
      <!-- 中标原因 -->
      <el-table-column
        prop="extWinReason"
        min-width="120"
        show-overflow-tooltip
        align="center"
      >
        <template #header>
          <span><i class="required">*</i>{{ $t('cusEntry.inq.bidReason') }}</span>
        </template>
        <template slot-scope="scope">
          <el-input
            v-if="orderStatus !== 'ACCEPT_ORDER'"
            v-model="scope.row.extWinReason"
            :disabled="orderStatus === 'PRICE_END'"
          />
        </template>
      </el-table-column>
      <el-table-column
        v-if="!['PRICE_END', 'ACCEPT_ORDER'].includes(orderStatus)"
        :label="$t('common.operation')"
        width="60"
        fixed="right"
      >
        <template slot-scope="scope">
          <el-button
            type="text"
            @click="setSupplierBid(scope.row)"
          >
            {{ $t('cusEntry.common.bid') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <div slot="footer">
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
import { inqBuyerHttp } from 'modcb@/inquiry/api'
export default {
  name: 'ChangeBidSupplierDialog',
  props: {
    /* 控制弹窗显隐 */
    visible: {
      type: Boolean,
      default: false
    },
    /* 评选行数据 */
    header: {
      type: Object,
      default: () => ({})
    },
    /* 供应商列表 */
    vendorList: {
      type: Array,
      default: () => []
    },
    /* 单据状态 */
    orderStatus: {
      type: String,
      dafault: ''
    }
  },
  data () {
    return {

    }
  },
  computed: {
    dialogVisible: {
      get () {
        return this.visible
      },
      set (value) {
        this.$emit('update:visible', value)
      }
    }
  },
  methods: {
    /* 设置中标供应商 */
    setSupplierBid (row) {
      /* 判断中标原因必填 */
      if (!row.extWinReason) {
        this.$message.warning(this.$t('cusEntry.tipMessage.extWinReasonMsg'))
        return false
      }
      const data = {
        toWin: true,
        extWinReason: row.extWinReason,
        orderItemIds: [row.orderItemId]
      }
      inqBuyerHttp.select.changeBidSupplier(data).then(res => {
        this.$message.success(this.$t('cusEntry.tipMessage.setBidSupplierSuccess'))
        this.$emit('refreshSelectList', row)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.change-bid-supplier {
  margin-top: 10px;
  .info-col-p {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    .value {
      padding-left: 15px;
    }
  }
}
.required {
  color: red;
  margin-right: 4px;
}
</style>
