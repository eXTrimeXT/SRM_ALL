<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import sourcingApplicationDetail from './sourcingApplicationDetail'

export default {
  name: 'SourcingApplicationDetailBuyer',
  components: {
    NavTabs
  },
  data () {
    return {
      currentTab: null,
      activeTab: 'sourcingApplicationDetail', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.sourcingApplicationDetail'), // 寻源需求列表
          name: 'sourcingApplicationDetail',
          component: sourcingApplicationDetail
        }
      ]
    }
  }
}
</script>
