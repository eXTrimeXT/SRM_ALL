<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import purchaseDirectoryList from './purchaseDirectoryChangeList'
export default {
  name: 'PurchaseDirectoryChangeSupplier',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'PurchaseDirectoryList',
      tabs: [{
        title: '货源变更',
        name: 'PurchaseDirectoryList',
        component: purchaseDirectoryList,
        closable: false
      }]
    }
  }
}
</script>
