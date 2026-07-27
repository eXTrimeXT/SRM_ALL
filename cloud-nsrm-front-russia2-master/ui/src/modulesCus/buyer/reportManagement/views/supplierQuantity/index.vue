<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import SupplierQuantityList from './supplier-quantity-list'
export default {
  name: 'SupplierQuantity',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'SupplierQuantityList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('cusEntry.reportManagement.supplierQuantityList'),
        name: 'SupplierQuantityList',
        component: SupplierQuantityList,
        closable: false
      }]
    }
  }
}
</script>
