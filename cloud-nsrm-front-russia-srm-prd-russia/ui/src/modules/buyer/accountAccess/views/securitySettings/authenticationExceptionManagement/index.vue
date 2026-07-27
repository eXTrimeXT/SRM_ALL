<template>
  <el-container class="authentication-exception-management" direction="vertical">
    <el-header class="authentication-exception-header" height="36">
      <span :class="{'is-active': rolesActiveName=='todo'}" @click="handleClick('todo')">待处理</span>
      <span :class="{'is-active': rolesActiveName=='done'}" @click="handleClick('done')">已处理</span>
    </el-header>
    <el-main class="deepClass">
      <div class="authentication-exception-content">
        <Todo
          v-if="rolesActiveName=='todo'"
          userType="BUYER"
          :configDate="buyerConfigDate"
          @saveSuccess="saveSuccessFn"
        />
        <Done
          v-if="rolesActiveName=='done'"
          userType="VENDOR"
          :configDate="vendorConfigDate"
          @saveSuccess="saveSuccessFn"
        />
      </div>
    </el-main>
  </el-container>
</template>

<script>
import Done from './done'
import Todo from './todo'
import { securitySettingsApi } from 'modb@/accountAccess/api'

export default {
  name: 'AuthenticationExceptionManagement',
  components: {
    Todo,
    Done
  },
  data () {
    return {
      rolesActiveName: 'todo',
      buyerConfigDate: {},
      vendorConfigDate: {},
      ifSave: false
    }
  },
  created () {
    this.getConfigByRole(this.rolesActiveName)
  },
  methods: {
    handleClick (type) {
      this.rolesActiveName = type
    },
    async getConfigByRole (userType) {
      // const { data } = await securitySettingsApi.getUserSecurityConfig({ userType })
      // if (userType === 'BUYER') {
      //   this.buyerConfigDate = data
      // } else {
      //   this.vendorConfigDate = data
      // }
    },
    saveSuccessFn (isSuccess, userType) {
      if (isSuccess) {
        this.ifSave = true
        this.getConfigByRole(userType)
      }
    }
  }
}
</script>

<style lang="scss">
.authentication-exception-management{
  .authentication-exception-header{
    position: relative;
    padding: 0 18px;
    height: 36px;
    border-bottom: 1px solid #DCDDDE;
    span{
      display: inline-block;
      border-radius: 4px 4px 0 0;
      margin-right: 2px;
      border-bottom: 2px solid transparent;
      font-size: 16px;
      color: #161C24;
      line-height: 34px !important;
      padding: 0 1px;
      margin-right: 24px;
      cursor: pointer;
      &.is-active {
        color: #0077FF;
        border-bottom-color: #0077FF;
      }
    }
  }
  .authentication-exception-content{
    height: calc(-112px + 100vh);
    padding: 16px;
    box-sizing: border-box;
  }
}
</style>
