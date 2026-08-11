<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
/**
 * 专家库
 */
import { tabTodoMixin } from '@/utils/mixins'
import NavTabs from 'lib@/components/NavTabs'
import expertDatabaseList from './expertDatabaseList'

export default {
  name: 'ExpertDatabase',
  components: {
    NavTabs
  },
  mixins: [tabTodoMixin],
  data () {
    return {
      activeTab: 'expertDatabaseList',
      tabs: [
        {
          title: this.$t('route.expertDatabase'),
          name: 'expertDatabaseList',
          component: expertDatabaseList,
          closable: false
        }
      ]
    }
  }
}
</script>
