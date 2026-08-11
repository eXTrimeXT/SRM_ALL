<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import bargainVendorList from './bargainVendorList'

export default {
  name: 'InquiryByProjectListVendor',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'bargainVendorList',
      tabs: [
        {
          title: this.$t('route.vendorBiddingList1'),
          name: 'bargainVendorList',
          component: bargainVendorList,
          closable: false
        }
      ]
    }
  }
}
</script>
