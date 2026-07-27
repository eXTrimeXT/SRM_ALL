<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import ChDesignPlanList from './list'

export default {
  name: 'ChDesignPlan',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'chDesignPlanList',
      tabs: [
        {
          title: this.$t('cusEntry.supplement20250121.chDesignPlan'), // 提报策划方案
          name: 'chDesignPlanList',
          component: ChDesignPlanList,
          closable: false
        }
      ]
    }
  }
}
</script>
