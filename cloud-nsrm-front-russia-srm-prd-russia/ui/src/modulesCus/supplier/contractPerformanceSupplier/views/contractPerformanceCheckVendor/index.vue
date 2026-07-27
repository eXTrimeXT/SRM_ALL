<!--
 * @Author: linyk7 && linyk7@meicloud.com
 * @Date: 2022-11-21 11:08:54
 * @LastEditors: linyk7 && linyk7@meicloud.com
 * @LastEditTime: 2022-11-25 14:57:13
 * @FilePath: \ui\src\modules\contractPerformanceSupplier\views\contractPerformanceCheckVendor\index.vue
 * @Description:
 * Copyright (c) 2022 by linyk7 linyk7@meicloud.com, All Rights Reserved.
-->
<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
    @first-tab-active="firstTabActive"
    @tab-remove="handleTabRemove"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import ContractPerformanceCheckList from './list'
import ContractPerformanceCheckListEngine from './list-engine'
import ContractPerformanceCheckEditEngine from './edit-engine'
import { bus } from '@/library/components/render-engine/components/bus'

const activeTab = 'ContractPerformanceCheckListEngine'

export default {
  name: 'ContractPerformanceCheckVendor',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: activeTab,
      tabs: [
        {
          title: '合同验收',
          name: activeTab,
          component: ContractPerformanceCheckListEngine,
          closable: false
        }
      ]
    }
  },

  methods: {
    firstTabActive () {
      this.$nextTick(() => {
        bus.$emit('PerAcceptance', { reCalcContainerHeight: true })
      })
    },

    handleTabRemove (data) {
      if (data.activeTab === activeTab) {
        this.firstTabActive()
      }
    }
  }
}
</script>
