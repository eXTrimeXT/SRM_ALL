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
import quotaflowList from './list'
export default {
  name: 'Quotaflow',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'quotaflowList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('quota.quotaflowList'), // 页面名称
          name: 'quotaflowList',
          component: quotaflowList,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (this.activeTab === 'quotaflowList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'quotaflowList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'quotaflowList') {
        this.dolayout()
      }
      this.activeTab = tab
    }
  }
}
</script>
