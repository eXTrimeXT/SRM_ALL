<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import riskList from './list'
export default {
  name: 'Risk',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'riskList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.risk'), // 供应商风险
          name: 'riskList',
          component: riskList,
          closable: false
        }
      ]
    }
  }
}
</script>
