<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import relationSuppliersList from './list'
export default {
  name: 'RelationSuppliers',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'relationSuppliersList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('cusEntry.route.relationSuppliers'), // 关联供应商
        name: 'relationSuppliersList',
        component: relationSuppliersList,
        closable: false
      }]
    }
  }
}
</script>
