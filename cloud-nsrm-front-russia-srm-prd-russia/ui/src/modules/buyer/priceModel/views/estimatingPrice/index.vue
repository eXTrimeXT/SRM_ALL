<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import estimatingPriceList from './list'

export default {
  name: 'EstimatingPrice',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'estimatingPriceList',
      tabs: [
        {
          title: this.$t('route.estimatingPrice'),
          name: 'estimatingPriceList',
          component: estimatingPriceList,
          closable: false
        }
      ]
    }
  }
}
</script>
