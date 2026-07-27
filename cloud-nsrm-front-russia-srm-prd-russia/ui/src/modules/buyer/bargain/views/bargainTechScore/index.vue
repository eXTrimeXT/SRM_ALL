<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
/**
 * 项目式询价技术评分
 */
import NavTabs from 'lib@/components/NavTabs'
import bargainTechScoreList from './bargainTechScoreList.vue'

export default {
  name: 'BargainTechScore',

  components: { NavTabs },

  data () {
    return {
      activeTab: 'bargainTechScoreList',
      tabs: [
        {
          title: '技术评分',
          name: 'bargainTechScoreList',
          component: bargainTechScoreList,
          closable: false
        }
      ]
    }
  }
}
</script>
