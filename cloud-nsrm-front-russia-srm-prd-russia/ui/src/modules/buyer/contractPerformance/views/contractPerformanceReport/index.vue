<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import contractPerformanceReportList from './list'
export default {
  name: 'ContractPerformanceReport',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'contractPerformanceReportList', // 当前激活标签  与name相同
      tabs: [
        {
          title: '合同履约报表', // 页面名称
          name: 'contractPerformanceReportList',
          component: contractPerformanceReportList,
          closable: false
        }
      ]
    }
  }
}
</script>
