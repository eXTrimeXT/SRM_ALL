<template>
  <nav-tabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" @tab-change="tabChange" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import dataPermissionOptionList from './list'

export default {
  name: 'DataPermissionOption',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'dataPermissionOptionList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.dataPermissionOptionList'), // 页面名称
          name: 'dataPermissionOptionList',
          component: dataPermissionOptionList,
          closable: false
        }
      ]
    }
  },
  activated () {
    if (this.activeTab === 'dataPermissionOptionList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'dataPermissionOptionList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'dataPermissionOptionList') {
        this.dolayout()
      }
      this.activeTab = tab
    }
  }
}
</script>
