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
import processExceptionList from './processExceptionList.vue'
export default {
  name: 'ProcessException',
  components: {
    NavTabs
  },
  data () {
    return {
      currentTab: null,
      activeTab: 'processExceptionList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('qualitySynergy.processExceptionList'), // 制程异常处理单列表
          name: 'processExceptionList',
          component: processExceptionList,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (
      this.$route.params.autoQuery &&
      this.currentTab === 'processExceptionList'
    ) {
      this.$nextTick(() => {
        const data = {
          name: 'processExceptionList', // 组件名
          methods: 'getQuerydata', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    }
  },
  methods: {
    tabChange (tab) {
      this.currentTab = tab
    }
  }
}
</script>
