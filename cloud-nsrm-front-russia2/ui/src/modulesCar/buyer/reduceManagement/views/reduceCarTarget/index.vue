<template>
  <NavTabs :tabs-list="tabs" :cur-tab="activeTab" />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import reduceCarTargetList from './list-engine'

export default {
  name: 'ReduceCarTarget',

  components: { NavTabs },

  data () {
    return {
      activeTab: 'reduceCarTargetList',
      tabs: [
        {
          title: this.$t('reduce.reduceCarTarget'),
          name: 'reduceCarTargetList',
          component: reduceCarTargetList,
          closable: false
        }
      ]
    }
  }
}
</script>
