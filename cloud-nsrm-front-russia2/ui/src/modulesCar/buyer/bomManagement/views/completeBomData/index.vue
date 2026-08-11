<template>
  <NavTabs :tabs-list="tabs" :cur-tab="activeTab" />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import completeBomDataList from './list-engine'

export default {
  name: 'CompleteBomData',

  components: { NavTabs },

  data () {
    return {
      activeTab: 'completeBomDataList',
      tabs: [
        {
          title: this.$t('bom.completeBomData'),
          name: 'completeBomDataList',
          component: completeBomDataList,
          closable: false
        }
      ]
    }
  }
}
</script>
