<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import CentralizedAgreeList from './list'

export default {
  name: 'CentralizedAgree',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'centralizedAgreeList',
      tabs: [
        {
          title: this.$t('cusEntry.inq.centralizedAgreeExportHeader'), // '集采协议',
          name: 'centralizedAgreeList',
          component: CentralizedAgreeList,
          closable: false
        }
      ]
    }
  }
}
</script>
