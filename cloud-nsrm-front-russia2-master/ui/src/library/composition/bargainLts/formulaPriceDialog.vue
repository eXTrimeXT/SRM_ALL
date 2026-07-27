<template>
  <srm-dialog
    size="large"
    :title="$t('bidMod.formulaQuote')"
    :visible.sync="dialogVisible"
    append-to-body
    :close-on-click-modal="false"
  >
    <div class="ladder-price-form-row">
      <div class="form-col">
        <!--物料编码-->
        <span class="label">{{ $t('bidMod.itemCode') }}:</span>
        <span class="value">{{ targetNum }}</span>
      </div>
      <div class="form-col">
        <!--物料名称-->
        <span class="label">{{ $t('bidMod.itemName') }}:</span>
        <span class="value">{{ viewRow.itemDesc }}</span>
      </div>
      <div class="form-col">
        <!--预计数量-->
        <span class="label">{{ $t('bidMod.demandQuantity') }}:</span>
        <span class="value">{{ viewRow.requireQuantity }}</span>
      </div>
    </div>

    <div class="ladder-price-form-row">
      <div class="form-col">
        <!--公式值-->
        <span class="label">{{ $t('bid_mod.formulaValue') }}:</span>
        <span
          class="value"
          :title="viewRow.formulaValue"
        >{{ viewRow.formulaValue }}</span>
      </div>
    </div>

    <BaseTable
      ref="currentBaseTable"
      stripe
      :data="currentSupplier"
      :columns="currentSupplierColumns"
      :empty-text="$t('components.noData')"
      border
      height="200"
    />

    <div
      slot="footer"
      class="dialog-footer"
    >
      <el-button @click="dialogVisible = false">
        {{ $t('common.cancel') }}
      </el-button>
    </div>
  </srm-dialog>
</template>

<script>
/**
 * 公式报价查看弹窗
 */
import { bigPriceRound } from 'lib@/composition/commonComposition'
import { targetNumReveal } from 'lib@/composition/origin/composition'
import BaseTable from 'lib@/components/BaseTable'

export default {
  name: 'FormulaPriceDialog',

  components: { BaseTable },

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    viewRow: {
      type: Object,
      required: true
    }
  },

  data () {
    return {
      currentSupplier: [],
      currentSupplierColumns: []
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
    },
    targetNum () {
      return targetNumReveal(this.viewRow.itemCode)
    }
  },

  created () {
    if (this.dialogVisible && this.viewRow) {
      this.getQuoteFormulaPrices()
    }
  },

  methods: {
    /* 查询当前供应商的公式报价 */
    getQuoteFormulaPrices () {
      const {
        souItemId = '',
        orderItemId = '',
        orderCurrency = '',
        vendorId = ''
      } = this.viewRow || {}

      this.$http({
        url: '/api-sou/buyer/brg/order/getOrderFormulaPrices',
        method: 'GET',
        loading: true,
        params: {
          souItemId,
          orderItemId,
          // 币种，用于基材价格根据汇率转换
          currencyCode: orderCurrency,
          vendorId
        }
      }).then(data => {
        if (data && data.data && Array.isArray(data.data)) {
          // 编排表格columns配置
          this.currentSupplierColumns = this.initTableColumnsConfig(data.data)

          // 编排表格数据
          this.currentSupplier = this.initTableData(data.data)

          this.$nextTick(() => {
            // 更新table布局
            this.$refs.currentBaseTable.$children[0].doLayout()
          })
        }
      })
    },

    /* 编排公式报价表格columns配置返回 */
    initTableColumnsConfig (columnsList) {
      let columns = []
      const [
        priceColChildren,
        mainColChildren,
        materialColChildren,
        priceLibraryChildren
      ] = [[], [], [], []]
      columnsList.forEach(item => {
        const attrs = {
          minWidth: '110',
          label: item.essentialFactorName,
          prop: item.essentialFactorId.toString()
        }
        switch (item.essentialFactorFrom) {
          case 'SUPPLIER_QUOTED_PRICE':
            // 价格输入框
            priceColChildren.push({ attrs })
            break
          case 'MATERIAL_MAIN_DATA':
            // 物料属性
            mainColChildren.push({ attrs })
            break
          case 'BASE_MATERIAL_PRICE':
            // 基材价格
            materialColChildren.push({ attrs })
            break
          case 'PRICE_LIBRARY':
            // 价格目录
            priceLibraryChildren.push(({ attrs }))
            break
          default:
        }
      })
      // 往下按顺序push 供应商报价列 => 物料属性 => 基材价格 => 价格目录
      columns = [
        ...columns,
        // 供应商报价列
        ...priceColChildren,
        // 物料属性
        ...mainColChildren,
        // 基材价格
        ...materialColChildren,
        // 价格目录
        ...priceLibraryChildren
      ]

      return columns
    },

    /* 编排表格数据 */
    initTableData (columnsList) {
      // 编排表格数据，只有一行
      const tableData = [{}]
      // 查询的数据
      columnsList.forEach(item => {
        tableData[0] = {
          ...tableData[0],
          [item.essentialFactorId.toString()]: bigPriceRound(item.value, 10) || ''
        }
      })
      return tableData
    }
  }
}
</script>

<style lang="scss" scoped>
.ladder-price-form-row {
  display: flex;
  margin-bottom: 10px;
  .form-col:not(:last-child) {
    margin-right: 30px;
  }
  .form-col {
    height: 30px;
    line-height: 30px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    .label {
      padding-right: 15px;
    }
  }
}
</style>
