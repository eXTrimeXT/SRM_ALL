<!--
 * @Author: linyk7 linyk7@meicloud.com
 * @Date: 2023-06-21 16:13:50
 * @LastEditors: linyk7 linyk7@meicloud.com
 * @LastEditTime: 2023-06-21 16:21:02
 * @FilePath: \ui\src\modules\buyer\competition\views\competitionManageBuyer\competitionManagementDetail\inviteVendor.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
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
import { carBuyerHttp } from 'modb@/competition/api'
import OriginInviteSuppliers from 'lib@/composition/competition/inviteSuppliers'
import { transformMQL } from 'lib@/utils/util'

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
      let transfromParams = transformMQL.save('AuctSouProjectForBuyer', [{ projectId: this.baseInfo.projectId }], 'listVendorInfo')
      const response = await carBuyerHttp.init.getInviteSupplier(transfromParams)
      if (response?.data?.records) {
        this.inviteVendorList = response.data.records || []

        // 查询物料
        await this.getProjectRequirementsData()
      }
    },

    /* 查询物料需求信息 */
    async getProjectRequirementsData () {
      let transfromParams = transformMQL.save('AuctSouProjectForBuyer', [{ projectId: this.baseInfo.projectId }], 'listRequireInfo')
      const response = await carBuyerHttp.init.listRequireInfo(transfromParams)
      if (response && response.data && Array.isArray(response.data.records)) {
        this.requirementLineList = response.data.records || []
      }
    },

    /* 保存邀请供应商数据 */
    async saveInviteSuppliers (type) {
      const param = {
        projectId: this.baseInfo.projectId,
        vendorList: this.$refs.inviteSuppliers.getSuppliersPermissionData(),
        tempSave: type !== 'nextOne'
      }

      if (param.vendorList.length === 0) {
        this.$message.warning('请至少新增一个供应商！')
        return { status: false }
      }

      try {
        let transfromParams = transformMQL.save('AuctSouProjectForBuyer', [param], 'editVendorInfo')
        const response = await carBuyerHttp.init.editInviteSupplier(transfromParams)
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
