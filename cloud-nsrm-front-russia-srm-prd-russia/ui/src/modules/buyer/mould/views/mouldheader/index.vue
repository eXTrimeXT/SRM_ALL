<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import mouldheaderList from './list'
export default {
  name: 'Mouldheader',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'mouldheaderList', // 当前激活标签  与name相同
      tabs: [
        {
          title: () => this.$t('route.moudleHeader'), // 页面名称
          name: 'mouldheaderList',
          component: mouldheaderList,
          closable: false
        }
      ]
    }
  }
}
</script>
