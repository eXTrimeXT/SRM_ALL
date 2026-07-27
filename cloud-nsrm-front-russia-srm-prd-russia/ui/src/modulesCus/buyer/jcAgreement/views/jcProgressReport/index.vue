<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import JcProgressReportList from './list'

export default {
  name: 'JcProgressReport',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'jcProgressReportList',
      tabs: [
        {
          title: '集采进度',
          name: 'jcProgressReportList',
          component: JcProgressReportList,
          closable: false
        }
      ]
    }
  }
}
</script>
