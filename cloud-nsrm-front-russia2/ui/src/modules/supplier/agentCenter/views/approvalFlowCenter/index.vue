<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
    :isFixedFirstItem="false"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import processTaskList from './processTaskList'
export default {
  name: 'ApprovalFlowCenterVendor',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'processTaskList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('flowMod.flowCenter'), // '我的流程中心',
        name: 'processTaskList',
        component: processTaskList,
        closable: false
      }]
    }
  }
}
</script>
