<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import MaterialPriceTrendsList from './material-price-trends-list'
export default {
  name: 'MaterialPriceTrends',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'MaterialPriceTrendsList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('cusEntry.reportManagement.materialPriceTrendsList'),
        name: 'MaterialPriceTrendsList',
        component: MaterialPriceTrendsList,
        closable: false
      }]
    }
  }
}
</script>
