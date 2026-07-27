<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
  import NavTabs from 'lib@/components/NavTabs'
  import performanceScoreItemsList from './pingFenList'
  export default {
    name: 'Pingfen',
    components: {
      NavTabs
    },
    data () {
      return {
        activeTab: 'performanceScoreItemsList', // 当前激活标签  与name相同
        tabs: [{
          title: '项目化绩效评分',
          name: 'performanceScoreItemsList',
          component: performanceScoreItemsList,
          closable: false
        }]
      }
    }
  }
</script>
