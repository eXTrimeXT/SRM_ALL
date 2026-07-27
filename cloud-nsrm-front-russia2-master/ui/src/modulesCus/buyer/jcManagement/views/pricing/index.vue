<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import JcPricingList from './list'
export default {
  name: 'JcPricing',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'List', // 当前激活标签  与name相同
      tabs: [
        {
          title: '定价单',
          name: 'List',
          component: JcPricingList,
          closable: false
        }
      ]
    }
  }
}
</script>
