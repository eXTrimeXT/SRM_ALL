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
        <!--公式值-->
        <span class="label">{{ $t('bid_mod.formulaValue') }}:</span>
        <span
          class="value"
          :title="viewRow.formulaValue"
        >{{ viewRow.formulaValue }}</span>
      </div>
    </div>

    <base-table
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
 * 公式报价弹窗
 */
import { bigPriceRound } from 'lib@/composition/commonComposition'
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
      this.$http({
        url: '/api-inq/quote/quoteHeader/getQuoteFormulaPrices',
        method: 'GET',
        loading: true,
        params: {
          inquiryItemId: this.viewRow.inquiryItemId || '',
          quoteItemId: this.viewRow.quoteItemId || '',
          // 币种，用于基材价格根据汇率转换
          currencyCode: this.viewRow.quoteCurrency
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

      const [priceColChildren, mainColChildren, materialColChildren] = [[], [], []]
      columnsList.forEach(item => {
        const attrs = {
          align: 'center',
          minWidth: '100',
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
          default:
        }
      })
      // 往下按顺序push 供应商报价列 => 物料属性 => 基材价格
      // 供应商报价列
      if (priceColChildren.length > 0) {
        columns = [...columns, ...priceColChildren]
      }
      // 物料属性
      if (mainColChildren.length > 0) {
        columns = [...columns, ...mainColChildren]
      }
      // 基材价格
      if (materialColChildren.length > 0) {
        columns = [...columns, ...materialColChildren]
      }

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
    line-height: 30px;
    .label {
      padding-right: 15px;
    }
  }
}
</style>
