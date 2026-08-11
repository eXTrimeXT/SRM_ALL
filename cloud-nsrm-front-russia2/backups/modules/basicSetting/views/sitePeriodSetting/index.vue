<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import byVendor from './byVendor'
import byCategory from './byCategory'
import byPerformance from './byPerformance'
export default {
  name: 'SitePeriodSetting',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'byVendor', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('dataConfMod.byVendor'), // '按供应商'
        name: 'byVendor',
        component: byVendor,
        closable: false
      }, {
        title: () => this.$t('dataConfMod.byCategory'), // '按品类'
        name: 'byCategory',
        component: byCategory,
        closable: false
      }
      // {
      //   title: '按绩效等级',
      //   name: 'byPerformance',
      //   component: byPerformance,
      //   closable: false
      // }
      ]
    }
  },
  mounted () {
    // if (this.$route.params) {
    //   if (this.$route.params.tabName) {
    //     this.$refs.tabs.tabShow(this.$route.params.tabName)
    //   } else {
    //     this.$refs.tabs.tabShow('byVendor')
    //   }
    // }
  }
  // methods: {
  // }
}
</script>
