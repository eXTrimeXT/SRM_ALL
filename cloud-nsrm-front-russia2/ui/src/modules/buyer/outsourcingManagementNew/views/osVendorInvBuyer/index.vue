<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import OsVendorInvBuyerList from './list'
export default {
  name: 'OsVendorInvBuyer',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'OsVendorInvBuyerList',
      tabs: [{
        title: this.$t('route.osVendorInvBuyer'),  // '供方委外库存盘点'
        name: 'OsVendorInvBuyerList',
        component: OsVendorInvBuyerList,
        closable: false
      }]
    }
  }
}
</script>
