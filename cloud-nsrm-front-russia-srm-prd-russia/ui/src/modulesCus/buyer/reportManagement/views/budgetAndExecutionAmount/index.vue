<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import BudgetAndExecutionAmountList from './budget-and-execution-amount-list'
export default {
  name: 'BudgetAndExecutionAmount',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'BudgetAndExecutionAmountList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('cusEntry.reportManagement.budgetAndExecutionAmountList'),
        name: 'BudgetAndExecutionAmountList',
        component: BudgetAndExecutionAmountList,
        closable: false
      }]
    }
  }
}
</script>
