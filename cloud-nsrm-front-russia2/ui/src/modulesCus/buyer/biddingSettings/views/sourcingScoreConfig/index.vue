<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import sourcingScoreConfigList from './list'
export default {
  name: 'SourcingScoreConfig',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'sourcingScoreConfigList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('cusEntry.route.sourcingScoreConfig'), // 评分配置
          name: 'sourcingScoreConfigList',
          component: sourcingScoreConfigList,
          closable: false
        }
      ]
    }
  }
}
</script>
