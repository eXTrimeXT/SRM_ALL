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
import interfaceconfigList from './list'
export default {
  name: 'Interfaceconfig',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'interfaceconfigList', // 当前激活标签  与name相同
      tabs: [
        {
          title: '接口配置列表',
          name: 'interfaceconfigList',
          component: interfaceconfigList,
          closable: false
        }
      ],
      currentTab: null
    }
  },
  activated () {
    console.log('[activated]')
    if (this.currentTab === 'interfaceconfigList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'interfaceconfigList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'interfaceconfigList') {
        this.dolayout()
      }
      this.currentTab = tab
    }
  }
}
</script>
