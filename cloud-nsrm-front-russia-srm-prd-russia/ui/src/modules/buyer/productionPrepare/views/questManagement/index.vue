<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import questManagementList from './questManagementList'
export default {
  name: 'QuestManagementProd',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'questManagementList', // 当前激活标签  与name相同
      tabs: [
        {
          title: '调查表管理', // 页面名称
          name: 'questManagementList',
          component: questManagementList,
          closable: false
        }
      ]
    }
  }
}
</script>
