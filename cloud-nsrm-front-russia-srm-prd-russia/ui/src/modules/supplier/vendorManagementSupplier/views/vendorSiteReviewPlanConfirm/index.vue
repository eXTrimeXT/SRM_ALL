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
import vendorSiteReviewPlanConfirmEngine from './list'
export default {
  name: 'VendorSiteReviewPlanConfirm',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'vendorSiteReviewPlanConfirmEngine', // 当前激活标签  与name相同
      tabs: [
        {
          title: '供应商计划落实', // 页面名称
          name: 'vendorSiteReviewPlanConfirmEngine',
          component: vendorSiteReviewPlanConfirmEngine,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (this.activeTab === 'vendorSiteReviewPlanConfirmEngine') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'vendorSiteReviewPlanConfirmEngine', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        console.log('[SET_NAV_TABS_TODO]', data)
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'vendorSiteReviewPlanConfirmEngine') {
        this.dolayout()
      }
      console.log('[tab]', tab)
      this.activeTab = tab
    }
  }
}
</script>
