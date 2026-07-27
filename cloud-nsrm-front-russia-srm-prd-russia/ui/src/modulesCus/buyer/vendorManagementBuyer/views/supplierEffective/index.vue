<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import supplierEffectiveList from './list'
export default {
  name: 'SupplierEffective',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'supplierEffectiveList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('cusEntry.route.supplierEffective'), // 供方生效
        name: 'supplierEffectiveList',
        component: supplierEffectiveList,
        closable: false
      }]
    }
  }
}
</script>
