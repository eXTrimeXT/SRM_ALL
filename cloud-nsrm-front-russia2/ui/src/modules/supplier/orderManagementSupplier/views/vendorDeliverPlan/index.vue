<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import vendorDeliverPlanList from './vendorDeliverPlanList'
export default {
  name: 'VendorDeliverPlan',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'vendorDeliverPlanList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.arrivalPlanList'),
          name: 'vendorDeliverPlanList',
          component: vendorDeliverPlanList,
          closable: false
        }
      ]
    }
  }
}
</script>
