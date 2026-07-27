<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>

<script>
import NavTabs from 'lib@/components/NavTabs'
import quoteTemplateList from './quoteTemplateList.vue'

export default {
  name: 'QuoteTemplateList',

  components: { NavTabs },

  data () {
    return {
      activeTab: 'quoteTemplateList',
      tabs: [
        {
          title: this.$t('route.quoteTemplateList'),
          name: 'quoteTemplateList',
          component: quoteTemplateList,
          closable: false
        }
      ]
    }
  }
}
</script>
