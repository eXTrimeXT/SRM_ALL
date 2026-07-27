<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import ExpertReviewList from './list'

export default {
  name: 'ExpertReview',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'expertReviewList',
      tabs: [
        {
          // title: '专家评审详情',
          title: () => this.$t('cusEntry.supplement20250205.expertReviewDetails'),
          name: 'expertReviewList',
          component: ExpertReviewList,
          closable: false
        }
      ]
    }
  }
}
</script>
