<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import OsVendorInvList from './list'
export default {
  name: 'OsVendorInv',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'OsVendorInvList',
      tabs: [{
        title: this.$t('outsource.countingCollaboration'), // 供方库存盘点协同
        name: 'OsVendorInvList',
        component: OsVendorInvList,
        closable: false
      }]
    }
  }
}
</script>
