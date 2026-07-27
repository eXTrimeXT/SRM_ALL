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
import contractPerformanceBillList from './list'
export default {
  name: 'ContractPerformanceBill',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'contractPerformanceBillList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.contractPerformanceBill'), // 页面名称
          name: 'contractPerformanceBillList',
          component: contractPerformanceBillList,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (this.activeTab === 'contractPerformanceBillList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'contractPerformanceBillList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'contractPerformanceBillList') {
        this.dolayout()
      }
      this.activeTab = tab
    }
  }
}
</script>
