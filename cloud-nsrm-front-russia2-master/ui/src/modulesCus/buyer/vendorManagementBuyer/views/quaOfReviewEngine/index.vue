<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import quaOfReviewList from './quaOfReviewList'
export default {
  name: 'QuaOfReview',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'quaOfReviewList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('vendorMod.qua'), // '资质审查'
        name: 'quaOfReviewList',
        component: quaOfReviewList,
        closable: false
      }]
    }
  },
  activated () {
    if (this.currentTab === 'quaOfReviewList') {
      this.dolayout()
    }
  },
  methods: {
    tabChange (tab) {
      if (tab === 'quaOfReviewList') {
        this.dolayout()
      }
      this.currentTab = tab
    },
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'quaOfReviewList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    }
  }
}
</script>
