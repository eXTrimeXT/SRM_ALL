<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import vendorDemotionList from './vendorDemotionList'
export default {
  name: 'VendorDemotion',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'vendorDemotionList', // 当前激活标签  与name相同
      tabs: [{
        title: this.$t('vendorMod.relegation.promotionDemotionList'),
        name: 'vendorDemotionList',
        component: vendorDemotionList,
        closable: false
      }]
    }
  }
}
</script>
