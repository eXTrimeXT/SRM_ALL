<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import SourcingCooperation from './list'

export default {
  name: 'SourcingCooperation',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'SourcingCooperation',
      tabs: [
        {
          title: this.$t('cusEntry.route.sourcingCooperation'), // 寻源需求协同
          name: 'SourcingCooperation',
          component: SourcingCooperation,
          closable: false
        }
      ]
    }
  }
}
</script>
