<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import VendorDemotion from './list-engine'
export default {
  name: 'VendorDemotion',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'VendorDemotionList',
      tabs: [{
        title: () => this.$t('vendorMod.relegation.promotionDemotionList'),
        name: 'VendorDemotionList',
        component: VendorDemotion,
        closable: false
      }]
    }
  }
}
</script>
