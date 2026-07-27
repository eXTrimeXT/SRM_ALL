<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import contractList from './list'
export default {
  name: 'ContractMaintainList',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'contractList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.contractMaintainList'),
          name: 'contractList',
          component: contractList,
          closable: false
        }
      ]
    }
  }
}
</script>
