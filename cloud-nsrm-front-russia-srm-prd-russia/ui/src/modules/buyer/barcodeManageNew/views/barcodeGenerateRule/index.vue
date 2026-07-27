<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import BarcodeGenerateRuleList from './list'
export default {
  name: 'BarcodeGenerateRule',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'BarcodeGenerateRuleList',
      tabs: [{
        title: () => this.$t('route.barcodeGenerateRule'),
        name: 'BarcodeGenerateRuleList',
        component: BarcodeGenerateRuleList,
        closable: false
      }]
    }
  }
}
</script>
