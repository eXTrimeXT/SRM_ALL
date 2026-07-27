<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import ipAddressList from './list'
export default {
  name: 'IpAddress',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'ipAddressList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('cusEntry.route.ipAddress'), // IP地址
          name: 'ipAddressList',
          component: ipAddressList,
          closable: false
        }
      ]
    }
  }
}
</script>
