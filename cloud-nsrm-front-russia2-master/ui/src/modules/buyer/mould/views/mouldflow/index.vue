<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import mouldflowList from './list'
export default {
  name: 'Mouldflow',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'mouldflowList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('mould.MouldflowPage'), // 页面名称
          name: 'mouldflowList',
          component: mouldflowList,
          closable: false
        }
      ]
    }
  }
}
</script>
