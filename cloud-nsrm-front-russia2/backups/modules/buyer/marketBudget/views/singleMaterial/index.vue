<template>
  <NavTabs :tabs-list="tabs" :cur-tab="activeTab" />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import singleMaterialList from './list'

export default {
  name: 'SingleMaterial',

  components: { NavTabs },

  data () {
    return {
      activeTab: 'singleMaterialList',
      tabs: [
        {
          title: this.$t('marketBudget.singleMaterial'),
          name: 'singleMaterialList',
          component: singleMaterialList,
          closable: false
        }
      ]
    }
  }
}
</script>
