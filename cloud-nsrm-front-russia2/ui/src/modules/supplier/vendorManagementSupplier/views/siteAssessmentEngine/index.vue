<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import SiteAssessmentListEngine from './siteAssessmentListEngine'
export default {
  name: 'SiteAssessment',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'SiteAssessmentListEngine', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.siteAssessmentV'), // 供应商评审
          name: 'SiteAssessmentListEngine',
          component: SiteAssessmentListEngine,
          closable: false
        }
      ]
    }
  }
}
</script>
