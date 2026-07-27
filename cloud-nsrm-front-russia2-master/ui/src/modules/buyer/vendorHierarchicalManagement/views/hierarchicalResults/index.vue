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
  import hierarchicalResultsList from './hierarchicalResultsList'
  export default {
    name: 'HierarchicalResults',
    components: {
      NavTabs
    },
    data () {
      return {
        activeTab: 'hierarchicalResultsList', // 当前激活标签  与name相同
        tabs: [{
          title: this.$t('route.gradingResults'), // '分级结果'
          name: 'hierarchicalResultsList',
          component: hierarchicalResultsList,
          closable: false
        }]
      }
    },
    activated () {
      console.log('[activated]')
      if (this.activeTab === 'hierarchicalResultsList') {
        this.dolayout()
      }
    },
    methods: {
      dolayout () {
        this.$nextTick(() => {
          const data = {
            name: 'hierarchicalResultsList', // 组件名
            methods: 'dolayout', // 方法名
            params: null, // 参数
            random: Math.random()
          }
          this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
        })
      },
      tabChange (tab) {
        if (tab === 'hierarchicalResultsList') {
          this.dolayout()
        }
        this.activeTab = tab
      }
    }
  }
</script>
