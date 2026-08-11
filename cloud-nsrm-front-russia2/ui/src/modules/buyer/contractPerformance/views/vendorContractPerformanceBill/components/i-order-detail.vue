<!-- 订单明细 -->
<template>
  <i-mini-table
    :columns="columns"
    :data="data"
    border
  />
</template>

<script lang="jsx">
import IMiniTable from './i-mini-table.vue'

export default {
  name: 'IOrderDetail',
  components: { IMiniTable },
  props: {
    data: {},
    editable: {
      type: Boolean,
      default: false
    },
    disabled: {}
  },
  data () {
    return {
      columns: []
    }
  },
  computed: {},
  watch: {
    editable: {
      handler (value) {
        let cols = [
          { prop: 'index', type: 'index' },
          { prop: 'buName', label: this.$t('mould.orgId') },
          { prop: 'materialCode', label: this.$t('common.materialCode') },
          { prop: 'materialName', label: this.$t('common.materialName') },
          { prop: 'specification', label: this.$t('contractMod.specification') },
          { prop: 'unitName', label: this.$t('dataConfMod.unitName') },
          { prop: 'contractQuantity', label: this.$t('contractMod.contractQuantity') },
          { prop: 'untaxedPrice', label: this.$t('contractMod.untaxedPrice') },
          { prop: 'taxedPrice', label: this.$t('contractMod.taxedPrice') },
          { prop: 'taxRate', label: this.$t('purchaseDemand.taxRate') },
          { prop: 'amount', label: this.$t('contractMod.totalAmountTax'), width: 150 }
        ]
        if (value) {
          cols = cols.concat([
            { prop: 'alreadyDeliveryNum', label: this.$t('contract_mod.deliveredQuantity'), width: 150 },
            {
              prop: 'deliveryNum',
              label: this.$t('contract_mod.deliveryNum'),
              renderHeader: this._addStarToColumn,
              width: 150,
              render: (h, scope) => {
                return <el-input disabled={this.disabled} v-model={scope.row.deliveryNum} clearable />
              }
            }
          ])
        }
        this.columns = cols
        console.log('[cols]', this.columns)
      },
      immediate: true
    }
  },
  created () {},
  mounted () {},
  methods: {}
}
</script>
<style scoped lang="scss">
</style>
