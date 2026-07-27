<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import penaltyDeductionOrderList from './penaltyDeductionOrderList'
export default {
  name: 'ContractTemplate',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'penaltyDeductionOrderList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('accountMod.penaltyDeductionOrderList'), // 扣罚款列表
          name: 'penaltyDeductionOrderList',
          component: penaltyDeductionOrderList,
          closable: false
        }
      ]
    }
  }
}
</script>
