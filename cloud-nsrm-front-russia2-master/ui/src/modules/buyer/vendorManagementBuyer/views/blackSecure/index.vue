<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import Black from './list-engine'
export default {
  name: 'BlackSecure',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'blackSecure',
      tabs: [{
        title: () => this.$t('route.blackSecure'),
        name: 'blackSecure',
        component: Black,
        closable: false
      }]
    }
  }
}
</script>
