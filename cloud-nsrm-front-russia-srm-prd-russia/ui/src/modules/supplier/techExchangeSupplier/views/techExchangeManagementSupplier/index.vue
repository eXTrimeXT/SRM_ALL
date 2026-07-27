<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import techExchangeManagementSupplierList from './techExchangeManagementSupplierList.vue'

export default {
  name: 'TechExchangeManagementSupplier',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'techExchangeManagementSupplierList',
      tabs: [
        {
          title: this.$t('techExchange.managementTabTitle'),
          name: 'techExchangeManagementSupplierList',
          component: techExchangeManagementSupplierList,
          closable: false
        }
      ]
    }
  }
}
</script>
