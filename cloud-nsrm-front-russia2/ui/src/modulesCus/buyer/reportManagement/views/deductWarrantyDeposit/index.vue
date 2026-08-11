<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import DeductWarrantyDepositList from './deduct-warranty-deposit-list'
export default {
  name: 'DeductWarrantyDeposit',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'DeductWarrantyDepositList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('cusEntry.reportManagement.deductWarrantyDepositList'),
        name: 'DeductWarrantyDepositList',
        component: DeductWarrantyDepositList,
        closable: false
      }]
    }
  }
}
</script>
