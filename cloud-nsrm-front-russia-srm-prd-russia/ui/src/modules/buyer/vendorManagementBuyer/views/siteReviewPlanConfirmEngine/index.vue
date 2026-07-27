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
import sitereviewplanconfirmList_engine from './list-engine'
export default {
  name: 'Sitereviewplanconfirm',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'sitereviewplanconfirmList_engine', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('vendorMod.planConfirmManagement'), // 计划落实管理
          name: 'sitereviewplanconfirmList_engine',
          component: sitereviewplanconfirmList_engine,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (this.activeTab === 'sitereviewplanconfirmList_engine') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'sitereviewplanconfirmList_engine', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        console.log('[SET_NAV_TABS_TODO]', data)
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'sitereviewplanconfirmList_engine') {
        this.dolayout()
      }
      console.log('[tab]', tab)
      this.activeTab = tab
    }
  }
}
</script>
