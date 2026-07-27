<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import competitionProjectList from './competitionProjectList'

export default {
  name: 'CompetitionProject',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'competitionProjectList',
      tabs: [
        {
          title: this.$t('bidMod.biddingItems'),
          name: 'competitionProjectList',
          component: competitionProjectList,
          closable: false
        }
      ]
    }
  }
}
</script>
