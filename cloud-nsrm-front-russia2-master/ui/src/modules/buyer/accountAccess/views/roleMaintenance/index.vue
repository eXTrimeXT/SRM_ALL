<template>
  <NavTabs
    ref="tabs"
    :tabs-list="tabs"
    :cur-tab="activeTab"
  />
</template>
<script>
import NavTabs from 'lib@/components/NavTabs'
import roleMaintenance from './list'
export default {
  name: 'RoleMaintenance',
  components: {
    NavTabs
  },
  data () {
    return {
      activeTab: 'roleMaintenance', // 当前激活标签  与name相同
      tabs: [
        {
          title: this.$t('route.roleMaintenance'),
          name: 'roleMaintenance',
          component: roleMaintenance,
          closable: false
        }
      ]
    }
  },
  activated () {
    if (this.activeTab === 'roleMaintenance') {
      this.dolayout()
    }
  },
  methods: {
    dolayout () {
      this.$nextTick(() => {
        const data = {
          name: 'roleMaintenance', // 组件名
          methods: 'dolayout', // 方法名
          params: null, // 参数
          random: Math.random()
        }
        this.$store.commit('navTabs/SET_NAV_TABS_TODO', data)
      })
    },
    tabChange (tab) {
      if (tab === 'roleMaintenance') {
        this.dolayout()
      }
      this.activeTab = tab
    }
  }
}
</script>
<style scoped lang="scss">
.the_functionMaintenance_wrapper {
  .menuAccessCtrl {
    border: 1px solid #dfe6ec;
  }
}
.form-roleContainer {
  padding: 0 10px;
  .main-header {
    background-color: #f2f2f2;
  }
  .roleNames {
    .el-row .el-form-item:first-child {
      margin-bottom: 10px !important;
    }
    .el-col {
      padding-left: 5px !important;
      padding-right: 5px !important;
    }
  }
}
.menuAccessCtrl ::v-deep {
  .el-tree {
    overflow-y: auto;
    max-height: 222px;
  }
}
.form-roleContainer .el-row.roleNames .el-form-item:first-child {
  margin-bottom: 10px !important;
}
.roleLanguages-required:before {
  content: '*';
  color: #ff4949;
  margin-right: 4px;
}
.borderStyle {
  border: 1px solid #ebebeb;
  border-top: 0;
  border-bottom: 0;
}
</style>
