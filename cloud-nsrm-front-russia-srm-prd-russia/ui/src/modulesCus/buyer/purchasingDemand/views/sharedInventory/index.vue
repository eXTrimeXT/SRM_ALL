<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import drawingsheadList from './list'
export default {
  name: 'SharedInventory',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'drawingsheadList',
      tabs: [
        {
          title: '供方库存',
          name: 'drawingsheadList',
          component: drawingsheadList,
          closable: false
        }
      ]
    }
  }
}
</script>
