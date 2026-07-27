<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import outsourceMaterialsListSup from './list'
export default {
  name: 'OutsourceMaterials',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'outsourceMaterialsListSup',
      tabs: [{
        title: this.$t('route.outsourceMaterialsSup'), // '供方领料单'
        name: 'outsourceMaterialsListSup',
        component: outsourceMaterialsListSup,
        closable: false
      }]
    }
  }
}
</script>
