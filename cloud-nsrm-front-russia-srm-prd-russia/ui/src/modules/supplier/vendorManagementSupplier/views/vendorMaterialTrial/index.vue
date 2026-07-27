<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
  import NavTabs from 'lib@/components/NavTabs'
  import materialTrialList from './materialTrialList'
  export default {
    name: 'VendorMaterialTrial',
    components: {
      NavTabs
    },
    data () {
      return {
        activeTab: 'materialTrialList', // 当前激活标签  与name相同
        tabs: [{
          title: () => this.$t('vendorMod.mtTrial'), // 物料试用
          name: 'materialTrialList',
          component: materialTrialList,
          closable: false
        }]
      }
    }
  }
</script>
