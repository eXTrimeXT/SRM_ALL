<template>
  <div class="password-management">
    <div class="password-management-header">
      <span :class="{'is-active': rolesActiveName=='BUYER'}" @click="handleClick('BUYER',false)">采购商</span>
      <span :class="{'is-active': rolesActiveName=='VENDOR'}" @click="handleClick('VENDOR',false)">供应商</span>
    </div>
    <div class="password-management-content">
      <PassConfigEditBuyer
        v-if="rolesActiveName=='BUYER'"
        userType="BUYER"
        :configDate="buyerConfigDate"
        @saveSuccess="saveSuccessFn"
        @isChange="getChangeTimeBuyer"
      />
      <PassConfigEditVendor
        v-if="rolesActiveName=='VENDOR'"
        userType="VENDOR"
        :configDate="vendorConfigDate"
        @saveSuccess="saveSuccessFn"
        @isChange="getChangeTimeVendor"
      />
    </div>
  </div>
</template>

<script>
import PassConfigEditBuyer from './passConfigEditBuyer'
import PassConfigEditVendor from './passConfigEditVendor'
import { securitySettingsApi } from 'modb@/accountAccess/api'

export default {
  name: 'PasswordManagement',
  components: {
    PassConfigEditBuyer,
    PassConfigEditVendor
  },
  data () {
    return {
      rolesActiveName: 'BUYER',
      buyerConfigDate: {},
      vendorConfigDate: {},
      ifSave: false,
      buyerChangeTimes: 0,
      vendorChangeTimes: 0
    }
  },
  created () {
    this.getConfigByRole(this.rolesActiveName, false)
  },
  methods: {
    handleClick (userType, loading = false) {
      let buyerT = this.buyerChangeTimes
      let vendorT = this.vendorChangeTimes

      if (buyerT > 1 || vendorT > 1) {
        this.$confirm('请确认修改的配置已经保存！', {
          confirmButtonText: this.$t('common.affirm'),
          cancelButtonText: this.$t('common.cancel'),
          type: 'warning'
        }).then(() => {
          this.rolesActiveName = userType
          this.getConfigByRole(userType, loading)
          if (userType == 'BUYER') {
            this.vendorChangeTimes = 0
          } else {
            this.buyerChangeTimes = 0
          }
        }).catch(() => {

        })
      } else {
        this.rolesActiveName = userType
        this.getConfigByRole(userType, loading)
        if (userType == 'BUYER') {
          this.vendorChangeTimes = 0
        } else {
          this.buyerChangeTimes = 0
        }
      }
    },
    async getConfigByRole (userType, loading) {
      const { data } = await securitySettingsApi.getUserSecurityConfig({ userType }, loading)
      if (userType === 'BUYER') {
        this.buyerConfigDate = data
      } else {
        this.vendorConfigDate = data
      }
    },
    saveSuccessFn (isSuccess, userType) {
      if (isSuccess) {
        this.ifSave = true
        this.getConfigByRole(userType)
      }
    },
    getChangeTimeBuyer (times) {
      this.buyerChangeTimes = times
    },
    getChangeTimeVendor (times) {
      this.vendorChangeTimes = times
    }
  }
}
</script>

<style lang="scss">
.password-management{
  .password-management-header{
    margin-bottom: 16px;
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
  .password-management-content{
    padding: 16px;
    padding-bottom: 40px !important;
  }
}
.passConfigEdit {
  position: relative;
  color: #161C24;
  .passConfigEdit-footer{
    position: fixed;
    padding: 9px 24px;
    padding-left: 206px;
    left: 0;
    bottom: 0;
    right: 0;
    text-align: right;
    transition: padding-left 0.28s;
    box-sizing: border-box;
    background-color: #ffffff;
    box-shadow: 0 -1px 2px 0 rgba(182, 182, 182, 0.5);
    z-index: 100;
  }
}
</style>
