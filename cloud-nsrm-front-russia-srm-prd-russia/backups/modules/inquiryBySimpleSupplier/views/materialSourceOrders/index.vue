<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
  import NavTabs from 'lib@/components/NavTabs'
  import materialSourceOrdersList from './materialSourceOrdersList'
  export default {
    name: 'MaterialSourceOrders',
    components: {
      NavTabs
    },
    data () {
      return {
        activeTab: 'materialSourceOrdersList', // 当前激活标签  与name相同
        tabs: [{
          title: '物料寻源需求',
          name: 'materialSourceOrdersList',
          component: materialSourceOrdersList,
          closable: false
        }]
      }
    }
  }
</script>
