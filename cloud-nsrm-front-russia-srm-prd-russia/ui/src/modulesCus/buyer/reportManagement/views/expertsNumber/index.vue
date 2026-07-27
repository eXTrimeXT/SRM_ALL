<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import ExpertsNumberList from './expert-number-list'
export default {
  name: 'SpecialBidding',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'ExpertsNumberList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('cusEntry.reportManagement.expertsNumberList'),
        name: 'ExpertsNumberList',
        component: ExpertsNumberList,
        closable: false
      }]
    }
  }
}
</script>
