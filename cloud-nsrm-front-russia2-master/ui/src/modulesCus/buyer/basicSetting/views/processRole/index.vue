<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import prcList from './prcList'
export default {
  name: 'ProcessRole',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'prcList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('cusEntry.dataConfMod.processRole'), // '流程角色'
        name: 'prcList',
        component: prcList,
        closable: false
      }]
    }
  },
  mounted () {
  }
}
</script>
