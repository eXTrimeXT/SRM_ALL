<template>
  <NavTabs :tabs-list="tabs" :cur-tab="activeTab" />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import reduceBasePriceList from './list-engine'

export default {
  name: 'ReduceBasePrice',

  components: { NavTabs },

  data () {
    return {
      activeTab: 'reduceBasePriceList',
      tabs: [
        {
          title: this.$t('reduce.reduceBasePrice'),
          name: 'reduceBasePriceList',
          component: reduceBasePriceList,
          closable: false
        }
      ]
    }
  }
}
</script>
