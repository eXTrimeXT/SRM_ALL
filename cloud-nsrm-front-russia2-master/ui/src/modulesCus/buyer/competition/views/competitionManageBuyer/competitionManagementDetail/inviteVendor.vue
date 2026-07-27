
<template>
  <!-- 邀请供应商 -->
  <el-container class="flex-container flex-container-right invite-vendor-list" direction="vertical">
    <el-table
      :data="inviteVendorList"
      style="width: 100%;"
      border
      max-height="450"
    >
      <el-table-column
        align="center"
        type="index"
        width="50"
      />

      <!--供应商编码-->
      <el-table-column
        align="center"
        prop="vendorCode"
        :label="$t('bidMod.vendorCode')"
        min-width="120"
        show-overflow-tooltip
      />

      <!--供应商名称-->
      <el-table-column
        align="center"
        prop="vendorName"
        :label="$t('bidMod.vendorName')"
        min-width="120"
        show-overflow-tooltip
      />

      <!--联系人-->
      <el-table-column
        align="center"
        prop="linkmanName"
        :label="$t('bidMod.linkMan')"
        min-width="150"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <QuickSearch
            :pre-query-data="{ 't.COMPANY_ID': scope.row.vendorId }"
            :show-input="scope.row.linkmanName"
            show-key="contactName"
            allow-input
            :scope-data="scope.row"
            :table-index="scope.$index"
            name="scc_sup_contact_info"
            :disabled="readonly"
            @close-quicksearch="setContactObj"
          />
        </template>
      </el-table-column>

      <!--电话-->
      <el-table-column
        align="center"
        prop="phone"
        :label="$t('bidMod.phone')"
        min-width="150"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <el-input v-model="scope.row.phone" :disabled="readonly" />
        </template>
      </el-table-column>

      <!--邮箱-->
      <el-table-column
        align="center"
        prop="email"
        :label="$t('bidMod.email2')"
        min-width="150"
        show-overflow-tooltip
      >
        <template v-slot="scope">
          <el-input v-model="scope.row.email" :disabled="readonly" />
        </template>
      </el-table-column>
    </el-table>
  </el-container>
</template>

<script>
/**
 * 邀请供应商
 */
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import { carBuyerHttp } from 'modcb@/competition/api'
import { transformMQL } from 'lib@/utils/util'
import QuickSearch from 'lib@/components/QuickSearch'
export default {
  name: 'InviteSuppliers',
  components: {
    QuickSearch
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
    /* 获取联系人信息 */
    setContactObj (value, row) {
      const {
        contactName = '',
        ceeaContactMethod = '',
        email = ''
      } = value || {}
      row.linkmanName = contactName
      row.phone = ceeaContactMethod
      row.email = email
    },
    /* 查询邀请供应商数据 */
    async getInviteSupplier () {
      // let transfromParams = transformMQL.save('AuctSouProjectForBuyer', [{ projectId: this.baseInfo.projectId }], 'listVendorInfo')
      const response = await carBuyerHttp.init.getInviteSupplier(this.baseInfo.projectId)
      if (response?.data) {
        this.inviteVendorList = response.data || []
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
        vendorList: this.inviteVendorList,
        businessType: 'SOU_BID_FLOW',
        isTempSave: type !== 'nextOne'
      }

      if (param.vendorList.length === 0) {
        this.$message.warning('请至少新增一个供应商！')
        return { status: false }
      }

      try {
        // let transfromParams = transformMQL.save('AuctSouProjectForBuyer', [param], 'editVendorInfo')
        const response = await carBuyerHttp.init.editInviteSupplier(param)
        if (response && response.data) {
          this.$message.success(this.$t('common.successSave'))
          const submitEngineData = {
            businessType: 'SOU_BID_FLOW',
            businessId: this.baseInfo.projectId
          }
          if (type === 'nextOne') {
            await carBuyerHttp.calibrate.submitEngine(submitEngineData)
          }
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

<style scoped>
.invite-vendor-list {
  height: 450px !important;
}
</style>
