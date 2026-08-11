<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import materialMaintenanceList from './materialMaintenanceList'
export default {
  name: 'MaterialMaintenance',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'materialMaintenanceList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('dataConfMod.materialMaintenance'),
          name: 'materialMaintenanceList',
          component: materialMaintenanceList,
          closable: false
        }
      ]
    }
  }
}
</script>
