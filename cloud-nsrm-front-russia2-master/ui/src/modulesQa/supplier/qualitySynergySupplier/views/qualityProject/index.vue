<template>
  <NavTabs :tabsList="tabs" :curTab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import HomeList from './homeList'
export default {
  name: 'QualityProject',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'HomeList', // 当前激活标签  与name相同
      tabs: [{
        title: this.$t('route.qualityProject'),
        name: 'HomeList',
        component: HomeList,
        closable: false
      }]
    }
  }
}
</script>
