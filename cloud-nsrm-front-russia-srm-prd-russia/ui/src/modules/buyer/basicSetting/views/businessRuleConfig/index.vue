<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
    :isFixedFirstItem="false"
    class="dome_set"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import list from './list'
export default {
  name: 'OrderStorage',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'list', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.businessRuleConfig'), // 业务规则配置
          name: 'list',
          component: list,
          closable: false
        }
      ]
    }
  }
}
</script>
<style>
.dome_set .tabs-nav.el-tabs.el-tabs--card > .el-tabs__content > .el-tab-pane{
  padding: 0;
}
</style>
