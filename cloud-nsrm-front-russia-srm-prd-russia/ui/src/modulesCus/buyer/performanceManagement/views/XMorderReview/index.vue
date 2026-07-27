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
import performanceModelList from './orderReviewList'
export default {
  name: 'XMorderReview',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'performanceModelList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('cusEntry.route.XMorderReview'),
          name: 'performanceModelList',
          component: performanceModelList,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (
      this.$route.params.autoQuery &&
      this.currentTab === 'performanceModelList'
    ) {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'performanceModelList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'performanceModelList') {
        this.dolayout()
      }
      console.log('[tab]', tab)
      this.currentTab = tab
    }
  }
}
</script>
