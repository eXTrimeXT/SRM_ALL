<template>
  <NavTabs :tabsList="tabs" :curTab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import PdcaPageList from './pdcaPageList'
export default {
  name: 'PdcaPage',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'PdcaPageList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('pdca.pdcaPageList'),
        name: 'PdcaPageList',
        component: PdcaPageList,
        closable: false
      }]
    }
  }
}
</script>
