<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import BarcodeRuleConfig from './list'
export default {
  name: 'BarcodeRuleConfig',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'barcodeRuleConfigList',
      tabs: [{
        title: () => this.$t('barcodeManageNew.barcodeRuleConfig'),
        name: 'barcodeRuleConfigList',
        component: BarcodeRuleConfig,
        clsable: false
      }]
    }
  }
}
</script>
