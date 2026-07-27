<template>
  <NavTabs :tabs-list="tabs" :cur-tab="activeTab" />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import reduceNegotiationList from './list-engine'

export default {
  name: 'ReduceNegotiation',

  components: { NavTabs },

  data () {
    return {
      activeTab: 'reduceNegotiationList',
      tabs: [
        {
          title: this.$t('reduce.reduceNegotiation'),
          name: 'reduceNegotiationList',
          component: reduceNegotiationList,
          closable: false
        }
      ]
    }
  }
}
</script>
