<template>
  <!-- 邀请供应商 -->
  <el-container
    class="flex-container flex-container-right"
    direction="vertical"
    style="padding-top: 10px"
  >
    <OriginInviteSuppliers
      ref="inviteSuppliers"
      :business-type="BUSINESS_TYPE_ENUM.BARGAIN_LTS"
      :invite-suppliers-data="inviteVendorList"
      :material-data="itemList"
      :base-info="bargainBase"
      :is-readonly="readonly"
    />
  </el-container>
</template>

<script>
import { brgBuyerHttp } from 'modb@/bargain/api'
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import OriginInviteSuppliers from 'lib@/composition/origin/inviteSuppliers'

export default {
  name: 'InviteVendor',

  components: { OriginInviteSuppliers },

  props: {
    bargainBase: {
      type: Object,
      default: () => ({})
    },
    readonly: {
      type: Boolean,
      default: false
    },
    isActiveMenu: {
      type: Boolean,
      required: true
    }
  },

  data () {
    return {
      inviteVendorList: [],
      itemList: [],
      BUSINESS_TYPE_ENUM
    }
  },

  watch: {
    isActiveMenu: {
      handler (val) {
        if (val) {
          this.getInviteSupplier()
        }
      },
      immediate: true
    }
  },

  methods: {
    /* 查询邀请供应商数据 */
    async getInviteSupplier () {
      const response = await brgBuyerHttp.init.getInviteSupplier(this.bargainBase.projectId)
      if (response && response.data) {
        this.inviteVendorList = response.data || []

        if (this.inviteVendorList.length === 0 && this.readonly) {
          // 查看，没有邀请供应商，无需查询
          return
        }
        // 查询物料
        await this.getProjectRequirementsData()
      }
    },

    /* 查询物料需求信息 */
    async getProjectRequirementsData () {
      const response = await brgBuyerHttp.init.getRequireInfo(this.bargainBase.projectId)
      if (response && response.data) {
        this.itemList = response.data
      }
    },

    /* 保存邀请供应商数据 */
    async saveInviteSuppliers (type) {
      const param = {
        projectId: this.bargainBase.projectId,
        vendorList: this.$refs.inviteSuppliers.getSuppliersPermissionData(),
        isTempSave: type !== 'nextOne'
      }
      const response = await brgBuyerHttp.init.editInviteSupplier(param)
      if (response) {
        this.$message.success(this.$t('common.success'))

        if (type !== 'nextOne') {
          // 更新列表
          await this.getInviteSupplier()
        }

        // 发起保存成功回调
        this.$emit('temp-save-success', type)
      }
    }
  }
}
</script>
