<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
    @tab-remove="tabRemove"
    @tab-show="tabShow"
  />
</template>

<script>
import { tabTodoMixin } from '@/utils/mixins'
import NavTabs from 'lib@/components/NavTabs'
import vendorBiddingList from './vendorBiddingList'

export default {
  name: 'VendorBiddingList',

  components: {
    NavTabs
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      activeTab: 'VendorBiddingList',
      tabs: [
        {
          title: this.$t('route.vendorBiddingList'),
          name: 'VendorBiddingList',
          component: vendorBiddingList,
          closable: false
        }
      ]
    }
  },

  methods: {
    tabRemove ({ activeTab }) {
      if (activeTab === 'VendorBiddingList') {
        this.__setTabTodo('vendorBiddingList.getQueryData')
      }
    },

    tabShow (name) {
      if (name === 'VendorBiddingList') {
        this.__setTabTodo('vendorBiddingList.getQueryData')
      }
    }
  }
}
</script>
