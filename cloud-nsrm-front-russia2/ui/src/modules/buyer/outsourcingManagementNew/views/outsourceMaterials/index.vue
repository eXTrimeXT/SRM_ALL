<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import OutsourceMaterialsList from './list'
export default {
  name: 'OutsourceMaterials',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'OutsourceMaterialsList',
      tabs: [{
        title: () => this.$t('route.outsourceMaterials'),
        name: 'OutsourceMaterialsList',
        component: OutsourceMaterialsList,
        closable: false
      }]
    }
  }
}
</script>
