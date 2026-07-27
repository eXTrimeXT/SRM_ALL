<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import supplierinventorylogList from './list'
export default {
  name: 'Supplierinventorylog',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'supplierinventorylogList', // 当前激活标签  与name相同
      tabs: [
        {
          title: '供方历史库存查询', // 页面名称
          name: 'supplierinventorylogList',
          component: supplierinventorylogList,
          closable: false
        }
      ]
    }
  }
}
</script>
