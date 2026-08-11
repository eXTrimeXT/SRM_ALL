<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import supplierinventoryList from './list'
export default {
  name: 'Supplierinventory',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'supplierinventoryList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.supplierInventory'), // 页面名称
          name: 'supplierinventoryList',
          component: supplierinventoryList,
          closable: false
        }
      ]
    }
  }
}
</script>
