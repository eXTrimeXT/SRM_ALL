<template>
  <NavTabs
    :tabs-list="tabs"
    :cur-tab="activeTab"
    @tab-change="tabChange"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import surveyList from './surveyList'

export default {
  name: 'QuestionnaireSurveySupplier',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'surveyList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.questionnaireSurveySupplier'), // 问卷调查
          name: 'surveyList',
          component: surveyList,
          closable: false
        }
      ]
    }
  },

  activated () {
    if (this.$route.params.autoQuery && this.currentTab === 'surveyList') {
      this.dolayout()
    }
  },

  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'surveyList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },

    tabChange (tab) {
      if (tab === 'surveyList') {
        this.dolayout()
      }
      this.currentTab = tab
    }
  }
}
</script>
