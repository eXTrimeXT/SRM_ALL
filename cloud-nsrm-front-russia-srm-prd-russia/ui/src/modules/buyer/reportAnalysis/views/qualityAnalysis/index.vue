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
  name: 'QualityAnalysis',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'qualityAnalysis', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('reportMod.qualityAnalysis'), // 采购降本
          name: 'qualityAnalysis',
          component: costReductionAnalysis,
          closable: false
        }
      ]
    }
  }
}
</script>
