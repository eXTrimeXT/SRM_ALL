<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import supplierAnalysisReportList from './list.vue'

export default {
  name: 'SupplierAnalysisReport',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'supplierAnalysisReportList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('cusEntry.route.supplierAnalysisReport'),
          name: 'supplierAnalysisReportList',
          component: supplierAnalysisReportList,
          closable: false
        }
      ]
    }
  }
}
</script>
