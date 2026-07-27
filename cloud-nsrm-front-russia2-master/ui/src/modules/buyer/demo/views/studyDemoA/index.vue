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
    name: 'StudyDemoA',
    components: {
      NavTabs
    },
    data () {
      return {
        activeTab: 'demoList', // 当前激活标签  与name相同
        tabs: [{
          title: this.$t('cusEntry.supplement20250211.exampleList'),  // '例子列表'
          name: 'demoList',
          component: list,
          closable: false
        }]
      }
    }
  }
</script>
