<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
    @tab-change="tabChange"
    @tab-remove="tabRemove"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import QuestionList from './questionList'
import AnswerList from './answerList'
export default {
  name: 'QaList',
  components: {
    NavTabs
  },
  props: {
    // 寻源类型
    souType: {
      type: String,
      required: true
    },
    // 查询项目快查code
    quickSearchCode: {
      type: String,
      default: ''
    }
  },
  data () {
    return {
      historyTabName: 'QuestionList',
      activeTab: 'QuestionList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('bidMod.challengeList'), // 质疑列表
          name: 'QuestionList',
          component: QuestionList,
          closable: false,
          params: {
            souType: this.souType,
            quickSearchCode: this.quickSearchCode
          }
        },
        {
          title: this.$t('bidMod.clarificationList'), // 澄清列表
          name: 'AnswerList',
          component: AnswerList,
          closable: false,
          params: {
            souType: this.souType,
            quickSearchCode: this.quickSearchCode
          }
        }
      ]
    }
  },
  watch: {
    activeTab (_newVal, oldVal) {
      if (['QuestionList', 'AnswerList'].includes(oldVal)) {
        this.historyTabName = oldVal
      }
    }
  },
  methods: {
    tabChange (tab) {
      this.activeTab = tab
      this.tabs = this.$refs.tabs.tabs
    },
    tabRemove ({ activeTab }) {
      if (!['listEngine', 'AnswerList'].includes(activeTab)) return
      this.$refs.tabs.activeTab = this.historyTabName
    }
  }
}
</script>
