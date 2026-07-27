<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import innerboxcodeList from './list'
export default {
  name: 'InnerBoxCode',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'innerboxcodeList', // 当前激活标签  与name相同
      tabs: [
        {
          title: '内箱条码', // 页面名称
          name: 'innerboxcodeList',
          component: innerboxcodeList,
          closable: false
        }
      ]
    }
  }
}
</script>
