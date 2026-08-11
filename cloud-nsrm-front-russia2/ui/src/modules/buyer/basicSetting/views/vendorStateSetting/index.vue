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
import companystateList from './list'
export default {
  name: 'Companystate',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'companystateList',
      tabs: [
        {
          title: () => this.$t('route.vendorStateSetting'),
          name: 'companystateList',
          component: companystateList,
          closable: false
        }
      ]
    }
  },
  activated () {
    if (this.activeTab === 'companystateList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'companystateList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'companystateList') {
        this.dolayout()
      }
      this.activeTab = tab
    }
  }
}
</script>
