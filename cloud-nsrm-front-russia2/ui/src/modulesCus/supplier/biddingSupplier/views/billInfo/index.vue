<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import vendorBillInfo from './vendorBillInfo'
export default {
  name: 'BillInfo',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'vendorBillInfo', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('cusEntry.supplement20250205.vendorBillInfo'), // 开票信息维护
          name: 'vendorBillInfo',
          component: vendorBillInfo,
          closable: false
        }
      ]
    }
  }
}
</script>
