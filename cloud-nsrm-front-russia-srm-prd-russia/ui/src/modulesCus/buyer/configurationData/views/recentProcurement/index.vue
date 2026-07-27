<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import recentProcurement from './indexList'
export default {
  name: 'RecentProcurement',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'List', // 当前激活标签  与name相同
      tabs: [
        {
          title: '近期采购类参数配置',
          name: 'List',
          component: recentProcurement,
          closable: false
        }
      ]
    }
  }
}
</script>
