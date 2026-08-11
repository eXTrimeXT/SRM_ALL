<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import supervisionReportList from './list.vue'

export default {
  name: 'SupervisionReport',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'supervisionReportList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('cusEntry.route.supervisionReport'),
          name: 'supervisionReportList',
          component: supervisionReportList,
          closable: false
        }
      ]
    }
  }
}
</script>
