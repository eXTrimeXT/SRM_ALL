<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import paymentPlanList from './paymentPlanList'
export default {
  name: 'PaymentPlan',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'paymentPlanList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('accountMod.paymentPlanList'), // 付款计划列表
          name: 'paymentPlanList',
          component: paymentPlanList,
          closable: false
        }
      ]
    }
  }
}
</script>
