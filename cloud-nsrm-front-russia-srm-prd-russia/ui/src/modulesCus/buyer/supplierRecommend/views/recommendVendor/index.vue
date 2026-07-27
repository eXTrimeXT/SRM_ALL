<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import RecommendVendorList from './list'

export default {
  name: 'RecommendVendor',

  components: {
    NavTabs
  },

  data () {
    return {
      activeTab: 'recommendVendorList',
      tabs: [
        {
          title: '推荐供应商',
          name: 'recommendVendorList',
          component: RecommendVendorList,
          closable: false
        }
      ]
    }
  }
}
</script>
