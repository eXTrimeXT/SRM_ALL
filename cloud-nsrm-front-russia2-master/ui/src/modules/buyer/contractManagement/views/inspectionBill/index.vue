<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import inspectionBillList from './inspectionBillList'
export default {
  name: 'InspectionBill',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'inspectionBillList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.inspectionBill'), // 验收单列表
          name: 'inspectionBillList',
          component: inspectionBillList,
          closable: false
        }
      ]
    }
  }
}
</script>
