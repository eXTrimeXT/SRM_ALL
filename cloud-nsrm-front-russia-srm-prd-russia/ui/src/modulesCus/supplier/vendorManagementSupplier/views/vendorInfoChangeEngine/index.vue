<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import vendorInfoChangeList from './list'
export default {
  name: 'VendorInfoChange',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'vendorInfoChangeList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('vendorMod.vendorInfoChange'), // '供应商信息变更',
        name: 'vendorInfoChangeList',
        component: vendorInfoChangeList,
        closable: false
      }]
    }
  }
}
</script>
