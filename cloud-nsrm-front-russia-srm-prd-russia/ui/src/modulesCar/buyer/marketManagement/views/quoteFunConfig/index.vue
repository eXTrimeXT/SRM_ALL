<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import quoteFunConfig from './list.vue'

export default {
  name: 'quoteFunConfig',

  components: { NavTabs },

  data () {
    return {
      activeTab: 'quoteFunConfig',
      tabs: [
        {
          title: this.$t('route.quoteFunConfig'),
          name: 'quoteFunConfig',
          component: quoteFunConfig,
          closable: false
        }
      ]
    }
  }
}
</script>
