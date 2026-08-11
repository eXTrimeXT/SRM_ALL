<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import OutsourcingBomList from './list'
export default {
  name: 'OutsourcingBomNew',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'OutsourcingBomList',
      tabs: [{
        title: () => this.$t('outsourcingBomNew.outsourcingBomMgr'), // 委外BOM维护
        name: 'OutsourcingBomList',
        component: OutsourcingBomList,
        closable: false
      }]
    }
  }
}
</script>
