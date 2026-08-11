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
import formPageList from './list'
export default {
  name: 'FormPage',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'formPageList',
      tabs: [
        {
          title: () => this.$t('route.formPageScene'),
          name: 'formPageList',
          component: formPageList,
          closable: false
        }
      ]
    }
  },
  activated () {
    if (this.activeTab === 'formPageList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'formPageList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'formPageList') {
        this.dolayout()
      }
      this.activeTab = tab
    }
  }
}
</script>
