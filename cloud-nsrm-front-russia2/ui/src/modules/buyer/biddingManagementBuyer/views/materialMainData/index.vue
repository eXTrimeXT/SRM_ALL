<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import { tabTodoMixin } from '@/utils/mixins'
import NavTabs from 'lib@/components/NavTabs'
import materialMainDataList from './materialMainDataList'

export default {
  name: 'VendorBiddingList',

  components: {
    NavTabs
  },

  mixins: [tabTodoMixin],

  data () {
    return {
      activeTab: 'MaterialMainDataList',
      tabs: [
        {
          title: this.$t('route.materialMainData'),
          name: 'MaterialMainDataList',
          component: materialMainDataList,
          closable: false
        }
      ]
    }
  }
}
</script>
