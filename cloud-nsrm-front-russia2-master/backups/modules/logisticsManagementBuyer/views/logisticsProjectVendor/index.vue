<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
    @tab-remove="tabRemove"
    @tab-show="tabShow"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import vendorBiddingList from './vendorBiddingList'
import { tabTodoMixin } from '@/utils/mixins'
export default {
  name: 'LogisticsProjectVendor',
  components: {
    NavTabs
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      activeTab: 'vendorBiddingList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.vendorBiddingList'), // 招标项目列表
          name: 'vendorBiddingList',
          component: vendorBiddingList,
          closable: false
        }
      ]
    }
  },
  methods: {
    tabRemove ({ name, activeTab, tabs }) {
      if (activeTab === 'vendorBiddingList') {
        this.__setTabTodo('vendorBiddingList.getQuerydata')
      }
    },
    tabShow (name) {
      if (name === 'vendorBiddingList') {
        this.__setTabTodo('vendorBiddingList.getQuerydata')
      }
    }
  }
}
</script>
