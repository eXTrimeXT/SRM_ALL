<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
  import NavTabs from 'lib@/components/NavTabs'
  import purchaseMaterialMaintainList from './purchaseMaterialMaintainList'
  export default {
    name: 'PurchaseMaterialMaintain',
    components: {
      NavTabs
    },
    data () {
      return {
        activeTab: 'purchaseMaterialMaintainList', // 当前激活标签  与name相同
        tabs: [{
          title: this.$t('dataConfMod.materialMaintenance'),
          name: 'purchaseMaterialMaintainList',
          component: purchaseMaterialMaintainList,
          closable: false
        }]
      }
    }
  }
</script>
