<template>
  <NavTabs
    :tabs-list="tabs"
    :cur-tab="activeTab"
    @tab-change="tabChange"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import workflowList from './workflowList'
export default {
  name: 'WorkflowSetting',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'workflowList', // 当前激活标签  与name相同
      tabs: [
        {
          title: () => this.$t('dataConfMod.workflowList'), // '流程模板列表'
          name: 'workflowList',
          component: workflowList,
          closable: false
        }
      ],
      params: {}
    }
  },
  activated () {
    if (this.activeTab === 'workflowList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'workflowList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'workflowList') {
        this.dolayout()
      }
      this.activeTab = tab
    }
  }
}
</script>
