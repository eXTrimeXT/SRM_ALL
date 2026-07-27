<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
    @tab-change="tabChange"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import surveyList from './surveyList'

export default {
  name: 'QuestionnaireSurvey',

  components: {
    NavTabs
  },

  data () {
    return {
      currentTab: null,
      activeTab: 'surveyList', // 当前激活标签  与name相同
      tabs: [{
        title: () => '问卷调查',
        name: 'surveyList',
        component: surveyList,
        closable: false
      }]
    }
  },

  activated () {
    if (
      this.$route.params.autoQuery &&
      this.currentTab === 'surveyList'
    ) {
      this.dolayout('surveyList')
    }
  },

  methods: {
    dolayout (type) {
      this.$nextTick(() => {
        const data = {
          name: type, // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },

    tabChange (tab) {
      if (tab === 'surveyList') {
        this.dolayout('surveyList')
      } else if (tab.search('surveyResult') != -1) {
        this.$bus.$emit('surveyResultDoLayout', true)
      }
      this.currentTab = tab
    }
  }
}
</script>
