<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import sourcingPublicityConfigList from './list'
export default {
  name: 'SourcingPublicityConfig',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'sourcingPublicityConfigList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('cusEntry.route.sourcingPublicityConfig'), // 寻源公示配置
          name: 'sourcingPublicityConfigList',
          component: sourcingPublicityConfigList,
          closable: false
        }
      ]
    }
  }
}
</script>
