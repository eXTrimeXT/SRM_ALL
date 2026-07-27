<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import JcQuotationList from './list'
export default {
  name: 'JcQuotation',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'List', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('cusEntry.supplement20250205.supplierQuoteForm'),  // '供应商报价单'
          name: 'List',
          component: JcQuotationList,
          closable: false
        }
      ]
    }
  }
}
</script>
