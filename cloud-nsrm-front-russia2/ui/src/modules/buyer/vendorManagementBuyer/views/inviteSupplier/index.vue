<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
  import NavTabs from 'lib@/components/NavTabs'
  import inviteSupplierList from './inviteSupplierList'
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
    }
  }
</script>
