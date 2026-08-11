<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import purchaseOrderChangeList from './purchaseOrderChangeList'
export default {
  name: 'PurchaseOrderChange',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'purchaseOrderChangeList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('orderMod.purchaseOrderChangeList'),
          name: 'purchaseOrderChangeList',
          component: purchaseOrderChangeList,
          closable: false
        }
      ]
    }
  }
}
</script>
