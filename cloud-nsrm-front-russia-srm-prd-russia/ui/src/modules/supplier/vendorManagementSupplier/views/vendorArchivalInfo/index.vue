<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import vendorArchival from './vendorArchival'
export default {
  name: 'VendorArchivalInfo',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'vendorArchival', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('vendorMod.archivalInfo'), // 档案信息
          name: 'vendorArchival',
          component: vendorArchival,
          closable: false
        }
      ]
    }
  }
}
</script>
