<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import vendorDeliveryOrderList from './vendorDeliveryOrderList'
export default {
  name: 'BuyerDeliveryOrder',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'buyerDeliveryOrderList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('orderMod.deliveryNoteList'),
          name: 'buyerDeliveryOrderList',
          component: vendorDeliveryOrderList,
          closable: false
        }
      ]
    }
  },
  activated () {
    if (this.currentTab === 'buyerDeliveryOrderList') {
      this.dolayout()
    }
  },
  methods: {
    tabChange (tab) {
      if (tab === 'buyerDeliveryOrderList') {
        this.dolayout()
      }
      this.currentTab = tab
    },
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'buyerDeliveryOrderList', // 组件名
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
