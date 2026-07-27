<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
    @tab-change="tabChange"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import reportStatisticsList from './list'
export default {
  name: 'ReportStatistics',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'reportStatisticsList', // 当前激活标签  与name相同
      tabs: [
        {
          title: '报表汇总列表',
          name: 'reportStatisticsList',
          component: reportStatisticsList,
          closable: false
        }
      ],
      currentTab: null
    }
  },
  activated () {
    console.log('[activated]')
    if (this.currentTab === 'reportStatisticsList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'reportStatisticsList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'reportStatisticsList') {
        this.dolayout()
      }
      console.log('[tab]', tab)
      this.currentTab = tab
    }
  }
}
</script>
