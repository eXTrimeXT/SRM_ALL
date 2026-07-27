<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import hierarchicalRatingList from './list'
export default {
  name: 'HierarchicalRating',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'hierarchicalRatingList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.hierarchicalRating'), // 页面名称
          name: 'hierarchicalRatingList',
          component: hierarchicalRatingList,
          closable: false
        }
      ]
    }
  }
}
</script>
