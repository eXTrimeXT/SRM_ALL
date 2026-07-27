<!-- 订单明细 -->
<template>
  <i-mini-table
    :columns="columns"
    :data="data"
    border
  />
</template>

<script>
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
          { prop: 'index', type: 'index', label: this.$t('components.common.sort'), width: 50 },
          { prop: 'buName', label: this.$t('components.organization.ORG') },
          { prop: 'invName', label: this.$t('components.organization.INV') },
          { prop: 'materialCode', label: this.$t('common.materialCode') },
          { prop: 'materialName', label: this.$t('common.materialName') },
          // { prop: 'specification', label: this.$t('vendorMod.specification') },
          { prop: 'unitName', label: this.$t('dataConfMod.unitName') },
          { prop: 'contractQuantity', label: this.$t('bid_mod.quantity') },
          { prop: 'untaxedPrice', label: this.$t('bid_mod.untaxedPrice') },
          { prop: 'taxedPrice', label: this.$t('bid_mod.taxUnitPrice') },
          { prop: 'taxRate', label: this.$t('bidMod.taxRate2') },
          { prop: 'unAmount', label: this.$t('contractMod.totalAmountNoTax2'), width: 130 },
          { prop: 'amount', label: this.$t('contractMod.totalAmountTax'), width: 150 }
        ]
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
