<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import quotaModulationList from './quotaModulationList'
export default {
  name: 'QuotaModulation',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'quotaModulationList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('quota.quotaModulation'), // 配额调整
        name: 'quotaModulationList',
        component: quotaModulationList,
        closable: false
      }]
    }
  }
}
</script>
