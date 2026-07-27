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
import inspectionItemList from './list'

export default {
  name: 'InspectionItem',
  components: {
    NavTabs
  },
  data () {
    return {
      currentTab: null,
      activeTab: 'inspectionItemList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('qualitySynergy.inspectionItemList'), // 校验项目列表
          name: 'inspectionItemList',
          component: inspectionItemList,
          closable: false
        }
      ]
    }
  },
  activated () {
    if (this.currentTab === 'inspectionItemList') {
      this.dolayout()
    }
  },
  methods: {
    tabChange (tab) {
      if (tab === 'inspectionItemList') {
        this.dolayout()
      }
      this.currentTab = tab
    },
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'inspectionItemList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    }
  }

}
</script>
