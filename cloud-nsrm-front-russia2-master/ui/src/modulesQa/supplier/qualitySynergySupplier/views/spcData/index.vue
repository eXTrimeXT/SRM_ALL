<template>
  <NavTabs :tabsList="tabs" :curTab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import SPCDataList from './SPCDataList'
export default {
  name: 'SpcData',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'SPCDataList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('quality.spcList'),
        name: 'SPCDataList',
        component: SPCDataList,
        closable: false
      }]
    }
  }
}
</script>
