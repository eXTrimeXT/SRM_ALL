<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
    @tab-change="tabChange"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import AttributeConfList from './AttributeConfList'
export default {
  name: 'PurchaseBaseSetting',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'AttributeConfList', // 当前激活标签  与name相同
      tabs: [
        {
          title: () => this.$t('dataConfMod.attributeConfList'), // '模板配置列表',
          name: 'AttributeConfList',
          component: AttributeConfList,
          closable: false
        }
      ]
    }
  },
  activated () {
    if (
      this.$route.params.autoQuery &&
      this.currentTab === 'AttributeConfList'
    ) {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'AttributeConfList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'AttributeConfList') {
        this.dolayout()
      }
      this.currentTab = tab
    }
  }
}
</script>
