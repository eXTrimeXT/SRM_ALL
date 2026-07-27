<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
  import NavTabs from 'lib@/components/NavTabs'
  import performanceScoreItemsList from './performanceScoreItemsList'
  export default {
    name: 'PerformanceScoreItems',
    components: {
      NavTabs
    },
    data () {
      return {
        activeTab: 'performanceScoreItemsList', // 当前激活标签  与name相同
        tabs: [{
          title: this.$t('route.performanceScoreItems'),
          name: 'performanceScoreItemsList',
          component: performanceScoreItemsList,
          closable: false
        }]
      }
    }
  }
</script>
