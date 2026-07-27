<template>
  <NavTabs ref="tabs" :tabs-list="tabs" :cur-tab="activeTab" @tab-change="tabChange" />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import outerboxcodeList from './list'
export default {
  name: 'OuterBoxCode',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'outerboxcodeList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('orderMod.outerBoxBarcode'), // 外箱条码
          name: 'outerboxcodeList',
          component: outerboxcodeList,
          closable: false
        }
      ]
    }
  },
  activated () {
    console.log('[activated]')
    if (this.activeTab === 'outerboxcodeList') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'outerboxcodeList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        console.log('[SET_NAV_TABS_TODO]', data)
        this.$store.commit('SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === ' outerboxcodeList') {
        this.dolayout()
      }
      console.log('[tab]', tab)
      this.activeTab = tab
    }
  }
}
</script>
