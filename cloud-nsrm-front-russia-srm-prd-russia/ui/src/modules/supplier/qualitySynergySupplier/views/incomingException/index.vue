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
import incomingExceptionList from './incomingExceptionList.vue'
export default {
  name: 'IncomingException',
  components: {
    NavTabs
  },
  data () {
    return {
      currentTab: null,
      activeTab: 'incomingExceptionList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('qualitySynergy.incomingExceptionList'), // 来料异常单列表
          name: 'incomingExceptionList',
          component: incomingExceptionList,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (
      this.$route.params.autoQuery &&
      this.currentTab === 'incomingExceptionList'
    ) {
      this.$nextTick(() => {
        const data = {
          name: 'incomingExceptionList', // 组件名
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
      console.log('[tab]', tab)
      this.currentTab = tab
    }
  }
}
</script>
