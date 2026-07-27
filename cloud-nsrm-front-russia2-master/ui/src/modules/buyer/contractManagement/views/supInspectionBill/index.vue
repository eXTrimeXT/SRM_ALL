<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import supInspectionBillList from './supInspectionBillList'
export default {
  name: 'ContractTemplate',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'supInspectionBillList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.supInspectionBill'), // 供方验收协同
          name: 'supInspectionBillList',
          component: supInspectionBillList,
          closable: false
        }
      ]
    }
  }
}
</script>
