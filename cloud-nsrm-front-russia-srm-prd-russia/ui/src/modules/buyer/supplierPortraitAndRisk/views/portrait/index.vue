<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
  import NavTabs from 'lib@/components/NavTabs'
  import vendorProfileList from './vendorProfileList'
  export default {
    name: 'Portrait',
    components: {
      NavTabs
    },
    data () {
      return {
        activeTab: 'vendorProfileList', // 当前激活标签  与name相同
        tabs: [{
          title: () => this.$t('route.portrait'), // '供应商档案',
          name: 'vendorProfileList',
          component: vendorProfileList,
          closable: false
        }]
      }
    }
  }
</script>
