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
import quotadetailList from './list'
export default {
  name: 'Quotadetail',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'quotadetailList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('quota.quotaList'), // 页面名称
          name: 'quotadetailList',
          component: quotadetailList,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (this.activeTab === 'quotadetailList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'quotadetailList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'quotadetailList') {
        this.dolayout()
      }
      this.activeTab = tab
    }
  }
}
</script>
