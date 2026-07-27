<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import contractList from './list'
import contractListEngine from './list-engine'
import EditContractListEngine from './edit-engine'
export default {
  name: 'ContractMaintainList',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'contractListEngine', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.contractMaintainList'),
          name: 'contractListEngine',
          component: contractListEngine,
          closable: false
        }
        // {
        //   title: this.$t('route.contractMaintainList'),
        //   name: 'contractList',
        //   component: contractList,
        //   closable: false
        // },
        // {
        //   title: this.$t('route.contractMaintainList') + '-engine',
        //   name: 'EditContractListEngine',
        //   component: EditContractListEngine
        // }
      ]
    }
  }
}
</script>
