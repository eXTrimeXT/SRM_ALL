<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import goodsEngine from './goods-engine'
import matterEngine from './matter-engine'
export default {
  name: 'BarcodeRelationBuyer',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'goodsEngine',
      tabs: [
        {
          title: this.$t('cusEntry.supplement20250211.deliveryOrder'),  // '按送货单'
          name: 'goodsEngine',
          component: goodsEngine,
          closable: false
        },
        {
          title: this.$t('cusEntry.supplement20250211.materialBased'),  // '按物料'
          name: 'matterEngine',
          component: matterEngine,
          closable: false
        }
      ]
    }
  }
}
</script>
