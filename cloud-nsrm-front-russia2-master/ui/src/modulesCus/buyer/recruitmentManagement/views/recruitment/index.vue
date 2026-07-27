<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import recruitmentList from './list'
export default {
  name: 'Recruitment',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'recruitmentList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('cusEntry.route.recruitment'), // 招募管理
          name: 'recruitmentList',
          component: recruitmentList,
          closable: false
        }
      ]
    }
  }
}
</script>
