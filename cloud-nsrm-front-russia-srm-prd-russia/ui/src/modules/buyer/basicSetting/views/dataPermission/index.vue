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
import datapermissionList from './list'
export default {
  name: 'Datapermission',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'datapermissionList',
      tabs: [
        {
          title: this.$t('route.dataPermission'),
          name: 'datapermissionList',
          component: datapermissionList,
          closable: false
        }
      ]
    }
  },
  activated () {
    if (this.activeTab === 'datapermissionList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'datapermissionList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'datapermissionList') {
        this.dolayout()
      }
      this.activeTab = tab
    }
  }
}
</script>
