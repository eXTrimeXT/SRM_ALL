<template>
  <SrmDialog
    :visible.sync="visible"
    size="large"
    :title="$t('cusEntry.biddingSettings.bondRefundHistory')"
    :close-on-click-modal="false"
    :before-close="() => {
      $emit('update:visible', false)
    }"
  >
    <el-form :model="showData" disabled>
      <srm-row :gutter="50">
        <srm-col :init-col="3">
          <el-form-item :label="$t('cusEntry.bidMod.refundAccount')" prop="refundAccount">
            <el-input v-model="showData.refundAccount" />
          </el-form-item>
        </srm-col>
        <srm-col :init-col="3">
          <el-form-item :label="$t('cusEntry.bidMod.refundAccountName')" prop="refundAccountName">
            <el-input v-model="showData.refundAccountName" />
          </el-form-item>
        </srm-col>
        <srm-col :init-col="3">
          <el-form-item :label="$t('cusEntry.bidMod.refundBank')" prop="refundBank">
            <el-input v-model="showData.refundBank" />
          </el-form-item>
        </srm-col>
        <srm-col :init-col="3">
          <el-form-item :label="$t('cusEntry.bidMod.chargeAmount1')" prop="amount">
            <el-input v-model="showData.amount" />
          </el-form-item>
        </srm-col>
        <srm-col :init-col="3">
          <el-form-item :label="$t('cusEntry.bidMod.refundStatus')" prop="refundStatus">
            <DictSelect
              v-model="showData.refundStatus"
              code="SOU_INT_DEPOSIT_REFUND_STATUS"
            />
          </el-form-item>
        </srm-col>
        <srm-col :init-col="3">
          <el-form-item :label="$t('cusEntry.bidMod.refundPaymentDate')" prop="refundPaymentDate">
            <el-date-picker
              v-model="showData.refundPaymentDate"
              :format="$formatDatePicker"
              type="date"
            />
          </el-form-item>
        </srm-col>
        <srm-col :init-col="3">
          <el-form-item :label="$t('cusEntry.bidMod.refundFile')">
            <SrmCommonFile
              :default-file="{
                fileId: showData.fileId,
                fileName: showData.fileName
              }"
              :readonly="true"
            />
          </el-form-item>
        </srm-col>
        <srm-col :init-col="1">
          <el-form-item :label="$t('cusEntry.bidMod.refundRemark')" prop="description">
            <el-input v-model="showData.description" type="textarea" />
          </el-form-item>
        </srm-col>
      </srm-row>
    </el-form>
    <div slot="footer">
      <el-button @click="$emit('update:visible', false)">
        {{ $t('common.close') }}
      </el-button>
    </div>
  </SrmDialog>
</template>

<script>
export default {
  name: 'BondRefundDialog',
  props: {
    visible: {
      type: Boolean,
      default: false,
      required: true
    },
    showData: {
      type: Object,
      default: () => ({})
    }
  },
  data () {
    return {

    }
  }
}
</script>
