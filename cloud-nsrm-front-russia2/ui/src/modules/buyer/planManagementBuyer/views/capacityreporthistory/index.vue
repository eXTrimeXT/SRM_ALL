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
import capacityreporthistoryList from './list'
export default {
  name: 'Capacityreporthistory',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'capacityreporthistoryList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('supplierCapacityReport.supplierCapacityHistory'), // 页面名称
          name: 'capacityreporthistoryList',
          component: capacityreporthistoryList,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (this.activeTab === 'capacityreporthistoryList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'capacityreporthistoryList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === ' capacityreporthistoryList') {
        this.dolayout()
      }
      console.log('[tab]', tab)
      this.activeTab = tab
    }
  }
}
</script>
