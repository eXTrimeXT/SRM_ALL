<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import modelConfig from './list'
export default {
  name: 'ModelConfig',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'modelConfig', // 当前激活标签  与name相同
      tabs: [
        {
          title: () => this.$t('route.modelConfig'), // 页面名称
          name: 'modelConfig',
          component: modelConfig,
          closable: false
        }
      ]
    }
  },
  activated () {
    if (this.activeTab === 'modelConfig') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'modelConfig', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'modelConfig') {
        this.dolayout()
      }
      this.activeTab = tab
    }
  }
}
</script>
