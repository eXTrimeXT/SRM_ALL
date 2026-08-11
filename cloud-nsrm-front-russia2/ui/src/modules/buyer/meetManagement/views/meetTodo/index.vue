<template>
  <NavTabs :tabs-list="tabs" :cur-tab="activeTab" />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import meetTodoList from './meetTodoList'

export default {
  name: 'MeetTodo',

  components: { NavTabs },

  data () {
    return {
      activeTab: 'meetTodoList',
      tabs: [
        {
          title: this.$t('meeting.todo'),
          name: 'meetTodoList',
          component: meetTodoList,
          closable: false
        }
      ]
    }
  }
}
</script>
