<template>
  <div>
    <div
      v-if="isBuyer && !isReadOnlyByBuyer"
      class="model-quote-buttons"
    >
      <!--新建费用项-->
      <el-button
        type="primary"
        @click="createNewCost"
      >
        {{ $t("bid_mod.createNewCost") }}
      </el-button>
      <!--删除费用项-->
      <el-button
        type="primary"
        @click="deleteCost"
      >
        {{ $t("bid_mod.deleteCost") }}
      </el-button>
      <!--删除费用类-->
      <el-button
        class="delete-cost-class"
        type="primary"
        @click="deleteCostClass"
      >
        {{ $t("bid_mod.deleteCostClass") }}
      </el-button>
    </div>

    <base-table
      stripe
      :data="modelQuoteLinesData"
      :columns="columns"
      :empty-text="$t('components.noData')"
      border
      @selection-change="selectionChangeHandle"
    >
      <!--费用-->
      <template #costType="scope">
        <el-input
          v-if="isBuyer && !isReadOnlyByBuyer"
          v-model="scope.row.costType"
        />
        <span v-else>{{ scope.row.costType }}</span>
      </template>

      <!-- 费用描述 -->
      <template #costDescription="scope">
        <el-input
          v-if="isBuyer && !isReadOnlyByBuyer"
          v-model="scope.row.costDescription"
        />
        <span v-else>{{ scope.row.costDescription }}</span>
      </template>

      <!-- 数量 -->
      <template #quantity="scope">
        <el-input
          v-if="isBuyer && !isReadOnlyByBuyer"
          v-model="scope.row.quantity"
        />
        <span v-else>{{ scope.row.quantity }}</span>
      </template>

      <!-- 单位 -->
      <template #unit="scope">
        <dict-select
          v-if="isBuyer && !isReadOnlyByBuyer"
          v-model="scope.row.unit"
          code="unit"
        />
        <span v-else>{{ $getDictLabel('unit', scope.row.unit) }}</span>
      </template>

      <!-- 未税单价 -->
      <template #notaxPrice="scope">
        <span v-if="isReadOnlyByVendor || isBuyer">{{ scope.row.notaxPrice }}</span>
        <el-input
          v-else
          v-model="scope.row.notaxPrice"
          @change="calcTaxTotalPrice(scope)"
        />
      </template>

      <!-- 税率 -->
      <template #taxKey="scope">
        <span v-if="isReadOnlyByVendor || isBuyer">{{ $getDictLabel('tax', scope.row.taxKey) }}</span>
        <dict-select
          v-else
          v-model="scope.row.taxKey"
          code="tax"
          @change-value="(value, dictItem) => taxKeyChange(value, dictItem, scope)"
        />
      </template>

      <!--备注-->
      <template #remark="scope">
        <span v-if="isReadOnlyByVendor || isBuyer">{{ scope.row.remark }}</span>
        <el-input
          v-else
          v-model="scope.row.remark"
        />
      </template>
    </base-table>
  </div>
</template>

<script>
import { createDictClass } from 'lib@/utils/dict/dict-utils'
import { bigCalcTaxPrice } from 'lib@/composition/origin/composition'
import BaseTable from 'lib@/components/BaseTable'
import Big from 'big.js'

export default {
  name: 'ModelQuote',
  components: { BaseTable },
  props: {
    linesData: {
      type: Array
    },
    isBuyer: {
      type: Boolean
    },
    isReadOnlyByVendor: {
      type: Boolean
    },
    isReadOnlyByBuyer: {
      type: Boolean
    }
  },
  data () {
    return {
      dictClass: createDictClass({ 'tax': [] }),
      selection: [],
      columns: [
        // 费用
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('bid_mod.costType'),
            prop: 'costType',
            showOverflowTooltip: true
          },
          slot: 'costType'
        },
        // 描述
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('bid_mod.costDescription'),
            prop: 'costDescription',
            showOverflowTooltip: true
          },
          slot: 'costDescription'
        },
        // 数量
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('bid_mod.quantity'),
            prop: 'quantity'
          },
          slot: 'quantity'
        },
        // 单位
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('bid_mod.unit'),
            prop: 'unit'
          },
          slot: 'unit'
        },
        // 未税单价
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('bid_mod.untaxedPrice'),
            prop: 'notaxPrice'
          },
          slot: 'notaxPrice'
        },
        // 税率
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('bid_mod.taxRate'),
            prop: 'taxKey',
            showOverflowTooltip: true
          },
          slot: 'taxKey'
        },
        // 含税合计
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('bid_mod.taxTotalPrice'),
            prop: 'taxTotalPrice'
          }
        },
        // 备注
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('bid_mod.remark'),
            prop: 'remark',
            showOverflowTooltip: true
          },
          slot: 'remark'
        }
      ],
      modelQuoteLinesData: []
    }
  },
  watch: {
    linesData: {
      handler (val) {
        this.modelQuoteLinesData = (val || []).map((item, index) => {
          return {
            ...item,
            // 冗余一个key，用于定位
            sortKey: index
          }
        })
      },
      immediate: true
    }
  },
  created () {
    if (this.isBuyer && !this.isReadOnlyByBuyer) {
      // 往开头写选择框
      this.columns.unshift({
        attrs: {
          align: 'center',
          type: 'selection'
        }
      })
    }
  },
  methods: {
    /* 税率改变 */
    taxKeyChange (val, dictItem, scope) {
      if (!val) return
      scope.row.taxRate = dictItem.key
      // 计算含税单价
      this.calcTaxTotalPrice(scope)
    },

    /* 计算含税总价 */
    calcTaxTotalPrice (scope) {
      const { quantity, notaxPrice, taxRate } = scope.row
      if (quantity && notaxPrice && taxRate) {
        const bigQuantity = Big(quantity)
        // 数量 * 未税单价 * 税率倍数
        scope.row.taxTotalPrice = Big(bigCalcTaxPrice(notaxPrice, taxRate, 10)).times(bigQuantity).toString()
      }
    },

    /* 新建费用项 */
    createNewCost () {
      this.modelQuoteLinesData.push({
        // 冗余一个key，用于定位
        sortKey: this.modelQuoteLinesData.length,
        costType: '',
        costDescription: '',
        quantity: '',
        unit: '',
        notaxPrice: '',
        taxKey: '',
        taxRate: '',
        taxTotalPrice: '',
        remark: ''
      })
    },

    /* 删除费用项 */
    deleteCost () {
      if (!this.selection.length) {
        // 未选择要删除的项
        return this.$message.warning(this.$t('bid_mod.noneSelection'))
      }
      // 把选中的直接过滤掉
      this.modelQuoteLinesData = this.modelQuoteLinesData.filter(item => {
        return !this.selection.find(selectItem => item.sortKey === selectItem.sortKey)
      }).map((item, index) => {
        return {
          ...item,
          // 更新定位的key
          sortKey: index
        }
      })
    },

    /* 删除费用类 */
    deleteCostClass () {
      this.$emit('deleteCostClass')
    },

    /* 记录表格选择 */
    selectionChangeHandle (selection) {
      this.selection = selection
    },

    /* 返回当前的数据 */
    getModelQuoteLinesData () {
      return this.modelQuoteLinesData
    }
  }
}
</script>

<style scoped>
.model-quote-buttons {
  margin-bottom: 10px;
}
.delete-cost-class {
  float: right;
}
</style>
