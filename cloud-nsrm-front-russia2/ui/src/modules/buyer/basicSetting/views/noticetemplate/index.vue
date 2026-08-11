<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import noticetemplateList from './list'
export default {
  name: 'Noticetemplate',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'noticetemplateList',
      tabs: [
        {
          title: () => this.$t('route.noticetemplate'),
          name: 'noticetemplateList',
          component: noticetemplateList,
          closable: false
        }
      ]
    }
  }
}
</script>
