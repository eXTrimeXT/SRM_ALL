<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
    @tab-change="tabChange"
  />
</template>

<script>
import { tabTodoMixin } from '@/utils/mixins'
import NavTabs from 'lib@/components/NavTabs'
import priceOrderList from './priceOrderList'

export default {
  name: 'PriceOrders',

  components: { NavTabs },

  mixins: [tabTodoMixin],

  data () {
    return {
      activeTab: 'priceOrderList',
      tabs: [
        {
          title: this.$t('cusEntry.inq.priceOrderList'),
          name: 'priceOrderList',
          component: priceOrderList,
          closable: false
        }
      ]
    }
  },

  methods: {
    /* tab切换 */
    tabChange (val) {
      this.activeTab = val
    }
  }
}
</script>
