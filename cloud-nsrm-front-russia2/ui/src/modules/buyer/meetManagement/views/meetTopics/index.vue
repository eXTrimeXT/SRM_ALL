<template>
  <NavTabs :tabs-list="tabs" :cur-tab="activeTab" />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import meetTopicsList from './meetTopicsList'

export default {
  name: 'MeetSubject',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'meetTopicsList',
      tabs: [
        {
          title: this.$t('meeting.topicManagement'),
          name: 'meetTopicsList',
          component: meetTopicsList,
          closable: false
        }
      ]
    }
  }
}
</script>
