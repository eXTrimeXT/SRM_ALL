<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import BlackSupplierQuantityList from './black-supplier-quantity-list'
export default {
  name: 'BlackSupplierQuantity',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'BlackSupplierQuantityList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('cusEntry.reportManagement.blackSupplierQuantityList'),
        name: 'BlackSupplierQuantityList',
        component: BlackSupplierQuantityList,
        closable: false
      }]
    }
  }
}
</script>
