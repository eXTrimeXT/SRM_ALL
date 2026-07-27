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
          { prop: 'index', type: 'index', label: '序号', width: 50 },
          { prop: 'buName', label: this.$t('业务实体') },
          { prop: 'invName', label: this.$t('库存组织') },
          { prop: 'materialCode', label: this.$t('物料编码') },
          { prop: 'materialName', label: this.$t('物料名称') },
          // { prop: 'specification', label: this.$t('规格型号') },
          { prop: 'unitName', label: this.$t('单位名称') },
          { prop: 'contractQuantity', label: this.$t('数量') },
          { prop: 'untaxedPrice', label: this.$t('未税单价') },
          { prop: 'taxedPrice', label: this.$t('含税单价') },
          { prop: 'taxRate', label: this.$t('税率') },
          { prop: 'unAmount', label: this.$t('未税总金额'), width: 130 },
          { prop: 'amount', label: this.$t('含税总金额'), width: 150 }
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
