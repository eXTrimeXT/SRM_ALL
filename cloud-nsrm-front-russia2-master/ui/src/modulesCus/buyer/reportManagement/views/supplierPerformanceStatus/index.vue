<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import SupplierPerformanceStatusList from './supplier-performance-status-list'
export default {
  name: 'SupplierPerformanceStatus',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'SupplierPerformanceStatusList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('cusEntry.reportManagement.supplierPerformanceStatusList'),
        name: 'SupplierPerformanceStatusList',
        component: SupplierPerformanceStatusList,
        closable: false
      }]
    }
  }
}
</script>
