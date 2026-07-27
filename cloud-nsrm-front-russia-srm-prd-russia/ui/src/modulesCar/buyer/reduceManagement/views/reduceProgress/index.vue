<template>
  <NavTabs :tabs-list="tabs" :cur-tab="activeTab" />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import reduceProgressList from './list-engine'

export default {
  name: 'ReduceProgress',

  components: { NavTabs },

  data () {
    return {
      activeTab: 'reduceProgressList',
      tabs: [
        {
          title: this.$t('route.reduceProgress'),
          name: 'reduceProgressList',
          component: reduceProgressList,
          closable: false
        }
      ]
    }
  }
}
</script>
