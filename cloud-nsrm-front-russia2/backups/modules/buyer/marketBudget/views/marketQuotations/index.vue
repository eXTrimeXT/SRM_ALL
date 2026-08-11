v<template>
  <NavTabs :tabs-list="tabs" :cur-tab="activeTab" />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import marketQuotationsList from './list'

export default {
  name: 'MarketQuotations',

  components: { NavTabs },

  data () {
    return {
      activeTab: 'marketQuotationsList',
      tabs: [
        {
          title: this.$t('marketBudget.marketQuotations'),
          name: 'marketQuotationsList',
          component: marketQuotationsList,
          closable: false
        }
      ]
    }
  }
}
</script>
