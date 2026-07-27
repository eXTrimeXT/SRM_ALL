<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import vendorProfileListEngine from './vendorProfileListEngine'
export default {
  name: 'VendorProfile',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'vendorProfileListEngine', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('vendorMod.vendorProfileList'), // 供应商清单
          name: 'vendorProfileListEngine',
          component: vendorProfileListEngine,
          closable: false
        }
      ]
    }
  }
}
</script>
