<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
  import NavTabs from 'lib@/components/NavTabs'
  import quotationPricesList from './quotationPricesList'
  export default {
    name: 'QuotationPrices',
    components: {
      NavTabs
    },
    data () {
      return {
        activeTab: 'quotationPricesList', // 当前激活标签  与name相同
        tabs: [{
          title: '报价单列表',
          name: 'quotationPricesList',
          component: quotationPricesList,
          closable: false
        }]
      }
    }
  }
</script>
