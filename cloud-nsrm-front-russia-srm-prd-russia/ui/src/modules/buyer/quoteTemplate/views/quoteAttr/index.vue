<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import quoteAttrList from './quoteAttrList.vue'

export default {
  name: 'QuoteAttr',

  components: { NavTabs },

  data () {
    return {
      activeTab: 'quoteAttrList',
      tabs: [
        {
          title: this.$t('route.quoteAttr'),
          name: 'quoteAttrList',
          component: quoteAttrList,
          closable: false
        }
      ]
    }
  }
}
</script>
