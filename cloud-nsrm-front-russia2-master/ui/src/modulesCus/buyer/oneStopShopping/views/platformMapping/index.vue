<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import list from './list-engine'
export default {
  name: 'PlatformMapping',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'platformMapping',
      tabs: [{
        title: '平台映射关系',
        name: 'platformMapping',
        component: list,
        closable: false
      }]
    }
  }
}
</script>
