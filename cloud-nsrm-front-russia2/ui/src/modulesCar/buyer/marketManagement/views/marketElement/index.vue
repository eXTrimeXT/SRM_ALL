<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import marketElementList from './list'
export default {
  name: 'MarketElement',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'marketElementList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('marketBudget.marketElement'), // 页面名称
          name: 'marketElementList',
          component: marketElementList,
          closable: false
        }
      ]
    }
  }
}
</script>
