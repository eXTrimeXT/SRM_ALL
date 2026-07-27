<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import SupplierActivityList from './supplier-activity-list'
export default {
  name: 'SupplierActivity',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'SupplierActivityList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('cusEntry.reportManagement.supplierActivityList'),
        name: 'SupplierActivityList',
        component: SupplierActivityList,
        closable: false
      }]
    }
  }
}
</script>
