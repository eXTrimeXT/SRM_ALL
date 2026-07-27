<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import PartyProjectNumberList from './party-project-number-list'
export default {
  name: 'PartyProjectNumber',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'PartyProjectNumberList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('cusEntry.reportManagement.partyProjectNumberList'),
        name: 'PartyProjectNumberList',
        component: PartyProjectNumberList,
        closable: false
      }]
    }
  }
}
</script>
