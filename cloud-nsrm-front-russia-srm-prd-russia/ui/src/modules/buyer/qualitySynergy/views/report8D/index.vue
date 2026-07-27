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
import report8DList from './report8DList.vue'
export default {
  name: 'Report8D',
  components: {
    NavTabs
  },
  data () {
    return {
      currentTab: null,
      activeTab: 'report8DList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.report8D'), // 8D报告
          name: 'report8DList',
          component: report8DList,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (this.$route.params.autoQuery && this.currentTab === 'report8DList') {
      this.$nextTick(() => {
        const data = {
          name: 'report8DList', // 组件名
          // methods: "getQuerydata", // 方法名
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
