<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
  import NavTabs from 'lib@/components/NavTabs'
  import priceLibraryList from './priceLibraryList'
  export default {
    name: 'PriceLibrary',
    components: {
      NavTabs
    },
    data () {
      return {
        activeTab: 'priceLibraryList', // 当前激活标签  与name相同
        tabs: [{
          title: this.$t('dataConfMod.materialMaintenance'),
          name: 'priceLibraryList',
          component: priceLibraryList,
          closable: false
        }]
      }
    }
  }
</script>
