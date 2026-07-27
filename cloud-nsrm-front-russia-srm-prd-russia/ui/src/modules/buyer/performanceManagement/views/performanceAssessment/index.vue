<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import assessmentList from './assessmentList'
export default {
  name: 'PerformanceAssessment',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'assessmentList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('route.performanceAssessment'),
        name: 'assessmentList',
        component: assessmentList,
        closable: false
      }]
    }
  }
}
</script>
