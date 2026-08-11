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
import reviewformstandardList from './list'
export default {
  name: 'Reviewformstandard',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'reviewformstandardList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.reviewformstandardList'), // 页面名称  '资质审查标准管理'
          name: 'reviewformstandardList',
          component: reviewformstandardList,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (this.activeTab === 'reviewformstandardList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'reviewformstandardList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        console.log('[SET_NAV_TABS_TODO]', data)
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'reviewformstandardList') {
        this.dolayout()
      }
      console.log('[tab]', tab)
      this.activeTab = tab
    }
  }
}
</script>
