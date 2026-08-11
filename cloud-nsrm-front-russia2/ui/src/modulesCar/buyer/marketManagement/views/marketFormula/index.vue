<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import marketFormulaList from './list'
export default {
  name: 'MarketFormula',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'marketFormulaList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('marketBudget.marketFormula'), // 页面名称
          name: 'marketFormulaList',
          component: marketFormulaList,
          closable: false
        }
      ]
    }
  }
}
</script>
