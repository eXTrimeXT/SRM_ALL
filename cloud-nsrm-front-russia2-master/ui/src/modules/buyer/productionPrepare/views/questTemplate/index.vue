<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import questTemplateList from './questTemplateList'
export default {
  name: 'QuestTemplateManage',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'questTemplateList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('vendorMod.questTemplateList'), // 调查表模板管理
          name: 'questTemplateList',
          component: questTemplateList,
          closable: false
        }
      ]
    }
  }

}
</script>
