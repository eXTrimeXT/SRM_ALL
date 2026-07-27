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
import interfacelogList from './list'
export default {
  name: 'Interfacelog',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'interfacelogList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('vendor.interfaceLogList'), // 接口日志列表
          name: 'interfacelogList',
          component: interfacelogList,
          closable: false
        }
      ],
      currentTab: null
    }
  },
  activated () {
    console.log('[activated]')
    if (this.currentTab === 'interfacelogList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'interfacelogList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'interfacelogList') {
        this.dolayout()
      }
      this.currentTab = tab
    }
  }
}
</script>
