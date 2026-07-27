<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import CommunicateNoticeList from './list'

export default {
  name: 'CommunicateNotice',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'communicateNoticeList',
      tabs: [
        {
          // title: '标前通知查询',
          title: () => this.$t('cusEntry.supplement20250205.preBidNotificationInquiry'),
          name: 'communicateNoticeList',
          component: CommunicateNoticeList,
          closable: false
        }
      ]
    }
  }
}
</script>
