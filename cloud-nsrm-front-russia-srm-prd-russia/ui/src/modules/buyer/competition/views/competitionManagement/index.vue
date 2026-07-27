<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import competitionManagementList from './competitionManagementList'

export default {
  name: 'CompetitionManagement',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'competitionManagementList',
      tabs: [
        {
          title: this.$t('route.competitionManagement'),
          name: 'competitionManagementList',
          component: competitionManagementList,
          closable: false
        }
      ]
    }
  }
}
</script>
