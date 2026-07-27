<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import ProtocolQueryList from './protocol-query-list'
export default {
  name: 'ProtocolQuery',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'ProtocolQueryList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('cusEntry.reportManagement.protocolQueryList'),
        name: 'ProtocolQueryList',
        component: ProtocolQueryList,
        closable: false
      }]
    }
  }
}
</script>
