<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import ExpertDatabaseList from './list'

export default {
  name: 'ExpertDatabase',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'expertDatabaseList',
      tabs: [
        {
          title: '专家库列表',
          name: 'expertDatabaseList',
          component: ExpertDatabaseList,
          closable: false
        }
      ]
    }
  }
}
</script>
