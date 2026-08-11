<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import BudgetMakingList from './list'
export default {
  name: 'BudgetMaking',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'BudgetMakingList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('marketBudget.budgetMaking'), // 页面名称
          name: 'BudgetMakingList',
          component: BudgetMakingList,
          closable: false
        }
      ]
    }
  }
}
</script>
