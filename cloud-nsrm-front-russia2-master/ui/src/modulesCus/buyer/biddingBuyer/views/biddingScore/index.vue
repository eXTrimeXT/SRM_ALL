<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import biddingScoreList from './list.vue'

export default {
  name: 'BiddingScore',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'biddingScoreList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('cusEntry.route.biddingScore'),
          name: 'biddingScoreList',
          component: biddingScoreList,
          closable: false
        }
      ]
    }
  }
}
</script>
