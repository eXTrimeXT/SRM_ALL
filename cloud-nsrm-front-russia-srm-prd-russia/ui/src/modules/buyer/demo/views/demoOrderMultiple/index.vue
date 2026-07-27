<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import demoorderList from './list'
export default {
  name: 'Demoorder',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'demoorderList', // 当前激活标签  与name相同
      tabs: [
        {
          title: '订单头行结构', // 页面名称
          name: 'demoorderList',
          component: demoorderList,
          closable: false
        }
      ]
    }
  }
}
</script>
