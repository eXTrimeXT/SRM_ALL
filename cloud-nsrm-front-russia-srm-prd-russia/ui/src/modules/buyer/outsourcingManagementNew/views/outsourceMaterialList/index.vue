<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
    @tab-change="tabChange"
    @tab-remove="tabRemove"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import OutsourceMaterialList from './outsourceMaterialList'
import OutsourceMaterialDetailList from './outsourceMaterialDetailList'
export default {
  name: 'OutsourceMaterialHead',
  components: {
    NavTabs
  },
  data () {
    return {
      historyTabName: 'OutsourceMaterialList',
      activeTab: 'OutsourceMaterialList', // 当前激活标签  与name相同
      tabs: [
        {
          title: () => this.$t('outsourceMaterialHead.outsourceMaterialList'), // 委外用料清单列表
          name: 'OutsourceMaterialList',
          component: OutsourceMaterialList,
          closable: false
        },
        {
          title: () => this.$t('outsourceMaterialHead.outsourceMaterialDetailList'), // 委外用料清单明细
          name: 'OutsourceMaterialDetailList',
          component: OutsourceMaterialDetailList,
          closable: false
        }
      ]
    }
  },
  watch: {
    activeTab (_newVal, oldVal) {
      if (['OutsourceMaterialList', 'OutsourceMaterialDetailList'].includes(oldVal)) {
        this.historyTabName = oldVal
      }
    }
  },
  methods: {
    tabChange (tab) {
      this.activeTab = tab
      this.tabs = this.$refs.tabs.tabs
    },
    tabRemove ({ activeTab }) {
      if (!['OutsourceMaterialList', 'OutsourceMaterialDetailList'].includes(activeTab)) return
      this.$refs.tabs.activeTab = this.historyTabName
    }
  }
}
</script>
