<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import taskList from './taskList'
export default {
  name: 'TaskCenter',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'taskList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('flowMod.myTask'), // '我的任务',
        name: 'taskList',
        component: taskList,
        closable: false
      }]
    }
  }
}
</script>
