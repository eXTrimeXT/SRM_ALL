<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import agentOnlineInvoiceList from './agentOnlineInvoiceList'
export default {
  name: 'AgentOnlineInvoice',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'agentOnlineInvoiceList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('route.invoiceManagement'),
        name: 'agentOnlineInvoiceList',
        component: agentOnlineInvoiceList,
        closable: false
      }]
    }
  }
}
</script>
