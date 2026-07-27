<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import ExpertApplyList from './list'

export default {
  name: 'ExpertApply',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'expertApplyList',
      tabs: [
        {
          title: '专家申请',
          name: 'expertApplyList',
          component: ExpertApplyList,
          closable: false
        }
      ]
    }
  }
}
</script>
