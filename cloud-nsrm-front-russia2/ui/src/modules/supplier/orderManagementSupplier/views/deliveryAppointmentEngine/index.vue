<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import listEngine from './list-engine'
export default {
  name: 'DeliveryAppointments',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'listEngine', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('orderMod.deliveryAppointmentList'),
          name: 'listEngine',
          component: listEngine,
          closable: false
        }
      ]
    }
  }
}
</script>
