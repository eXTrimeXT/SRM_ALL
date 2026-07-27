<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import ProcurementProgressRUBList from './list'
export default {
  name: 'ProcurementProgressRUB',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'ProcurementProgressRUBList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('cusEntry.route.procurementProgressRUB'),
        name: 'ProcurementProgressRUBList',
        component: ProcurementProgressRUBList,
        closable: false
      }]
    }
  }
}
</script>
