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
  import hierarchicalReviewList from './hierarchicalReviewList'
  export default {
    name: 'HierarchicalReview',
    components: {
      NavTabs
    },
    data () {
      return {
        activeTab: 'hierarchicalReviewList', // 当前激活标签  与name相同
        tabs: [{
          title: this.$t('route.hierarchicalReview'), // '分级评审'
          name: 'hierarchicalReviewList',
          component: hierarchicalReviewList,
          closable: false
        }]
      }
    },
    activated () {
      console.log('[activated]')
      if (this.activeTab === 'hierarchicalReviewList') {
        this.dolayout()
      }
    },
    methods: {
      dolayout () {
        this.$nextTick(() => {
          const data = {
            name: 'hierarchicalReviewList', // 组件名
            methods: 'dolayout', // 方法名
            params: null, // 参数
            random: Math.random()
          }
          this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
        })
      },
      tabChange (tab) {
        if (tab === 'hierarchicalReviewList') {
          this.dolayout()
        }
        this.activeTab = tab
      }
    }
  }
</script>
