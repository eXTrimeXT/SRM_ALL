<!-- 订单明细 -->
<template>
  <IMiniTable
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
          // { prop: 'index', type: 'index', label: '序号', width: 50 },
          { prop: 'index', type: 'index', label: this.$t('components.common.sort'), width: 50 },
          // { prop: 'buName', label: '公司' },
          { prop: 'buName', label: this.$t('components.organization.COMPANY') },
          // { prop: 'invName', label: this.$t('库存组织') },
          { prop: 'materialCode', label: this.$t('common.materialCode') }, //物料编码
          { prop: 'materialName', label: this.$t('common.materialName') }, //'物料名称'
          // { prop: 'specification', label: this.$t('规格型号') },
          { prop: 'unitName', label: this.$t('dataConfMod.unitName') },
          { prop: 'contractQuantity', label: this.$t('bid_mod.quantity') }, // 数量
          { prop: 'untaxedPrice', label: this.$t('bid_mod.untaxedPrice') },  // 未税单价
          { prop: 'taxedPrice', label: this.$t('bid_mod.taxUnitPrice') },   // 含税单价
          { prop: 'taxRate', label: this.$t('bidMod.taxRate2') },   // 税率（%）
          { prop: 'unAmount', label: this.$t('contractMod.totalAmountNoTax2'), width: 130 },  // 未税总金额
          { prop: 'amount', label: this.$t('contractMod.totalAmountTax'), width: 150 }  // 含税总金额
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
