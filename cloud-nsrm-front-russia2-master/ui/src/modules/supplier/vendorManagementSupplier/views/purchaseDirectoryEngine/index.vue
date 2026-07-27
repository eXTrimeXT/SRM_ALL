<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import purchaseDirectoryList from './purchaseDirectoryList'
export default {
  name: 'PurchaseDirectory',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'PurchaseDirectoryList',
      tabs: [{
        title: this.$t('vendorMod.supplyList'), // 货源清单
        name: 'PurchaseDirectoryList',
        component: purchaseDirectoryList,
        closable: false
      }]
    }
  }
}
</script>
