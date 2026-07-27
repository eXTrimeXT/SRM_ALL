<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import list from './list'
export default {
  name: 'MeiqlVue',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'meiqlVueList', // 当前激活标签  与name相同
      tabs: [
        {
          title: 'meiQl+VUE开发', // 页面名称
          name: 'meiqlVueList',
          component: list,
          closable: false
        }
      ]
    }
  }
}
</script>
