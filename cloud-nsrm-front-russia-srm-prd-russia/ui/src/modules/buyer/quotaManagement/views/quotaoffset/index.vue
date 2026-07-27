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
import quotaoffsetList from './list'
export default {
  name: 'Quotaoffset',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'quotaoffsetList', // 当前激活标签  与name相同
      tabs: [
        {
          title: '配额执行偏差报表', // 页面名称
          name: 'quotaoffsetList',
          component: quotaoffsetList,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (this.activeTab === 'quotaoffsetList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'quotaoffsetList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'quotaoffsetList') {
        this.dolayout()
      }
      this.activeTab = tab
    }
  }
}
</script>
