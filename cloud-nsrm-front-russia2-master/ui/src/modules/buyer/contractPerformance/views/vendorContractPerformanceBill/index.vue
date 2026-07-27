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
import vendorContractPerformanceBillList from './list'
export default {
  name: 'VendorContractPerformanceBill',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'vendorContractPerformanceBillList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.contractPerformanceBill'), // 页面名称
          name: 'vendorContractPerformanceBillList',
          component: vendorContractPerformanceBillList,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (this.activeTab === 'vendorContractPerformanceBillList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'vendorContractPerformanceBillList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'vendorContractPerformanceBillList') {
        this.dolayout()
      }
      this.activeTab = tab
    }
  }
}
</script>
