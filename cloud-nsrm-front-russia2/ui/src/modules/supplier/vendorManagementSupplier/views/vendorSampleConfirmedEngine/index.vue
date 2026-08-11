<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
  import NavTabs from 'lib@/components/NavTabs'
  import sampleConfirmedList from './list_engine'
  export default {
    name: 'VendorSampleConfirmed',
    components: {
      NavTabs
    },
    data () {
      return {
        activeTab: 'sampleConfirmedList', // 当前激活标签  与name相同
        tabs: [{
          title: () => this.$t('vendorMod.sampleConfirmed'), // 样品确认
          name: 'sampleConfirmedList',
          component: sampleConfirmedList,
          closable: false
        }]
      }
    }
  }
</script>
