<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import priceModelList from './list'
export default {
  name: 'PriceModel',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'priceModelList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.priceModel'),
          name: 'priceModelList',
          component: priceModelList,
          closable: false
        }
      ]
    }
  }
}
</script>
