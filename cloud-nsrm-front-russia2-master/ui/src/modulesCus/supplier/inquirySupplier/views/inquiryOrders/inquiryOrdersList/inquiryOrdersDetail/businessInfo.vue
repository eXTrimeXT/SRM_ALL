<template>
  <div>
    <SrmRow>
      <SrmCol :init-col="3">
        <!--本位币-->
        <el-form-item :label="$t('bid_mod.standardCurrency')">
          <dict-select
            v-model="header.standardCurrency"
            code="currency"
            disabled
          />
        </el-form-item>
      </SrmCol>

      <SrmCol :init-col="3">
        <el-form-item :label="$t('bidMod.priceNum')">
          <el-input
            v-model="header.pricePrecision"
            disabled
          />
        </el-form-item>
      </SrmCol>
    </SrmRow>

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

export default {
  name: 'BusinessInfo',
  components: { BaseTable },

  props: {
    header: {
      type: Object,
      required: true
    },
    currencyList: {
      type: Array,
      required: true
    }
  },

  data () {
    return {
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
