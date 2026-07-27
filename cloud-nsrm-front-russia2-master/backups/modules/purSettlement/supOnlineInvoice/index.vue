<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
  import NavTabs from 'lib@/components/NavTabs'
  import supOnlineInvoiceList from './supOnlineInvoiceList'
  export default {
    name: 'SupOnlineInvoice',
    components: {
      NavTabs
    },
    data () {
      return {
        activeTab: 'supOnlineInvoiceList', // 当前激活标签  与name相同
        tabs: [{
          title: () => this.$t('route.supOnlineInvoice'),
          name: 'supOnlineInvoiceList',
          component: supOnlineInvoiceList,
          closable: false
        }]
      }
    }
  }
</script>
