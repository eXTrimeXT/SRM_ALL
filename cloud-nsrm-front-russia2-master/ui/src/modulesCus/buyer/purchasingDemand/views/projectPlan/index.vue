<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import drawingsheadList from './list'
export default {
  name: 'Drawingshead',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'drawingsheadList',
      tabs: [
        {
          title: this.$t('cusEntry.supplement20250121.projectPlan'),// 项目计划
          name: 'drawingsheadList',
          component: drawingsheadList,
          closable: false
        }
      ]
    }
  }
}
</script>
