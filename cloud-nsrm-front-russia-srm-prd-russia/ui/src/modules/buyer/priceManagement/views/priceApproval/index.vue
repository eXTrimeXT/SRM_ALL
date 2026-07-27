<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import priceApprovalList from './priceApprovalList'

export default {
  name: 'PriceApproval',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'priceApprovalList',
      tabs: [
        {
          title: this.$t('bidMod.inquiryapproval'),
          name: 'priceApprovalList',
          component: priceApprovalList,
          closable: false
        }
      ]
    }
  }
}
</script>
