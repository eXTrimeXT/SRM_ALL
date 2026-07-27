v<template>
  <nav-tabs :tabs-list="tabs" :cur-tab="activeTab" />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import marketPartsPriceList from './list'

export default {
  name: 'MarketPartsPrice',

  components: { NavTabs },

  data () {
    return {
      activeTab: 'marketPartsPriceList',
      tabs: [
        {
          title: '市况联动零件价格列表',
          name: 'marketPartsPriceList',
          component: marketPartsPriceList,
          closable: false
        }
      ]
    }
  }
}
</script>
