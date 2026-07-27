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
import questManagementList from './questManagementList'
export default {
  name: 'QuestManagement',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'questManagementList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.questSupplierList'), // 页面名称
          name: 'questManagementList',
          component: questManagementList,
          closable: false
        }
      ]
    }
  },
  activated () {
    if (this.activeTab === 'questManagementList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'questManagementList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'questManagementList') {
        this.dolayout()
      }
      this.activeTab = tab
    }
  }
}
</script>
