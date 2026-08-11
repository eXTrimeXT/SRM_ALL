<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import SourcingRequireList from './list'

export default {
  name: 'SourcingRequireBuyer',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'sourcingRequireList',
      tabs: [
        {
          // title: '寻源需求列表',
          title: () => this.$t('route.sourcingApplicationBuyer'),
          name: 'sourcingRequireList',
          component: SourcingRequireList,
          closable: false
        }
      ]
    }
  }
}
</script>
