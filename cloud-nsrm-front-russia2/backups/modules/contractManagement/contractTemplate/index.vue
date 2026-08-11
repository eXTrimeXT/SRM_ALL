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
import contractTemplateList from './contractTemplateList'
export default {
  name: 'ContractTemplate',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'contractTemplateList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('contractMod.contractTemplateList'),
          name: 'contractTemplateList',
          component: contractTemplateList,
          closable: false
        }
      ],
      currentTab: null
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'contractTemplateList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'contractTemplateList') {
        this.dolayout()
      }
      this.currentTab = tab
    }
  }
}
</script>
