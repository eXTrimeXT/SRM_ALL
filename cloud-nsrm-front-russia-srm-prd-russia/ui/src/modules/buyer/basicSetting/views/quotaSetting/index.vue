<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import quotaSettingList from './quotaSettingList'
export default {
  name: 'QuotaSetting',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'quotaSettingList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('dataConfMod.quotaSetting'), // '配额管理'
        name: 'quotaSettingList',
        component: quotaSettingList,
        closable: false
      }]
    }
  }
}
</script>
