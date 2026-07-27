<template>
  <NavTabs :tabs-list="tabs" :cur-tab="activeTab" />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import meetModelList from './meetModelList'

export default {
  name: 'MeetModel',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'meetModelList',
      tabs: [
        {
          title: this.$t('meeting.issueTemplate'),
          name: 'meetModelList',
          component: meetModelList,
          closable: false
        }
      ]
    }
  }
}
</script>
