<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import ProjectProgressList from './project-progress-list'
export default {
  name: 'ProjectProgress',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'ProjectProgressList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('cusEntry.reportManagement.projectProgressList'),
        name: 'ProjectProgressList',
        component: ProjectProgressList,
        closable: false
      }]
    }
  }
}
</script>
