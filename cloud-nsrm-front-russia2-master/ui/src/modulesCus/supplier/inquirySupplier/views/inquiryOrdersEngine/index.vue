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
          // title: '询价单列表',
          title: this.$t("cusEntry.supplement20250121.inquiryList"),
          name: 'inquiryOrdersList',
          component: inquiryOrdersList,
          closable: false
        }
      ]
    }
  }
}
</script>
