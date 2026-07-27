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
  },
  activated () {
    if (this.currentTab === 'vendorPurchaseOrderList') {
      this.dolayout()
    }
  },
  methods: {
    tabChange (tab) {
      if (tab === 'vendorPurchaseOrderList') {
        this.dolayout()
      }
      this.currentTab = tab
    },
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'vendorPurchaseOrderList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    }
  }
}
</script>
