<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
  import NavTabs from 'lib@/components/NavTabs'
  import list from './list'
  export default {
    name: 'StudyDemoC',
    components: {
      NavTabs
    },
    data () {
      return {
        activeTab: 'demoList', // 当前激活标签  与name相同
        tabs: [{
          title: '示例',
          name: 'demoList',
          component: list,
          closable: false
        }]
      }
    }
  }
</script>
