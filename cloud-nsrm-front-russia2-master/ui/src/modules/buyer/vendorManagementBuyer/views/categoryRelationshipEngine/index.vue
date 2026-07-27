<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
  import NavTabs from 'lib@/components/NavTabs'
  import categoryRelationshipList from './list'
  export default {
    name: 'CategoryRelationship',
    components: {
      NavTabs
    },
    data () {
      return {
        activeTab: 'categoryRelationshipList', // 当前激活标签  与name相同
        tabs: [{
          title: () => this.$t('route.vendorOrgAndCatRel'), // 供应商品类关系
          name: 'categoryRelationshipList',
          component: categoryRelationshipList,
          closable: false
        }]
      }
    }
  }
</script>
