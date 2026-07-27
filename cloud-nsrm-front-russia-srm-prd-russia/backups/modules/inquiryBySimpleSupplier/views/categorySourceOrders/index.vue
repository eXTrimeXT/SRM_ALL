<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
  import NavTabs from 'lib@/components/NavTabs'
  import categorySourceOrdersList from './categorySourceOrdersList'
  export default {
    name: 'CategorySourceOrders',
    components: {
      NavTabs
    },
    data () {
      return {
        activeTab: 'categorySourceOrdersList', // 当前激活标签  与name相同
        tabs: [{
          title: '品类寻源需求',
          name: 'categorySourceOrdersList',
          component: categorySourceOrdersList,
          closable: false
        }]
      }
    }
  }
</script>
