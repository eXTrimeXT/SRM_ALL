<template>
  <NavTabs :tabs-list="tabs" :cur-tab="activeTab" />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import list from './list'
export default {
  name: 'TodolistConfig',

  components: { NavTabs },

  data () {
    return {
      activeTab: 'todolistConfigList',
      tabs: [
        {
          title: () => this.$t('todolistConfig.navTabsTitle'),
          name: 'todolistConfigList',
          component: list,
          closable: false
        }
      ]
    }
  }
}
</script>
