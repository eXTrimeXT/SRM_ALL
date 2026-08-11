<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import deliveryAppointmentList from './deliveryAppointmentList'
export default {
  name: 'DeliveryAppointment',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'deliveryAppointmentList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('orderMod.deliveryAppointmentList'),
          name: 'deliveryAppointmentList',
          component: deliveryAppointmentList,
          closable: false
        }
      ]
    }
  }
}
</script>
