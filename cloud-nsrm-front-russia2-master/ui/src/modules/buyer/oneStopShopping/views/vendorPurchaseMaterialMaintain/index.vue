<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
  import NavTabs from 'lib@/components/NavTabs'
  import vendorPurchaseMaterialMaintainList from './vendorPurchaseMaterialMaintainList'
  export default {
    name: 'VendorPurchaseMaterialMaintain',
    components: {
      NavTabs
    },
    data () {
      return {
        activeTab: 'vendorPurchaseMaterialMaintainList', // 当前激活标签  与name相同
        tabs: [{
          title: this.$t('oneStopShopping.vendorPurchaseMaterialMaintain'),
          name: 'vendorPurchaseMaterialMaintainList',
          component: vendorPurchaseMaterialMaintainList,
          closable: false
        }]
      }
    }
  }
</script>
