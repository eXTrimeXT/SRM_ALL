<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import annualMarginList from './list.vue'

export default {
  name: 'AnnualMargin',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'annualMarginList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('cusEntry.route.annualMargin'),
          name: 'annualMarginList',
          component: annualMarginList,
          closable: false
        }
      ]
    }
  }
}
</script>
