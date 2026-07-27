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
import sitereviewplanList_engine from './list-engine'
export default {
  name: 'Sitereviewplan',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'sitereviewplanList_engine', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('vendorMod.siteReviewPlan'), // 现场评审计划管理
          name: 'sitereviewplanList_engine',
          component: sitereviewplanList_engine,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (this.activeTab === 'sitereviewplanList_engine') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'sitereviewplanList_engine', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        console.log('[SET_NAV_TABS_TODO]', data)
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'sitereviewplanList_engine') {
        this.dolayout()
      }
      console.log('[tab]', tab)
      this.activeTab = tab
    }
  }
}
</script>
