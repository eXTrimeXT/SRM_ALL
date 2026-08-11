<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import improvementList from './improvementList'
export default {
  name: 'VendorImprovement',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'improvementList', // 当前激活标签  与name相同
      tabs: [{
        title: this.$t('route.vendorImprovement'),
        name: 'improvementList',
        component: improvementList,
        closable: false
      }]
    }
  }
}
</script>
