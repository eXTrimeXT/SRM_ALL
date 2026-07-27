<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import reportSetting from './reportSetting'
export default {
  name: 'ReportSetting',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'reportSetting', // 当前激活标签  与name相同
      tabs: [
        {
          title: () => this.$t('route.reportSetting'), // 报表配置
          name: 'reportSetting',
          component: reportSetting,
          ctrlHeight: true,
          closable: false
        }
      ]
    }
  }
}
</script>
