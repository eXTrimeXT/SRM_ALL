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
import dynamicReportPageList from './list'
import { findMenuInfoByPath } from '@/utils'

export default {
  name: 'DynamicReportPage',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'dynamicReportPageList', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('dataConfMod.dynamicReportPageList'),
          name: 'dynamicReportPageList',
          component: dynamicReportPageList,
          closable: false
        }
      ],
      currentTab: null,
      menuInfo: {}
    }
  },
  activated () {
    if (this.currentTab === 'dynamicReportPageList') {
      this.dolayout()
    }
  },
  created () {
    const menus = this.$store.getters.userInfo.menus
    this.menuInfo = findMenuInfoByPath(this.$route.path, menus) || {}
    this.tabs[0].title = this.menuInfo.permissionName ? this.menuInfo.permissionName : this.$t('dataConfMod.dynamicReportPageList')
    this.$nextTick(() => {
      this.$forceUpdate()
    })
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'dynamicReportPageList', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'dynamicReportPageList') {
        this.dolayout()
      }
      this.currentTab = tab
    }
  }
}
</script>
