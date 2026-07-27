<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import costElementList from './costElementList'

export default {
  name: 'CostElement',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'costElementList',
      tabs: [
        {
          title: this.$t('route.costElement'),
          name: 'costElementList',
          component: costElementList,
          closable: false
        }
      ]
    }
  }
}
</script>
