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
import capacityreportList from './list'
export default {
  name: 'Capacityreport',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'capacityreportList', // 当前激活标签  与name相同
      tabs: [
        {
          title: '供方产能提报', // 页面名称
          name: 'capacityreportList',
          component: capacityreportList,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (this.activeTab === 'capacityreportList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'capacityreportList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === ' capacityreportList') {
        this.dolayout()
      }
      console.log('[tab]', tab)
      this.activeTab = tab
    }
  }
}
</script>
