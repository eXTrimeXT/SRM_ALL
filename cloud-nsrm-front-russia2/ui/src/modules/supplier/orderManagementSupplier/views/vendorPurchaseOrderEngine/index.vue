<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
    @tab-change="tabChange"
    @tab-remove="tabRemove"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import vendorPurchaseOrderList from './vendorPurchaseOrderList'
import vendorPurchaseOrderDetailList from './vendorPurchaseOrderDetailList'
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
          title: this.$t('orderMod.purchaseOrderList'),
          name: 'vendorPurchaseOrderList',
          component: vendorPurchaseOrderList,
          closable: false
        },
        {
          title: this.$t('orderMod.purchaseOrderDetailList'),
          name: 'vendorPurchaseOrderDetailList',
          component: vendorPurchaseOrderDetailList,
          closable: false
        }
      ]
    }
  },
  watch: {
    activeTab (_newVal, oldVal) {
      if (['vendorPurchaseOrderList', 'vendorPurchaseOrderDetailList'].includes(oldVal)) {
        this.historyTabName = oldVal
      }
    }
  },
  methods: {
    tabChange (tab) {
      this.activeTab = tab
      this.tabs = this.$refs.tabs.tabs
    },
    tabRemove ({ activeTab }) {
      if (!['vendorPurchaseOrderList', 'vendorPurchaseOrderDetailList'].includes(activeTab)) return
      this.$refs.tabs.activeTab = this.historyTabName
    }
  }
}
</script>
