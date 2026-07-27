<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import BidNoticeAbandonList from './list'

export default {
  name: 'BidNoticeAbandon',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'bidNoticeAbandonList',
      tabs: [
        {
          title: this.$t('cusEntry.route.bidNoticeAbandon'), // '中/落标废弃申请',
          name: 'bidNoticeAbandonList',
          component: BidNoticeAbandonList,
          closable: false
        }
      ]
    }
  }
}
</script>
