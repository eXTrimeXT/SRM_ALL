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
import purchaseAnalysis from './purchaseAnalysis'
export default {
  name: 'PurchaseAnalysis',
  components: {
    NavTabs
  },
  data () {
    return {
      currentTab: null,
      activeTab: 'purchaseAnalysis', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.purchaseAnalysis'),
          name: 'purchaseAnalysis',
          component: purchaseAnalysis,
          closable: false
        }
      ]
    }
  },
  activated () {
    this.layout(this.currentTab)
  },
  methods: {
    tabChange (tab) {
      this.currentTab = tab
      console.log('[tab]', tab)
      this.layout(tab)
    },
    layout (tab) {
      if (/moreInfo_/g.test(tab)) {
        const data = {
          name: 'more-info', // 组件名
          methods: 'doLayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      }
      if (/moreInfoDetail_/g.test(tab)) {
        const data = {
          name: 'more-info-detail', // 组件名
          methods: 'doLayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      }
    }
  }
}
</script>
