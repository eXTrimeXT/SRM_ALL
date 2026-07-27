<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import riskList from './list-engine'
export default {
  name: 'Risk',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'riskList',
      tabs: [{
        title: () => this.$t('route.risk'),
        name: 'riskList',
        component: riskList,
        closable: false
      }]
    }
  }
}
</script>
