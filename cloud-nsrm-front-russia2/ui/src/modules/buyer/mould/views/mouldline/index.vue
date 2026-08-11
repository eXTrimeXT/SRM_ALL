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
import mouldlineList from './list'
export default {
  name: 'Mouldline',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'mouldlineList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('mould.mouldItemType'), // 页面名称
          name: 'mouldlineList',
          component: mouldlineList,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (this.activeTab === 'mouldlineList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'mouldlineList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === ' mouldlineList') {
        this.dolayout()
      }
      this.activeTab = tab
    }
  }
}
</script>
