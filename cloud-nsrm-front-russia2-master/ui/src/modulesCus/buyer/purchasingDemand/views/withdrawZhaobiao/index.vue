<!--
 * @Author: linyk7 && linyk7@meicloud.com
 * @Date: 2022-12-20 18:03:17
 * @LastEditors: linyk7 && linyk7@meicloud.com
 * @LastEditTime: 2022-12-28 15:00:17
 * @FilePath: \ui\src\modules\buyer\purchasingDemand\views\purchaseApplication\index.vue
 * @Description:
 * Copyright (c) 2022 by linyk7 linyk7@meicloud.com, All Rights Reserved.
-->
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
import purchaseApplicationList from './list'
export default {
  name: 'PurchaseApplication',
  components: {
    NavTabs
  },
  data () {
    return {
      currentTab: null,
      activeTab: 'purchaseApplicationList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('cusEntry.supplement20250121.demandCancel'), // 需求申请取消
          name: 'purchaseApplicationList',
          component: purchaseApplicationList,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (
      this.$route.params.autoQuery &&
      this.currentTab === 'purchaseApplicationList'
    ) {
      this.$nextTick(() => {
        const data = {
          name: 'purchaseApplicationList', // 组件名
          methods: 'getQuerydata', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    }
  },
  methods: {
    tabChange (tab) {
      console.log('[tab]', tab)
      this.currentTab = tab
    }
  }
}
</script>
