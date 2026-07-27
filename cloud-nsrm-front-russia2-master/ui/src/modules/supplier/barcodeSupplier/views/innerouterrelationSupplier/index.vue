<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" @tab-change="tabChange" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import innerouterrelationList from './list'
export default {
  name: 'InnerOuterRelation',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'innerouterrelationList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('hierarchical.management'), // 内外箱关联管理
          name: 'innerouterrelationList',
          component: innerouterrelationList,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (this.activeTab === 'innerouterrelationList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'innerouterrelationList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        console.log('[SET_NAV_TABS_TODO]', data)
        this.$store.commit('SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === ' innerouterrelationList') {
        this.dolayout()
      }
      console.log('[tab]', tab)
      this.activeTab = tab
    }
  }
}
</script>
