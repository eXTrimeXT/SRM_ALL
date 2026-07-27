<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import ContractPerformanceInvoiceList from './list'
export default {
  name: 'ContractPerformanceInvoiceVendor',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'contractPerformanceInvoiceList',
      tabs: [
        {
          title: this.$t('route.contractPerformanceInvoice'), // 合同履约开票
          name: 'contractPerformanceInvoiceList',
          component: ContractPerformanceInvoiceList,
          closable: false
        }
      ]
    }
  }
}
</script>
