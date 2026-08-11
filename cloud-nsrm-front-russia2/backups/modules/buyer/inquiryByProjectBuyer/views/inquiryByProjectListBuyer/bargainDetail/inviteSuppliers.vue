<template>
  <!-- 邀请供应商 -->
  <el-container
    class="flex-container flex-container-right"
    direction="vertical"
    style="padding-top: 10px"
  >
    <OriginInviteSuppliers
      ref="inviteSuppliers"
      business-type="BARGAIN"
      :invite-suppliers-data="inviteVendorList"
      :material-data="brgRequirementLineList"
      :base-info="bargainBase"
      :is-readonly="readOnly"
    />
  </el-container>
</template>

<script>
import OriginInviteSuppliers from 'lib@/composition/origin/inviteSuppliers'

export default {
  name: 'InviteSuppliers',

  components: {
    OriginInviteSuppliers
  },

  props: {
    scopeBargainId: {
      // 招标ID
      type: [Number, String],
      default: ''
    },
    activeMenu: {
      type: String,
      default: ''
    },
    bargainBase: {
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
      brgRequirementLineList: []
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
      this.$api.brg.inquiryByProject.getInviteSupplier(this.scopeBargainId).then(data => {
        if (data && data.data) {
          this.inviteVendorList = data.data || []

          // 查询物料
          this.getProjectRequirementsData()
        }
      })
    },

    /* 查询物料需求信息 */
    async getProjectRequirementsData () {
      const response = await this.$api.brg.getRequireInfoByBargainId(this.scopeBargainId)
      if (response && response.data) {
        this.brgRequirementLineList = response.data
      }
    },

    /* 保存邀请供应商数据 */
    saveInviteSuppliers (type) {
      const param = {
        bargainId: this.scopeBargainId,
        vendorList: this.$refs.inviteSuppliers.getSuppliersPermissionData(),
        isTempSave: type !== 'nextOne'
      }
      this.$api.brg.inquiryByProject.tempSaveOrSubmitInviteSupplier(param).then(() => {
        this.$message.success(this.$t('common.success'))

        // 请求数据更新
        this.getInviteSupplier()

        // 下一步保存触发
        if (type === 'nextOne') {
          this.$emit('fetchBaseInfo')
          this.$emit('saveNextTodo')
        } else {
          // 暂存触发 更新节点
          this.$emit('updateProcessNode')
        }
      })
    }
  }
}
</script>
