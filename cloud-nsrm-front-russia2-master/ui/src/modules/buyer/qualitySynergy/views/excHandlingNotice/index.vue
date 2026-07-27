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
import excHandlingNoticeList from './excHandlingNoticeList.vue'
export default {
  name: 'ExcHandlingNotice',
  components: {
    NavTabs
  },
  data () {
    return {
      currentTab: null,
      activeTab: 'excHandlingNoticeList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.excHandlingNotice'), // 异常问题处理通知
          name: 'excHandlingNoticeList',
          component: excHandlingNoticeList,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (
      this.$route.params.autoQuery &&
      this.currentTab === 'excHandlingNoticeList'
    ) {
      this.$nextTick(() => {
        const data = {
          name: 'excHandlingNoticeList', // 组件名
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
