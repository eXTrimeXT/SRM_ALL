<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import comparisonTableList from './list'
export default {
  name: 'ComparisonTable',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'comparisonTableList', // 当前激活标签  与name相同
      tabs: [{
        title: this.$t('cusEntry.route.comparisonTable'),
        name: 'comparisonTableList',
        component: comparisonTableList,
        closable: false
      }]
    }
  }
}
</script>
