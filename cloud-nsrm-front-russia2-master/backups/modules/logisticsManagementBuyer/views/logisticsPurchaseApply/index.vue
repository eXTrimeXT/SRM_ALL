<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import logisticsPurchaseApplyList from './logisticsPurchaseApplyList'
export default {
  name: 'LogisticsPurchaseApply',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'logisticsPurchaseApplyList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('logisticsMod.logisticsPurchaseApply'), /// 物流采购申请
          name: 'logisticsPurchaseApplyList',
          component: logisticsPurchaseApplyList,
          closable: false
        }
      ]
    }
  }
}
</script>
