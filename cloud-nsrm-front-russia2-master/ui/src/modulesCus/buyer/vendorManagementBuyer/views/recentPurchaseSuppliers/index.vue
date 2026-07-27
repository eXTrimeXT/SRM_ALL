<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import recentPurchaseSuppliersList from './list'
export default {
  name: 'RecentPurchaseSuppliers',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'recentPurchaseSuppliersList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('cusEntry.route.recentPurchaseSuppliers'), // 近期采购供应商
        name: 'recentPurchaseSuppliersList',
        component: recentPurchaseSuppliersList,
        closable: false
      }]
    }
  }
}
</script>
