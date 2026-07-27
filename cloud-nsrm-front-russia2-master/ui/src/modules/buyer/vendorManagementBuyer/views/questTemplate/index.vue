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
import questTemplateList from './questTemplateList'
export default {
  name: 'QuestTemplate',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'questTemplateList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('vendorMod.questTemplateList'), // 调查表模板管理
          name: 'questTemplateList',
          component: questTemplateList,
          closable: false
        }
      ]
    }
  },
  activated () {
    if (this.activeTab === 'questTemplateList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'questTemplateList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'questTemplateList') {
        this.dolayout()
      }
      this.activeTab = tab
    }
  }
}
</script>
