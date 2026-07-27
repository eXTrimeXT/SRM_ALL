<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import ViewQuotationOrder from './viewQuotation'
export default {
  name: 'ViewQuotation',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'order', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('bidMod.readQuote'), // '查看报价'
          name: 'order',
          component: ViewQuotationOrder,
          closable: false
        }
      ]
    }
  }
}
</script>
