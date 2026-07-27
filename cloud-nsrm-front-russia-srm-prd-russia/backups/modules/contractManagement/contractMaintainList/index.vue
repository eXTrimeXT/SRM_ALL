<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import contractMaintainList from './contractMaintainList'
export default {
  name: 'ContractTemplate',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'contractMaintainList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.contractMaintainList'),
          name: 'contractMaintainList',
          component: contractMaintainList,
          closable: false
        }
      ]
    }
  }
}
</script>
