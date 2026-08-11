<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import invoiceNoticeList from './invoiceNoticeList'
export default {
  name: 'PurStatementBillSupplier',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'invoiceNoticeList', // 当前激活标签  与name相同
      tabs: [{
        title: this.$t('route.statementBill'),
        name: 'invoiceNoticeList',
        component: invoiceNoticeList,
        closable: false
      }]
    }
  }
}
</script>
