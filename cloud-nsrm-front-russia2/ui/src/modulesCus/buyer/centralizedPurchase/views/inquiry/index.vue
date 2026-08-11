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
import InquiryList from './inquiry-list'

export default {
  name: 'Inquiry',

  components: { NavTabs },

  mixins: [tabTodoMixin],

  data () {
    return {
      activeTab: 'InquiryList',
      tabs: [
        {
          title: this.$t('cusEntry.centralizedPurchase.inquiryList'),
          name: 'InquiryList',
          component: InquiryList,
          closable: false
        }
      ]
    }
  },

  methods: {
    /* tab切换 */
    tabChange (val) {
      this.activeTab = val
    }
  }
}
</script>
