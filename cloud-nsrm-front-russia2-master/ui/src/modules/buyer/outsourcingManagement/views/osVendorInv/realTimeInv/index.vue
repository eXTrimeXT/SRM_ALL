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
import realTimeInvList from './realTimeInvList.vue'
export default {
  name: 'RealTimeInv',
  components: {
    NavTabs
  },
  data () {
    return {
      currentTab: null,
      activeTab: 'realTimeInvList', // 当前激活标签  与name相同
      tabs: [
        {
          // '供方委外库存明细'
          title: this.$t('cusEntry.supplement20250211.supplierOutsourcedInventoryDetails'),
          name: 'realTimeInvList',
          component: realTimeInvList,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (this.$route.params.autoQuery && this.currentTab === 'realTimeInvList') {
      this.$nextTick(() => {
        const data = {
          name: 'realTimeInvList', // 组件名
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
