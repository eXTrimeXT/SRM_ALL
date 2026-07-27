<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import loanApplyList from './list'
export default {
  name: 'LoanApply',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'loanApplyList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('cusEntry.route.loanApply'), // 借阅申请
          name: 'loanApplyList',
          component: loanApplyList,
          closable: false
        }
      ]
    }
  }
}
</script>
