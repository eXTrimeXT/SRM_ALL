<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import quoteReportList from './list'

export default {
  name: 'QuoteReport',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'quoteReportList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('sourcingBuyer.quoteReport'), // 报价查询报表
          name: 'quoteReportList',
          component: quoteReportList,
          closable: false
        }
      ]
    }
  }
}
</script>
