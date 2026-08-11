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
import vendorSelfHelpBillingList from './vendorSelfHelpBillingList'
export default {
  name: 'VendorSelfHelpBilling',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'vendorSelfHelpBillingList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('accountMod.vendorSelfHelpBilling'), // 供应商自助开票
          name: 'vendorSelfHelpBillingList',
          component: vendorSelfHelpBillingList,
          closable: false
        }
      ]
    }
  },
  activated () {
    if (
      this.$route.params.autoQuery &&
      this.currentTab === 'vendorSelfHelpBillingList'
    ) {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'vendorSelfHelpBillingList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'vendorSelfHelpBillingList') {
        this.dolayout()
      }
      this.currentTab = tab
    }
  }
}
</script>
