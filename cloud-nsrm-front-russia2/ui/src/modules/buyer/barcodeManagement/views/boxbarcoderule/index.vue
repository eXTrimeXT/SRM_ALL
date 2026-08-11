<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import barcoderuleheadList from './list'
export default {
  name: 'BoxBarcodeRule',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'barcoderuleheadList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.barcodeGenerateRule'), // 页面名称   '条码生成规则配置'
          name: 'barcoderuleheadList',
          component: barcoderuleheadList,
          closable: false
        }
      ]
    }
  }
}
</script>
