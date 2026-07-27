<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import supplierAnalysis from './supplierAnalysis'
export default {
  name: 'SupplierAnalysis',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'supplierAnalysis', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('reportMod.supplierAnalysis'), // 供应商分析
          name: 'supplierAnalysis',
          component: supplierAnalysis,
          closable: false
        }
      ]
    }
  }
}
</script>
