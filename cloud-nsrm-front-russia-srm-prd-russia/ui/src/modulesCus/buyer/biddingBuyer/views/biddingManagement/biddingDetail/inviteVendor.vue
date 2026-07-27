<template>
  <!-- 供应商清单 -->
  <div style="padding-top: 10px">
    <el-form ref="form" :model="biddingBase">
      <el-form-item
        prop="extRecommendNo"
        label="推荐供应商单号"
      >
        <span
          v-for="(item,index) in extRecommendNoList"
          :key="index"
          style="cursor: pointer; color: #0077ff; margin-right: 5px;"
          @click="extRecommendNoClick(item,index)"
        >
          {{ item }}
        </span>
      </el-form-item>
    </el-form>
    <el-button
      type="primary"
      style="margin: 10px 0;"
      :disabled="readonly"
      @click="getRecommVendorInfoByNo"
    >
      获取供应商
    </el-button>

    <el-table
      :data="inviteVendorList"
      style="width: 100%"
      border
    >
      <!-- 序号 -->
      <el-table-column
        align="center"
        type="index"
        fixed="left"
        :label="$t('common.sort')"
        width="50"
      />
      <el-table-column
        align="center"
        prop="vendorCode"
        :label="$t('common.vendorCode')"
        min-width="150"
      />
      <el-table-column
        align="center"
        prop="vendorName"
        :label="$t('common.vendorName')"
        min-width="150"
      />
      <el-table-column
        align="center"
        prop="linkmanName"
        :label="$t('vendorMod.contactPerson')"
        min-width="120"
      />
      <el-table-column
        align="center"
        prop="phone"
        :label="$t('common.phone')"
        min-width="150"
      />
      <el-table-column
        align="center"
        prop="email"
        :label="$t('common.email')"
        min-width="150"
      />
      <el-table-column
        align="center"
        prop="extIsMainPoint"
        label="是否重点关注"
        :formatter="(row, column, val) => val ? $getDictLabel('YES_OR_NO', val) : ''"
        min-width="120"
      />
    </el-table>
  </div>
</template>

<script>
import { bidBuyerHttp } from 'modcb@/biddingBuyer/api'
import { BUSINESS_TYPE_ENUM } from 'lib@/composition/origin/enum'
import RecommendVendorDetail from 'modcb@/supplierRecommend/views/recommendVendor/edit'

export default {
  name: 'InviteVendor',
  props: {
    biddingBase: {
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
      BUSINESS_TYPE_ENUM
    }
  },

  computed: {
    extRecommendNoList () {
      if (this.biddingBase.extRecommendNo) {
        return this.biddingBase.extRecommendNo.toString().split(',')
      }
      return []
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
    // 跳转至供应商推荐单号详情页
    extRecommendNoClick (item, index) {
      if (this.extRecommendNoList) {
        let extRecommendId = this.biddingBase.extRecommendId.toString().split(',')[index]
        const row = {
          extRecommendNo: item,
          projectId: extRecommendId
        }
        this.$emit('tab-add', {
          component: RecommendVendorDetail,
          params: {
            flag: 'view',
            row,
            tabName: 'RecommendVendorDetail' + row.extRecommendNo
          },
          title: '推荐供应商' + row.extRecommendNo,
          name: 'RecommendVendorDetail' + row.extRecommendNo
        })
      }
    },
    getRecommVendorInfoByNo () {
      if (!this.biddingBase.extRecommendNo) {
        this.$message.error('未绑定供应商推荐单号')
        return
      }
      this.$http({
        url: '/api-sou/ext/buyer/recommvendor/getRecommVendorInfoByNo',
        method: 'GET',
        params: { recommVendorNo: this.biddingBase.extRecommendNo },
        loading: true
      }).then(res => {
        this.inviteVendorList = res.data.souVendor
        if (res.data.souVendor.length > 0) {
          this.$message.success('获取供应商成功')
        } else {
          this.$message.error('未查询到关联已审批的推荐供应商单据')
        }
      })
    },
    /* 查询邀请供应商数据 */
    async getInviteSupplier () {
      const response = await bidBuyerHttp.init.getInviteSupplier(this.biddingBase.projectId)
      if (response && response.data) {
        this.inviteVendorList = response.data || []
      }
    },

    /* 保存邀请供应商数据 */
    async saveInviteSuppliers (type) {
      const param = {
        projectId: this.biddingBase.projectId,
        vendorList: this.inviteVendorList,
        tempSave: type !== 'nextOne'
      }
      const response = await bidBuyerHttp.init.editInviteSupplier(param)
      if (response) {
        this.$message.success(this.$t('common.success'))

        if (type !== 'nextOne') {
          // 更新列表
          await this.getInviteSupplier()
        } else {
          // 提交审批成功回调-关闭单据
          this.$emit('after-submit')
        }
        // 发起保存成功回调
        // this.$emit('temp-save-success', type)
      }
    }
  }
}
</script>
