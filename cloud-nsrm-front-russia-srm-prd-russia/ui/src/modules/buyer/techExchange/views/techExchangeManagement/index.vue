<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import techExchangeManagementList from './techExchangeManagementList.vue'

export default {
  name: 'TechExchangeManagement',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'techExchangeManagementList',
      tabs: [
        {
          title: this.$t('techExchange.managementTabTitle'),
          name: 'techExchangeManagementList',
          component: techExchangeManagementList,
          closable: false
        }
      ]
    }
  }
}
</script>
