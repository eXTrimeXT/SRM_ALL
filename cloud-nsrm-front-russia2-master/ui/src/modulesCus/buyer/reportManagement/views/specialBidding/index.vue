<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import SpecialBiddingList from './special-bidding-list'
export default {
  name: 'SpecialBidding',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'SpecialBiddingList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('cusEntry.reportManagement.specialBiddingList'),
        name: 'SpecialBiddingList',
        component: SpecialBiddingList,
        closable: false
      }]
    }
  }
}
</script>
