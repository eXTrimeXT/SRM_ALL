<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import BidNoticeList from './list'

export default {
  name: 'BidNotice',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'bidNoticeList',
      tabs: [
        {
          title: '中/落标通知',
          name: 'bidNoticeList',
          component: BidNoticeList,
          closable: false
        }
      ]
    }
  }
}
</script>
