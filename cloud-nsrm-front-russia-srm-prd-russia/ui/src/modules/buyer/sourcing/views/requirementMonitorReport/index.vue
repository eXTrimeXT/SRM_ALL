<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import requirementMonitorReportList from './list'

export default {
  name: 'RequirementMonitorReport',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'requirementMonitorReportList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('sourcingBuyer.requirementMonitorReport'), // 需求监控报表
          name: 'requirementMonitorReportList',
          component: requirementMonitorReportList,
          closable: false
        }
      ]
    }
  }
}
</script>
