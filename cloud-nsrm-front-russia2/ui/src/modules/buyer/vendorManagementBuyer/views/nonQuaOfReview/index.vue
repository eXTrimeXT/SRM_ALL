<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
  import NavTabs from 'lib@/components/NavTabs'
  import quaOfReviewList from './quaOfReviewList'
  export default {
    name: 'NonQuaOfReview',
    components: {
      NavTabs
    },
    data () {
      return {
        activeTab: 'quaOfReviewList', // 当前激活标签  与name相同
        tabs: [{
          title: () => this.$t('vendorMod.noQua'), // '非材料类资质审查'
          name: 'quaOfReviewList',
          component: quaOfReviewList,
          closable: false
        }]
      }
    }
  }
</script>
