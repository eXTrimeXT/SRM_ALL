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
import contractPerformanceProcessConfigList from './list'
export default {
  name: 'ContractPerformanceProcessConfig',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'contractPerformanceProcessConfigList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.contractPerformanceProcessConfig'), // 页面名称
          name: 'contractPerformanceProcessConfigList',
          component: contractPerformanceProcessConfigList,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (this.activeTab === 'contractPerformanceProcessConfigList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'contractPerformanceProcessConfigList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'contractPerformanceProcessConfigList') {
        this.dolayout()
      }
      this.activeTab = tab
    }
  }
}
</script>
