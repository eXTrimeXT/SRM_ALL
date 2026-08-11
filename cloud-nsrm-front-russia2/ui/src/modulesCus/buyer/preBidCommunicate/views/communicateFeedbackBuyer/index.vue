<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import CommunicateFeedbackBuyerList from './list'

export default {
  name: 'CommunicateFeedbackBuyer',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'communicateFeedbackBuyerList',
      tabs: [
        {
          // title: '标前交流反馈',
          title: () => this.$t('cusEntry.supplement20250205.preBidCommunicationFeedback'),
          name: 'communicateFeedbackBuyerList',
          component: CommunicateFeedbackBuyerList,
          closable: false
        }
      ]
    }
  }
}
</script>
