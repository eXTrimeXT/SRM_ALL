<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import abnormalList from './list'
export default {
  name: 'AbnormalList',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'List', // 当前激活标签  与name相同
      tabs: [
        {
          title: '异常登记',
          name: 'List',
          component: abnormalList,
          closable: false
        }
      ]
    }
  }
}
</script>
