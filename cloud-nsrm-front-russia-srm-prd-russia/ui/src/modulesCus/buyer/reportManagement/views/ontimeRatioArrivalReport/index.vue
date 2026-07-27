<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
    @tab-change="tabChange"
    @tab-remove="tabRemove"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import OntimeRatioArrivalReportList from './list'
import OntimeRatioArrivalReportDetailList from './detailList'

export default {
  name: 'OntimeRatioArrivalReport',
  components: {
    NavTabs
  },
  data () {
    return {
      historyTabName: 'OntimeRatioArrivalReportList',
      activeTab: 'OntimeRatioArrivalReportList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('cusEntry.route.ontimeRatioArrivalReportList'),
          name: 'OntimeRatioArrivalReportList',
          component: OntimeRatioArrivalReportList,
          closable: false
        },
        {
          title: this.$t('cusEntry.route.ontimeRatioArrivalReportDetailList'),
          name: 'OntimeRatioArrivalReportDetailList',
          component: OntimeRatioArrivalReportDetailList,
          closable: false
        }
      ]
    }
  },
  watch: {
    activeTab (_newVal, oldVal) {
      if (['OntimeRatioArrivalReportList', 'OntimeRatioArrivalReportDetailList'].includes(oldVal)) {
        this.historyTabName = oldVal
      }
    }
  },
  methods: {
    tabChange (tab) {
      this.activeTab = tab
      this.tabs = this.$refs.tabs.tabs
    },
    tabRemove ({ activeTab }) {
      if (!['OntimeRatioArrivalReportList', 'OntimeRatioArrivalReportDetailList'].includes(activeTab)) return
      this.$refs.tabs.activeTab = this.historyTabName
    }
  }
}
</script>
