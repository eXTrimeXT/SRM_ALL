<template>
  <!-- 价格调整 -->
  <srm-dialog
    :visible.sync="batchFormulaInformationVisible"
    :title="$t('purchaseDemand.priceAdjust')"
    size="large"
    :close-on-click-modal="false"
    v-bind="attrs"
    v-on="$listeners"
  >
    <div style="text-align: center">
      {{ ceeaFormulaValueData }}
    </div>
    <ul
      class="the_render_list"
      style="
          display: flex;
          list-style: none;
          text-align: center;
          justify-content: center;
          padding: 0px;
        "
    >
      <li v-for="(val, key) in zrr1" :key="key" style="border: 1px solid #dfe6ec; min-width: 150px">
        <span
          style="
              display: block;
              padding: 5px;
              background: #88c1f4 !important;
              border-bottom: 1px solid #dfe6ec;
            "
        >
          {{ val.label }}
        </span>
        <span style="display: block; padding: 5px">{{ val.value }}</span>
      </li>
    </ul>
    <base-table
      :data-source="priceAdjustmentDataPush"
      :columns="basicMaterialPriceColumns"
      columns-name="basicMaterialPriceColumns"
      :empty-text="$t('components.noData')"
      @row-dblclick="handleItemDBClick"
    >
      <template #priceFrom="scope">
        <el-select
          v-if="scope.row.prices.length > 1"
          v-model="scope.row.priceFrom"
          @change="value => priceFromChangeHandler(value, scope)"
        >
          <el-option
            v-for="item in scope.row.prices"
            :key="item.baseMaterialPriceId"
            :value="item.priceFrom"
            :label="item.priceFrom"
          />
        </el-select>
        <span v-else>{{ scope.row.priceFrom }}</span>
      </template>
      <template #baseMaterialPrice="scope">
        <el-input
          v-if="scope.row.prices.length > 1"
          v-model="scope.row.baseMaterialPrice"
          style="text-align: center"
          :disabled="scope.row.baseMaterialName === '汇率' ? false : true"
          type="number"
          @input="value => getPricesA(value, scope)"
        />
        <el-input
          v-else
          v-model="scope.row.baseMaterialPrice"
          style="text-align: center"
          :disabled="scope.row.baseMaterialName === '汇率' ? false : true"
          type="number"
          @input="value => getPricesB(value, scope)"
        />
      </template>
    </base-table>

    <template #footer class="dialog-footer">
      <el-button @click="batchFormulaInformationVisible = false">
        {{ $t('common.cancel') }}
      </el-button>
      <el-button type="primary" :disabled="isReadOnly" @click="priceAdjustmentSave">
        {{ $t('common.submit') }}
      </el-button>
    </template>
  </srm-dialog>
</template>

<script>
import BaseTable from 'lib@/components/BaseTable'
export default {
  name: 'PriceAdjust',
  components: {
    BaseTable
  },
  props: {
    zrr1: {
      type: Array,
      default: () => {
        return []
      }
    },
    priceAdjustmentDataPush: {
      type: Array,
      default: () => {
        return []
      }
    },
    batchFormulaInformationVisible: {
      type: Boolean,
      default: () => {
        return false
      }
    },
    ceeaFormulaValueData: {
      type: String,
      default: () => {
        return ''
      }
    },
    detailModelReal: {
      type: Array,
      default: () => {
        return []
      }
    }
  },
  data () {
    return {
      priceAdjustmentData: [],
      queryTotal: -1,
      viewSize: 10,
      viewIndex: 1,
      basicMaterialPriceColumns: [
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: this.$t('baseMaterial.baseMaterialName'),
            prop: 'baseMaterialName',
            showOverflowTooltip: true
          }
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: this.$t('basicPrice.dataSource'),
            prop: 'priceFrom'
          },
          slot: 'priceFrom'
        },
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: this.$t('basicPrice.baseMaterialPrice'),
            prop: 'baseMaterialPrice'
          },
          slot: 'baseMaterialPrice'
        }
      ]
    }
  },
  methods: {
    getPricesA (value, scope) {
      if (scope.row.baseMaterialPrice && scope.row.selectedPrice) {
        scope.row.baseMaterialPrice = value
        scope.row.selectedPrice.baseMaterialPrice = value
      }
    },
    getPricesB (value, scope) {
      if (scope.row.baseMaterialPrice) {
        scope.row.baseMaterialPrice = value
      }
    },
    priceFromChangeHandler (value, scope) {
      if (scope.row.baseMaterialName.includes('汇率')) {
        this.rateName = value
        const valueA = value.split('兑')[0]
        const valueB = value.split('兑')[1]
        const { prices } = scope.row
        const target = prices.find(item => item.priceFrom === value) || {}
        scope.row.selectedPrice = target
        this.$http({
          url: '/api-base/purchase/latest-gidaily-rate/loadRateByName',
          method: 'GET',
          params: { toCurrencyName: valueA, fromCurrencyName: valueB },
          loading: true
        }).then(res => {
          this.$set(scope.row, 'baseMaterialPrice')
          this.conversionRate = res.data.conversionRate
          this.maxConversionRate = res.data.maxConversionRate
          this.minConversionRate = res.data.minConversionRate
        })
      } else {
        const { prices } = scope.row
        const target = prices.find(item => item.priceFrom === value) || {}
        scope.row.selectedPrice = target
        this.$set(scope.row, 'baseMaterialPrice', target.baseMaterialPrice)
      }
    },
    handlePriceChange (val) {
      this.priceAdjustmentData = val
    },
    handleItemDBClick (val) {
      this.priceAdjustmentData = [val]
      this.priceAdjustmentSave()
    },
    priceAdjustmentSave () {
      this.$emit('priceAdjustmentSave', this.priceAdjustmentData)
    }
  }
}
</script>

<style></style>
