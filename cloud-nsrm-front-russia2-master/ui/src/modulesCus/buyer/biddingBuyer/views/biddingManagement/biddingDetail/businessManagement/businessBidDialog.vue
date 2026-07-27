<template>
  <!-- 组织商务报价 -->
  <SrmDialog
    :title="$t('cusEntry.bidMod.businessQuota')"
    size="large"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
  >
    <el-form
      ref="businessQuotaForm"
      :model="businessQuotaForm"
      label-position="left"
      label-width="110px"
      style="width: 100%; padding-bottom: 10px"
    >
      <!-- 报价截止时间 -->
      <el-form-item
        prop="orderEndTime"
        :label="$t('bidMod.quotedeadline')"
        :rules="[{required: true, message: $t('common.pleaseSelect'), trigger: ['blur', 'change']}]"
        class="custom-spacing"
      >
        <el-date-picker
          v-model="businessQuotaForm.orderEndTime"
          type="datetime"
          :format="$formatDatePickerTime"
          value-format="yyyy-MM-dd HH:mm:ss"
          :placeholder="$t('common.pleaseSelect')"
          :picker-options="cannotLessCurrentTimeOptions"
          style="width: 100%"
        />
      </el-form-item>
      <!-- 选择报价供应商 -->
      <el-form-item
        prop="quoteVendorList"
        :label="$t('cusEntry.bidMod.selectVendor')"
        :rules="[{required: true, message: $t('common.pleaseSelect'), trigger: ['blur', 'change']}]"
      >
        <el-table
          border
          style="width: 100%;"
          max-height="220"
          :data="vendorList"
          highlight-current-row
          @selection-change="handleSelectionChange"
        >
          <el-table-column
            align="center"
            type="selection"
            width="60"
          />
          <el-table-column
            align="center"
            :label="$t('common.sort')"
            type="index"
            width="60"
          />
          <el-table-column
            align="center"
            prop="vendorName"
            :label="$t('common.vendorName')"
            show-overflow-tooltip
            minWidth="150"
          />
          <el-table-column
            align="center"
            prop="orderStatus"
            :label="$t('bidMod.orderStatus')"
            :formatter="(row, column, cellValue) => $getDictLabel('SOU_ORDER_STATUS', cellValue)"
            minWidth="100"
          />
        </el-table>
      </el-form-item>
      <!-- 组织报价原因 -->
      <el-form-item
        prop="extOrderReason"
        :label="$t('cusEntry.bidMod.quotaReason')"
        :rules="[{required: true, message: $t('cusEntry.common.pleaseFill'), trigger: ['blur', 'change']}]"
      >
        <el-input v-model="businessQuotaForm.extOrderReason" />
      </el-form-item>
      <!-- 是否更新报价数量 -->
      <el-form-item
        prop="extPriceFlag"
        :label="$t('cusEntry.bidMod.updateCount')"
      >
        <el-switch
          v-model="businessQuotaForm.extPriceFlag"
          active-value="Y"
          inactive-value="N"
        />
      </el-form-item>
    </el-form>
    <el-table
      v-if="businessQuotaForm.extPriceFlag=='Y'"
      border
      max-height="180"
      :data="itemList"
      style="width: 100%"
      highlight-current-row
    >
      <el-table-column
        align="center"
        type="index"
        fixed="left"
        :label="$t('common.sort')"
        width="50"
      />
      <el-table-column
        align="center"
        prop="itemDesc"
        :label="$t('bidMod.designation')"
        min-width="100"
        show-overflow-tooltip
      />
      <el-table-column
        align="center"
        prop="extQuantity"
        :label="$t('bid_mod.quantity')"
        min-width="100"
        show-overflow-tooltip
      >
        <template slot-scope="scope">
          <el-input-number
            v-model="scope.row.extQuantity"
            style="width: 100%"
            :controls="false"
            :min="0"
          />
        </template>
      </el-table-column>
    </el-table>
    <template #footer>
      <el-button @click="dialogVisible = false">
        {{ $t("common.cancel") }}
      </el-button>

      <el-button type="primary" @click="confirm">
        {{ $t("common.confirm") }}
      </el-button>
    </template>
  </SrmDialog>
</template>

<script>
/**
 * 组织商务报价
 */
import { bidBuyerHttp } from 'modcb@/biddingBuyer/api'
import { cannotLessCurrentTime } from '@/library/mixins/datePickerOptions'

export default {
  name: 'BusinessBidDialog',

  mixins: [cannotLessCurrentTime],

  props: {
    visible: {
      type: Boolean,
      required: true
    },
    projectId: {
      type: [String, Number],
      required: true
    }
  },

  data () {
    return {
      businessQuotaForm: {
        orderEndTime: '',
        extOrderReason: '',
        quoteVendorList: [],
        extPriceFlag: 'N'
      },
      vendorList: [],
      itemList: []
    }
  },

  computed: {
    dialogVisible: {
      get: function () {
        return this.visible
      },
      set: function (val) {
        this.$emit('update:visible', val)
      }
    }
  },

  created () {
    this.getBusinessInfo()
  },

  methods: {
    // 商务标管理-组织商务标-查询接口
    getBusinessInfo () {
      bidBuyerHttp.control.getBusinessInfo(this.projectId).then(res => {
        if (res && res.data) {
          this.vendorList = res.data.vendorList
          this.itemList = res.data.itemList
        }
      })
    },
    handleSelectionChange (val) {
      this.businessQuotaForm.quoteVendorList = val
    },
    confirm () {
      this.$refs.businessQuotaForm.validate(valid => {
        if (valid) {
          const {
            orderEndTime,
            extOrderReason,
            extPriceFlag
          } = this.businessQuotaForm
            bidBuyerHttp.control.confirmBusinessQuota({
            projectId: this.projectId,
            vendorList: this.businessQuotaForm.quoteVendorList,
            itemList: this.itemList,
            orderEndTime,
            extOrderReason,
            extPriceFlag
          }).then(res => {
            this.dialogVisible = false
            this.$message.success(res.message)
            this.$emit('success')
          })
        } else {
          return false
        }
      })
    }
  }
}
</script>
<style scoped>
.custom-spacing {
  margin-bottom: 20px !important;
}
</style>
