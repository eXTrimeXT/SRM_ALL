<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import OutsourceReturnMaterialsList from './list'
export default {
  name: 'OutsourceMaterialsSup',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'outsourceReturnMaterialsList',
      tabs: [{
        title: () => this.$t('route.outsourceReturnMaterialsSup'), // '供方退料单',
        name: 'outsourceReturnMaterialsList',
        component: OutsourceReturnMaterialsList,
        closable: false
      }]
    }
  }
}
</script>
