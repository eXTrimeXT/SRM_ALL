<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import returnedGoodsNoticeList from './returnedGoodsNoticeList'
export default {
  name: 'ReturnedGoodsNotice',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'returnedGoodsNoticeList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('orderMod.returnOrderList'),
          name: 'returnedGoodsNoticeList',
          component: returnedGoodsNoticeList,
          closable: false
        }
      ]
    }
  },
  activated () {
    if (this.currentTab === 'returnedGoodsNoticeList') {
      this.dolayout()
    }
  },
  methods: {
    tabChange (tab) {
      if (tab === 'returnedGoodsNoticeList') {
        this.dolayout()
      }
      this.currentTab = tab
    },
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'returnedGoodsNoticeList', // 组件名
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
