<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import BidPriceLibraryList from './list'

export default {
  name: 'BidPriceLibrary',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'bidPriceLibraryList',
      tabs: [
        {
          title: '招标价格库',
          name: 'bidPriceLibraryList',
          component: BidPriceLibraryList,
          closable: false
        }
      ]
    }
  }
}
</script>
