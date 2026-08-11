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
import osmaterialrequisitionList from './list'
export default {
  name: 'Osmaterialrequisition',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'osmaterialrequisitionList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.outsourceMaterials'), // 页面名称   '委外领料单'
          name: 'osmaterialrequisitionList',
          component: osmaterialrequisitionList,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (this.activeTab === 'osmaterialrequisitionList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'osmaterialrequisitionList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab == ' osmaterialrequisitionList') {
        this.dolayout()
      }
      console.log('[tab]', tab)
      this.activeTab = tab
    }
  }
}
</script>
