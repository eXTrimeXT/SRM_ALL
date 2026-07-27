<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import SiteAssessmentList from './siteAssessmentList'
export default {
  name: 'NonSiteAssessment',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'SiteAssessmentList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.siteAssessment'), // 供应商评审
          name: 'SiteAssessmentList',
          component: SiteAssessmentList,
          closable: false
        }
      ]
    }
  }
}
</script>
