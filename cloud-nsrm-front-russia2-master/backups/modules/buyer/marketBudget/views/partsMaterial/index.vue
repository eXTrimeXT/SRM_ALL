v<template>
  <NavTabs :tabs-list="tabs" :cur-tab="activeTab" />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import partsMaterialList from './list'

export default {
  name: 'PartsMaterial',

  components: { NavTabs },

  data () {
    return {
      activeTab: 'partsMaterialList',
      tabs: [
        {
          title: this.$t('marketBudget.partsMaterial'),
          name: 'partsMaterialList',
          component: partsMaterialList,
          closable: false
        }
      ]
    }
  }
}
</script>
