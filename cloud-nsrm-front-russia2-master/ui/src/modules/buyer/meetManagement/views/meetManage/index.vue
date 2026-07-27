<template>
  <NavTabs :tabs-list="tabs" :cur-tab="activeTab" />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import meetManageList from './meetManageList'

export default {
  name: 'MeetManage',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'meetManageList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('meeting.conferenceList'), // 页面名称
          name: 'meetManageList',
          component: meetManageList,
          closable: false
        }
      ]
    }
  }
}
</script>
