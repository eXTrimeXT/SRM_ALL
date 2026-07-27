<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import OutsourceReturnMaterialsList from './list'
export default {
  name: 'OutsourceMaterials',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'OutsourceReturnMaterialsList',
      tabs: [{
        title: () => this.$t('route.outsourceReturnMaterials'), // '委外退料单',
        name: 'OutsourceReturnMaterialsList',
        component: OutsourceReturnMaterialsList,
        closable: false
      }]
    }
  }
}
</script>
