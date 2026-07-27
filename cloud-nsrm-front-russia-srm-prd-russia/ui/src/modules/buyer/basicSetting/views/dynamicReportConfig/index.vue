<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
    @tab-change="tabChange"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import dynamicReportConfigList from './list'
export default {
  name: 'DynamicReportConfig',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'dynamicReportConfigList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('dataConfMod.dynamicReportConfigList'),
          name: 'dynamicReportConfigList',
          component: dynamicReportConfigList,
          closable: false
        }
      ],
      currentTab: null
    }
  },
  activated () {
    if (this.currentTab === 'dynamicReportConfigList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'dynamicReportConfigList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'dynamicReportConfigList') {
        this.dolayout()
      }
      this.currentTab = tab
    }
  }
}
</script>
