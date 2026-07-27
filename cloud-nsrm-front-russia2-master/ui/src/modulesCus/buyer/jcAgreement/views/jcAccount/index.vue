<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import JcAccountList from './list'

export default {
  name: 'JcAccount',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'jcAccountList',
      tabs: [
        {
          title: this.$t('cusEntry.supplement20250121.jcAccount'), // '集采台账'
          name: 'jcAccountList',
          component: JcAccountList,
          closable: false
        }
      ]
    }
  }
}
</script>
