<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import vendorGreenChannelListEngine from './vendorGreenChannelListEngine'
export default {
  name: 'VendorGreenChannel',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'vendorGreenChannelListEngine', // 当前激活标签  与name相同
      tabs: [
        {
          title: () => this.$t('vendorMod.vendorGreenChannel'), // "供应商绿色通道",
          name: 'vendorGreenChannelListEngine',
          component: vendorGreenChannelListEngine,
          closable: false
        }
      ]
    }
  },
  mounted () {
    // 即将进行【导入供应商】，您需要完成：1、导入企业管理的供应商清单；2、维护供应商合格组织和品类关系；
    if (this.$route.params.openSavePage) this.openSavePage()
  },
  methods: {
    openSavePage () {
      // this.$refs.tabs.tabShow("vendorGreenChannelDetail");
    }
  }
}
</script>
