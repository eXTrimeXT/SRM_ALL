<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import PriceAdjustApplyList from './list'

export default {
  name: 'PriceAdjustApply',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'priceAdjustApplyList',
      tabs: [
        {
          title: this.$t('cusEntry.supplement20250121.priceAdjustApply'), // 调价申请
          name: 'priceAdjustApplyList',
          component: PriceAdjustApplyList,
          closable: false
        }
      ]
    }
  }
}
</script>
