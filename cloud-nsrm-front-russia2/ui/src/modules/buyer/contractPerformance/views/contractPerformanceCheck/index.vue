<template>
  <nav-tabs
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
import edits from './edit'
import { bus } from '@/library/components/render-engine/components/bus'

const activeTab = 'contractPerformanceCheckList'

export default {
  name: 'ContractPerformanceCheck',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab,
      tabs: [
        {
          title: this.$t('contractMod.contractAcceptance'),  // '合同验收'
          name: activeTab,
          component: ContractPerformanceCheckList,
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
