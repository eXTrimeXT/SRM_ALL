<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import foundation from './foundationList'
export default {
  name: 'Foundation',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'List', // 当前激活标签  与name相同
      tabs: [
        {
          title: '近期采购类基础数据',
          name: 'List',
          component: foundation,
          closable: false
        }
      ]
    }
  }
}
</script>
