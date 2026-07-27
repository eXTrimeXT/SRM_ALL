<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import warehousingAndReturnGoodsList from './list'
export default {
  name: 'WarehousingAndReturnGoods',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'warehousingAndReturnGoodsList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.warehousingAndReturnGoods'),
          name: 'warehousingAndReturnGoodsList',
          component: warehousingAndReturnGoodsList,
          closable: false
        }
      ]
    }
  },
  activated () {
    if (this.currentTab === 'warehousingAndReturnGoodsList') {
      this.dolayout()
    }
  }
}
</script>
