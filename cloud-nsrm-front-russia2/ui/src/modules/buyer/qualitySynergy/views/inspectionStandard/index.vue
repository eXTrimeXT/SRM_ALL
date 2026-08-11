<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" @tab-change="tabChange" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import inspectionStandardList from './list.vue'
export default {
  name: 'InspectionStandard',
  components: {
    NavTabs
  },
  data () {
    return {
      currentTab: null,
      activeTab: 'inspectionStandardList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.inspectionStandard'), // 检验标准
          name: 'inspectionStandardList',
          component: inspectionStandardList,
          closable: false
        }
      ]
    }
  },
  activated () {
    if (this.currentTab === 'inspectionStandardList') {
      this.dolayout()
    }
  },
  methods: {
    tabChange (tab) {
      if (tab === 'inspectionStandardList') {
        this.dolayout()
      }
      this.currentTab = tab
    },
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'inspectionStandardList', // 组件名
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
