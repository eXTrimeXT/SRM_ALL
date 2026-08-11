<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import purchaseDirectoryChangeList from './purchaseDirectoryChangeList'
export default {
  name: 'PurchaseDirectoryChange',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'PurchaseDirectoryChangeList',
      tabs: [{
        title: this.$t('route.purchaseDirectoryChange'),
        name: 'PurchaseDirectoryChangeList',
        component: purchaseDirectoryChangeList,
        closable: false
      }]
    }
  }
}
</script>
