<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import blackReport from './list'

export default {
  name: 'Black',

  components: { NavTabs },

  data () {
    return {
      activeTab: 'blackReport',
      tabs: [
        {
          title: this.$t('route.blackReport'),
          name: 'blackReport',
          component: blackReport,
          closable: false
        }
      ]
    }
  }
}
</script>
