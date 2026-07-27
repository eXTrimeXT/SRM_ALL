<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import workflowReport from 'lib@/components/c-workflow/workflowReport'

export default {
  name: 'Approval',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'workflowReport', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('flowMod.flowDetail'), // '流程详情',
        name: 'workflowReport',
        component: workflowReport,
        closable: false,
        params: {
          tabName: 'workflowReport' + this.$route.query.fdId
        }
      }]
    }
  }
}
</script>
