<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import BarcodePrintTemp from './list'
export default {
  name: 'BarcodePrintTemp',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'barcodePrintTempList',
      tabs: [{
        title: () => this.$t('barcodeManageNew.barcodePrintTemp'),
        name: 'barcodePrintTempList',
        component: BarcodePrintTemp,
        clsable: false
      }]
    }
  }
}
</script>
