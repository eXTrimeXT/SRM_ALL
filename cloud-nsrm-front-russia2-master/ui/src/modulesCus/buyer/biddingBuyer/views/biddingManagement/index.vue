<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import biddingList from './biddingList.vue'

export default {
  name: 'BiddingManagementNew',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'biddingList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.biddingManagement'),
          name: 'biddingList',
          component: biddingList,
          closable: false
        }
      ]
    }
  }
}
</script>
