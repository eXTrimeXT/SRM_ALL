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
import purchaseOrderList from './purchaseOrderList'
import purchaseOrderDetailList from './purchaseOrderDetailList'
export default {
  name: 'BuyerPurchaseOrder',
  components: {
    NavTabs
  },
  data () {
    return {
      historyTabName: 'purchaseOrderList',
      activeTab: 'purchaseOrderList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('orderMod.purchaseOrderList'),
          name: 'purchaseOrderList',
          component: purchaseOrderList,
          closable: false
        },
        {
          title: this.$t('orderMod.purchaseOrderDetailList'),
          name: 'purchaseOrderDetailList',
          component: purchaseOrderDetailList,
          closable: false
        }
      ]
    }
  },
  watch: {
    activeTab (_newVal, oldVal) {
      if (['purchaseOrderList', 'purchaseOrderDetailList'].includes(oldVal)) {
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
      if (!['purchaseOrderList', 'purchaseOrderDetailList'].includes(activeTab)) return
      this.$refs.tabs.activeTab = this.historyTabName
    }
  }
}
</script>
