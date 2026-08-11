<template>
  <nav-tabs
    ref="accessTabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
    @tab-change="tabChange"
    @tab-remove="tabRemove"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import usersAccessList from './usersAccessList'
import orgAccessList from './orgAccessList'
import roleAccessList from './roleAccessList'
export default {
  name: 'UsersAccess',
  components: {
    NavTabs
  },
  data () {
    return {
      historyTabName: 'usersAccessSingle',
      activeTab: 'usersAccessSingle', // 当前激活标签  与name相同
      tabs: [
        {
          title: () => this.$t('dataConfMod.usersAccessList'), // '用户权限列表',
          name: 'usersAccessSingle',
          component: usersAccessList,
          closable: false
        },
        //  todo 联调接口时放开
        {
          title: () => this.$t('dataConfMod.orgAccessList'), // '组织权限列表',
          name: 'orgAccessList',
          component: orgAccessList,
          closable: false
        },
        {
          title: () => this.$t('dataConfMod.roleAccessList'), // '角色权限列表',
          name: 'roleAccessList',
          component: roleAccessList,
          closable: false
        }
      ],
      params: {

      }
    }
  },
  watch: {
    activeTab (_newVal, oldVal) {
      if (['usersAccessSingle', 'orgAccessList', 'roleAccessList'].includes(oldVal)) {
        this.historyTabName = oldVal
      }
    }
  },
  mounted () {
    // 即将进行【导入子账号】，您需要完成：1、导入企业采购业务的子账号；2、维护子账号的组织权限和角色权限；
    let userAccessTip = localStorage.getItem('userAccessTip') || 'Y'
    if (userAccessTip === 'Y') {
      this.$confirm(
        this.$t('dataConfMod.usersAccessListAlert'),
        this.$t('common.tips'), {
          distinguishCancelAndClose: true,
          confirmButtonText: this.$t('common.start'),
          cancelButtonText: this.$t('common.toNotshowTip')
        }).then(() => {
        // 点击开始
      }).catch(action => {
        // 不再提示
        localStorage.setItem('userAccessTip', 'N')
      })
    }
  },
  methods: {
    tabChange (tab) {
      this.activeTab = tab
      this.tabs = this.$refs.accessTabs.tabs
    },
    tabRemove ({ activeTab }) {
      if (!['usersAccessSingle', 'orgAccessList', 'roleAccessList'].includes(activeTab)) return
      this.$refs.accessTabs.activeTab = this.historyTabName
    }
  }
}
</script>
