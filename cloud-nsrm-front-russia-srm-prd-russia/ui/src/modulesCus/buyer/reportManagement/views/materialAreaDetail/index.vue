<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import MaterialAreaDetailList from './material-area-detail-list'
export default {
  name: 'MaterialAreaDetail',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'MaterialAreaDetailList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('cusEntry.route.materialAreaDetailList'),
        name: 'MaterialAreaDetailList',
        component: MaterialAreaDetailList,
        closable: false
      }]
    }
  }
}
</script>
