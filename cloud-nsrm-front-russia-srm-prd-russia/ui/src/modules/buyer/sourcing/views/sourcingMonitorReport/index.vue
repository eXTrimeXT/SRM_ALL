<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import sourcingMonitorReport from './list'

export default {
  name: 'SourcingMonitorReport',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'sourcingMonitorReport', // 当前激活标签  与name相同
      tabs: [
        {
          title: '寻源监控报表', // 寻源监控报表
          name: 'sourcingMonitorReport',
          component: sourcingMonitorReport,
          closable: false
        }
      ]
    }
  }
}
</script>
