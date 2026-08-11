<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
  import NavTabs from 'lib@/components/NavTabs'
  import statementBillList from './statementBillList'
  export default {
    name: 'ContractTemplate',
    components: {
      NavTabs
    },
    data () {
      return {
        activeTab: 'statementBillList', // 当前激活标签  与name相同
        tabs: [{
          title: () => this.$t('route.statementBill'),
          name: 'statementBillList',
          component: statementBillList,
          closable: false
        }]
      }
    }
  }
</script>
