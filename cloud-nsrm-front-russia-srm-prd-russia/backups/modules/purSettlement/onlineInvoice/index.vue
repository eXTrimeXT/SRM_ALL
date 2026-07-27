<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
  import NavTabs from 'lib@/components/NavTabs'
  import onlineInvoiceList from './onlineInvoiceList'
  export default {
    name: 'OnlineInvoice',
    components: {
      NavTabs
    },
    data () {
      return {
        activeTab: 'onlineInvoiceList', // 当前激活标签  与name相同
        tabs: [{
          title: () => this.$t('route.onlineInvoice'),
          name: 'onlineInvoiceList',
          component: onlineInvoiceList,
          closable: false
        }]
      }
    }
  }
</script>
