v<template>
  <nav-tabs :tabs-list="tabs" :cur-tab="activeTab" />
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
          title: this.$t('零件与原材料联动关系维护'),
          name: 'partsMaterialList',
          component: partsMaterialList,
          closable: false
        }
      ]
    }
  }
}
</script>
