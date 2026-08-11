<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import menuMaintenance from './list'
export default {
  name: 'MenuMaintenance',
  components: {
    NavTabs
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      activeTab: 'menuMaintenance', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.menuMaintenance'),
          name: 'menuMaintenance',
          component: menuMaintenance,
          closable: false
        }
      ]
    }
  },
  activated () {
    if (this.activeTab === 'roleMaintenance') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'roleMaintenance', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'roleMaintenance') {
        this.dolayout()
      }
      this.activeTab = tab
    }
  }
}
</script>
