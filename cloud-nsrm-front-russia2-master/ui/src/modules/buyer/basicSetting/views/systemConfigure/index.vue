<template>
  <nav-tabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" @tab-change="tabChange" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import systemconfigureList from './list'
export default {
  name: 'Systemconfigure',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'systemconfigureList', // 当前激活标签  与name相同
      tabs: [
        {
          title: () => this.$t('dataConfMod.systemconfigureList'), // 页面名称
          name: 'systemconfigureList',
          component: systemconfigureList,
          closable: false
        }
      ]
    }
  },
  activated () {
    if (this.activeTab === 'systemconfigureList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'systemconfigureList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'systemconfigureList') {
        this.dolayout()
      }
      this.activeTab = tab
    }
  }
}
</script>
