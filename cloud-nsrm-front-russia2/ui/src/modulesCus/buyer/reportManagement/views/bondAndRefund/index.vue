<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import BondAndRefundList from './bond-and-refund-list'
export default {
  name: 'BondAndRefund',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'BondAndRefundList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('cusEntry.reportManagement.bondAndRefundList'),
        name: 'BondAndRefundList',
        component: BondAndRefundList,
        closable: false
      }]
    }
  }
}
</script>
