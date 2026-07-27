<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import logisticsPurchaseOrderList from './logisticsPurchaseOrderList'
export default {
  name: 'LogisticsPurchaseOrder',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'logisticsPurchaseOrderList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.logisticsPurchaseOrder'), // 物流采购订单
          name: 'logisticsPurchaseOrderList',
          component: logisticsPurchaseOrderList,
          closable: false
        }
      ]
    }
  }
}
</script>
