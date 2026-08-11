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
import financialInforChangesList from './list'
export default {
  name: 'FinancialInforChanges',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'financialInforChangesList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.financialInforChanges'), // 财务信息变更
          name: 'financialInforChangesList',
          component: financialInforChangesList,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (this.activeTab === 'sitereviewplanList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'sitereviewplanList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        console.log('[SET_NAV_TABS_TODO]', data)
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'financialInforChangesList') {
        this.dolayout()
      }
      console.log('[tab]', tab)
      this.activeTab = tab
    }
  }
}
</script>
