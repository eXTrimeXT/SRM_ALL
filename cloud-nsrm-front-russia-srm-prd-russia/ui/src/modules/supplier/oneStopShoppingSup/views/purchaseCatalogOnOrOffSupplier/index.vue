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
  name: 'PurchaseCatalogOnOrOffSupplier',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'purchaseCatalogOnOrOffList', // 当前激活标签  与name相同
      tabs: [
        {
          title: '采购目录上下架',
          name: 'purchaseCatalogOnOrOffList',
          component: purchaseCatalogOnOrOffList,
          closable: false
        }
      ]
    }
  }
}
</script>
