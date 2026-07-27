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
import vendorInvList from './vendorInvList.vue'
export default {
  name: 'VendorInv',
  components: {
    NavTabs
  },
  data () {
    return {
      currentTab: null,
      activeTab: 'vendorInvList', // 当前激活标签  与name相同
      tabs: [
        {
          // '供方委外库存盘点'
          title: this.$t('route.osVendorInvBuyer'),
          name: 'vendorInvList',
          component: vendorInvList,
          closable: false
        }
      ]
    }
  },
  activated () {
    if (this.$route.params.autoQuery && this.currentTab === 'vendorInvList') {
      this.$nextTick(() => {
        const data = {
          name: 'vendorInvList', // 组件名
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
      this.currentTab = tab
    }
  }
}
</script>
