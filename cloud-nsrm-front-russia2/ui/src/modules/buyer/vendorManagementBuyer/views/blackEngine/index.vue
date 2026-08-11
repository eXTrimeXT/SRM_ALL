<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import Black from './list-engine'
export default {
  name: 'Black',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'BlackList',
      tabs: [{
        title: () => this.$t('vendorMod.blackListInfo'),
        name: 'BlackList',
        component: Black,
        closable: false
      }]
    }
  }
}
</script>
