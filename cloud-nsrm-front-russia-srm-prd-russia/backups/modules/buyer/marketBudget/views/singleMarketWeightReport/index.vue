<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import singleMarketWeightReportList from './list'
export default {
  name: 'SingleMarketWeightReport',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'singleMarketWeightReportList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('marketBudget.singleMarketWeightReport'), // 页面名称
          name: 'singleMarketWeightReportList',
          component: singleMarketWeightReportList,
          closable: false
        }
      ]
    }
  }
}
</script>
