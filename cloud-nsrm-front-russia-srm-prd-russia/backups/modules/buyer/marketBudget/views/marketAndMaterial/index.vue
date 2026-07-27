v<template>
  <NavTabs :tabs-list="tabs" :cur-tab="activeTab" />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import marketAndMaterialList from './list'

export default {
  name: 'MarketAndMaterial',

  components: { NavTabs },

  data () {
    return {
      activeTab: 'marketAndMaterialList',
      tabs: [
        {
          title: this.$t('marketBudget.marketAndMaterial'),
          name: 'marketAndMaterialList',
          component: marketAndMaterialList,
          closable: false
        }
      ]
    }
  }
}
</script>
