<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
  import NavTabs from 'lib@/components/NavTabs'
  import inspectionApplyBillList from './inspectionApplyBillList'
  export default {
    name: 'ContractTemplate',
    components: {
      NavTabs
    },
    data () {
      return {
        activeTab: 'inspectionApplyBillList', // 当前激活标签  与name相同
        tabs: [{
          title: '验收申请列表',
          name: 'inspectionApplyBillList',
          component: inspectionApplyBillList,
          closable: false
        }]
      }
    }
  }
</script>
