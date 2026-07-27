<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import CommunicateNoticeBuyerList from './list'

export default {
  name: 'CommunicateNoticeBuyer',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'communicateNoticeBuyerList',
      tabs: [
        {
          title: '标前交流通知',
          name: 'communicateNoticeBuyerList',
          component: CommunicateNoticeBuyerList,
          closable: false
        }
      ]
    }
  }
}
</script>
