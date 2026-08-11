<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import funMaintenance from './list'
export default {
  name: 'FunctionMaintenance',
  components: {
    NavTabs
  },
  provide () {
    return { context: this }
  },
  data () {
    return {
      activeTab: 'functionMaintenanceList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.functionMaintenance'),
          name: 'functionMaintenanceList',
          component: funMaintenance,
          closable: false
        }
      ]
    }
  },
  activated () {
    if (this.activeTab === 'functionMaintenanceList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'functionMaintenanceList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'functionMaintenanceList') {
        this.dolayout()
      }
      this.activeTab = tab
    }
  }
}
</script>
