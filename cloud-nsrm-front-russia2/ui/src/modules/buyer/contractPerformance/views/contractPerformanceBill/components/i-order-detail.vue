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
          { prop: 'index', type: 'index', label: this.$t('components.common.sort'), width: 50 },  // '序号'
          { prop: 'buName', label: this.$t('components.organization.ORG') },  // 业务实体
          { prop: 'invName', label: this.$t('components.organization.INV') },  // 库存组织
          { prop: 'materialCode', label: this.$t('common.materialCode') },  // 物料编码
          { prop: 'materialName', label: this.$t('common.materialName') },  // 物料名称
          // { prop: 'specification', label: this.$t('规格型号') },
          { prop: 'unitName', label: this.$t('dataConfMod.unitName') },  // 单位名称
          { prop: 'contractQuantity', label: this.$t('bid_mod.quantity') },  // 数量
          { prop: 'untaxedPrice', label: this.$t('bid_mod.untaxedPrice') },  // 未税单价
          { prop: 'taxedPrice', label: this.$t('bid_mod.taxUnitPrice') }, // 含税单价
          { prop: 'taxRate', label: this.$t('components.ocr.commodityTaxRate') },  // 税率
          { prop: 'unAmount', label: this.$t('contractMod.totalAmountNoTax2'), width: 130 },  // 未税总金额
          { prop: 'amount', label: this.$t('contractMod.totalAmountTax'), width: 150 }  // 含税总金额
        ]
        // if (value) {
        //   cols = cols.concat([
        //     { prop: 'alreadyDeliveryNum', label: this.$t('已交付数量'), width: 150 },
        //     {
        //       prop: 'deliveryNum',
        //       label: this.$t('本次交交付量'),
        //       renderHeader: this._addStarToColumn,
        //       width: 150,
        //       render: (h, scope) => {
        //         return <el-input disabled={this.disabled} v-model={scope.row.deliveryNum} clearable />
        //       }
        //     }
        //   ])
        // }
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
