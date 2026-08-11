<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import biddingProjectList from './biddingProjectList'
export default {
  name: 'LogisticsProject',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'biddingProjectList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('bidMod.biddingProject'), // 招标项目管理
          name: 'biddingProjectList',
          component: biddingProjectList,
          closable: false
        }
      ]
    }
  }
}
</script>
