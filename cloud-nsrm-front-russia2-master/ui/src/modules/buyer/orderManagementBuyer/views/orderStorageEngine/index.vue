<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import orderStorageList from './list'
export default {
  name: 'OrderStorage',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'orderStorageList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('orderMod.orderStorageList'),
          name: 'orderStorageList',
          component: orderStorageList,
          closable: false
        }
      ]
    }
  }
}
</script>
