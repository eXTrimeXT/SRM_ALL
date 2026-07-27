<!--
 * @Author: linyk7 && linyk7@meicloud.com
 * @Date: 2023-03-03 08:59:28
 * @LastEditors: linyk7 && linyk7@meicloud.com
 * @LastEditTime: 2023-03-03 11:32:58
 * @FilePath: \ui\src\modules\supplier\barcodeNewSupplier\views\barcodeRelation\index.vue
 * @Description:
 * Copyright (c) 2023 by ${git_name_email}, All Rights Reserved.
-->
<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
    @tab-change="tabChange"
    @tab-remove="tabRemove"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import goodsEngine from './goods-engine'
import matterEngine from './matter-engine'
export default {
  name: 'BarcodeRelation',
  components: {
    NavTabs
  },
  data () {
    return {
      historyTabName: 'goodsEngine',
      activeTab: 'goodsEngine', // 当前激活标签  与name相同
      tabs: [
        {
          title: '按送货单',
          name: 'goodsEngine',
          component: goodsEngine,
          closable: false
        },
        {
          title: '按物料',
          name: 'matterEngine',
          component: matterEngine,
          closable: false
        }
      ]
    }
  },
  watch: {
    activeTab (_newVal, oldVal) {
      if (['goodsEngine', 'matterEngine'].includes(oldVal)) {
        this.historyTabName = oldVal
      }
    }
  },
  methods: {
    tabChange (tab) {
      this.activeTab = tab
      this.tabs = this.$refs.tabs.tabs
    },
    tabRemove ({ activeTab }) {
      if (!['goodsEngine', 'matterEngine'].includes(activeTab)) return
      this.$refs.tabs.activeTab = this.historyTabName
    }
  }
}
</script>
