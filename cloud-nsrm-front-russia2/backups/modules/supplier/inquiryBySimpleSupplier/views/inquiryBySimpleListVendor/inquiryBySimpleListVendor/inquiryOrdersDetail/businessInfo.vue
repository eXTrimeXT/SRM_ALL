<template>
  <div>
    <srm-row>
      <srm-col :initCol="3">
        <!--本位币-->
        <el-form-item :label="$t('bid_mod.standardCurrency')">
          <dict-select
            v-model="header.currency"
            code="currency"
            disabled
          />
        </el-form-item>
      </srm-col>

      <srm-col :initCol="3">
        <el-form-item :label="$t('bidMod.priceNum')">
          <el-input
            v-model="header.priceNum"
            disabled
          />
        </el-form-item>
      </srm-col>
    </srm-row>

    <p>{{ $t('bid_mod.quoteCurrencyExplain1') }}</p>

    <BaseTable
      stripe
      :data="currencyList"
      :columns="columns"
      :empty-text="$t('components.noData')"
      border
    >
      <!--t 币种-->
      <template #currencyCode="scope">
        <dict-select
          v-model="scope.row.currencyCode"
          code="currency"
          disabled
        />
      </template>

      <!--t 描述-->
      <template #currencyDesc="scope">
        <el-input
          v-model="scope.row.currencyDesc"
          maxlength="100"
          show-word-limit
          disabled
        />
      </template>

      <!--价格精度-->
      <template #pricePrecision="scope">
        <dict-select
          v-model="scope.row.pricePrecision"
          code="PRICE_PRECISION"
          :dictClass="dictClass"
          disabled
        />
      </template>
    </BaseTable>
  </div>
</template>

<script>
/**
 * 商务信息
 */
import BaseTable from 'lib@/components/BaseTable'
import { createDictClass } from '@/library/utils/dict/dict-utils'
const dictClass = createDictClass({
   'PRICE_PRECISION': [ { id: 0, label: '0', value: 0 },
          { id: 1, label: '1', value: 1 },
          { id: 2, label: '2', value: 2 },
          { id: 3, label: '3', value: 3 },
          { id: 4, label: '4', value: 4 },
          { id: 5, label: '5', value: 5 },
          { id: 6, label: '6', value: 6 }]
    }, false)

export default {
  name: 'BusinessInfo',
  components: { BaseTable },
  props: {
    header: Object,
    currencyList: Array
  },
  data () {
    return {
      dictClass: dictClass,
      columns: [
        // t 币种
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('bid_mod.currencyName'),
            prop: 'currencyCode'
          },
          slot: 'currencyCode'
        },
        // t 描述
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('bid_mod.currencyDesc'),
            prop: 'currencyDesc'
          },
          slot: 'currencyDesc'
        },
        // t 汇率
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('bid_mod.priceTax'),
            prop: 'priceTax'
          }
        },
        // t 价格精度
        {
          attrs: {
            align: 'center',
            minWidth: '100',
            label: t => t.$t('bid_mod.pricePrecision'),
            prop: 'pricePrecision'
          },
          slot: 'pricePrecision'
        }
      ]
    }
  }
}
</script>
