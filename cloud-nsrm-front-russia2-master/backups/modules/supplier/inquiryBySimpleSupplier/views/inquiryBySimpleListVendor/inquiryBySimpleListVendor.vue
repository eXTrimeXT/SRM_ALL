<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import inquiryOrdersList from './inquiryBySimpleListVendor/inquiryOrdersList'

export default {
  name: 'InquiryOrders',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'inquiryOrdersList', // 当前激活标签  与name相同
      tabs: [
        {
          title: '询价单列表',
          name: 'inquiryOrdersList',
          component: inquiryOrdersList,
          closable: false
        }
      ]
    }
  }
}
</script>
