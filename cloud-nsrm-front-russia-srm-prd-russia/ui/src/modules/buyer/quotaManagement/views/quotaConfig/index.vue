<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
  import NavTabs from 'lib@/components/NavTabs'
  import quotaConfigList from './quotaConfigList'
  export default {
    name: 'QuotaConfig',
    components: {
      NavTabs
    },
    data () {
      return {
        activeTab: 'quotaConfigList', // 当前激活标签  与name相同
        tabs: [{
          title: () => this.$t('quota.quotaSetting'), // 配额配置
          name: 'quotaConfigList',
          component: quotaConfigList,
          closable: false
        }]
      }
    }
  }
</script>
