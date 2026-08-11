<template>
  <nav-tabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import OrgList from './OrgList'
export default {
  name: 'OrganizationSetting',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'orgList', // 当前激活标签  与name相同
      tabs: [{
        title: () => this.$t('dataConfMod.orgList'), // '组织设置'
        name: 'orgList',
        component: OrgList,
        closable: false
      }]
    }
  },
  mounted () {
    // orgListAlert 即将进行【组织设置】，您需要完成：1、创建组织，根据公司和采购管理现状创建组织；2、设置组织层级，根据公司和采购管理层级，设置组织间的层级关系。
    // '提示'
    // '开始'
    let orgTip = localStorage.getItem('orgTip') || 'Y'
    if (orgTip === 'Y') {
      this.$confirm(
        this.$t('dataConfMod.orgListAlert'),
        this.$t('common.tips'), {
          distinguishCancelAndClose: true,
          confirmButtonText: this.$t('common.start'),
          cancelButtonText: this.$t('common.toNotshowTip')
        }).then(() => {
        // 点击开始
      }).catch(() => {
        // 不再提示
        localStorage.setItem('orgTip', 'N')
      })
    }
  }
}
</script>
