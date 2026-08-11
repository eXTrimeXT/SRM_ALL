<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import purchaseOrderChangeList from './purchaseOrderChangeList'
export default {
  name: 'PurchaseOrderChange',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'purchaseOrderChangeList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('orderMod.purchaseOrderChangeList'),
          name: 'purchaseOrderChangeList',
          component: purchaseOrderChangeList,
          closable: false
        }
      ]
    }
  },
  activated () {
    if (this.currentTab === 'purchaseOrderChangeList') {
      this.dolayout()
    }
  },
  methods: {
    tabChange (tab) {
      if (tab === 'purchaseOrderChangeList') {
        this.dolayout()
      }
      this.currentTab = tab
    },
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'purchaseOrderChangeList', // 组件名
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
