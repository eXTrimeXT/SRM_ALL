<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import demandPoolList from './list'
export default {
  name: 'DemandPoolManagement',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'demandPoolList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.demandPoolManagement'),
          name: 'demandPoolList',
          component: demandPoolList,
          closable: false
        }
      ]
    }
  },
  activated () {
    if (this.currentTab === 'demandPoolList') {
      this.dolayout()
    }
  }
}
</script>
