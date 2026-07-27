<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import biddingOrdersList from './biddingOrdersList.vue'

export default {
  name: 'BiddingList',

  components: { NavTabs },

  data () {
    return {
      activeTab: 'biddingOrdersList',
      tabs: [
        {
          title: this.$t('cusEntry.route.biddingList'),
          name: 'biddingOrdersList',
          component: biddingOrdersList,
          closable: false
        }
      ]
    }
  }
}
</script>
