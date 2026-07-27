<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import autoOrderSplitList from './list'
export default {
  name: 'AutoOrderSplit',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'List', // 当前激活标签  与name相同
      tabs: [
        {
          title: '自动分单',
          name: 'List',
          component: autoOrderSplitList,
          closable: false
        }
      ]
    }
  }
}
</script>
