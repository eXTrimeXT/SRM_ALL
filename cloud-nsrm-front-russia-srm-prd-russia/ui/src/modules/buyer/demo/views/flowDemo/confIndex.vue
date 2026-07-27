<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import flowList from './listConf'
export default {
  name: 'FlowDemo',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'blackList', // 当前激活标签  与name相同
      tabs: [
        {
          title: '列表页条件配置', // 页面名称
          name: 'blackList',
          component: flowList,
          closable: false
        }
      ]
    }
  },
  activated () {
    if (this.activeTab === 'blackList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'blackList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'blackList') {
        this.dolayout()
      }
      console.log('[tab]', tab)
      this.activeTab = tab
    }
  }
}
</script>
