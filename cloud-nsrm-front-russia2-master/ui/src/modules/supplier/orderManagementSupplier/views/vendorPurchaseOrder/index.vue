<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import vendorPurchaseOrderList from './vendorPurchaseOrderList'
export default {
  name: 'VendorPurchaseOrder',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'vendorPurchaseOrderList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.buyerPurchaseOrder'), // 采购订单
          name: 'vendorPurchaseOrderList',
          component: vendorPurchaseOrderList,
          closable: false
        }
      ]
    }
  }
}
</script>
