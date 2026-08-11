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
import inquiryList from './inquiryList.vue'

export default {
  name: 'InquiryManagement',

  components: { NavTabs },

  mixins: [tabTodoMixin],

  data () {
    return {
      activeTab: 'inquiryList',
      tabs: [
        {
          title: this.$t('inquiryBySimple.inquiryList'),
          name: 'inquiryList',
          component: inquiryList,
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
      if (this.activeTab && this.activeTab.indexOf('priceComparison-REQ') === 0) {
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
