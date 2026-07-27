<template>
  <nav-tabs
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import purchaseCategoryMaintenance from './list'
export default {
  name: 'PurchaseCategoryMaintenance',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'purchaseCategoryMaintenance', // 当前激活标签  与name相同
      tabs: [{
        title: this.$t('route.purCategoryMaintenance'),
        name: 'purchaseCategoryMaintenance',
        component: purchaseCategoryMaintenance,
        closable: false
      }]
    }
  }
}
</script>