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
import budgetmanagementList from './budgetManagementList'
export default {
  name: 'Budgetmanagement',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'budgetmanagementList',
      tabs: [
        {
          title: this.$t('route.budgetManagement'), // 预算管理
          name: 'budgetmanagementList',
          component: budgetmanagementList,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (this.activeTab === 'budgetmanagementList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'budgetmanagementList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'budgetmanagementList') {
        this.dolayout()
      }
      console.log('[tab]', tab)
      this.activeTab = tab
    }
  }
}
</script>
