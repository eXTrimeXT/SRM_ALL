<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
// import ContractPerformancePlanList from './list_backup'
import contractPerformancePlanListEngine from './list-engine'

export default {
  name: 'ContractPerformancePlan',
  components: {
    NavTabs
  },
  data () {
    return {
      // activeTab: 'contractPerformancePlanList',
      activeTab: 'contractPerformancePlanListEngine',
      tabs: [
        {
          // title: '合同履约计划',
          // name: 'contractPerformancePlanList',
          // component: ContractPerformancePlanList,
          title: this.$t('route.contractPerformancePlanList'),
          name: 'contractPerformancePlanListEngine',
          component: contractPerformancePlanListEngine,
          closable: false
        },
        // {
        //   title: '合同履约计划 old',
        //   name: 'contractPerformancePlanList',
        //   component: ContractPerformancePlanList,
        //   closable: false
        // }
      ]
    }
  }
}
</script>
