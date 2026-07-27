<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import LimitSupplierQuantityList from './limit-supplier-quantity-list'
export default {
  name: 'LimitSupplierQuantity',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'LimitSupplierQuantityList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('cusEntry.reportManagement.limitSupplierQuantityList'),
        name: 'LimitSupplierQuantityList',
        component: LimitSupplierQuantityList,
        closable: false
      }]
    }
  }
}
</script>
