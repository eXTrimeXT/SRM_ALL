<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import ProcurementProgressList from './list'
export default {
  name: 'ProcurementProgress',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'ProcurementProgressList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('cusEntry.route.procurementProgress'),
        name: 'ProcurementProgressList',
        component: ProcurementProgressList,
        closable: false
      }]
    }
  }
}
</script>
