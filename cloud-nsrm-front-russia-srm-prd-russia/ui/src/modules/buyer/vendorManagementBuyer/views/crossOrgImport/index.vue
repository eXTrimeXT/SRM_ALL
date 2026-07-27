<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import crossOrgImportList from './crossOrgImportList'
export default {
  name: 'CrossOrgImport',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'crossOrgImportList', // 当前激活标签  与name相同
      tabs: [
        {
          title: () => this.$t('route.crossOrgImport'), // 跨组织引入
          name: 'crossOrgImportList',
          component: crossOrgImportList,
          closable: false
        }
      ]
    }
  }
}
</script>
