<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import bargainOrdersList from './bargainOrdersList.vue'

export default {
  name: 'BargainOrders',

  components: { NavTabs },

  data () {
    return {
      activeTab: 'bargainOrdersList',
      tabs: [
        {
          title: this.$t('route.vendorBiddingList1'),
          name: 'bargainOrdersList',
          component: bargainOrdersList,
          closable: false
        }
      ]
    }
  }
}
</script>
