<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import OutsourceMaterialListChange from './list'
export default {
  name: 'OutsourceMaterialChange',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'OutsourceMaterialListChange',
      tabs: [{
        title: () => this.$t('outsourceMaterialHead.outsourceMaterialListChange'), // 委外用料清单变更
        name: 'OutsourceMaterialListChange',
        component: OutsourceMaterialListChange,
        closable: false
      }]
    }
  }
}
</script>
