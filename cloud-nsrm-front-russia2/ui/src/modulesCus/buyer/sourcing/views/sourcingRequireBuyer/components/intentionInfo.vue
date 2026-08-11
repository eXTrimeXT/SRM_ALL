<template>
  <SrmRow>
    <SrmCol :init-col="1">
      <!-- <el-form-item label="需缴纳意向金" prop="isNeedDeposit"> -->
      <el-form-item :label="$t('cusEntry.supplement20250121.intentionDepositRequiredToBePaid')" prop="isNeedDeposit">
        <el-switch v-model="baseForm.isNeedDeposit" active-value="Y" inactive-value="N" :disabled="readonly">
          <!-- 需缴纳意向金 -->
          {{ $t("cusEntry.supplement20250121.intentionDepositRequiredToBePaid") }}
        </el-switch>
      </el-form-item>
    </SrmCol>
    <template v-if="baseForm.isNeedDeposit === 'Y'">
      <SrmCol :init-col="4">
        <!-- <el-form-item label="意向金缴纳金额（元）" prop="depositAmount"> -->
        <el-form-item :label="$t('cusEntry.supplement20250121.intentionalDepositPaymentAmount_yuan')" prop="depositAmount">
          <el-input-number v-model="baseForm.depositAmount" style="width:100%" :min="0" :disabled="readonly || ['NB', 'GF', 'MD', 'JG'].includes(baseForm.organizationCode)" />
        </el-form-item>
      </SrmCol>
      <SrmCol :init-col="4">
        <!-- <el-form-item label="开户银行" prop="bankName"> -->
        <el-form-item  :label="$t('cusEntry.biddingSettings.bankName')" prop="bankName">
          <el-input v-model="baseForm.bankName" disabled />
        </el-form-item>
      </SrmCol>
      <SrmCol :init-col="4">
        <!-- <el-form-item label="银行联行号" prop="bankNumber"> -->
        <el-form-item :label="$t('cusEntry.biddingSettings.bankNum')" prop="bankNumber">
          <el-input v-model="baseForm.bankNumber" disabled />
        </el-form-item>
      </SrmCol>
      <SrmCol :init-col="4">
        <!-- <el-form-item label="开户账号" prop="bankAccount"> -->
        <el-form-item :label="$t('cusEntry.biddingSettings.accountNum')" prop="bankAccount">
          <el-input v-model="baseForm.bankAccount" disabled />
        </el-form-item>
      </SrmCol>
      <SrmCol :init-col="4">
        <!-- <el-form-item label="开户户名" prop="bankAccountName"> -->
        <el-form-item :label="$t('cusEntry.biddingSettings.accountName')" prop="bankAccountName">
          <el-input v-model="baseForm.bankAccountName" disabled />
        </el-form-item>
      </SrmCol>
    </template>
  </SrmRow>
</template>
<script>
import QuickSearch from 'lib@/components/QuickSearch'
import OrganizationSelector from 'lib@/components/organization-selector'
export default {
  components: {
    QuickSearch,
    OrganizationSelector
  },
  props: {
    form: {
      type: Object,
      default: () => ({})
    },
    readonly: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {

    }
  },
  computed: {
    baseForm: {
      get: function () {
        return this.form
      },
      set: function (val) {
        this.$emit('update:form', val)
      }
    }
  },
  methods: {
    applyNoClick () {

    }
  }
}
</script>
