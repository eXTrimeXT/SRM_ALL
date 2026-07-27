<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import CalibrationApplyList from './list'

export default {
  name: 'CalibrationApply',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'calibrationApplyList',
      tabs: [
        {
          title: '定标审批单',
          name: 'calibrationApplyList',
          component: CalibrationApplyList,
          closable: false
        }
      ]
    }
  }
}
</script>
