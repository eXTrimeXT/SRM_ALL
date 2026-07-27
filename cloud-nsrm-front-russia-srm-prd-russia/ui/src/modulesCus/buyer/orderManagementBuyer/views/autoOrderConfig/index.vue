<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import autoOrderConfigList from './list'
export default {
  name: 'AutoOrderConfig',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'autoOrderConfigList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('cusEntry.route.autoOrderConfig'), // 自动转订单配置规则
          name: 'autoOrderConfigList',
          component: autoOrderConfigList,
          closable: false
        }
      ]
    }
  }
}
</script>
