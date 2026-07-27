<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import InviteVendorList from './list'

export default {
  name: 'InviteVendor',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'inviteVendorList',
      tabs: [
        {
          title: '邀请供应商',
          name: 'inviteVendorList',
          component: InviteVendorList,
          closable: false
        }
      ]
    }
  }
}
</script>
