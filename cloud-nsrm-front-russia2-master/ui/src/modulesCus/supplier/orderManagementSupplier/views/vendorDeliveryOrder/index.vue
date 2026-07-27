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
  name: 'VendorDeliveryOrder',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'vendorDeliveryOrderList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('orderMod.deliveryNoteList'),
          name: 'vendorDeliveryOrderList',
          component: vendorDeliveryOrderList,
          closable: false
        }
      ]
    }
  },
  activated () {
    if (this.currentTab === 'vendorDeliveryOrderList') {
      this.dolayout()
    }
  },
  methods: {
    tabChange (tab) {
      if (tab === 'vendorDeliveryOrderList') {
        this.dolayout()
      }
      this.currentTab = tab
    },
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'vendorDeliveryOrderList', // 组件名
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
