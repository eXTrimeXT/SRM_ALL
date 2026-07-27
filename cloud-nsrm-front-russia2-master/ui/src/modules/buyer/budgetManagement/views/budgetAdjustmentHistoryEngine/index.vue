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
import budgetAdjustmentHistory from './list-engine'
export default {
  name: 'Budgetmanagement',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'budgetAdjustmentHistory',
      tabs: [
        {
          title: this.$t('route.budgetAdjustmentHistory'), // 预算管理
          name: 'budgetAdjustmentHistory',
          component: budgetAdjustmentHistory,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (this.activeTab === 'budgetAdjustmentHistory') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'budgetAdjustmentHistory', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'budgetAdjustmentHistory') {
        this.dolayout()
      }
      console.log('[tab]', tab)
      this.activeTab = tab
    }
  }
}
</script>
