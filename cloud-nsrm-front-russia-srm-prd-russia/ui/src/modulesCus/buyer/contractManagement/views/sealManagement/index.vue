<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import SealManagementList from './seal-management-list'
export default {
  name: 'SealManagement',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'SealManagementList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('cusEntry.route.sealManagementList'),
          name: 'SealManagementList',
          component: SealManagementList,
          closable: false
        }
      ]
    }
  }
}
</script>
