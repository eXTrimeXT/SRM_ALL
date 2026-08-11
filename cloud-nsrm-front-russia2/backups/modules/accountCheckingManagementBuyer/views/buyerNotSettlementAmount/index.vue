<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import notSettlementAmountList from './notSettlementAmountList'
export default {
  name: 'NotSettlementAmount',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'notSettlementAmountList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.notSettlementAmount'), // 未结算数量对账
          name: 'notSettlementAmountList',
          component: notSettlementAmountList,
          closable: false
        }
      ]
    }
  }
}
</script>
