<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import PlanImplementationList from './plan-implementation-list'
export default {
  name: 'PlanImplementation',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'PlanImplementationList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('cusEntry.reportManagement.planImplementationList'),
        name: 'PlanImplementationList',
        component: PlanImplementationList,
        closable: false
      }]
    }
  }
}
</script>
