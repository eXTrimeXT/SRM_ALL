<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import DepositCollectAndRefundList from './deposit-collect-and-refund-list'
export default {
  name: 'DepositCollectAndRefund',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'DepositCollectAndRefundList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('cusEntry.reportManagement.depositCollectAndRefundList'),
        name: 'DepositCollectAndRefundList',
        component: DepositCollectAndRefundList,
        closable: false
      }]
    }
  }
}
</script>
