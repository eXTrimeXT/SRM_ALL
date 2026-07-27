<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import contractPaymentTypeList from './list'

export default {
  name: 'ContractPaymentTypeList',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'contractPaymentTypeList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.contractPaymentType'),
          name: 'contractPaymentTypeList',
          component: contractPaymentTypeList,
          closable: false
        }
      ]
    }
  }
}
</script>
