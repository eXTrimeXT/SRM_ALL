<template>
  <!-- 邀请供应商 -->
  <el-container
    class="flex-container flex-container-right"
    direction="vertical"
    style="padding-top: 10px"
  >
    <OriginInviteSuppliers
      ref="inviteSuppliers"
      :business-type="BUSINESS_TYPE_ENUM.BIDING"
      :invite-suppliers-data="inviteVendorList"
      :material-data="bidRequirementLineList"
      :base-info="bidingBase"
      :is-readonly="readOnly"
    />
  </el-container>
</template>

<script>
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import OriginInviteSuppliers from 'lib@/composition/origin/inviteSuppliers'
import { getRequireInfoByBidingId } from 'modb@/biddingManagementBuyer/api'

export default {
  name: 'InviteSuppliers',

  components: {
    OriginInviteSuppliers
  },

  props: {
    scopeBidingId: {
      // 招标ID
      type: [Number, String],
      default: ''
    },
    activeMenu: {
      type: String,
      default: ''
    },
    bidingBase: {
      type: Object,
      default: () => {}
    },
    readOnly: {
      type: Boolean,
      default: false
    }
  },

  data () {
    return {
      inviteVendorList: [],
      bidRequirementLineList: [],
      BUSINESS_TYPE_ENUM
    }
  },

  created () {
    if (this.activeMenu === 't13') {
      this.getInviteSupplier()
    }
  },

  methods: {
    /* 查询邀请供应商数据 */
    getInviteSupplier () {
      this.$http({
        url: `/api-bid/bidInitiating/biding/getInviteSupplier/${this.scopeBidingId}`,
        method: 'GET',
        loading: true
      }).then(data => {
        if (data && data.data) {
          this.inviteVendorList = data.data || []

          // 查询物料
          this.getProjectRequirementsData()
        }
      })
    },

    /* 查询物料需求信息 */
    async getProjectRequirementsData () {
      const response = await getRequireInfoByBidingId(this.scopeBidingId)
      if (response && response.data) {
        this.bidRequirementLineList = response.data
      }
    },

    /* 保存邀请供应商数据 */
    saveInviteSuppliers (type) {
      const param = {
        bidingId: this.scopeBidingId,
        vendorList: this.$refs.inviteSuppliers.getSuppliersPermissionData(),
        isTempSave: !(type === 'nextOne')
      }
      this.$http({
        url: '/api-bid/bidInitiating/biding/tempSaveOrSubmitInviteSupplier',
        method: 'POST',
        data: param,
        loading: true
      }).then(() => {
        this.$message.success(this.$t('common.success'))

        // 请求数据更新
        this.getInviteSupplier()

        // 下一步保存触发
        if (type === 'nextOne') {
          this.$emit('fetchBaseInfo')
          this.$emit('saveNextTodo')
        } else {
          // 暂存触发
          // 更新节点
          this.$emit('updateProcessNode')
        }
      })
    }
  }
}
</script>
