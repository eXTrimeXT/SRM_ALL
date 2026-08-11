<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import inquiryOrdersList from './inquiryOrdersList'

export default {
  name: 'InquiryOrders',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'inquiryOrdersList',
      tabs: [
        {
          title: this.$t('cusEntry.supplement20250121.inquiryList'), // 询价单列表
          name: 'inquiryOrdersList',
          component: inquiryOrdersList,
          closable: false
        }
      ]
    }
  }
}
</script>
