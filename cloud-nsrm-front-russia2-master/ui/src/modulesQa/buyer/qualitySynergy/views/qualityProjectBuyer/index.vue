<template>
  <NavTabs :tabsList="tabs" :curTab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import HomeList from './homeList'
export default {
  name: 'QualityProjectBuyer',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'QualityProjectBuyerList', // 当前激活标签  与name相同
      tabs: [{
        title: this.$t('route.qualityProject'),
        name: 'QualityProjectBuyerList',
        component: HomeList,
        closable: false
      }]
    }
  }
}
</script>
