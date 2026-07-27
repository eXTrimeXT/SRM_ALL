<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import problemList from './list'

export default {
  name: 'ProblemManagement',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'problemList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('problemManagement.problemList'), // 活动问题点清单
          name: 'problemList',
          component: problemList,
          closable: false
        }
      ]
    }
  }
}
</script>
