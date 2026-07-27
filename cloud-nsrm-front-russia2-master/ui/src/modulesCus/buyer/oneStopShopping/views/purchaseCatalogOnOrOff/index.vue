<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import purchaseCatalogOnOrOffList from './purchaseCatalogOnOrOffList'
export default {
  name: 'PurchaseCatalogOnOrOff',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'purchaseCatalogOnOrOffList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.purchaseCatalogOnOrOff'),  // '采购目录上下架'
          name: 'purchaseCatalogOnOrOffList',
          component: purchaseCatalogOnOrOffList,
          closable: false
        }
      ]
    }
  }
}
</script>
