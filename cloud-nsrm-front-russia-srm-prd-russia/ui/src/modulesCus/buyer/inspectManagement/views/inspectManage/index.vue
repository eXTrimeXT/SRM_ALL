<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import inspectManageList from './list'
export default {
  name: 'InspectManage',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'inspectManageList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('cusEntry.route.inspectManage'), // 考察管理列表
          name: 'inspectManageList',
          component: inspectManageList,
          closable: false
        }
      ]
    }
  }
}
</script>
