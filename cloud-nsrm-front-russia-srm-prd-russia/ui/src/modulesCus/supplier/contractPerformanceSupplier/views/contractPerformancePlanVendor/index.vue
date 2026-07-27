<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import ContractPerformancePlanList from './list'
import ContractPerformancePlanListEngine from './list-engine'
import ContractPerformancePlanDetailEngine from './edit-engine'
export default {
  name: 'ContractPerformancePlanVendor',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'ContractPerformancePlanListEngine',
      tabs: [
        {
          title: '合同履约计划',
          name: 'ContractPerformancePlanListEngine',
          component: ContractPerformancePlanListEngine,
          closable: false
        },
        // {
        //   title: '合同履约计划详情-engine',
        //   name: 'ContractPerformancePlanDetailEngine',
        //   component: ContractPerformancePlanDetailEngine,
        //   closable: false
        // },
        // {
        //   title: '合同履约计划',
        //   name: 'contractPerformancePlanList',
        //   component: ContractPerformancePlanList,
        //   closable: false
        // }
      ]
    }
  }
}
</script>
