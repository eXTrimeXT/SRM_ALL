<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import questSupplierList from './questSupplierList'
export default {
  name: 'QuestSupplierBuyer',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'questSupplierList', // 当前激活标签  与name相同
      tabs: [
        {
          title: '调查表管理', // 页面名称
          name: 'questSupplierList',
          component: questSupplierList,
          closable: false
        }
      ]
    }
  }
}
</script>
