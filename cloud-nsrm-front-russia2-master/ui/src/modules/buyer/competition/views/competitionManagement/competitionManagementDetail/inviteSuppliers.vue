<template>
  <!-- 邀请供应商 -->
  <el-container class="flex-container flex-container-right" direction="vertical">
    <OriginInviteSuppliers
      ref="inviteSuppliers"
      :business-type="BUSINESS_TYPE_ENUM.COMPETITION"
      :invite-suppliers-data="inviteVendorList"
      :material-data="requirementLineList"
      :base-info="baseInfo"
      :is-readonly="readonly"
    />
  </el-container>
</template>

<script>
/**
 * 邀请供应商
 */
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import { compBuyerHttp } from 'modb@/competition/api'
import OriginInviteSuppliers from 'lib@/composition/origin/inviteSuppliers'

export default {
  name: 'InviteSuppliers',

  components: {
    OriginInviteSuppliers
  },

  props: {
    baseInfo: {
      type: Object,
      default: () => { /* nothing */ }
    },
    readonly: {
      type: Boolean,
      default: false
    },
    // 是否当前tab页
    isCurrentActiveTab: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      inviteVendorList: [],
      requirementLineList: [],
      BUSINESS_TYPE_ENUM
    }
  },

  watch: {
    isCurrentActiveTab: {
      handler (newValue, oldValue) {
        // 切换到当前标签页
        if (newValue && !oldValue) {
          this.getInviteSupplier()
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 查询邀请供应商数据 */
    async getInviteSupplier () {
      const response = await compBuyerHttp.init.getInviteSupplier(this.baseInfo.projectId)
      if (response && response.data) {
        this.inviteVendorList = response.data

        // 查询物料
        await this.getProjectRequirementsData()
      }
    },

    /* 查询物料需求信息 */
    async getProjectRequirementsData () {
      const response = await compBuyerHttp.init.getRequireInfo(this.baseInfo.projectId)
      if (response && response.data && Array.isArray(response.data)) {
        this.requirementLineList = response.data
      }
    },

    /* 保存邀请供应商数据 */
    async saveInviteSuppliers (type) {
      const param = {
        projectId: this.baseInfo.projectId,
        vendorList: this.$refs.inviteSuppliers.getSuppliersPermissionData(),
        isTempSave: type !== 'nextOne'
      }

      if (param.vendorList.length === 0) {
        // '请至少新增一个供应商！'
        this.$message.warning(this.$t('competition.vendorListTip'))
        return { status: false }
      }

      try {
        const response = await compBuyerHttp.init.editInviteSupplier(param)
        if (response && response.data) {
          this.$message.success(this.$t('common.successSave'))

          // 请求数据更新
          await this.getInviteSupplier()

          return { status: true }
        } else {
          return { status: false }
        }
      } catch (e) {
        return { status: false }
      }
    }
  }
}
</script>
