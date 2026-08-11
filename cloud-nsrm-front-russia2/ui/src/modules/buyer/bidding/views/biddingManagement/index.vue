<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
    @tab-change="tabChange"
  />
</template>

<script>
import { tabTodoMixin } from '@/utils/mixins'
import NavTabs from 'lib@/components/NavTabs'
import biddingList from './biddingList.vue'

export default {
  name: 'BiddingManagementLTS',

  components: { NavTabs },

  mixins: [tabTodoMixin],

  data () {
    return {
      activeTab: 'biddingList',
      tabs: [
        {
          title: this.$t('route.biddingManagement'),
          name: 'biddingList',
          component: biddingList,
          closable: false
        }
      ]
    }
  },

  activated () {
    this.tabChangeTodo()
  },

  methods: {
    /* tab切换以及激活时候 */
    tabChangeTodo () {
      // 用于比价tab页签，触发比价tab页签doLayout方法
      if (this.activeTab && this.activeTab.indexOf('priceComparison-BID') === 0) {
        this.__setTabTodo(`PriceComparison.doLayout.${this.activeTab.replace('priceComparison-', '')}`)
      }
    },

    /* tab切换 */
    tabChange (val) {
      this.activeTab = val
      this.tabChangeTodo()
    }
  }
}
</script>
