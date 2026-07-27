<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import monitorBizConfigList from './list'
export default {
  name: 'MonitorBizConfig',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'monitorBizConfigList', // 当前激活标签  与name相同
      tabs: [
        {
          title: () => this.$t('dataConfMod.monitorBizConfigList'), // 页面名称
          name: 'monitorBizConfigList',
          component: monitorBizConfigList,
          closable: false
        }
      ]
    }
  }
}
</script>
