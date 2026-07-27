<template>
  <NavTabs :tabs-list="tabs" :cur-tab="activeTab" />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import reduceCarDimensionList from './list-engine'

export default {
  name: 'ReduceCarDimension',

  components: { NavTabs },

  data () {
    return {
      activeTab: 'reduceCarDimensionList',
      tabs: [
        {
          title: this.$t('reduce.reduceCarDimension'),
          name: 'reduceCarDimensionList',
          component: reduceCarDimensionList,
          closable: false
        }
      ]
    }
  }
}
</script>
