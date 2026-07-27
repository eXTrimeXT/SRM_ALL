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
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import listEngine from './list-engine'
export default {
  name: 'PurchaseApplication',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'listEngine', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('purchaseDemand.purchaseApplication'),
          name: 'listEngine',
          component: listEngine,
          closable: false
        }
      ]
    }
  }
}
</script>
