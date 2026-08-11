<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import contractDemoList from './list'
export default {
  name: 'ContractDemo',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'contractDemoList', // 当前激活标签  与name相同
      tabs: [
        {
          title: '合同管理demo',
          name: 'contractDemoList',
          component: contractDemoList,
          closable: false
        }
      ]
    }
  }
}
</script>
