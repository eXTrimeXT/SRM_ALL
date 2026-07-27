<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import blacktemporary from './list'
export default {
  name: 'Blacktemporary',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'blacktemporary',
      tabs: [
        {
          title: this.$t('route.blacktemporary'),
          name: 'blacktemporary',
          component: blacktemporary,
          closable: false
        }
      ]
    }
  }
}
</script>
