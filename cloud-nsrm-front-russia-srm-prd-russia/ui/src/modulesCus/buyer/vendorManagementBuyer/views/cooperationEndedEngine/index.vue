<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import cooperationEndedList from './cooperationEndedList'
export default {
  name: 'CooperationEnded',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'cooperationEndedList', // 当前激活标签  与name相同
      tabs: [
        {
          title: () => this.$t('cusEntry.vendorMod.cooperationEnded'), // '合作终止',
          name: 'cooperationEndedList',
          component: cooperationEndedList,
          closable: false
        }
      ]
    }
  }
}
</script>
