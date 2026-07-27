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
import sitereviewmodelList from './list'
export default {
  name: 'Sitereviewmodel',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'sitereviewmodelList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('vendorMod.reviewTemplateManagement'), // 页面名称  '现场评审模板管理'
          name: 'sitereviewmodelList',
          component: sitereviewmodelList,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (this.activeTab === 'sitereviewmodelList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'sitereviewmodelList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        console.log('[SET_NAV_TABS_TODO]', data)
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'sitereviewmodelList') {
        this.dolayout()
      }
      console.log('[tab]', tab)
      this.activeTab = tab
    }
  }
}
</script>
