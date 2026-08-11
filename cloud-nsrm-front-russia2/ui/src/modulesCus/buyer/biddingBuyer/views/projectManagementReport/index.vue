<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import projectManagementReportList from './list.vue'

export default {
  name: 'ProjectManagementReport',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'projectManagementReportList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('cusEntry.route.projectManagementReport'),
          name: 'projectManagementReportList',
          component: projectManagementReportList,
          closable: false
        }
      ]
    }
  }
}
</script>
