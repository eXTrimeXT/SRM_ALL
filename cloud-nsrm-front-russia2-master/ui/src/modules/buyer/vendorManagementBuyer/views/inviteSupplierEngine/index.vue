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
  import inviteSupplierList from './list_engine'
  export default {
    name: 'InviteSupplier',
    components: {
      NavTabs
    },
    data () {
      return {
        activeTab: 'inviteSupplierList', // 当前激活标签  与name相同
        tabs: [{
          title: this.$t('vendorMod.inviteSupplierList'), // 邀请供应商
          name: 'inviteSupplierList',
          component: inviteSupplierList,
          closable: false
        }]
      }
    },
    activated () {
      console.log('[activated]')
      if (this.activeTab === 'inviteSupplierList') {
        this.dolayout()
      }
    },
    methods: {
      dolayout () {
        this.$nextTick(() => {
          const data = {
            name: 'inviteSupplierList', // 组件名
            methods: 'dolayout', // 方法名
            params: null, // 参数
            random: Math.random()
          }
          this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
        })
      },
      tabChange (tab) {
        if (tab === 'inviteSupplierList') {
          this.dolayout()
        }
        this.activeTab = tab
      }
    }
  }
</script>
