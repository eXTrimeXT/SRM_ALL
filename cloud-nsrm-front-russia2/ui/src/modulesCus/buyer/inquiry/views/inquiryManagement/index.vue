<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
    @tab-remove="tabRemove"
  />
</template>

<script>
import { tabTodoMixin } from '@/utils/mixins'
import NavTabs from 'lib@/components/NavTabs'
import inquiryList from './inquiryList'
import InquiryDetailList from './inquiry-detail-list'
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
        },
        {
          title: this.$t('cusEntry.inq.InquiryDetailList'),
          name: 'InquiryDetailList',
          component: InquiryDetailList,
          closable: false
        }
      ]
    }
  },
  methods: {
    /* 关闭页签回到询价管理页 */
    tabRemove () {
      this.$refs.tabs.activeTab = 'inquiryList'
    }
  }
}
</script>
