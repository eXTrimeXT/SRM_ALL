<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import performanceQueryList from './performanceQueryList'
export default {
  name: 'PerformanceQuery',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'performanceQueryList', // 当前激活标签  与name相同
      tabs: [{
        title: this.$t('route.performanceQuery'),
        name: 'performanceQueryList',
        component: performanceQueryList,
        closable: false
      }]
    }
  }
}
</script>
