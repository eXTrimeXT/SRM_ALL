<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import demoList from './demoList'
export default {
  name: 'CategorySourcing',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'demoList', // 当前激活标签  与name相同
      tabs: [{
        title: this.$t('cusEntry.supplement20250211.demoVariable'),  // '演示'
        name: 'demoList',
        component: demoList,
        closable: false
      }]
    }
  }
}
</script>
