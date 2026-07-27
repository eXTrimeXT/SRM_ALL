<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import ContractAgreeList from './list'

export default {
  name: 'ContractAgree',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'contractAgreeList',
      tabs: [
        {
          title: '合同协议',
          name: 'contractAgreeList',
          component: ContractAgreeList,
          closable: false
        }
      ]
    }
  }
}
</script>
