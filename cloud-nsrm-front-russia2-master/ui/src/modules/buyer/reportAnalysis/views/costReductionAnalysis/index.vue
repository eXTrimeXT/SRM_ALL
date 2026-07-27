<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import costReductionAnalysis from './costReductionAnalysis'
export default {
  name: 'CostReductionAnalysis',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'costReductionAnalysis', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('reportMod.costReductionAnalysis'), // 采购降本
          name: 'costReductionAnalysis',
          component: costReductionAnalysis,
          closable: false
        }
      ]
    }
  }
}
</script>
