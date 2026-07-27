<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import ContractAgreeList from './list'

export default {
  name: 'ContractAgree',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'contractAgreeList',
      tabs: [
        {
          // title: '标前交流反馈',
          title: () => this.$t('cusEntry.supplement20250205.preBidCommunicationFeedback'),
          name: 'contractAgreeList',
          component: ContractAgreeList,
          closable: false
        }
      ]
    }
  }
}
</script>
