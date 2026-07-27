<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
/**
 * 项目式询价技术评分
 */
import NavTabs from 'lib@/components/NavTabs'
import biddingTechScoreList from './biddingTechScoreList.vue'

export default {
  name: 'BiddingTechScore',

  components: { NavTabs },

  data () {
    return {
      activeTab: 'biddingTechScoreList',
      tabs: [
        {
          title: '技术评分',
          name: 'biddingTechScoreList',
          component: biddingTechScoreList,
          closable: false
        }
      ]
    }
  }
}
</script>
