<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import warehousReturnGoodsVendorList from './list'
export default {
  name: 'WarehousReturnGoodsVendor',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'warehousReturnGoodsVendorList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.warehousingAndReturnGoods'),
          name: 'warehousReturnGoodsVendorList',
          component: warehousReturnGoodsVendorList,
          closable: false
        }
      ]
    }
  },
  activated () {
    if (this.currentTab === 'warehousReturnGoodsVendorList') {
      this.dolayout()
    }
  }
}
</script>
