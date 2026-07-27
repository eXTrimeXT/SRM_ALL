<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import businessOperationLogList from './list'
export default {
  name: 'BusinessOperationLog',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'businessOperationLogList', // 当前激活标签  与name相同
      tabs: [
        {
          title: () => this.$t('route.businessOperationLog'), // 页面名称
          name: 'businessOperationLogList',
          component: businessOperationLogList,
          closable: false
        }
      ]
    }
  },
  methods: {
  }
}
</script>
