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
import sourcingApplicationList from './sourcingApplicationList'

export default {
  name: 'SourcingApplicationBuyer',
  components: {
    NavTabs
  },
  data () {
    return {
      currentTab: null,
      activeTab: 'sourcingApplicationList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('sourcingBuyer.sourcingApplicationList'), // 寻源需求列表
          name: 'sourcingApplicationList',
          component: sourcingApplicationList,
          closable: false
        }
      ]
    }
  },
  activated () {
    if (this.currentTab === 'sourcingApplicationList') {
      this.dolayout()
    }
  },
  methods: {
    tabChange (tab) {
      if (tab === 'sourcingApplicationList') {
        this.dolayout()
      }
      this.currentTab = tab
    },
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'sourcingApplicationList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    }
  }
}
</script>
