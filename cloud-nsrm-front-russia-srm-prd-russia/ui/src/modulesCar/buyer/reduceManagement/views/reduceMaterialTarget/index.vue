<template>
  <NavTabs :tabs-list="tabs" :cur-tab="activeTab" />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import reduceMaterialTargetList from './list-engine'

export default {
  name: 'ReduceMaterialTarget',

  components: { NavTabs },

  data () {
    return {
      activeTab: 'reduceMaterialTargetList',
      tabs: [
        {
          title: this.$t('reduce.reduceMaterialTarget'),
          name: 'reduceMaterialTargetList',
          component: reduceMaterialTargetList,
          closable: false
        }
      ]
    }
  }
}
</script>
