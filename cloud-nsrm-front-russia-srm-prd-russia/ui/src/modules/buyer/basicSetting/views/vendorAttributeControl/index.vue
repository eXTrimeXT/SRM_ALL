<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import VendorAttributeList from './VendorAttributeList'
export default {
  name: 'VendorAttributeControl',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'VendorAttributeList', // 当前激活标签  与name相同
      tabs: [
        {
          title: () => this.$t('dataConfMod.attributeList'), // '属性列表',
          name: 'VendorAttributeList',
          component: VendorAttributeList,
          closable: false
        }
      ]
    }
  }
}
</script>
