<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import orderAcceptanceList from './list'
export default {
  name: 'VendorOrderAcceptance',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'orderAcceptanceList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('cusEntry.route.orderAcceptance'), // 订单验收单列表
          name: 'orderAcceptanceList',
          component: orderAcceptanceList,
          closable: false
        }
      ]
    }
  }
}
</script>
