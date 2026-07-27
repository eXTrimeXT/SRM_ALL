<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
    @tab-change="tabChange"
  />
</template>
<script>
  import NavTabs from 'lib@/components/NavTabs'
  import carInfoList from './list-engine'
  export default {
    name: 'CarInfo',
    components: {
      NavTabs
    },
    data () {
      return {
        activeTab: 'carInfoList', // 当前激活标签  与name相同
        tabs: [{
          title: this.$t('vendorMod.carInfoMaintenance'), // 车辆信息列表
          name: 'carInfoList',
          component: carInfoList,
          closable: false
        }]
      }
    },
    activated () {
      console.log('[activated]')
      if (this.activeTab === 'carInfoList') {
        this.dolayout()
      }
    },
    methods: {
      dolayout () {
        this.$nextTick(() => {
          const data = {
            name: 'carInfoList', // 组件名
            methods: 'dolayout', // 方法名
            params: null, // 参数
            random: Math.random()
          }
          this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
        })
      },
      tabChange (tab) {
        if (tab === 'carInfoList') {
          this.dolayout()
        }
        this.activeTab = tab
      }
    }
  }
</script>
