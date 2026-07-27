<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
    @tab-change="tabChange"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import sitereviewplanList from './list'
export default {
  name: 'PerformanceWarning',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'performanceWarningList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('cusEntry.supplement20250205.orderImprovementWarning'), // 订单改善预警
          name: 'performanceWarningList',
          component: sitereviewplanList,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (this.activeTab === 'performanceWarningList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'performanceWarningList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'performanceWarningList') {
        this.dolayout()
      }
      console.log('[tab]', tab)
      this.activeTab = tab
    }
  }
}
</script>
