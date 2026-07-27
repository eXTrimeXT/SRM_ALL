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
import vendorInvForVendorList from './vendorInvForVendorList.vue'
export default {
  name: 'VendorInvForVendor',
  components: {
    NavTabs
  },
  data () {
    return {
      currentTab: null,
      activeTab: 'vendorInvForVendorList', // 当前激活标签  与name相同
      tabs: [
        {
          title: '供方委外盘点协同',
          name: 'vendorInvForVendorList',
          component: vendorInvForVendorList,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (this.$route.params.autoQuery && this.currentTab === 'vendorInvForVendorList') {
      this.$nextTick(() => {
        const data = {
          name: 'vendorInvForVendorList', // 组件名
          // methods: "getQuerydata", // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    }
  },
  methods: {
    tabChange (tab) {
      console.log('[tab]', tab)
      this.currentTab = tab
    }
  }
}
</script>
