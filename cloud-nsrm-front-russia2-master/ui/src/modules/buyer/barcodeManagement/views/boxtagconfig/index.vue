<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import boxtagconfigList from './list'
export default {
  name: 'BoxTagConfig',
  components: {
    NavTabs
  },
  data () {
    return {

      activeTab: 'boxtagconfigList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('cusEntry.supplement20250211.cartonBarcodeLabelConfig'), // 页面名称   '装箱条码标签配置'
          name: 'boxtagconfigList',
          component: boxtagconfigList,
          closable: false
        }
      ]
    }
  }
}
</script>
