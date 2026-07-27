<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import monitorbizlogList from './list'
export default {
  name: 'Monitorbizlog',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'monitorbizlogList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.monitorBizLog'), // 页面名称
          name: 'monitorbizlogList',
          component: monitorbizlogList,
          closable: false
        }
      ]
    }
  }
}
</script>
