<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import CalibrationApplyAbandonList from './list'

export default {
  name: 'CalibrationApplyAbandon',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'calibrationApplyAbandonList',
      tabs: [
        {
          // title: '定标废弃申请',
          title: () => this.$t('cusEntry.supplement20250205.bidAwardCancellationRequest'),
          name: 'calibrationApplyAbandonList',
          component: CalibrationApplyAbandonList,
          closable: false
        }
      ]
    }
  }
}
</script>
