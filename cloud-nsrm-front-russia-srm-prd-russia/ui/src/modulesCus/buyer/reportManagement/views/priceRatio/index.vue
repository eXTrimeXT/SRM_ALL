<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import PriceRatioList from './price-ratio-list'
export default {
  name: 'PriceRatio',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'PriceRatioList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('cusEntry.reportManagement.priceRatioList'),
        name: 'PriceRatioList',
        component: PriceRatioList,
        closable: false
      }]
    }
  }
}
</script>
