<template>
  <NavTabs :tabs-list="tabs" :cur-tab="activeTab" />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import reduceRebateList from './list-engine'

export default {
  name: 'ReduceRebate',

  components: { NavTabs },

  data () {
    return {
      activeTab: 'reduceRebateList',
      tabs: [
        {
          title: this.$t('route.reduceRebate'),
          name: 'reduceRebateList',
          component: reduceRebateList,
          closable: false
        }
      ]
    }
  }
}
</script>
