<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import indicatorsList from './indicatorsList'
export default {
  name: 'Indicators',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'indicatorsList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.indicators'),
          name: 'indicatorsList',
          component: indicatorsList,
          closable: false
        }
      ]
    }
  }
}
</script>
