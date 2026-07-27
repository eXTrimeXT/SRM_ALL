<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
/**
 * 招标技术评分
 */
import NavTabs from 'lib@/components/NavTabs'
import technologyScoreList from './technologyScoreList'

export default {
  name: 'TechnologyScore',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'technologyScoreList',
      tabs: [
        {
          title: '招标技术评分',
          name: 'technologyScoreList',
          component: technologyScoreList,
          closable: false
        }
      ]
    }
  }
}
</script>
