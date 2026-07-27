<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
  import NavTabs from 'lib@/components/NavTabs'
  import shoppingCartList from './shoppingCartList'
  export default {
    name: 'ShoppingCartList',
    components: {
      NavTabs
    },
    data () {
      return {
        activeTab: 'shoppingCartList', // 当前激活标签  与name相同
        tabs: [{
          title: this.$t('route.shoppingCart'),
          name: 'shoppingCartList',
          component: shoppingCartList,
          closable: false
        }]
      }
    }
  }
</script>
