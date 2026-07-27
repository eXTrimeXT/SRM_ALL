<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import complaintinfoList from './list'
export default {
  name: 'Complaintinfo',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'complaintinfoList', // 当前激活标签  与name相同
      tabs: [{
        title: '供应商投诉', // '合作终止',
        name: 'complaintinfoList',
        component: complaintinfoList,
        closable: false
      }]
    }
  }

}
</script>
