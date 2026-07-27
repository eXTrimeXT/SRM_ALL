<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
  import NavTabs from 'lib@/components/NavTabs'
  import returnGoodsBillList from './returnGoodsBillList'
  export default {
    name: 'ContractTemplate',
    components: {
      NavTabs
    },
    data () {
      return {
        activeTab: 'returnGoodsBillList', // 当前激活标签  与name相同
        tabs: [{
          title: '退货单列表',
          name: 'returnGoodsBillList',
          component: returnGoodsBillList,
          closable: false
        }]
      }
    }
  }
</script>
