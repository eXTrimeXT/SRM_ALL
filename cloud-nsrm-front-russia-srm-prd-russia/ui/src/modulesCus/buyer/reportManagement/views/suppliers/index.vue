<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import SuppliersList from './suppliers-list'
export default {
  name: 'Suppliers',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'SuppliersList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('cusEntry.reportManagement.suppliersList'),
        name: 'SuppliersList',
        component: SuppliersList,
        closable: false
      }]
    }
  }
}
</script>
