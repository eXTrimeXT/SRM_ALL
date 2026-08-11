<template>
  <nav-tabs
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import quickSearchConfigList from './quickSearchConfigList'
export default {
  name: 'QuickSearchConfig',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'quickSearchConfigList', // 当前激活标签  与name相同
      tabs: [{
        title: this.$t('bidMod.quickSearchConfigList'),
        name: 'quickSearchConfigList',
        component: quickSearchConfigList,
        closable: false
      }]
    }
  }
}
</script>
